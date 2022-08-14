package org.gdglille.devfest.android.theme.vitamin.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.decathlon.vitamin.compose.foundation.VitaminTheme
import org.gdglille.devfest.android.theme.vitamin.ui.R
import org.gdglille.devfest.android.theme.vitamin.ui.components.events.EventMap
import org.gdglille.devfest.android.theme.vitamin.ui.components.events.TicketDetailed
import org.gdglille.devfest.android.theme.vitamin.ui.components.events.TicketQrCode
import org.gdglille.devfest.android.theme.vitamin.ui.components.structure.SocialsSection
import org.gdglille.devfest.android.theme.vitamin.ui.theme.Conferences4HallTheme
import org.gdglille.devfest.models.EventUi

@Composable
fun Event(
    event: EventUi,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onLinkClicked: (url: String?) -> Unit,
    onItineraryClicked: (lat: Double, lng: Double) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            SocialsSection(
                title = event.eventInfo.name,
                subtitle = event.eventInfo.date,
                isLoading = isLoading,
                twitterUrl = event.eventInfo.twitterUrl,
                linkedinUrl = event.eventInfo.linkedinUrl,
                onLinkClicked = onLinkClicked
            )
        }
        event.ticket?.let {
            item {
                Text(
                    text = stringResource(R.string.title_ticket),
                    style = VitaminTheme.typography.h6,
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (it.info != null) {
                    TicketDetailed(
                        ticket = it.info!!,
                        qrCode = it.qrCode,
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = isLoading
                    )
                } else if (it.qrCode != null) {
                    TicketQrCode(
                        qrCode = it.qrCode!!,
                        modifier = Modifier.fillMaxWidth(),
                        isLoading = isLoading
                    )
                }
            }
        }
        item {
            Text(
                text = stringResource(R.string.title_plan),
                style = VitaminTheme.typography.h6,
            )
            Spacer(modifier = Modifier.height(8.dp))
            EventMap(
                formattedAddress = event.eventInfo.formattedAddress,
                isLoading = isLoading,
                onItineraryClicked = {
                    onItineraryClicked(event.eventInfo.latitude, event.eventInfo.longitude)
                }
            )
        }
    }
}

@ExperimentalFoundationApi
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview
@Composable
fun EventPreview() {
    Conferences4HallTheme {
        Scaffold {
            Event(
                event = EventUi.fake,
                onLinkClicked = {},
                onItineraryClicked = { _, _ -> }
            )
        }
    }
}
