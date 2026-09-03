package com.example.data.repository

import com.example.data.local.ToolStoreDao
import com.example.data.local.ToolStoreDatabase
import com.example.data.model.CalibrationEntity
import com.example.data.model.EmployeeEntity
import com.example.data.model.StockInwardEntity
import com.example.data.model.StockOutwardEntity
import com.example.data.model.SyncStatus
import com.example.data.model.ToolsMasterItem
import com.example.data.remote.PowerAutomateSyncService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ToolStoreRepository(
    private val dao: ToolStoreDao,
    private val syncService: PowerAutomateSyncService
) {
    private val _syncStatus = MutableStateFlow(
        if (syncService.isConfigured()) SyncStatus.CONNECTED else SyncStatus.LOCAL
    )
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<String?>(null)
    val lastSyncTime: StateFlow<String?> = _lastSyncTime.asStateFlow()

    val allInward: Flow<List<StockInwardEntity>> = dao.getAllInward()
    val allOutward: Flow<List<StockOutwardEntity>> = dao.getAllOutward()
    val allEmployees: Flow<List<EmployeeEntity>> = dao.getAllEmployees()
    val allCalibrations: Flow<List<CalibrationEntity>> = dao.getAllCalibrations()

    val toolsMaster: Flow<List<ToolsMasterItem>> = combine(
        allInward,
        allOutward,
        allCalibrations
    ) { inwardList, outwardList, calibrationList ->
        computeToolsMaster(inwardList, outwardList, calibrationList)
    }

    private fun computeToolsMaster(
        inwardList: List<StockInwardEntity>,
        outwardList: List<StockOutwardEntity>,
        calibrationList: List<CalibrationEntity>
    ): List<ToolsMasterItem> {
        val masterMap = linkedMapOf<String, MutableMaster>()

        // 1. Inward totals
        inwardList.forEach { inItem ->
            val code = inItem.itemCode.trim()
            if (code.isBlank()) return@forEach
            val entry = masterMap.getOrPut(code) {
                MutableMaster(
                    itemCode = code,
                    assetNo = inItem.assetNo,
                    poNo = inItem.poNo,
                    supplierName = inItem.supplierName,
                    type = inItem.type,
                    materialCode = inItem.materialCode,
                    description = inItem.description,
                    size = inItem.size,
                    brand = inItem.brand,
                    model = inItem.model,
                    serial = inItem.serial,
                    uom = inItem.uom,
                    location = inItem.location
                )
            }
            entry.receivedQty += inItem.receivedQty
        }

        // 2. Outward allocations
        outwardList.forEach { outItem ->
            val code = outItem.itemCode.trim()
            val entry = masterMap[code] ?: return@forEach
            val qty = outItem.qty

            if (outItem.returnDate.isBlank()) {
                entry.issuedQty += qty
                entry.currentHolder = "${outItem.empName.ifBlank { outItem.empId }} · ${outItem.workLocation}"
                entry.holderEmpId = outItem.empId
                entry.holderLocation = outItem.workLocation
            } else {
                when (outItem.returnCondition) {
                    "Repair" -> entry.repairQty += qty
                    "Damage", "Lost" -> entry.damageLostQty += qty
                }
            }
        }

        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        return masterMap.values.map { m ->
            val avail = m.receivedQty - m.issuedQty - m.repairQty - m.damageLostQty

            // Compute Item Status
            val status = when {
                avail > 0 -> "Good"
                m.repairQty > 0 && avail <= 0 -> "Repair"
                m.damageLostQty > 0 && avail <= 0 -> "Damage/Lost"
                else -> "Issued"
            }

            // Latest calibration
            val latestCal = calibrationList
                .filter { it.itemCode.equals(m.itemCode, ignoreCase = true) && it.nextCalibrationDate.isNotBlank() }
                .maxByOrNull { it.nextCalibrationDate }

            val calibStatus = computeCalibStatus(latestCal?.nextCalibrationDate, todayStr)

            ToolsMasterItem(
                itemCode = m.itemCode,
                assetNo = m.assetNo,
                poNo = m.poNo,
                supplierName = m.supplierName,
                type = m.type,
                materialCode = m.materialCode,
                description = m.description,
                size = m.size,
                brand = m.brand,
                model = m.model,
                serial = m.serial,
                uom = m.uom,
                receivedQty = m.receivedQty,
                issuedQty = m.issuedQty,
                repairQty = m.repairQty,
                damageLostQty = m.damageLostQty,
                availableQty = avail,
                location = m.location,
                holderSummary = m.currentHolder,
                holderEmpId = m.holderEmpId,
                holderLocation = m.holderLocation,
                status = status,
                calibStatus = calibStatus,
                nextCalibrationDate = latestCal?.nextCalibrationDate
            )
        }
    }

    private fun computeCalibStatus(nextDate: String?, today: String): String {
        if (nextDate.isNullOrBlank()) return "Not scheduled"
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val nextD = sdf.parse(nextDate)?.time ?: return "Not scheduled"
            val todayD = sdf.parse(today)?.time ?: return "Not scheduled"
            val diffDays = (nextD - todayD) / (1000 * 60 * 60 * 24)
            when {
                diffDays < 0 -> "Overdue"
                diffDays <= 30 -> "Due Soon"
                else -> "OK"
            }
        } catch (_: Exception) {
            "Not scheduled"
        }
    }

    suspend fun addStockInward(item: StockInwardEntity): Result<Unit> {
        dao.insertInward(item)
        if (syncService.isConfigured()) {
            _syncStatus.value = SyncStatus.SYNCING
            val json = JSONObject().apply {
                put("Type", item.type)
                put("ItemCode", item.itemCode)
                put("AssetNo", item.assetNo)
                put("PONo", item.poNo)
                put("SupplierName", item.supplierName)
                put("MaterialCode", item.materialCode)
                put("Description", item.description)
                put("Size", item.size)
                put("Brand", item.brand)
                put("Model", item.model)
                put("Serial", item.serial)
                put("ReceivedDate", item.receivedDate)
                put("UOM", item.uom)
                put("ReceivedQty", item.receivedQty)
                put("Location", item.location)
            }
            val res = syncService.pushAction("addInward", json)
            _syncStatus.value = if (res.isSuccess) SyncStatus.CONNECTED else SyncStatus.ERROR
        }
        return Result.success(Unit)
    }

    suspend fun issueTool(item: StockOutwardEntity): Result<Unit> {
        dao.insertOutward(item)
        if (syncService.isConfigured()) {
            _syncStatus.value = SyncStatus.SYNCING
            val json = JSONObject().apply {
                put("ItemCode", item.itemCode)
                put("Desc", item.desc)
                put("AssetNo", item.assetNo)
                put("MaterialCode", item.materialCode)
                put("Size", item.size)
                put("Brand", item.brand)
                put("Model", item.model)
                put("Serial", item.serial)
                put("UOM", item.uom)
                put("IssuedDate", item.issuedDate)
                put("Qty", item.qty)
                put("EmpID", item.empId)
                put("EmpName", item.empName)
                put("EmpPosition", item.empPosition)
                put("EmpContact", item.empContact)
                put("WorkLocation", item.workLocation)
                put("ReturnDate", item.returnDate)
                put("ReturnCondition", item.returnCondition)
                put("Status", item.status)
                put("Remarks", item.remarks)
                put("ReceivedBy", item.receivedBy)
            }
            val res = syncService.pushAction("addOutward", json)
            _syncStatus.value = if (res.isSuccess) SyncStatus.CONNECTED else SyncStatus.ERROR
        }
        return Result.success(Unit)
    }

    suspend fun returnTool(item: StockOutwardEntity): Result<Unit> {
        dao.updateOutward(item)
        if (syncService.isConfigured()) {
            _syncStatus.value = SyncStatus.SYNCING
            val json = JSONObject().apply {
                put("ItemCode", item.itemCode)
                put("Desc", item.desc)
                put("EmpID", item.empId)
                put("ReturnDate", item.returnDate)
                put("ReturnCondition", item.returnCondition)
                put("Status", item.status)
                put("ReceivedBy", item.receivedBy)
                put("Remarks", item.remarks)
            }
            val res = syncService.pushAction("updateOutward", json)
            _syncStatus.value = if (res.isSuccess) SyncStatus.CONNECTED else SyncStatus.ERROR
        }
        return Result.success(Unit)
    }

    suspend fun addEmployee(employee: EmployeeEntity): Result<Unit> {
        dao.insertEmployee(employee)
        if (syncService.isConfigured()) {
            _syncStatus.value = SyncStatus.SYNCING
            val json = JSONObject().apply {
                put("EmpID", employee.empId)
                put("Name", employee.name)
                put("Position", employee.position)
                put("Contact", employee.contact)
            }
            val res = syncService.pushAction("addEmployee", json)
            _syncStatus.value = if (res.isSuccess) SyncStatus.CONNECTED else SyncStatus.ERROR
        }
        return Result.success(Unit)
    }

    suspend fun addCalibration(calibration: CalibrationEntity): Result<Unit> {
        dao.insertCalibration(calibration)
        if (syncService.isConfigured()) {
            _syncStatus.value = SyncStatus.SYNCING
            val json = JSONObject().apply {
                put("ItemCode", calibration.itemCode)
                put("Description", calibration.description)
                put("CalibrationDate", calibration.calibrationDate)
                put("NextCalibrationDate", calibration.nextCalibrationDate)
                put("CalibratedBy", calibration.calibratedBy)
                put("CertificateNo", calibration.certificateNo)
                put("Remarks", calibration.remarks)
            }
            val res = syncService.pushAction("addCalibration", json)
            _syncStatus.value = if (res.isSuccess) SyncStatus.CONNECTED else SyncStatus.ERROR
        }
        return Result.success(Unit)
    }

    suspend fun syncAllWithCloud(): Result<String> {
        if (!syncService.isConfigured()) {
            _syncStatus.value = SyncStatus.LOCAL
            return Result.failure(IllegalStateException("No Power Automate Flow URL configured"))
        }

        _syncStatus.value = SyncStatus.SYNCING
        val res = syncService.fetchAllRemote()
        return if (res.isSuccess) {
            val bundle = res.getOrThrow()
            dao.replaceAllData(
                inward = bundle.stockInward,
                outward = bundle.stockOutward,
                employees = bundle.employees,
                calibrations = bundle.calibrations
            )
            _syncStatus.value = SyncStatus.CONNECTED
            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            _lastSyncTime.value = time
            Result.success("Synced successfully at $time")
        } else {
            _syncStatus.value = SyncStatus.ERROR
            Result.failure(res.exceptionOrNull() ?: Exception("Unknown sync error"))
        }
    }

    suspend fun resetToSampleData() {
        ToolStoreDatabase.populateInitialData(dao)
    }

    fun getCloudUrl(): String = syncService.flowUrl

    fun setCloudUrl(url: String) {
        syncService.flowUrl = url
        _syncStatus.value = if (url.isNotBlank()) SyncStatus.CONNECTED else SyncStatus.LOCAL
    }
}

private data class MutableMaster(
    val itemCode: String,
    val assetNo: String,
    val poNo: String,
    val supplierName: String,
    val type: String,
    val materialCode: String,
    val description: String,
    val size: String,
    val brand: String,
    val model: String,
    val serial: String,
    val uom: String,
    val location: String,
    var receivedQty: Int = 0,
    var issuedQty: Int = 0,
    var repairQty: Int = 0,
    var damageLostQty: Int = 0,
    var currentHolder: String? = null,
    var holderEmpId: String? = null,
    var holderLocation: String? = null
)
