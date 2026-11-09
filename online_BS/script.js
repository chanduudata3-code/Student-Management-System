let books = [];
let wishlist = [];

const genreOrder = [
    "Love & Romance",
    "Friendship",
    "Mythology",
    "Family Drama",
    "Comedy",
    "Story & Fiction",
    "Mystery & Thriller",
    "Self Help",
    "Science & Technology"
];

const coverClasses = {
    "Love & Romance": "cover-romance",
    "Friendship": "cover-friendship",
    "Mythology": "cover-mythology",
    "Family Drama": "cover-family",
    "Comedy": "cover-comedy",
    "Story & Fiction": "cover-fiction",
    "Mystery & Thriller": "cover-mystery",
    "Self Help": "cover-selfhelp",
    "Science & Technology": "cover-technology"
};

async function apiRequest(path, options = {}) {
    let response;
    try {
        response = await fetch(path, options);
    } catch {
        throw new Error("Cannot connect to BookNest. Make sure the Java server is running.");
    }

    const result = await response.json();
    if (!response.ok) {
        throw new Error(result.message || "The request could not be completed.");
    }
    return result;
}

function setupFilters() {
    const genreFilter = document.getElementById("genreFilter");
    const languageFilter = document.getElementById("languageFilter");
    const authorFilter = document.getElementById("authorFilter");

    genreOrder.forEach(genre => {
        genreFilter.innerHTML += `<option value="${genre}">${genre}</option>`;
    });

    const languages = [...new Set(books.map(book => book.language))].sort();
    languages.forEach(language => {
        languageFilter.innerHTML += `<option value="${language}">${language}</option>`;
    });

    const authors = [...new Set(books.map(book => book.author))].sort();
    authors.forEach(author => {
        authorFilter.innerHTML += `<option value="${author}">${author}</option>`;
    });
}

function createBookCard(book, isWishlist = false) {
    const coverClass = coverClasses[book.genre];
    const alreadyAdded = wishlist.includes(book.id);

    return `
        <div class="book-card">
            <div class="book-cover ${coverClass}">
                <div class="cover-icon">${book.icon}</div>
                <div class="cover-title">${book.title}</div>
                <div class="cover-author">${book.author}</div>
            </div>
            <div class="book-info">
                <h3>${book.title}</h3>
                <p>✍️ ${book.author}</p>
                <p>🏷️ ${book.genre}</p>
                <p>🌐 ${book.language}</p>
                <p class="book-price">₹${book.price}</p>
                <p class="${book.stock > 0 ? "stock" : "out-stock"}">
                    ${book.stock > 0 ? "✓ In Stock: " + book.stock : "✕ Out of Stock"}
                </p>
                ${isWishlist
                    ? `<button class="remove-btn" onclick="removeFromWishlist(${book.id})">Remove ❤️</button>`
                    : `<button onclick="addToWishlist(${book.id})"
                        ${alreadyAdded || wishlist.length >= 10 ? "disabled" : ""}>
                        ${alreadyAdded ? "✓ Added" : "♡ Add to Wishlist"}
                       </button>`
                }
            </div>
        </div>
    `;
}

function displayBooks() {
    const list = document.getElementById("bookList");
    const search = document.getElementById("search").value.toLowerCase();
    const genre = document.getElementById("genreFilter").value;
    const language = document.getElementById("languageFilter").value;
    const author = document.getElementById("authorFilter").value;

    const filteredBooks = books.filter(book =>
        (book.title.toLowerCase().includes(search) ||
         book.author.toLowerCase().includes(search) ||
         book.genre.toLowerCase().includes(search)) &&
        (!genre || book.genre === genre) &&
        (!language || book.language === language) &&
        (!author || book.author === author)
    );

    document.getElementById("totalBooks").innerText = filteredBooks.length + " Books";
    list.innerHTML = "";

    if (filteredBooks.length === 0) {
        list.innerHTML = "<p>No books found. Try another search!</p>";
        return;
    }

    genreOrder.forEach(category => {
        const categoryBooks = filteredBooks.filter(book => book.genre === category);
        if (categoryBooks.length === 0) return;

        list.innerHTML += `
            <div class="genre-section">
                <h3 class="genre-title">${category}</h3>
                <div class="book-grid">
                    ${categoryBooks.map(book => createBookCard(book)).join("")}
                </div>
            </div>
        `;
    });
}

async function addToWishlist(id) {
    try {
        wishlist = await apiRequest(`/api/wishlist/${id}`, {method: "POST"});
        displayBooks();
        displayWishlist();
    } catch (error) {
        alert(error.message);
    }
}

async function removeFromWishlist(id) {
    try {
        wishlist = await apiRequest(`/api/wishlist/${id}`, {method: "DELETE"});
        displayBooks();
        displayWishlist();
    } catch (error) {
        alert(error.message);
    }
}

function displayWishlist() {
    const list = document.getElementById("wishlistList");
    const count = document.getElementById("wishlistCount");
    const warning = document.getElementById("warning");

    count.innerText = wishlist.length + " / 10 books";
    warning.innerText = wishlist.length > 10
        ? "⚠ Existing wishlist exceeds 10 books! All existing entries are preserved."
        : "";

    const wishlistBooks = wishlist
        .map(id => books.find(book => book.id === id))
        .filter(book => book);

    if (wishlistBooks.length === 0) {
        list.innerHTML = "<p>Your wishlist is empty. Explore books above!</p>";
        return;
    }

    list.innerHTML = wishlistBooks.map(book => createBookCard(book, true)).join("");
}

async function initializeBookstore() {
    try {
        [books, wishlist] = await Promise.all([
            apiRequest("/api/books"),
            apiRequest("/api/wishlist")
        ]);
        setupFilters();
        displayBooks();
        displayWishlist();
    } catch (error) {
        document.getElementById("bookList").innerText = error.message;
        document.getElementById("wishlistList").innerText = error.message;
    }
}

initializeBookstore();
