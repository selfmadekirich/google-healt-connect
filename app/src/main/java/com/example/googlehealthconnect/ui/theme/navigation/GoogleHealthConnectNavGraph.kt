package com.example.googlehealthconnect.ui.theme.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.googlehealthconnect.repository.GoogleHealthRepository
import com.example.googlehealthconnect.ui.theme.home.HomeDestination
import com.example.googlehealthconnect.ui.theme.home.HomeScreen
import com.example.googlehealthconnect.ui.theme.permissions.PERMISSIONS
import com.example.googlehealthconnect.ui.theme.permissions.PermissionsDestination
import com.example.googlehealthconnect.ui.theme.permissions.PermissionsScreen
import com.example.googlehealthconnect.ui.theme.permissions.getPermissionStates
import com.example.googlehealthconnect.ui.theme.records.RecordsAddDestination
import com.example.googlehealthconnect.ui.theme.records.RecordsAddScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun GoogleHealthConnectNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    repository: GoogleHealthRepository
) {
    var permissionStates = mutableListOf<PermissionState>()
    val countPermissions = mutableIntStateOf(0)
    var realPermissions by countPermissions
    realPermissions += getPermissionStates(permissionStates)
    NavHost(
        navController = navController,
        startDestination =
        if(realPermissions == PERMISSIONS.size)
            HomeDestination.route
        else
            PermissionsDestination.route,
        modifier = modifier
    ) {
        composable(route = PermissionsDestination.route) {
            PermissionsScreen(
                modifier = modifier,
                states = permissionStates,
                permisesState = countPermissions,
            )
        }
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToDataAdd = {
                    navController.navigate(RecordsAddDestination.route)
                },
                repository = repository
            )
        }
        composable(route = RecordsAddDestination.route) {
            RecordsAddScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                repository = repository
            )
        }
    }
}