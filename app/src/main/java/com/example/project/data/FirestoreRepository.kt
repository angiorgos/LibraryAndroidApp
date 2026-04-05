package com.example.project.data

import com.example.project.model.Loan
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun addLoan(loan: Loan) {
        db.collection("loans")
            .document(loan.loanId)
            .set(loan)
            .await()
    }

    suspend fun deleteLoan(loanId: String) {
        db.collection("loans")
            .document(loanId)
            .delete()
            .await()
    }

    suspend fun getAllLoans(): List<Loan> {
        val snapshot = db.collection("loans").get().await()
        return snapshot.toObjects(Loan::class.java)
    }
}
