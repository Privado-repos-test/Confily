package org.gdglille.devfest.theme.m3.speakers.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.gdglille.devfest.android.theme.m3.style.Conferences4HallTheme
import org.gdglille.devfest.models.ui.SpeakerUi

@Suppress("UnusedPrivateMember")
@Preview
@Composable
private fun SpeakerDetailScreenPreview() {
    Conferences4HallTheme {
        Scaffold {
            SpeakerDetailScreen(
                speaker = SpeakerUi.fake,
                onTalkClicked = {},
                onFavoriteClicked = {},
                onLinkClicked = {},
                contentPadding = it
            )
        }
    }
}
