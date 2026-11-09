import java.util.ArrayList;
import java.util.List;

public class BookCatalog {
    private static final List<Book> INITIAL_BOOKS = List.of(
        new Book(1, "It Ends with Us", "Colleen Hoover", "Love & Romance", 450, 12, "💕"),
        new Book(2, "The Love Hypothesis", "Ali Hazelwood", "Love & Romance", 399, 8, "💗"),
        new Book(3, "The Notebook", "Nicholas Sparks", "Love & Romance", 350, 10, "🌹"),
        new Book(4, "Pride and Prejudice", "Jane Austen", "Love & Romance", 299, 15, "💌"),
        new Book(5, "Me Before You", "Jojo Moyes", "Love & Romance", 420, 7, "❤️"),
        new Book(6, "The Fault in Our Stars", "John Green", "Love & Romance", 380, 9, "✨"),
        new Book(7, "Five Feet Apart", "Rachael Lippincott", "Friendship", 320, 11, "🫂"),
        new Book(8, "Wonder", "R.J. Palacio", "Friendship", 350, 10, "🌈"),
        new Book(9, "The Kite Runner", "Khaled Hosseini", "Friendship", 450, 8, "🪁"),
        new Book(10, "Harry Potter", "J.K. Rowling", "Friendship", 500, 14, "🪄"),
        new Book(11, "The Palace of Illusions", "Chitra Banerjee", "Mythology", 480, 10, "🪷"),
        new Book(12, "The Immortals of Meluha", "Amish Tripathi", "Mythology", 420, 13, "🔱"),
        new Book(13, "Ramayana", "C. Rajagopalachari", "Mythology", 390, 12, "🏹"),
        new Book(14, "Mahabharata", "Devdutt Pattanaik", "Mythology", 550, 9, "📿"),
        new Book(15, "Sita: Warrior of Mithila", "Amish Tripathi", "Mythology", 450, 6, "👑"),
        new Book(16, "Little Women", "Louisa May Alcott", "Family Drama", 320, 10, "🏡"),
        new Book(17, "The God of Small Things", "Arundhati Roy", "Family Drama", 450, 8, "🌳"),
        new Book(18, "A Thousand Splendid Suns", "Khaled Hosseini", "Family Drama", 480, 7, "🌻"),
        new Book(19, "The Namesake", "Jhumpa Lahiri", "Family Drama", 390, 9, "🏠"),
        new Book(20, "Three Men in a Boat", "Jerome K. Jerome", "Comedy", 250, 12, "😂"),
        new Book(21, "Diary of a Wimpy Kid", "Jeff Kinney", "Comedy", 350, 15, "🤣"),
        new Book(22, "The Hitchhiker's Guide", "Douglas Adams", "Comedy", 399, 7, "🚀"),
        new Book(23, "Bossypants", "Tina Fey", "Comedy", 420, 6, "🎭"),
        new Book(24, "The Alchemist", "Paulo Coelho", "Story & Fiction", 299, 18, "🧭"),
        new Book(25, "The Little Prince", "Antoine de Saint", "Story & Fiction", 280, 11, "⭐"),
        new Book(26, "Malgudi Days", "R.K. Narayan", "Story & Fiction", 320, 9, "🌴"),
        new Book(27, "Wings of Fire", "A.P.J. Abdul Kalam", "Story & Fiction", 300, 20, "🕊️"),
        new Book(28, "The Blue Umbrella", "Ruskin Bond", "Story & Fiction", 220, 13, "☂️"),
        new Book(29, "The Silent Patient", "Alex Michaelides", "Mystery & Thriller", 450, 10, "🔍"),
        new Book(30, "Gone Girl", "Gillian Flynn", "Mystery & Thriller", 399, 8, "🕵️"),
        new Book(31, "And Then There Were None", "Agatha Christie", "Mystery & Thriller", 350, 12, "🔦"),
        new Book(32, "The Girl on the Train", "Paula Hawkins", "Mystery & Thriller", 380, 6, "🚂"),
        new Book(33, "Atomic Habits", "James Clear", "Self Help", 450, 15, "🌱"),
        new Book(34, "The Power of Now", "Eckhart Tolle", "Self Help", 390, 8, "🧘"),
        new Book(35, "Ikigai", "Hector Garcia", "Self Help", 350, 12, "🌸"),
        new Book(36, "Think Like a Monk", "Jay Shetty", "Self Help", 400, 10, "🧠"),
        new Book(37, "Java: The Complete Reference", "Herbert Schildt", "Science & Technology", 650, 10, "💻"),
        new Book(38, "Python Crash Course", "Eric Matthes", "Science & Technology", 700, 8, "🐍"),
        new Book(39, "Clean Code", "Robert C. Martin", "Science & Technology", 800, 5, "⌨️"),
        new Book(40, "Deep Learning", "Ian Goodfellow", "Science & Technology", 900, 6, "🤖")
    );

    public List<Book> getBooks() {
        return INITIAL_BOOKS;
    }

    public Book findBook(int id) {
        return INITIAL_BOOKS.stream()
                .filter(book -> book.id == id)
                .findFirst()
                .orElse(null);
    }
}
