package br.com.firstsoft.target.server

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.firstsoft.core.common.reporting.setDefaultUncaughtExceptionHandler
import br.com.firstsoft.core.os.hardwaremonitor.HardwareMonitorProcessManager
import br.com.firstsoft.core.os.hardwaremonitor.HardwareMonitorReader
import br.com.firstsoft.core.os.hardwaremonitor.PresentMonProcessManager
import br.com.firstsoft.target.server.data.ObserveHardwareReadings
import br.com.firstsoft.target.server.data.OverlaySettingsRepository
import br.com.firstsoft.target.server.model.OverlaySettings
import br.com.firstsoft.target.server.ui.overlay.OverlayWindow
import br.com.firstsoft.target.server.ui.settings.SettingsWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainState(
    val overlaySettings: OverlaySettings = OverlaySettings(),
)

class MainViewModel : ViewModel() {
    private val _state: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val state: Flow<MainState>
        get() = _state

    init {
        observeOverlaySettings()
    }

    private fun observeOverlaySettings() {
        CoroutineScope(Dispatchers.IO).launch {
            ObserveHardwareReadings.data.collect {
                println(it)
            }

            OverlaySettingsRepository
                .data
                .collectLatest { overlaySettings ->
                    _state.update { it.copy(overlaySettings = overlaySettings) }
                }
        }
    }
}

object ApplicationViewModelStoreOwner : ViewModelStoreOwner {
    private val _store = ViewModelStore()
    override val viewModelStore: ViewModelStore
        get() = _store

}

fun main() {
    setDefaultUncaughtExceptionHandler()

    val channel = Channel<Unit>()
    //registerKeyboardHook(channel)
    Runtime.getRuntime().addShutdownHook(Thread {
        HardwareMonitorProcessManager.stop()
        PresentMonProcessManager.stop()
    })

    PresentMonProcessManager.start()
    HardwareMonitorProcessManager.start()

    application {
        val viewModel: MainViewModel = viewModel(ApplicationViewModelStoreOwner)

        val state by viewModel.state.collectAsState(MainState())

        var overlayPosition by remember {
            mutableStateOf(
                IntOffset(
                    state.overlaySettings.positionX,
                    state.overlaySettings.positionY
                )
            )
        }

        OverlayWindow(
            channel = channel,
            onPositionChanged = {
                if (!state.overlaySettings.isPositionLocked) {
                    overlayPosition = it
                }
            },
        )

        SettingsWindow(
            getOverlayPosition = { overlayPosition },
            onApplicationExit = {
                HardwareMonitorProcessManager.stop()
                PresentMonProcessManager.stop()
            }
        )
    }
}
