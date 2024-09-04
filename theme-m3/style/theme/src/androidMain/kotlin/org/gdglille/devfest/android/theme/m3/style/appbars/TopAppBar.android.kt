package org.gdglille.devfest.android.theme.m3.style.appbars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paligot.confily.resources.Resource
import com.paligot.confily.resources.action_qrcode_scanner
import kotlinx.collections.immutable.persistentListOf
import org.gdglille.devfest.android.theme.m3.style.Conferences4HallTheme
import org.gdglille.devfest.android.theme.m3.style.actions.TopAction
import org.gdglille.devfest.android.theme.m3.style.actions.TopActionsUi
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Preview
@Composable
private fun TopAppBarPreview() {
    Conferences4HallTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            TopAppBar(
                title = "Speakers"
            )
            TopAppBar(
                title = "Speakers",
                navigationIcon = { Back { } }
            )
            TopAppBar(
                title = "QrCode Scanner",
                navigationIcon = { Back { } },
                topActionsUi = TopActionsUi(
                    actions = persistentListOf(
                        TopAction(
                            id = 0,
                            icon = Icons.Outlined.QrCodeScanner,
                            contentDescription = Resource.string.action_qrcode_scanner
                        )
                    )
                )
            )
        }
    }
}
