package com.example.project.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val confirmPassword = mutableStateOf("")
    val errorMessage = mutableStateOf<String?>(null)

    fun register(onSuccess: () -> Unit) {
        val emailValue = email.value.trim()
        val passwordValue = password.value.trim()
        val confirmPasswordValue = confirmPassword.value.trim()

        if (emailValue.isEmpty() || passwordValue.isEmpty() || confirmPasswordValue.isEmpty()) {
            errorMessage.value = "Συμπλήρωσε όλα τα πεδία."
            return
        }

        if (passwordValue != confirmPasswordValue) {
            errorMessage.value = "Οι κωδικοί δεν ταιριάζουν."
            return
        }

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(emailValue, passwordValue)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    errorMessage.value = null
                    onSuccess()
                } else {
                    val rawMessage = task.exception?.message ?: ""
                    errorMessage.value = when {
                        rawMessage.contains("email address is already in use", ignoreCase = true) ->
                            "Αυτό το email χρησιμοποιείται ήδη από άλλο λογαριασμό."
                        rawMessage.contains("badly formatted", ignoreCase = true) ||
                                rawMessage.contains("formatted", ignoreCase = true) ->
                            "Το email δεν είναι σωστά διαμορφωμένο."
                        rawMessage.contains("password should be at least", ignoreCase = true) ->
                            "Ο κωδικός πρέπει να έχει τουλάχιστον 6 χαρακτήρες."
                        rawMessage.contains("blocked all requests", ignoreCase = true) ->
                            "Έχουν μπλοκαριστεί προσωρινά οι αιτήσεις. Δοκίμασε αργότερα."
                        else -> "Σφάλμα κατά την εγγραφή: $rawMessage"
                    }
                }
            }
    }
}
