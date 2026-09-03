package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ToolStoreDatabase
import com.example.data.model.CalibrationEntity
import com.example.data.model.EmployeeEntity
import com.example.data.model.StockInwardEntity
import com.example.data.model.StockOutwardEntity
import com.example.data.model.SyncStatus
import com.example.data.model.ToolsMasterItem
import com.example.data.remote.PowerAutomateSyncService
import com.example.data.repository.ToolStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen(val label: String) {
    DASHBOARD("Tools Master"),
    INWARD("Stock Inward"),
    OUTWARD("Stock Outward"),
    RETURN("Stock Return"),
    CALIBRATION("Tool Calibration"),
    EMPLOYEES("Employees")
}

enum class UserRole {
    ADMIN,
    OPERATOR
}

class ToolStoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ToolStoreRepository

    init {
        val db = ToolStoreDatabase.getDatabase(application, viewModelScope)
        val syncService = PowerAutomateSyncService(application)
        repository = ToolStoreRepository(db.toolStoreDao(), syncService)
    }

    // Active Screen & Role
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.ADMIN)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    // Cloud connection dialog
    private val _isCloudSettingsOpen = MutableStateFlow(false)
    val isCloudSettingsOpen: StateFlow<Boolean> = _isCloudSettingsOpen.asStateFlow()

    private val _cloudUrlInput = MutableStateFlow(repository.getCloudUrl())
    val cloudUrlInput: StateFlow<String> = _cloudUrlInput.asStateFlow()

    // Message events
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    val syncStatus: StateFlow<SyncStatus> = repository.syncStatus
    val lastSyncTime: StateFlow<String?> = repository.lastSyncTime

    // Master Filters
    val filterType = MutableStateFlow("")
    val filterStatus = MutableStateFlow("")
    val filterEmployee = MutableStateFlow("")
    val filterLocation = MutableStateFlow("")
    val filterSearch = MutableStateFlow("")

    val rawToolsMaster: StateFlow<List<ToolsMasterItem>> = repository.toolsMaster
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allInward: StateFlow<List<StockInwardEntity>> = repository.allInward
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOutward: StateFlow<List<StockOutwardEntity>> = repository.allOutward
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEmployees: StateFlow<List<EmployeeEntity>> = repository.allEmployees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCalibrations: StateFlow<List<CalibrationEntity>> = repository.allCalibrations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    data class FilterParams(
        val type: String = "",
        val status: String = "",
        val employee: String = "",
        val location: String = "",
        val search: String = ""
    )

    private val filterParamsFlow: Flow<FilterParams> = combine(
        filterType,
        filterStatus,
        filterEmployee,
        filterLocation,
        filterSearch
    ) { type, status, emp, loc, search ->
        FilterParams(type, status, emp, loc, search)
    }

    // Filtered Tools Master
    val filteredToolsMaster: StateFlow<List<ToolsMasterItem>> = combine(
        rawToolsMaster,
        filterParamsFlow
    ) { items, filters ->
        items.filter { item ->
            if (filters.type.isNotBlank() && item.type != filters.type) return@filter false
            if (filters.status.isNotBlank() && item.status != filters.status) return@filter false
            if (filters.employee.isNotBlank() && item.holderEmpId != filters.employee) return@filter false
            if (filters.location.isNotBlank() && item.location != filters.location && item.holderLocation != filters.location) return@filter false
            if (filters.search.isNotBlank()) {
                val q = filters.search.trim().lowercase()
                val match = item.itemCode.lowercase().contains(q) ||
                        item.description.lowercase().contains(q) ||
                        item.brand.lowercase().contains(q) ||
                        item.model.lowercase().contains(q) ||
                        item.assetNo.lowercase().contains(q) ||
                        (item.holderSummary ?: "").lowercase().contains(q)
                if (!match) return@filter false
            }
            true
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun toggleUserRole() {
        _userRole.value = if (_userRole.value == UserRole.ADMIN) UserRole.OPERATOR else UserRole.ADMIN
    }

    fun openCloudSettings() {
        _cloudUrlInput.value = repository.getCloudUrl()
        _isCloudSettingsOpen.value = true
    }

    fun closeCloudSettings() {
        _isCloudSettingsOpen.value = false
    }

    fun updateCloudUrlInput(url: String) {
        _cloudUrlInput.value = url
    }

    fun saveCloudUrl(url: String) {
        repository.setCloudUrl(url)
        closeCloudSettings()
        viewModelScope.launch {
            _snackbarMessage.emit(if (url.isNotBlank()) "Power Automate URL updated" else "Cloud sync disconnected (Local mode)")
            if (url.isNotBlank()) {
                syncWithCloud()
            }
        }
    }

    fun syncWithCloud() {
        viewModelScope.launch {
            val result = repository.syncAllWithCloud()
            if (result.isSuccess) {
                _snackbarMessage.emit(result.getOrNull() ?: "Synced with Microsoft Lists")
            } else {
                _snackbarMessage.emit("Sync failed: ${result.exceptionOrNull()?.message ?: "Check URL / Network"}")
            }
        }
    }

    fun resetSampleData() {
        viewModelScope.launch {
            repository.resetToSampleData()
            _snackbarMessage.emit("Sample data reset successfully")
        }
    }

    // Submit actions
    fun submitStockInward(item: StockInwardEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (item.itemCode.isBlank()) {
                _snackbarMessage.emit("Item Code is required")
                return@launch
            }
            if (item.receivedQty <= 0) {
                _snackbarMessage.emit("Received quantity must be greater than 0")
                return@launch
            }
            repository.addStockInward(item)
            _snackbarMessage.emit("Added ${item.itemCode} to Stock Inward")
            onSuccess()
        }
    }

    fun submitIssueTool(
        selectedMaster: ToolsMasterItem,
        employee: EmployeeEntity,
        qty: Int,
        workLocation: String,
        issuedDate: String,
        remarks: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (qty < 1 || qty > selectedMaster.availableQty) {
                _snackbarMessage.emit("Quantity must be between 1 and available quantity (${selectedMaster.availableQty})")
                return@launch
            }
            val outward = StockOutwardEntity(
                itemCode = selectedMaster.itemCode,
                desc = selectedMaster.description,
                assetNo = selectedMaster.assetNo,
                materialCode = selectedMaster.materialCode,
                size = selectedMaster.size,
                brand = selectedMaster.brand,
                model = selectedMaster.model,
                serial = selectedMaster.serial,
                uom = selectedMaster.uom,
                issuedDate = issuedDate.ifBlank { todayStr() },
                qty = qty,
                empId = employee.empId,
                empName = employee.name,
                empPosition = employee.position,
                empContact = employee.contact,
                workLocation = workLocation,
                returnDate = "",
                returnCondition = "",
                status = "Issued",
                remarks = remarks,
                receivedBy = ""
            )
            repository.issueTool(outward)
            _snackbarMessage.emit("Issued ${selectedMaster.itemCode} to ${employee.name}")
            onSuccess()
        }
    }

    fun submitReturnTool(
        outwardItem: StockOutwardEntity,
        returnDate: String,
        returnCondition: String,
        receivedBy: String,
        remarks: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val status = when (returnCondition) {
                "Good" -> "Ready for Reissue"
                "Repair" -> "Under Repair"
                "Damage", "Lost" -> "Not available for Issue"
                else -> "Returned"
            }
            val updated = outwardItem.copy(
                returnDate = returnDate.ifBlank { todayStr() },
                returnCondition = returnCondition,
                receivedBy = receivedBy,
                status = status,
                remarks = remarks.ifBlank { outwardItem.remarks }
            )
            repository.returnTool(updated)
            _snackbarMessage.emit("Received ${outwardItem.itemCode} back from ${outwardItem.empName.ifBlank { outwardItem.empId }}")
            onSuccess()
        }
    }

    fun submitEmployee(employee: EmployeeEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (employee.empId.isBlank() || employee.name.isBlank()) {
                _snackbarMessage.emit("Employee ID and Name are required")
                return@launch
            }
            if (allEmployees.value.any { it.empId.equals(employee.empId, ignoreCase = true) }) {
                _snackbarMessage.emit("Employee ID ${employee.empId} already exists")
                return@launch
            }
            repository.addEmployee(employee)
            _snackbarMessage.emit("Added ${employee.name} (${employee.empId})")
            onSuccess()
        }
    }

    fun submitCalibration(calibration: CalibrationEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (calibration.itemCode.isBlank() || calibration.nextCalibrationDate.isBlank()) {
                _snackbarMessage.emit("Item Code and Next Calibration Date are required")
                return@launch
            }
            repository.addCalibration(calibration)
            _snackbarMessage.emit("Logged calibration for ${calibration.itemCode}")
            onSuccess()
        }
    }

    companion object {
        fun todayStr(): String =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
