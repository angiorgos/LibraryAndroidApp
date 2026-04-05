package com.example.project.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.project.data.DatabaseProvider
import com.example.project.model.Book
import com.example.project.viewmodel.BookViewModel
import com.example.project.viewmodel.BookViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    bookViewModel: BookViewModel = viewModel(factory = BookViewModelFactory(DatabaseProvider.provideDatabase(LocalContext.current).bookDao()))
) {

    val configuration = LocalContext.current.resources.configuration
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val books by bookViewModel.books.collectAsState()

    var searchQuery by rememberSaveable { mutableStateOf("") }
    val scrollState = rememberScrollState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Text("Μενού", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)

                NavigationDrawerItem(
                    label = { Text("Δανεισμοί Βιβλίων") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("loanScreen")
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
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                val userEmail = FirebaseAuth.getInstance().currentUser?.email
                if (userEmail == "admin@ihu.gr") {
                    NavigationDrawerItem(
                        label = { Text("Πίνακας Διαχείρισης") },
                        selected = false,
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            navController.navigate("adminPanel")
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Αρχική", fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Μενού")
                        }
                    },
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { navController.navigate("loanScreen") }
                                .padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = "Δανεισμοί Βιβλίων",
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Δανεισμοί Βιβλίων",
                                style = MaterialTheme.typography.labelLarge
                            )
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
                    .fillMaxSize()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Αναζήτηση Βιβλίου ή Συγγραφέα") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                )

                Text(
                    text = "Προτεινόμενα Βιβλία",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(books.take(5)) { book ->
                        BookCardSmall(book)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                if (isLandscape) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("💡 Tip", style = MaterialTheme.typography.titleMedium)
                            Text("Μην ξεχάσεις να επιστρέψεις τα βιβλία σου στην ώρα τους!")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }


                Text(
                    text = "Όλα τα Βιβλία",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val filteredBooks = books.filter { book ->
                    book.title.contains(searchQuery, ignoreCase = true) ||
                            book.author.contains(searchQuery, ignoreCase = true)
                }

                if (filteredBooks.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Δεν βρέθηκαν βιβλία",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Δοκιμάστε διαφορετική αναζήτηση.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    filteredBooks.forEach { book ->
                        Spacer(modifier = Modifier.height(12.dp))
                        BookCardLarge(book)
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        bookViewModel.insertDemoDataIfNeeded()
    }
}

@Composable
fun BookCardSmall(book: Book) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(220.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Εικονίδιο βιβλίου (placeholder)
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = "Εικονίδιο Βιβλίου",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = book.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Text(
                text = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}


@Composable
fun BookCardLarge(book: Book) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Συγγραφέας: ${book.author}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Έτος Έκδοσης: ${book.year}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
