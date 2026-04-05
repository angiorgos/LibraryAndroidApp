package com.example.project.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val errorMessage = mutableStateOf<String?>(null)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email.value, password.value).await()
                Log.d("Login", "Successful login")
                errorMessage.value = null
                onSuccess()
            } catch (e: Exception) {
                Log.e("Login", "Login failed", e)
                errorMessage.value = "Ο λογαριασμός δεν βρέθηκε ή τα στοιχεία είναι λάθος."
            }
        }
    }

}
