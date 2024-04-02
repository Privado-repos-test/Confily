package org.gdglille.devfest.backend.third.parties.openplanner

import org.gdglille.devfest.backend.categories.CategoryDb
import org.gdglille.devfest.backend.formats.FormatDb
import org.gdglille.devfest.backend.schedulers.ScheduleDb
import org.gdglille.devfest.backend.speakers.SpeakerDb
import org.gdglille.devfest.backend.talks.TalkDb

fun CategoryOP.convertToDb() = CategoryDb(
    id = id,
    name = name,
    color = "",
    icon = ""
)

fun CategoryDb.mergeWith(category: CategoryOP) = CategoryDb(
    id = category.id,
    name = if (this.name == category.name) this.name else category.name,
    color = if (this.color != "") this.color else category.color,
    icon = if (this.icon != "") this.icon else ""
)

fun FormatOP.convertToDb() = FormatDb(
    id = id,
    name = name,
    time = durationMinutes
)

fun FormatDb.mergeWith(formatOP: FormatOP) = FormatDb(
    id = formatOP.id,
    name = if (this.name == formatOP.name) this.name else formatOP.name,
    time = if (this.time != 0) this.time else formatOP.durationMinutes
)

fun SpeakerOP.convertToDb(): SpeakerDb {
    val twitter = socials.find { it.name == "Twitter" }?.link
    val github = socials.find { it.name == "GitHub" }?.link
    return SpeakerDb(
        id = id,
        displayName = name,
        pronouns = null,
        bio = bio ?: "",
        email = email,
        jobTitle = jobTitle,
        company = company,
        photoUrl = photoUrl ?: "",
        website = null,
        twitter = if (twitter?.contains("twitter.com") == true) {
            twitter
        } else if (twitter != null) {
            "https://twitter.com/$twitter"
        } else {
            null
        },
        mastodon = null,
        github = if (github?.contains("github.com") == true) {
            github
        } else if (github != null) {
            "https://github.com/$github"
        } else {
            null
        },
        linkedin = null
    )
}

fun SpeakerDb.mergeWith(speakerOP: SpeakerOP): SpeakerDb {
    val twitter = speakerOP.socials.find { it.name == "Twitter" }?.link
    val github = speakerOP.socials.find { it.name == "GitHub" }?.link
    return SpeakerDb(
        id = speakerOP.id,
        displayName = if (this.displayName == speakerOP.name) this.displayName else speakerOP.name,
        pronouns = null,
        bio = if (this.bio == speakerOP.bio) this.bio else speakerOP.bio ?: "",
        email = if (this.email == speakerOP.email) this.email else speakerOP.email,
        jobTitle = if (this.jobTitle == speakerOP.jobTitle) this.jobTitle else speakerOP.jobTitle,
        company = if (this.company == speakerOP.company) this.company else speakerOP.company,
        photoUrl = if (this.photoUrl == speakerOP.photoUrl) this.photoUrl else speakerOP.photoUrl ?: "",
        website = null,
        twitter = if (this.twitter == twitter) {
            this.twitter
        } else if (twitter?.contains("twitter.com") == true) {
            twitter
        } else if (twitter != null) {
            "https://twitter.com/$twitter"
        } else {
            null
        },
        github = if (this.github == github) {
            this.github
        } else if (github?.contains("github.com") == true) {
            github
        } else if (github != null) {
            "https://github.com/$github"
        } else {
            null
        },
        linkedin = null
    )
}

fun SessionOP.convertToTalkDb() = TalkDb(
    id = id,
    title = title,
    level = level,
    abstract = abstract,
    category = categoryId,
    format = formatId,
    language = language,
    speakerIds = speakerIds,
    linkSlides = null,
    linkReplay = null
)

fun TalkDb.mergeWith(sessionOP: SessionOP) = TalkDb(
    id = sessionOP.id,
    title = if (title == sessionOP.title) title else sessionOP.title,
    level = if (level == sessionOP.level) level else sessionOP.level,
    abstract = if (abstract == sessionOP.abstract) abstract else sessionOP.abstract,
    category = if (category == sessionOP.categoryId) category else sessionOP.categoryId,
    format = if (format == sessionOP.formatId) format else sessionOP.formatId,
    language = if (language == sessionOP.language) language else sessionOP.language,
    speakerIds = if (speakerIds == sessionOP.speakerIds) speakerIds else sessionOP.speakerIds,
    linkSlides = linkSlides,
    linkReplay = linkReplay
)

fun SessionOP.convertToScheduleDb(order: Int, tracks: List<TrackOP>) = ScheduleDb(
    order = order,
    startTime = dateStart?.split("+")?.first()
        ?: error("Can't schedule a talk without a start time"),
    endTime = dateEnd?.split("+")?.first()
        ?: error("Can't schedule a talk without a end time"),
    room = trackId?.let { tracks.find { it.id == trackId }?.name }
        ?: error("Can't schedule a talk without a room"),
    talkId = id
)
