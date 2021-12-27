package com.paligot.conferences.android.components.talks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.paligot.conferences.android.theme.ConferenceTheme

@Composable
fun ScheduleItem(
    time: String,
    talks: List<TalkItemUi>,
    modifier: Modifier = Modifier,
    onTalkClicked: (id: String) -> Unit
) {
    val timeSpace = 55.dp
    Box(modifier = modifier) {
        Box(
            modifier = Modifier.width(timeSpace),
            contentAlignment = Alignment.Center
        ) {
            Time(time = time, modifier = Modifier)
        }
        Column {
            talks.forEach {
                TalkBox(onClick = { onTalkClicked(it.id) }) {
                    TalkItem(
                        talk = it,
                        modifier = Modifier.padding(start = timeSpace)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ScheduleItemPreview() {
    ConferenceTheme {
        ScheduleItem(time = "10:00", talks = arrayListOf(talkItem, talkItem)) {}
    }
}
