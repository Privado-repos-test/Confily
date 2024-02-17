package org.gdglille.devfest.android.theme.m3.partners.feature

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.gdglille.devfest.android.theme.m3.partners.screens.PartnerDetailOrientable
import org.gdglille.devfest.android.theme.m3.style.R
import org.gdglille.devfest.android.theme.m3.style.appbars.AppBarIcons
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PartnerDetailVM(
    partnerId: String,
    onLinkClicked: (url: String) -> Unit,
    onItineraryClicked: (lat: Double, lng: Double) -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (AppBarIcons.() -> Unit)? = null,
    isLandscape: Boolean = false,
    viewModel: PartnerDetailViewModel = koinViewModel(
        key = partnerId,
        parameters = { parametersOf(partnerId) })
) {
    when (val uiState = viewModel.uiState.collectAsState().value) {
        is PartnerUiState.Loading -> PartnerDetailOrientable(
            partnerItemUi = uiState.partner,
            onLinkClicked = {},
            onItineraryClicked = { _, _ -> },
            modifier = modifier,
            navigationIcon = navigationIcon,
            isLandscape = isLandscape,
            isLoading = true
        )

        is PartnerUiState.Failure -> Text(text = stringResource(id = R.string.text_error))
        is PartnerUiState.Success -> PartnerDetailOrientable(
            partnerItemUi = uiState.partner,
            onLinkClicked = onLinkClicked,
            onItineraryClicked = onItineraryClicked,
            modifier = modifier,
            navigationIcon = navigationIcon,
            isLandscape = isLandscape,
        )
    }
}
