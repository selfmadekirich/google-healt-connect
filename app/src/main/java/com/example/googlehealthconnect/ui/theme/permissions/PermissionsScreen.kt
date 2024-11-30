package com.example.googlehealthconnect.ui.theme.permissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import com.example.googlehealthconnect.GoogleHealthConnectTopAppBar
import com.example.googlehealthconnect.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState

// Create a set of permissions for required data types
val PERMISSIONS =
    setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),

    )


@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun getPermissionStates(states: MutableList<PermissionState>): Int {
    var permises = 0
    PERMISSIONS.forEach {
        val state = rememberPermissionState(it)
        states.add(state)
        if (state.hasPermission) {
            permises++
        }
    }
    return permises
}


object PermissionsDestination {
     val route = "permissions"
     val titleRes = R.string.get_permissions
}
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun PermissionsScreen(
    modifier: Modifier = Modifier,
    states: MutableList<PermissionState>,
    permisesState: MutableIntState
) {
    var permises by permisesState
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GoogleHealthConnectTopAppBar(
            title = stringResource(PermissionsDestination.titleRes),
            canNavigateBack = false,
        )
        for (state in states){
            PermissionRequired(
                permissionState = state,
                permissionNotGrantedContent = {
                    Text("")
                    Button(onClick = {
                        state.launchPermissionRequest()
                    },
                        modifier = modifier
                    ){
                        Text("Allow " + state.permission.split('.')[3].replace('_', ' ').lowercase() + "?", fontSize = 19.sp)
                    }
                },
                permissionNotAvailableContent = {
                    Text("Allow in GoogleHealth settings " + state.permission.split('.').get(3), fontSize = 19.sp)
                },
                content = {
                    permises++
                }
            )
        }
    }
}