package com.example.quests.ui.backup

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.example.quests.R
import com.example.quests.ui.navigation.NavigationDestination
import com.example.quests.ui.util.BackupAppBar

object BackupDestination : NavigationDestination {
    override val route = "backup"
    override val titleRes = R.string.backup
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun BackupScreen(
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
            .semantics {
                testTagsAsResourceId = true
            },
        topBar = {
            BackupAppBar(
                openDrawer = openDrawer,
            )
        }

    ) {
        Text("test", modifier.padding(it))
    }
}