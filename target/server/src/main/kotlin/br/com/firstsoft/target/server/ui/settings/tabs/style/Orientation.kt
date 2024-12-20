package br.com.firstsoft.target.server.ui.settings.tabs.style

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import br.com.firstsoft.target.server.model.OverlaySettings
import br.com.firstsoft.target.server.ui.components.CollapsibleSection
import br.com.firstsoft.target.server.ui.components.StyleCard

@Composable
internal fun Orientation(
    overlaySettings: OverlaySettings,
    onLayoutChange: (Boolean) -> Unit
) {
    CollapsibleSection(title = "ORIENTATION") {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StyleCard(
                label = "Horizontal",
                isSelected = overlaySettings.isHorizontal,
                modifier = Modifier.weight(.5f),
                onClick = { onLayoutChange(true) },
                content = {
                    Image(
                        painter = painterResource("icons/horizontal.png"),
                        contentDescription = "Horizontal image"
                    )
                }
            )

            StyleCard(
                label = "Vertical",
                isSelected = !overlaySettings.isHorizontal,
                modifier = Modifier.weight(.5f),
                onClick = { onLayoutChange(false) },
                content = {
                    Image(
                        painter = painterResource("icons/vertical.png"),
                        contentDescription = "Vertical image",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            )
        }
    }
}