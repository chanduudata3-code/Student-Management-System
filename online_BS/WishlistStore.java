import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class WishlistStore {
    private static final int LIMIT = 10;
    private final Path storageFile = Path.of(".booknest-wishlists.properties")
            .toAbsolutePath();

    public enum AddResult {
        ADDED,
        ALREADY_ADDED,
        OVER_LIMIT,
        LIMIT_REACHED
    }

    public static class StorageException extends RuntimeException {
        public StorageException(IOException cause) {
            super("Could not read or write the local wishlist file.", cause);
        }
    }

    public synchronized List<Integer> getWishlist(String sessionId) {
        return readWishlist(loadState(), sessionId);
    }

    public synchronized AddResult addBook(String sessionId, int bookId) {
        Properties state = loadState();
        List<Integer> bookIds = readWishlist(state, sessionId);
        if (bookIds.size() > LIMIT) {
            return AddResult.OVER_LIMIT;
        }
        if (bookIds.contains(bookId)) {
            return AddResult.ALREADY_ADDED;
        }
        if (bookIds.size() >= LIMIT) {
            return AddResult.LIMIT_REACHED;
        }

        bookIds.add(bookId);
        state.setProperty(sessionId, formatWishlist(bookIds));
        saveState(state);
        return AddResult.ADDED;
    }

    public synchronized void removeBook(String sessionId, int bookId) {
        Properties state = loadState();
        List<Integer> bookIds = readWishlist(state, sessionId);
        if (bookIds.remove(Integer.valueOf(bookId))) {
            state.setProperty(sessionId, formatWishlist(bookIds));
            saveState(state);
        }
    }

    private Properties loadState() {
        Properties state = new Properties();
        if (Files.exists(storageFile)) {
            try (var input = Files.newInputStream(storageFile)) {
                state.load(input);
            } catch (IOException exception) {
                throw new StorageException(exception);
            }
        }
        return state;
    }

    private List<Integer> readWishlist(Properties state, String sessionId) {
        List<Integer> bookIds = new ArrayList<>();
        String stored = state.getProperty(sessionId, "");
        if (!stored.isEmpty()) {
            for (String bookId : stored.split(",")) {
                try {
                    bookIds.add(Integer.parseInt(bookId));
                } catch (NumberFormatException exception) {
                    throw new StorageException(new IOException(
                            "Invalid book ID in the local wishlist file.", exception));
                }
            }
        }
        return bookIds;
    }

    private String formatWishlist(List<Integer> bookIds) {
        return bookIds.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private void saveState(Properties state) {
        Path temporaryFile = null;
        try {
            temporaryFile = Files.createTempFile(
                    storageFile.getParent(), ".booknest-wishlists-", ".tmp");
            try (var output = Files.newOutputStream(temporaryFile)) {
                state.store(output, "BookNest wishlists");
            }
            try {
                Files.move(temporaryFile, storageFile,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, storageFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new StorageException(exception);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException exception) {
                    throw new StorageException(exception);
                }
            }
        }
    }
}
