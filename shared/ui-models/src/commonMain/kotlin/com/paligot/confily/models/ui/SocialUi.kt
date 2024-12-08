package com.paligot.confily.models.ui

import kotlinx.collections.immutable.ImmutableList

data class SocialUi(
    val type: SocialTypeUi?,
    val url: String
)

fun ImmutableList<SocialUi>.findLinkedIn(): SocialUi? = find { it.type == SocialTypeUi.LinkedIn }
fun ImmutableList<SocialUi>.findX(): SocialUi? = find { it.type == SocialTypeUi.X }
fun ImmutableList<SocialUi>.findMastodon(): SocialUi? = find { it.type == SocialTypeUi.Mastodon }
fun ImmutableList<SocialUi>.findBluesky(): SocialUi? = find { it.type == SocialTypeUi.Bluesky }
fun ImmutableList<SocialUi>.findFacebook(): SocialUi? = find { it.type == SocialTypeUi.Facebook }
fun ImmutableList<SocialUi>.findInstagram(): SocialUi? = find { it.type == SocialTypeUi.Instagram }
fun ImmutableList<SocialUi>.findYouTube(): SocialUi? = find { it.type == SocialTypeUi.YouTube }
fun ImmutableList<SocialUi>.findGitHub(): SocialUi? = find { it.type == SocialTypeUi.GitHub }
fun ImmutableList<SocialUi>.findEmail(): SocialUi? = find { it.type == SocialTypeUi.Email }
fun ImmutableList<SocialUi>.findWebsite(): SocialUi? = find { it.type == SocialTypeUi.Website }

enum class SocialTypeUi {
    LinkedIn,
    X,
    Mastodon,
    Bluesky,
    Facebook,
    Instagram,
    YouTube,
    GitHub,
    Email,
    Website
}
