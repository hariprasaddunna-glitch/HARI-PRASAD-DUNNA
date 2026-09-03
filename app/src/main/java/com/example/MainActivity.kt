package com.example

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.ToolStoreViewModel
import com.example.ui.components.CloudSyncDialog
import com.example.ui.components.DesktopNavigationSidebar
import com.example.ui.screens.CalibrationScreen
import com.example.ui.screens.EmployeesScreen
import com.example.ui.screens.StockInwardScreen
import com.example.ui.screens.StockOutwardScreen
import com.example.ui.screens.StockReturnScreen
import com.example.ui.screens.ToolsMasterDashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceCanvas

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Strictly Desktop View Only: Lock to landscape mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ToolStoreApp()
            }
        }
    }
}

@Composable
fun ToolStoreApp(
    viewModel: ToolStoreViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val lastSyncTime by viewModel.lastSyncTime.collectAsStateWithLifecycle()

    val rawMaster by viewModel.rawToolsMaster.collectAsStateWithLifecycle()
    val filteredMaster by viewModel.filteredToolsMaster.collectAsStateWithLifecycle()
    val allInward by viewModel.allInward.collectAsStateWithLifecycle()
    val allOutward by viewModel.allOutward.collectAsStateWithLifecycle()
    val allEmployees by viewModel.allEmployees.collectAsStateWithLifecycle()
    val allCalibrations by viewModel.allCalibrations.collectAsStateWithLifecycle()

    val filterType by viewModel.filterType.collectAsStateWithLifecycle()
    val filterStatus by viewModel.filterStatus.collectAsStateWithLifecycle()
    val filterEmployee by viewModel.filterEmployee.collectAsStateWithLifecycle()
    val filterLocation by viewModel.filterLocation.collectAsStateWithLifecycle()
    val filterSearch by viewModel.filterSearch.collectAsStateWithLifecycle()

    val isCloudSettingsOpen by viewModel.isCloudSettingsOpen.collectAsStateWithLifecycle()
    val cloudUrlInput by viewModel.cloudUrlInput.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var isSidebarCollapsed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val pendingReturnsCount = remember(allOutward) {
        allOutward.count { it.returnDate.isBlank() }
    }
    val calibrationAlertCount = remember(rawMaster) {
        rawMaster.count { it.calibStatus == "Overdue" || it.calibStatus == "Due Soon" }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceCanvas)
        ) {
            val minDesktopWidth = 980.dp
            val needsScroll = maxWidth < minDesktopWidth
            val hScroll = rememberScrollState()

            Row(
                modifier = if (needsScroll) {
                    Modifier
                        .fillMaxHeight()
                        .width(minDesktopWidth)
                        .horizontalScroll(hScroll)
                        .background(SurfaceCanvas)
                } else {
                    Modifier
                        .fillMaxSize()
                        .background(SurfaceCanvas)
                }
            ) {
                // Left Navigation Sidebar
                DesktopNavigationSidebar(
                    currentScreen = currentScreen,
                    userRole = userRole,
                    syncStatus = syncStatus,
                    lastSyncTime = lastSyncTime,
                    pendingReturnsCount = pendingReturnsCount,
                    calibrationAlertCount = calibrationAlertCount,
                    isCollapsed = isSidebarCollapsed,
                    onToggleCollapse = { isSidebarCollapsed = !isSidebarCollapsed },
                    onSelectScreen = { viewModel.selectScreen(it) },
                    onToggleRole = { viewModel.toggleUserRole() },
                    onOpenCloudSettings = { viewModel.openCloudSettings() },
                    onManualSync = { viewModel.syncWithCloud() },
                    onResetSampleData = { viewModel.resetSampleData() }
                )

            // Main Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(SurfaceCanvas)
            ) {
                when (currentScreen) {
                    AppScreen.DASHBOARD -> {
                        ToolsMasterDashboardScreen(
                            rawItems = rawMaster,
                            filteredItems = filteredMaster,
                            inwardList = allInward,
                            employees = allEmployees,
                            selectedType = filterType,
                            onTypeChange = { viewModel.filterType.value = it },
                            selectedStatus = filterStatus,
                            onStatusChange = { viewModel.filterStatus.value = it },
                            selectedEmployee = filterEmployee,
                            onEmployeeChange = { viewModel.filterEmployee.value = it },
                            selectedLocation = filterLocation,
                            onLocationChange = { viewModel.filterLocation.value = it },
                            searchQuery = filterSearch,
                            onSearchChange = { viewModel.filterSearch.value = it }
                        )
                    }

                    AppScreen.INWARD -> {
                        StockInwardScreen(
                            inwardList = allInward,
                            onSubmitInward = { item, onSuccess ->
                                viewModel.submitStockInward(item, onSuccess)
                            }
                        )
                    }

                    AppScreen.OUTWARD -> {
                        StockOutwardScreen(
                            masterItems = rawMaster,
                            employees = allEmployees,
                            outwardList = allOutward,
                            onSubmitIssue = { item, emp, qty, location, date, remarks, onSuccess ->
                                viewModel.submitIssueTool(item, emp, qty, location, date, remarks, onSuccess)
                            }
                        )
                    }

                    AppScreen.RETURN -> {
                        StockReturnScreen(
                            outwardList = allOutward,
                            onSubmitReturn = { outward, date, condition, receivedBy, remarks, onSuccess ->
                                viewModel.submitReturnTool(outward, date, condition, receivedBy, remarks, onSuccess)
                            }
                        )
                    }

                    AppScreen.CALIBRATION -> {
                        CalibrationScreen(
                            masterItems = rawMaster,
                            calibrationList = allCalibrations,
                            onSubmitCalibration = { cal, onSuccess ->
                                viewModel.submitCalibration(cal, onSuccess)
                            }
                        )
                    }

                    AppScreen.EMPLOYEES -> {
                        EmployeesScreen(
                            employees = allEmployees,
                            outwardList = allOutward,
                            onSubmitEmployee = { emp, onSuccess ->
                                viewModel.submitEmployee(emp, onSuccess)
                            }
                        )
                    }
                }
            }
        }

        // Cloud Sync Settings Dialog
        CloudSyncDialog(
            isOpen = isCloudSettingsOpen,
            currentUrl = cloudUrlInput,
            onUrlChange = { viewModel.updateCloudUrlInput(it) },
            onSaveAndSync = { viewModel.saveCloudUrl(it) },
            onDisconnect = { viewModel.saveCloudUrl("") },
            onDismiss = { viewModel.closeCloudSettings() }
        )
        }
    }
}

