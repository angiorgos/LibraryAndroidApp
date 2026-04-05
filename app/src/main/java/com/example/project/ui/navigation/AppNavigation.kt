package com.example.project.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.project.data.DatabaseProvider
import com.example.project.data.FirestoreRepository
import com.example.project.ui.screens.AboutScreen
import com.example.project.ui.screens.AdminPanelScreen
import com.example.project.ui.screens.BookManagementScreen
import com.example.project.ui.screens.BranchManagementScreen
import com.example.project.ui.screens.HomeScreen
import com.example.project.ui.screens.LoanManagementScreen
import com.example.project.ui.screens.LoanScreen
import com.example.project.ui.screens.LoginScreen
import com.example.project.ui.screens.RegisterScreen
import com.example.project.viewmodel.BookViewModel
import com.example.project.viewmodel.BookViewModelFactory
import com.example.project.viewmodel.FirestoreViewModel
import com.example.project.viewmodel.FirestoreViewModelFactory

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController) {
    val context = LocalContext.current

    val firestoreViewModel: FirestoreViewModel = viewModel(
        factory = FirestoreViewModelFactory(FirestoreRepository())
    )


    val bookViewModel: BookViewModel =
        viewModel(factory = BookViewModelFactory(DatabaseProvider.provideDatabase(context).bookDao()))

    NavHost(navController = navController, startDestination = "home") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") {
            HomeScreen(
                navController = navController
            )
        }
        composable("loanScreen") {
            LoanScreen(
                navController = navController,
                firestoreViewModel = firestoreViewModel,
                bookViewModel = bookViewModel
            )
        }

        composable("adminPanel") {
            AdminPanelScreen(navController)
        }
        composable("bookManagement") {
            BookManagementScreen(navController)
        }
        composable("branchManagement") {
            BranchManagementScreen(navController)
        }
        composable("loanManagement") {
            LoanManagementScreen(
                navController = navController,
                firestoreViewModel = firestoreViewModel,
                bookViewModel = bookViewModel
            )
        }
        composable("about") {
            AboutScreen(navController)
        }
    }
}
