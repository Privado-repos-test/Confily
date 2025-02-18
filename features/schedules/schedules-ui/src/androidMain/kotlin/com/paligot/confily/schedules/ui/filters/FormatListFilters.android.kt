package com.paligot.confily.schedules.ui.filters

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.paligot.confily.schedules.ui.models.FormatUi
import com.paligot.confily.style.theme.ConfilyTheme
import kotlinx.collections.immutable.persistentMapOf

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FormatListPreview() {
    ConfilyTheme {
        Surface {
            FormatListFilters(
                formats = persistentMapOf(
                    FormatUi.quickie to true,
                    FormatUi.conference to false
                ),
                onClick = { _, _ -> }
            )
        }
    }
}
