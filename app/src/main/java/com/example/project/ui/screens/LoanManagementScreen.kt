package com.example.project.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project.data.DatabaseProvider
import com.example.project.data.FirestoreRepository
import com.example.project.model.Book
import com.example.project.model.Branch
import com.example.project.viewmodel.BookViewModel
import com.example.project.viewmodel.BookViewModelFactory
import com.example.project.viewmodel.FirestoreViewModel
import com.example.project.viewmodel.FirestoreViewModelFactory
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoanManagementScreen(
    navController: NavController,
    firestoreViewModel: FirestoreViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = FirestoreViewModelFactory(FirestoreRepository())
    ),
    bookViewModel: BookViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = BookViewModelFactory(DatabaseProvider.provideDatabase(LocalContext.current).bookDao())
    )
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val loans by firestoreViewModel.loans.collectAsState()
    val books by bookViewModel.books.collectAsState()

    var userEmail by rememberSaveable { mutableStateOf("") }
    var selectedBookId by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedBranchId by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedReturnDate by rememberSaveable { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    var loanIdToDelete by rememberSaveable { mutableStateOf<String?>(null) }
    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }

    var availableBranches by remember { mutableStateOf<List<Branch>>(emptyList()) }

    LaunchedEffect(selectedBookId) {
        selectedBookId?.let { bookId ->
            availableBranches = bookViewModel.getBranchesForBook(bookId)
        } ?: run {
            availableBranches = emptyList()
        }
    }

    fun getBookTitleById(bookId: Long): String {
        return books.find { it.id == bookId }?.title ?: "Άγνωστο Βιβλίο"
    }

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
                            popUpTo("loanManagement") { inclusive = true }
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
                    title = { Text("Διαχείριση Δανεισμών") },
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
                Text("➤ Καταχώρηση Νέου Δανεισμού", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = userEmail,
                    onValueChange = { userEmail = it },
                    label = { Text("Email Χρήστη") },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenuBooks(
                    books = books,
                    selectedBookId = selectedBookId,
                    onBookSelected = {
                        selectedBookId = it
                        selectedBranchId = null
                    }
                )

                DropdownMenuBranches(
                    branches = availableBranches,
                    selectedBranchId = selectedBranchId,
                    onBranchSelected = { selectedBranchId = it }
                )

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (selectedReturnDate.isNotBlank())
                            "Ημερομηνία Επιστροφής: $selectedReturnDate"
                        else
                            "Επιλέξτε Ημερομηνία Επιστροφής",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = {
                        if (userEmail.isNotBlank() && selectedBookId != null && selectedBranchId != null && selectedReturnDate.isNotBlank()) {
                            firestoreViewModel.addLoan(
                                user = userEmail,
                                bookId = selectedBookId!!,
                                branchId = selectedBranchId!!,
                                returnDate = selectedReturnDate
                            )
                            userEmail = ""
                            selectedBookId = null
                            selectedBranchId = null
                            selectedReturnDate = ""
                            availableBranches = emptyList()

                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Καταχώρηση Δανεισμού")
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("➤ Λίστα Δανεισμών", style = MaterialTheme.typography.titleMedium)

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp) // Προστασία για πολύ μικρές οθόνες
                ) {
                    items(loans) { loan ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "Όνομα: ${loan.user}")
                                    Text(text = "Βιβλίο: ${getBookTitleById(loan.bookId)}")
                                    Text(text = "Ημερομηνία Δανεισμού: ${loan.dateLoaned}")
                                    Text(text = "Ημερομηνία Επιστροφής: ${loan.dateReturn}")
                                }
                                IconButton(
                                    onClick = {
                                        loanIdToDelete = loan.loanId
                                        showDeleteConfirmation = true
                                    }
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Διαγραφή")
                                }
                            }
                        }
                    }
                }

                if (showDeleteConfirmation) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirmation = false },
                        title = { Text("Επιβεβαίωση Διαγραφής") },
                        text = { Text("Είστε σίγουροι ότι θέλετε να διαγράψετε αυτόν τον δανεισμό;") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    loanIdToDelete?.let { firestoreViewModel.deleteLoan(it) }
                                    loanIdToDelete = null
                                    showDeleteConfirmation = false
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
                        title = { Text("Επιτυχία") },
                        text = { Text("Ο δανεισμός καταχωρήθηκε επιτυχώς!") },
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

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val selectedDate = Instant.ofEpochMilli(millis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                    selectedReturnDate = selectedDate.toString()
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showDatePicker = false }
                        ) {
                            Text("Άκυρο")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}
