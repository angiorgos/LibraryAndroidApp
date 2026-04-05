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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email

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
                    label = { Text("Δανεισμοί Βιβλίων") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("loanScreen")
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

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

                NavigationDrawerItem(
                    label = { Text("Αποσύνδεση") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("login") {
                            popUpTo("about") { inclusive = true }
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
                    title = { Text("Σχετικά με την Εφαρμογή", fontSize = 20.sp) },
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
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = "Περιγραφή",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text =  "Η εφαρμογή αυτή δημιουργήθηκε στο πλαίσιο του μαθήματος Προηγμένα Θέματα Αλληλεπίδρασης και προσομοιώνει τη λειτουργία μιας δανειστικής βιβλιοθήκης.\n\n" +
                            "Έχει δημιουργηθεί με Kotlin, Jetpack Compose, Room και Firebase Firestore.\n\n" +
                            "Οι χρήστες μπορούν να δανείζονται βιβλία, ενώ οι διαχειριστές έχουν πρόσβαση σε λειτουργίες διαχείρισης μέσω του Πίνακα Διαχείρισης.\nΜπορείτε να δανειστείτε βιβλία πατώντας το κουμπί Δανεισμοί Βιβλίων από το μενού.\n\n"+
                            "Δημιουργός: Αναγνώστου Γεώργιος 2022007\nΈκδοση: 1.0",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
