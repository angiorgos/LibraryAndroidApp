package com.example.project.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project.model.Branch
import com.example.project.model.Book
import com.example.project.model.Loan
import com.example.project.viewmodel.FirestoreViewModel
import com.example.project.viewmodel.BookViewModel
import com.example.project.util.checkAndRequestNotificationPermission
import com.example.project.util.sendNotification
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanScreen(
    navController: NavController,
    firestoreViewModel: FirestoreViewModel,
    bookViewModel: BookViewModel
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val books by bookViewModel.books.collectAsState()
    val branches by bookViewModel.branches.collectAsState()
    val loans by firestoreViewModel.loans.collectAsState()
    val uiState by firestoreViewModel.loanUiState.collectAsState()

    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
    var availableBranches by remember { mutableStateOf<List<Branch>>(emptyList()) }

    LaunchedEffect(uiState.selectedBookId) {
        uiState.selectedBookId?.let { bookId ->
            availableBranches = bookViewModel.getBranchesForBook(bookId)
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text("Μενού", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
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
                    label = { Text("Σχετικά") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("about")
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Αποσύνδεση") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("login") {
                            popUpTo("loanScreen") { inclusive = true }
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
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = userEmail,
                            onValueChange = {},
                            label = { Text("Χρήστης (Email)") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        DropdownMenuBooks(
                            books = books,
                            selectedBookId = uiState.selectedBookId,
                            onBookSelected = { firestoreViewModel.updateSelectedBook(it) }
                        )

                        DropdownMenuBranches(
                            branches = availableBranches,
                            selectedBranchId = uiState.selectedBranchId,
                            onBranchSelected = { firestoreViewModel.updateSelectedBranch(it) }
                        )

                        DatePickerField(
                            label = "Ημερομηνία Επιστροφής",
                            date = uiState.returnDate,
                            onDateSelected = { firestoreViewModel.updateReturnDate(it) }
                        )

                        Button(
                            onClick = {
                                if (uiState.selectedBookId != null && uiState.selectedBranchId != null && uiState.returnDate.isNotBlank()) {
                                    firestoreViewModel.addLoan(
                                        bookId = uiState.selectedBookId!!,
                                        branchId = uiState.selectedBranchId!!,
                                        user = userEmail,
                                        returnDate = uiState.returnDate
                                    )
                                    firestoreViewModel.resetLoanUiState()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Προσθήκη Δανεισμού")
                        }

                        Text(
                            text = "Οι Δανεισμοί μου",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                items(loans.filter { it.user == userEmail }) { loan ->
                    val bookTitle = books.find { it.id == loan.bookId }?.title ?: "Άγνωστο Βιβλίο"
                    val branchName = branches.find { it.id == loan.branchId }?.name ?: "Άγνωστο Υποκατάστημα"

                    val today = Calendar.getInstance().time
                    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val returnDateParsed = runCatching { formatter.parse(loan.dateReturn) }.getOrNull()
                    val isOverdue = returnDateParsed?.before(today) == true

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Βιβλίο: $bookTitle")
                            Text("Υποκατάστημα: $branchName")
                            Text("Ημερομηνία Επιστροφής: ${loan.dateReturn}")
                            if (isOverdue) {
                                Text(
                                    "⚠️ Έχετε καθυστερήσει την επιστροφή!",
                                    color = MaterialTheme.colorScheme.error
                                )
                                LaunchedEffect(loan.loanId) {
                                    checkAndRequestNotificationPermission(context)
                                    sendNotification(context, bookTitle)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DropdownMenuBooks(
    books: List<Book>,
    selectedBookId: Long?,
    onBookSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedBookTitle = books.find { it.id == selectedBookId }?.title ?: "Επιλογή Βιβλίου"

    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedBookTitle)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            books.forEach { book ->
                DropdownMenuItem(
                    text = { Text(book.title) },
                    onClick = {
                        expanded = false
                        onBookSelected(book.id)
                    }
                )
            }
        }
    }
}

@Composable
fun DropdownMenuBranches(
    branches: List<Branch>,
    selectedBranchId: Long?,
    onBranchSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedBranchName = branches.find { it.id == selectedBranchId }?.name ?: "Επιλογή Υποκαταστήματος"

    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedBranchName)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            branches.forEach { branch ->
                DropdownMenuItem(
                    text = { Text(branch.name) },
                    onClick = {
                        expanded = false
                        onBranchSelected(branch.id)
                    }
                )
            }
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    date: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    OutlinedButton(onClick = {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$year-${month + 1}-$dayOfMonth"
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }, modifier = Modifier.fillMaxWidth()) {
        Text(if (date.isBlank()) label else date)
    }
}
