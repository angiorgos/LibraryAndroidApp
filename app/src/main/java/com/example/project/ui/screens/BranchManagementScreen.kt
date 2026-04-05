package com.example.project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project.data.DatabaseProvider
import com.example.project.viewmodel.BookViewModel
import com.example.project.viewmodel.BookViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchManagementScreen(
    navController: NavController,
    bookViewModel: BookViewModel = viewModel(
        factory = BookViewModelFactory(DatabaseProvider.provideDatabase(LocalContext.current).bookDao())
    )
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var branchName by rememberSaveable { mutableStateOf("") }
    var branchLocation by rememberSaveable { mutableStateOf("") }
    var selectedBranchIdForDeletion by rememberSaveable { mutableStateOf<Long?>(null) }

    val branches by bookViewModel.branches.collectAsState()

    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }
    var showSuccessDialog by rememberSaveable { mutableStateOf(false) }
    var successMessage by rememberSaveable { mutableStateOf("") }

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
                            popUpTo("branchManagement") { inclusive = true }
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
                    title = { Text("Διαχείριση Υποκαταστημάτων") },
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
                Text("➤ Εισαγωγή Νέου Υποκαταστήματος", style = MaterialTheme.typography.titleMedium)

                OutlinedTextField(
                    value = branchName,
                    onValueChange = { branchName = it },
                    label = { Text("Όνομα Υποκαταστήματος") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = branchLocation,
                    onValueChange = { branchLocation = it },
                    label = { Text("Τοποθεσία") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (branchName.isNotBlank() && branchLocation.isNotBlank()) {
                            bookViewModel.insertBranch(branchName, branchLocation)
                            branchName = ""
                            branchLocation = ""
                            successMessage = "Το υποκατάστημα καταχωρήθηκε επιτυχώς!"
                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Καταχώρηση Υποκαταστήματος")
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("➤ Διαγραφή Υποκαταστήματος", style = MaterialTheme.typography.titleMedium)

                DropdownMenuBranches(
                    branches = branches,
                    selectedBranchId = selectedBranchIdForDeletion,
                    onBranchSelected = { selectedBranchIdForDeletion = it }
                )

                Button(
                    onClick = {
                        if (selectedBranchIdForDeletion != null) {
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
                        text = "Διαγραφή Υποκαταστήματος",
                        maxLines = 1,
                        softWrap = false
                    )
                }

                if (showDeleteConfirmation) {
                    AlertDialog(
                        onDismissRequest = { showDeleteConfirmation = false },
                        title = { Text("Επιβεβαίωση Διαγραφής") },
                        text = { Text("Είστε σίγουροι ότι θέλετε να διαγράψετε το υποκατάστημα;") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDeleteConfirmation = false
                                    bookViewModel.deleteBranchById(selectedBranchIdForDeletion!!)
                                    selectedBranchIdForDeletion = null
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
