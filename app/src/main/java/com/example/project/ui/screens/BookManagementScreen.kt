package com.example.project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project.data.DatabaseProvider
import com.example.project.model.Book
import com.example.project.viewmodel.BookViewModel
import com.example.project.viewmodel.BookViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookManagementScreen(
    navController: NavController,
    bookViewModel: BookViewModel = viewModel(
        factory = BookViewModelFactory(DatabaseProvider.provideDatabase(LocalContext.current).bookDao())
    )
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var title by rememberSaveable { mutableStateOf("") }
    var author by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var selectedBookIdForLinking by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedBranchId by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedBookIdForDeletion by rememberSaveable { mutableStateOf<Long?>(null) }

    val books by bookViewModel.books.collectAsState()
    val branches by bookViewModel.branches.collectAsState()

    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var successMessage by rememberSaveable { mutableStateOf("") }
    var dialogTitle by rememberSaveable { mutableStateOf("") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text("Admin Panel", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)

                NavigationDrawerItem(
                    label = { Text("Αρχική") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("home")
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Πίσω στο Admin Panel") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("adminPanel") {
                            popUpTo("bookManagement") { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Διαχείριση Βιβλίων") },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Μενού")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("➤ Εισαγωγή Νέου Βιβλίου", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Τίτλος Βιβλίου") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Συγγραφέας") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Έτος Έκδοσης") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (title.isNotBlank() && author.isNotBlank() && year.isNotBlank()) {
                            bookViewModel.insertBook(Book(title = title, author = author, year = year.toInt()))
                            title = ""
                            author = ""
                            year = ""
                            dialogTitle = "Επιτυχία"
                            successMessage = "Το βιβλίο καταχωρήθηκε επιτυχώς!"
                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Καταχώρηση Βιβλίου")
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("➤ Αντιστοίχιση Βιβλίου με Υποκατάστημα", style = MaterialTheme.typography.titleMedium)

                DropdownMenuBooks(
                    books = books,
                    selectedBookId = selectedBookIdForLinking,
                    onBookSelected = { selectedBookIdForLinking = it }
                )

                DropdownMenuBranches(
                    branches = branches,
                    selectedBranchId = selectedBranchId,
                    onBranchSelected = { selectedBranchId = it }
                )

                Button(
                    onClick = {
                        if (selectedBookIdForLinking != null && selectedBranchId != null) {
                            bookViewModel.linkBookToBranch(
                                selectedBookIdForLinking!!,
                                selectedBranchId!!
                            ) { success, message ->
                                dialogTitle = if (success) "Επιτυχία" else "Προειδοποίηση"
                                successMessage = message
                                showSuccessDialog = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Αντιστοίχιση Βιβλίου")
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("➤ Διαγραφή Βιβλίου", style = MaterialTheme.typography.titleMedium)

                DropdownMenuBooks(
                    books = books,
                    selectedBookId = selectedBookIdForDeletion,
                    onBookSelected = { selectedBookIdForDeletion = it }
                )

                Button(
                    onClick = {
                        if (selectedBookIdForDeletion != null) {
                            showDeleteConfirmation = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(
                        text = "Διαγραφή Βιβλίου",
                        maxLines = 1,
                        softWrap = false
                    )
                }

                if (showDeleteConfirmation) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirmation = false },
                        title = { Text("Επιβεβαίωση Διαγραφής") },
                        text = { Text("Είστε σίγουροι ότι θέλετε να διαγράψετε το βιβλίο;") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDeleteConfirmation = false
                                    bookViewModel.deleteBookById(selectedBookIdForDeletion!!)
                                    selectedBookIdForDeletion = null
                                }
                            ) {
                                Text("Ναι")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDeleteConfirmation = false }
                            ) {
                                Text("Άκυρο")
                            }
                        }
                    )
                }

                if (showSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = { showSuccessDialog = false },
                        title = { Text(dialogTitle) },
                        text = { Text(successMessage) },
                        confirmButton = {
                            TextButton(
                                onClick = { showSuccessDialog = false }
                            ) {
                                Text("ΟΚ")
                            }
                        }
                    )
                }
            }
        }
    }
}
