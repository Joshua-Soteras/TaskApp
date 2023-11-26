package com.example.quests.ui.util

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quests.R
import com.example.quests.ui.backup.BackupDestination
import com.example.quests.ui.home.HomeDestination
import com.example.quests.ui.navigation.QuestsNavigationActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppModalDrawer(
    drawerState: DrawerState,
    currentRoute: String,
    navigationActions: QuestsNavigationActions,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppModalDrawerSheet(
                currentRoute = currentRoute,
                navigateToHome = { navigationActions.navigateToHome() },
                navigateToBackup = { navigationActions.navigateToBackup() },
                closeDrawer = { coroutineScope.launch { drawerState.close() } }
            )
        }
    ) {
        content()
    }
}

@Composable
fun AppModalDrawerSheet(
    currentRoute: String,
    navigateToHome:() -> Unit,
    navigateToBackup: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet {
        Text(stringResource(R.string.app_name), modifier = Modifier.padding(16.dp))
        NavigationDrawerItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.lists_24px),
                    contentDescription = null
                )
            },
            label = { Text(stringResource(R.string.my_tasks)) },
            selected = currentRoute == HomeDestination.route,
            onClick = {
                navigateToHome()
                closeDrawer()
            }
        )
        NavigationDrawerItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.cloud_sync_24px),
                    contentDescription = null
                )
            },
            label = { Text(stringResource(R.string.backup)) },
            selected = currentRoute == BackupDestination.route,
            onClick = {
                navigateToBackup()
                closeDrawer()
            }
        )
    }
}

@Preview
@Composable
fun PreviewAppModalDrawerSheet() {
    Surface {
        AppModalDrawerSheet(
            currentRoute = HomeDestination.route,
            navigateToHome = { },
            navigateToBackup = { },
            closeDrawer = { }
        )
    }
}