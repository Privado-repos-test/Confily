package org.gdglille.devfest.android.theme.m3.infos.feature

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.paligot.confily.infos.panes.MenusScreen
import com.paligot.confily.resources.Resource
import com.paligot.confily.resources.text_error
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalResourceApi::class)
@Composable
fun MenusVM(
    modifier: Modifier = Modifier,
    viewModel: MenusViewModel = koinViewModel()
) {
    when (val uiState = viewModel.uiState.collectAsState().value) {
        is MenusUiState.Loading -> MenusScreen(
            menuItems = uiState.menus,
            modifier = modifier,
            isLoading = true
        )

        is MenusUiState.Failure -> Text(text = stringResource(Resource.string.text_error))
        is MenusUiState.Success -> MenusScreen(
            menuItems = uiState.menus,
            modifier = modifier,
            isLoading = false
        )
    }
}
