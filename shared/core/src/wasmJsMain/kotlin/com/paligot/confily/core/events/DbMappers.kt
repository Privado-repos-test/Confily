package com.paligot.confily.core.events

import com.paligot.confily.models.EventItemList
import kotlinx.datetime.Instant

fun EventItemList.convertToModelDb(past: Boolean): EventItemDb = EventItemDb(
    id = this.id,
    name = this.name,
    date = this.startDate,
    timestamp = Instant.parse(this.startDate).toEpochMilliseconds(),
    past = past
)
