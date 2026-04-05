package com.example.project.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.project.data.DatabaseProvider
import com.example.project.data.FirestoreRepository
import com.example.project.viewmodel.BookViewModel
import com.example.project.viewmodel.BookViewModelFactory
import com.example.project.viewmodel.FirestoreViewModel
import com.example.project.viewmodel.FirestoreViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    navController: NavController,
    bookViewModel: BookViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = BookViewModelFactory(
            DatabaseProvider.provideDatabase(LocalContext.current).bookDao()
        )
    ),
    firestoreViewModel: FirestoreViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = FirestoreViewModelFactory(FirestoreRepository())
    )
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val books by bookViewModel.books.collectAsState()
    val branches by bookViewModel.branches.collectAsState()
    val loans by firestoreViewModel.loans.collectAsState()
    val scrollState = rememberScrollState()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Πίνακας Διαχείρισης",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )

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
                    label = { Text("Διαχείριση Βιβλίων") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("bookManagement")
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Διαχείριση Καταστημάτων") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("branchManagement")
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                NavigationDrawerItem(
                    label = { Text("Διαχείριση Δανεισμών") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("loanManagement")
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Πίνακας Διαχείρισης") },
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
                    .verticalScroll(scrollState)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Στατιστικά", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Συνολικά Βιβλία: ${books.size}")
                        Text("Υποκαταστήματα: ${branches.size}")
                        Text("Συνολικοί Δανεισμοί: ${loans.size}")
                    }
                }


                Text("Επιλέξτε τι θέλετε να διαχειριστείτε:", style = MaterialTheme.typography.titleMedium)

                Button(
                    onClick = { navController.navigate("bookManagement") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Διαχείριση Βιβλίων")
                }

                Button(
                    onClick = { navController.navigate("branchManagement") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Διαχείριση Καταστημάτων")
                }

                Button(
                    onClick = { navController.navigate("loanManagement") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Διαχείριση Δανεισμών")
                }
            }
        }
    }
}
