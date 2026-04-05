package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.BookDao
import com.example.project.model.Book
import com.example.project.model.BookBranch
import com.example.project.model.Branch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel(private val dao: BookDao) : ViewModel() {

    private val _books = MutableStateFlow<List<Book>>(emptyList())
    val books: StateFlow<List<Book>> = _books

    private val _branches = MutableStateFlow<List<Branch>>(emptyList())
    val branches: StateFlow<List<Branch>> = _branches

    private val _branchBooks = MutableStateFlow<List<Book>>(emptyList())
    val branchBooks: StateFlow<List<Book>> = _branchBooks

    init {
        viewModelScope.launch {
            loadBooks()
            loadBranches()
        }

    }

    private suspend fun loadBooks() {
        _books.value = dao.getAllBooks()
    }

    suspend fun getBookById(id: Long): Book? {
        return dao.getAllBooks().find { it.id == id }
    }

    private suspend fun loadBranches() {
        _branches.value = dao.getAllBranches()
    }

    suspend fun getBranchesForBook(bookId: Long): List<Branch> {
        val allBranches = dao.getAllBranches()
        return allBranches.filter { branch ->
            val booksForBranch = dao.getBooksForBranch(branch.id)
            booksForBranch.books.any { it.id == bookId }
        }
    }

    fun insertBook(book: Book) {
        viewModelScope.launch {
            dao.insertBook(book)
            loadBooks()
        }
    }
    fun deleteBookById(bookId: Long) {
        viewModelScope.launch {
            dao.deleteBookById(bookId)
            loadBooks()
        }
    }

    fun linkBookToBranch(
        bookId: Long,
        branchId: Long,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val exists = dao.countBookBranchCrossRef(bookId, branchId) > 0
            if (exists) {
                onResult(false, "Το βιβλίο έχει ήδη αντιστοιχηθεί με το υποκατάστημα.")
            } else {
                dao.insertBookBranchCrossRef(BookBranch(bookId, branchId))
                loadBranches()
                onResult(true, "Το βιβλίο αντιστοιχήθηκε επιτυχώς με το υποκατάστημα!")
            }
        }
    }



    fun insertDemoDataIfNeeded() {
        viewModelScope.launch {
            val branches = dao.getAllBranches()
            val books = dao.getAllBooks()

            if (branches.isEmpty() && books.isEmpty()) {
                // ➤ Δημιουργία υποκαταστημάτων
                val branch1 = Branch(name = "Κεντρικό", location = "Αθήνα")
                val branch2 = Branch(name = "Υποκατάστημα Β", location = "Θεσσαλονίκη")

                val branch1Id = dao.insertBranchReturningId(branch1)
                val branch2Id = dao.insertBranchReturningId(branch2)

                // ➤ Δημιουργία πολλών βιβλίων
                val bookList = listOf(
                    Book(title = "Προγραμματισμός σε Kotlin", author = "Νίκος Παππάς", year = 2022),
                    Book(title = "Βάσεις Δεδομένων", author = "Μαρία Καραγιάννη", year = 2021),
                    Book(title = "Δομές Δεδομένων και Αλγόριθμοι", author = "Γιώργος Παπαδόπουλος", year = 2020),
                    Book(title = "Mobile Development", author = "Αλέξανδρος Σταυρόπουλος", year = 2023),
                    Book(title = "Αντικειμενοστραφής Προγραμματισμός", author = "Ελένη Ζαχαράκη", year = 2019),
                    Book(title = "Δίκτυα Υπολογιστών", author = "Πέτρος Λαμπρόπουλος", year = 2018),
                    Book(title = "Συστήματα Διαχείρισης Βάσεων Δεδομένων", author = "Σωτήρης Μιχαλόπουλος", year = 2021),
                    Book(title = "Android Programming", author = "Χρήστος Κωνσταντίνου", year = 2023),
                    Book(title = "Machine Learning", author = "Ανδρέας Φιλίππου", year = 2022),
                    Book(title = "Εισαγωγή στην Τεχνητή Νοημοσύνη", author = "Κατερίνα Δημητρίου", year = 2020),
                    Book(title = "Cybersecurity Fundamentals", author = "Δημήτρης Παπαδόπουλος", year = 2021),
                    Book(title = "Cloud Computing Basics", author = "Μιχάλης Ιωάννου", year = 2023)
                )
                bookList.forEach { dao.insertBook(it) }

                // ➤ Απόκτηση των ID των βιβλίων
                val allBooks = dao.getAllBooks()

                // ➤ Τυχαία αντιστοίχιση βιβλίων με υποκαταστήματα
                allBooks.forEachIndexed { index, book ->
                    val branchId = if (index % 2 == 0) branch1Id else branch2Id
                    dao.insertBookBranchCrossRef(BookBranch(book.id, branchId))
                }
            }
        }
    }

    fun insertBranch(name: String, location: String) {
        viewModelScope.launch {
            dao.insertBranch(Branch(name = name, location = location))
            loadBranches()
        }
    }

    fun deleteBranchById(branchId: Long) {
        viewModelScope.launch {
            dao.deleteBranchById(branchId)
            loadBranches()
        }
    }


}
