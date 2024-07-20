package org.gdglille.devfest.theme.m3.speakers.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.gdglille.devfest.models.ui.SpeakerUi
import org.gdglille.devfest.theme.m3.style.events.socials.SocialsSection
import org.gdglille.devfest.theme.m3.style.markdown.MarkdownText
import org.gdglille.devfest.theme.m3.style.placeholder.placeholder
import org.gdglille.devfest.theme.m3.style.speakers.avatar.MediumSpeakerAvatar

@Composable
fun SpeakerDetailSectionVertical(
    speaker: SpeakerUi,
    onLinkClicked: (url: String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    displayAvatar: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (displayAvatar) {
            MediumSpeakerAvatar(
                url = speaker.url,
                contentDescription = null,
                modifier = Modifier.placeholder(visible = isLoading)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        SocialsSection(
            title = speaker.name,
            pronouns = speaker.pronouns,
            subtitle = speaker.activity,
            onLinkClicked = onLinkClicked,
            isLoading = isLoading,
            twitterUrl = speaker.twitterUrl,
            mastodonUrl = speaker.mastodonUrl,
            githubUrl = speaker.githubUrl,
            linkedinUrl = speaker.linkedinUrl,
            websiteUrl = speaker.websiteUrl
        )
        Spacer(modifier = Modifier.height(8.dp))
        MarkdownText(
            text = speaker.bio,
            modifier = Modifier.placeholder(visible = isLoading)
        )
    }
}
