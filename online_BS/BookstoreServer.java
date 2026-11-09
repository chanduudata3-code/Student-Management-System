import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class BookstoreServer {
    private static final Path WEB_ROOT = Path.of(".").toAbsolutePath().normalize();
    private final BookCatalog catalog;
    private final WishlistStore wishlistStore;
    private final HttpServer server;

    public BookstoreServer(int port, BookCatalog catalog,
                           WishlistStore wishlistStore) throws IOException {
        this.catalog = catalog;
        this.wishlistStore = wishlistStore;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::handleRequest);
        server.setExecutor(Executors.newCachedThreadPool());
    }

    public void start() {
        server.start();
        System.out.println("BookNest is running at http://localhost:"
                + server.getAddress().getPort());
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        try {
            handleApiOrPage(exchange, path, method);
        } catch (WishlistStore.StorageException exception) {
            System.err.println("Wishlist storage failed: " + exception.getMessage());
            sendError(exchange, 500, "Wishlist storage failed. Check the server logs.");
        }
    }

    private void handleApiOrPage(HttpExchange exchange, String path,
                                 String method) throws IOException {
        if (path.equals("/api/books")) {
            if (!method.equals("GET")) {
                sendError(exchange, 405, "Method not allowed.");
                return;
            }
            sendBooks(exchange);
            return;
        }

        if (path.equals("/api/wishlist")) {
            if (!method.equals("GET")) {
                sendError(exchange, 405, "Method not allowed.");
                return;
            }
            sendJson(exchange, 200,
                    wishlistStore.getWishlist(getSessionId(exchange)).toString());
            return;
        }

        if (path.startsWith("/api/wishlist/")) {
            handleWishlistChange(exchange, path, method);
            return;
        }

        servePage(exchange, path, method);
    }

    private void sendBooks(HttpExchange exchange) throws IOException {
        List<String> bookJson = new ArrayList<>();
        for (Book book : catalog.getBooks()) {
            bookJson.add("{"
                    + "\"id\":" + book.id
                    + ",\"title\":" + jsonString(book.title)
                    + ",\"author\":" + jsonString(book.author)
                    + ",\"genre\":" + jsonString(book.genre)
                    + ",\"language\":" + jsonString(book.language)
                    + ",\"price\":" + book.price
                    + ",\"stock\":" + book.stock
                    + ",\"icon\":" + jsonString(book.icon)
                    + "}");
        }
        sendJson(exchange, 200, "[" + String.join(",", bookJson) + "]");
    }

    private void handleWishlistChange(HttpExchange exchange, String path,
                                      String method) throws IOException {
        if (!method.equals("POST") && !method.equals("DELETE")) {
            sendError(exchange, 405, "Method not allowed.");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(path.substring("/api/wishlist/".length()));
        } catch (NumberFormatException exception) {
            sendError(exchange, 400, "Invalid book ID.");
            return;
        }

        if (catalog.findBook(bookId) == null) {
            sendError(exchange, 404, "Book not found.");
            return;
        }

        String sessionId = getSessionId(exchange);
        if (method.equals("POST")) {
            WishlistStore.AddResult result = wishlistStore.addBook(sessionId, bookId);
            if (result != WishlistStore.AddResult.ADDED) {
                sendError(exchange, 409, addErrorMessage(result));
                return;
            }
        } else {
            wishlistStore.removeBook(sessionId, bookId);
        }

        sendJson(exchange, 200, wishlistStore.getWishlist(sessionId).toString());
    }

    private String addErrorMessage(WishlistStore.AddResult result) {
        return switch (result) {
            case ALREADY_ADDED -> "This book is already in your wishlist.";
            case OVER_LIMIT ->
                    "Your existing wishlist exceeds 10 books. Remove some books first.";
            case LIMIT_REACHED -> "Wishlist limit reached! Maximum 10 books allowed.";
            case ADDED -> "";
        };
    }

    private String getSessionId(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");
        if (cookieHeader != null) {
            for (String cookie : cookieHeader.split(";")) {
                String[] parts = cookie.trim().split("=", 2);
                if (parts.length == 2 && parts[0].equals("BOOKNEST_SESSION")) {
                    return parts[1];
                }
            }
        }

        String sessionId = UUID.randomUUID().toString();
        exchange.getResponseHeaders().add("Set-Cookie",
                "BOOKNEST_SESSION=" + sessionId
                        + "; Path=/; Max-Age=31536000; HttpOnly; SameSite=Lax");
        return sessionId;
    }

    private void servePage(HttpExchange exchange, String requestPath,
                           String method) throws IOException {
        if (!method.equals("GET")) {
            sendError(exchange, 405, "Method not allowed.");
            return;
        }

        String fileName = requestPath.equals("/") ? "index.html" : requestPath.substring(1);
        if (!fileName.equals("index.html")
                && !fileName.equals("script.js")
                && !fileName.equals("style.css")) {
            sendError(exchange, 404, "Not found.");
            return;
        }

        Path file = WEB_ROOT.resolve(fileName);
        String contentType = switch (fileName) {
            case "index.html" -> "text/html; charset=utf-8";
            case "script.js" -> "text/javascript; charset=utf-8";
            case "style.css" -> "text/css; charset=utf-8";
            default -> "application/octet-stream";
        };
        byte[] content = Files.readAllBytes(file);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, content.length);
        try (var output = exchange.getResponseBody()) {
            output.write(content);
        }
    }

    private void sendError(HttpExchange exchange, int status,
                           String message) throws IOException {
        sendJson(exchange, status, "{\"message\":" + jsonString(message) + "}");
    }

    private void sendJson(HttpExchange exchange, int status,
                          String json) throws IOException {
        byte[] content = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, content.length);
        try (var output = exchange.getResponseBody()) {
            output.write(content);
        }
    }

    private String jsonString(String value) {
        StringBuilder json = new StringBuilder("\"");
        for (char character : value.toCharArray()) {
            switch (character) {
                case '"' -> json.append("\\\"");
                case '\\' -> json.append("\\\\");
                case '\b' -> json.append("\\b");
                case '\f' -> json.append("\\f");
                case '\n' -> json.append("\\n");
                case '\r' -> json.append("\\r");
                case '\t' -> json.append("\\t");
                default -> {
                    if (character < 0x20) {
                        json.append(String.format("\\u%04x", (int) character));
                    } else {
                        json.append(character);
                    }
                }
            }
        }
        return json.append('"').toString();
    }
}
