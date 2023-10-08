package org.gdglille.devfest.android.components.speakers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.gdglille.devfest.android.theme.m3.style.Conferences4HallTheme
import org.gdglille.devfest.android.theme.m3.style.placeholder
import org.gdglille.devfest.models.SpeakerUi

@Composable
fun SpeakerDetailSection(
    speaker: SpeakerUi,
    onLinkClicked: (url: String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        SpeakerAvatar(
            url = speaker.url,
            contentDescription = null,
            shape = CircleShape,
            modifier = Modifier.size(128.dp).placeholder(visible = isLoading)
        )
        Spacer(modifier = Modifier.height(16.dp))
        SpeakerSocialSection(
            speaker = speaker,
            isLoading = isLoading,
            onLinkClicked = onLinkClicked
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview
@Composable
fun SpeakerDetailSectionPreview() {
    Conferences4HallTheme {
        SpeakerDetailSection(
            speaker = SpeakerUi.fake,
            onLinkClicked = {}
        )
    }
}
