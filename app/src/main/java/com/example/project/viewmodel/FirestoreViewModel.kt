package com.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project.data.FirestoreRepository
import com.example.project.model.Loan
import com.example.project.ui.state.LoanUiState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FirestoreViewModel(
    private val repo: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    private val _loans = MutableStateFlow<List<Loan>>(emptyList())
    val loans: StateFlow<List<Loan>> = _loans

    private val firestore = Firebase.firestore

    private val _users = MutableStateFlow<List<String>>(emptyList())
    val users: StateFlow<List<String>> = _users

    init {
        fetchLoans()
        fetchAllUsers()
    }

    fun fetchLoans() {
        viewModelScope.launch {
            _loans.value = repo.getAllLoans()
        }
    }

    fun addLoan(bookId: Long, branchId: Long, user: String, returnDate: String) {
        val loan = Loan(
            loanId = UUID.randomUUID().toString(),
            bookId = bookId,
            branchId = branchId,
            user = user,
            dateLoaned = currentDate(),
            dateReturn = returnDate
        )

        viewModelScope.launch {
            repo.addLoan(loan)
            fetchLoans()
        }
    }

    fun deleteLoan(id: String) {
        viewModelScope.launch {
            repo.deleteLoan(id)
            fetchLoans()
        }
    }

    private fun currentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }

    private val _loanUiState = MutableStateFlow(LoanUiState())
    val loanUiState: StateFlow<LoanUiState> = _loanUiState

    fun updateSelectedBook(bookId: Long) {
        _loanUiState.value = _loanUiState.value.copy(selectedBookId = bookId, selectedBranchId = null)
    }

    fun updateSelectedBranch(branchId: Long) {
        _loanUiState.value = _loanUiState.value.copy(selectedBranchId = branchId)
    }

    fun updateReturnDate(date: String) {
        _loanUiState.value = _loanUiState.value.copy(returnDate = date)
    }

    fun resetLoanUiState() {
        _loanUiState.value = LoanUiState()
    }

    fun fetchAllUsers() {
        firestore.collection("users").get()
            .addOnSuccessListener { result ->
                val emails = result.documents.mapNotNull { doc ->
                    doc.getString("email")
                }
                _users.value = emails
            }
    }

    fun deleteUser(email: String) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result.documents) {
                    firestore.collection("users").document(doc.id).delete()
                }
                fetchAllUsers()
            }
    }
}
