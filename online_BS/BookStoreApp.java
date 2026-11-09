import java.io.IOException;

public class BookStoreApp {
    public static void main(String[] args) throws IOException {
        int port = args.length == 0 ? 8080 : Integer.parseInt(args[0]);
        BookCatalog catalog = new BookCatalog();
        WishlistStore wishlistStore = new WishlistStore();
        new BookstoreServer(port, catalog, wishlistStore).start();
    }
}
