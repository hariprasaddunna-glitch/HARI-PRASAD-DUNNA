package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stock_inward")
data class StockInwardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String = "",
    val itemCode: String = "",
    val assetNo: String = "",
    val poNo: String = "",
    val supplierName: String = "",
    val materialCode: String = "",
    val description: String = "",
    val size: String = "",
    val brand: String = "",
    val model: String = "",
    val serial: String = "",
    val receivedDate: String = "",
    val uom: String = "Nos",
    val receivedQty: Int = 0,
    val location: String = ""
)

@Entity(tableName = "stock_outward")
data class StockOutwardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemCode: String = "",
    val desc: String = "",
    val assetNo: String = "",
    val materialCode: String = "",
    val size: String = "",
    val brand: String = "",
    val model: String = "",
    val serial: String = "",
    val uom: String = "Nos",
    val issuedDate: String = "",
    val qty: Int = 1,
    val empId: String = "",
    val empName: String = "",
    val empPosition: String = "",
    val empContact: String = "",
    val workLocation: String = "",
    val returnDate: String = "",
    val returnCondition: String = "",
    val status: String = "Issued",
    val remarks: String = "",
    val receivedBy: String = ""
)

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey val empId: String,
    val name: String = "",
    val position: String = "",
    val contact: String = ""
)

@Entity(tableName = "calibrations")
data class CalibrationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemCode: String = "",
    val description: String = "",
    val calibrationDate: String = "",
    val nextCalibrationDate: String = "",
    val calibratedBy: String = "",
    val certificateNo: String = "",
    val remarks: String = ""
)

data class ToolsMasterItem(
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
    val receivedQty: Int,
    val issuedQty: Int,
    val repairQty: Int,
    val damageLostQty: Int,
    val availableQty: Int,
    val location: String,
    val holderSummary: String?,
    val holderEmpId: String?,
    val holderLocation: String?,
    val status: String,
    val calibStatus: String,
    val nextCalibrationDate: String?
)

enum class SyncStatus {
    CONNECTED,
    SYNCING,
    OFFLINE,
    ERROR,
    LOCAL
}
