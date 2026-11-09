public class Book {
    final int id;
    final String title;
    final String author;
    final String genre;
    final String language;
    final int price;
    final int stock;
    final String icon;

    public Book(int id, String title, String author, String genre,
                int price, int stock, String icon) {
        this(id, title, author, genre, "English", price, stock, icon);
    }

    public Book(int id, String title, String author, String genre, String language,
                int price, int stock, String icon) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.language = language;
        this.price = price;
        this.stock = stock;
        this.icon = icon;
    }
}
