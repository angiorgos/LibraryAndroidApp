package com.example.project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    registerViewModel: RegisterViewModel = viewModel()
) {
    val email by registerViewModel.email
    val password by registerViewModel.password
    val confirmPassword by registerViewModel.confirmPassword
    val errorMessage by registerViewModel.errorMessage

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 📘 Εικονίδιο
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Εικονίδιο Βιβλιοθήκης",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Βιβλιοθήκη",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Κάντε εγγραφή για να δανειστείτε βιβλία",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = "Εγγραφή",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { registerViewModel.email.value = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { registerViewModel.password.value = it },
                    label = { Text("Κωδικός") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { registerViewModel.confirmPassword.value = it },
                    label = { Text("Επιβεβαίωση Κωδικού") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        registerViewModel.register {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Εγγραφή")
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Έχεις ήδη λογαριασμό; Σύνδεση")
                }

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
