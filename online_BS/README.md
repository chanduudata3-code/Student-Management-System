# BookNest

The existing HTML/CSS interface is served by a Java HTTP server. The server
provides the book catalog and manages each browser's wishlist.

## Run

Install JDK 17 or newer. From the project folder, compile and start the app:

```powershell
javac BookStoreApp.java
java BookStoreApp
```

Open [http://localhost:8080](http://localhost:8080) in your browser. The
catalog is bundled with the app, and wishlists are persisted in
`.booknest-wishlists.properties` in the project folder.

Java classes remain separate in the project folder: `Book` holds book details,
`BookCatalog` provides the catalog, `WishlistStore` manages persistent
wishlists, `BookstoreServer` serves the UI and API, and `BookStoreApp` starts
the application. Maven and PostgreSQL are not required.

## API

- `GET /api/books` — return the catalog.
- `GET /api/wishlist` — return the current browser session's wishlist.
- `POST /api/wishlist/{bookId}` — add a book, enforcing the 10-book limit.
- `DELETE /api/wishlist/{bookId}` — remove a book.
