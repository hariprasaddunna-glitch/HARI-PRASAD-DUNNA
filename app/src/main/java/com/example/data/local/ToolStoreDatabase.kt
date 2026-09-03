package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CalibrationEntity
import com.example.data.model.EmployeeEntity
import com.example.data.model.StockInwardEntity
import com.example.data.model.StockOutwardEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StockInwardEntity::class,
        StockOutwardEntity::class,
        EmployeeEntity::class,
        CalibrationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ToolStoreDatabase : RoomDatabase() {

    abstract fun toolStoreDao(): ToolStoreDao

    companion object {
        @Volatile
        private var INSTANCE: ToolStoreDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ToolStoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToolStoreDatabase::class.java,
                    "tool_store_register.db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            scope.launch(Dispatchers.IO) {
                                populateInitialData(database.toolStoreDao())
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(dao: ToolStoreDao) {
            val initialEmployees = listOf(
                EmployeeEntity("E-101", "Marcus Vance", "Lead Fitter", "+1 555-0142"),
                EmployeeEntity("E-102", "Elena Rostova", "Pipe Fabricator", "+1 555-0189"),
                EmployeeEntity("E-103", "David Chen", "NDT Technician", "+1 555-0193"),
                EmployeeEntity("E-104", "Tariq Mansoor", "Welding Specialist", "+1 555-0211"),
                EmployeeEntity("E-105", "Grace Hopper", "Outfitting Supervisor", "+1 555-0245")
            )
            dao.insertAllEmployees(initialEmployees)

            val initialInward = listOf(
                StockInwardEntity(
                    type = "Power Tool",
                    itemCode = "PT-1001",
                    assetNo = "AST-9021",
                    poNo = "PO-2026-081",
                    supplierName = "Gulf Tools & Engineering",
                    materialCode = "MAT-PT-01",
                    description = "Heavy Duty Angle Grinder 9 inch",
                    size = "9 inch / 230mm",
                    brand = "Makita",
                    model = "GA9020",
                    serial = "MK-882910",
                    receivedDate = "2026-08-10",
                    uom = "Nos",
                    receivedQty = 6,
                    location = "Store Rack A1"
                ),
                StockInwardEntity(
                    type = "Mechanical",
                    itemCode = "MC-2005",
                    assetNo = "AST-7741",
                    poNo = "PO-2026-085",
                    supplierName = "Apex Maritime Supplies",
                    materialCode = "MAT-MC-05",
                    description = "Heavy Duty Pipe Wrench 24\"",
                    size = "24 inch",
                    brand = "Ridgid",
                    model = "HD-24",
                    serial = "RG-49102",
                    receivedDate = "2026-08-12",
                    uom = "Nos",
                    receivedQty = 10,
                    location = "Store Rack B3"
                ),
                StockInwardEntity(
                    type = "Measuring Instrument",
                    itemCode = "MI-3010",
                    assetNo = "AST-6612",
                    poNo = "PO-2026-090",
                    supplierName = "Precision Metrology Corp",
                    materialCode = "MAT-MI-10",
                    description = "Digital Vernier Caliper 300mm",
                    size = "0-300mm / 0.01mm",
                    brand = "Mitutoyo",
                    model = "CD-12\"AX",
                    serial = "MT-77402",
                    receivedDate = "2026-08-15",
                    uom = "Nos",
                    receivedQty = 4,
                    location = "Cabinet C1 - Precision"
                ),
                StockInwardEntity(
                    type = "Measuring Instrument",
                    itemCode = "MI-3022",
                    assetNo = "AST-6630",
                    poNo = "PO-2026-092",
                    supplierName = "Precision Metrology Corp",
                    materialCode = "MAT-MI-22",
                    description = "Industrial Torque Wrench 40-200 Nm",
                    size = "40-200 Nm 1/2\" Dr",
                    brand = "Norbar",
                    model = "Pro 200",
                    serial = "NB-55018",
                    receivedDate = "2026-08-18",
                    uom = "Nos",
                    receivedQty = 3,
                    location = "Cabinet C1 - Precision"
                ),
                StockInwardEntity(
                    type = "Rigging",
                    itemCode = "RG-4050",
                    assetNo = "AST-5520",
                    poNo = "PO-2026-095",
                    supplierName = "Delta Lifting Gear",
                    materialCode = "MAT-RG-50",
                    description = "Lever Chain Hoist 1.5T",
                    size = "1.5 Tonne / 3m lift",
                    brand = "Vital",
                    model = "NR-2",
                    serial = "VT-90214",
                    receivedDate = "2026-08-20",
                    uom = "Nos",
                    receivedQty = 5,
                    location = "Bay D2 - Rigging"
                ),
                StockInwardEntity(
                    type = "Safety Equipment",
                    itemCode = "SF-5012",
                    assetNo = "AST-4419",
                    poNo = "PO-2026-099",
                    supplierName = "SafeMarine Ltd",
                    materialCode = "MAT-SF-12",
                    description = "Full Body Safety Harness w/ Double Lanyard",
                    size = "Universal (EN 361)",
                    brand = "3M Protecta",
                    model = "PRO-200",
                    serial = "3M-33290",
                    receivedDate = "2026-08-22",
                    uom = "Set",
                    receivedQty = 8,
                    location = "Rack E - PPE Store"
                )
            )
            dao.insertAllInward(initialInward)

            val initialOutward = listOf(
                StockOutwardEntity(
                    itemCode = "PT-1001",
                    desc = "Heavy Duty Angle Grinder 9 inch",
                    assetNo = "AST-9021",
                    materialCode = "MAT-PT-01",
                    size = "9 inch / 230mm",
                    brand = "Makita",
                    model = "GA9020",
                    serial = "MK-882910",
                    uom = "Nos",
                    issuedDate = "2026-08-25",
                    qty = 2,
                    empId = "E-101",
                    empName = "Marcus Vance",
                    empPosition = "Lead Fitter",
                    empContact = "+1 555-0142",
                    workLocation = "Drydock Quay 2",
                    returnDate = "",
                    returnCondition = "",
                    status = "Issued",
                    remarks = "Hull fabrication outfit",
                    receivedBy = ""
                ),
                StockOutwardEntity(
                    itemCode = "MC-2005",
                    desc = "Heavy Duty Pipe Wrench 24\"",
                    assetNo = "AST-7741",
                    materialCode = "MAT-MC-05",
                    size = "24 inch",
                    brand = "Ridgid",
                    model = "HD-24",
                    serial = "RG-49102",
                    uom = "Nos",
                    issuedDate = "2026-08-28",
                    qty = 3,
                    empId = "E-102",
                    empName = "Elena Rostova",
                    empPosition = "Pipe Fabricator",
                    empContact = "+1 555-0189",
                    workLocation = "Ballast Tank Module 4",
                    returnDate = "",
                    returnCondition = "",
                    status = "Issued",
                    remarks = "Flange bolt-up",
                    receivedBy = ""
                ),
                StockOutwardEntity(
                    itemCode = "MI-3010",
                    desc = "Digital Vernier Caliper 300mm",
                    assetNo = "AST-6612",
                    materialCode = "MAT-MI-10",
                    size = "0-300mm / 0.01mm",
                    brand = "Mitutoyo",
                    model = "CD-12\"AX",
                    serial = "MT-77402",
                    uom = "Nos",
                    issuedDate = "2026-08-20",
                    qty = 1,
                    empId = "E-103",
                    empName = "David Chen",
                    empPosition = "NDT Technician",
                    empContact = "+1 555-0193",
                    workLocation = "Quality Inspection Bay",
                    returnDate = "2026-08-29",
                    returnCondition = "Good",
                    status = "Ready for Reissue",
                    remarks = "Returned in clean condition with case",
                    receivedBy = "Store Operator A"
                ),
                StockOutwardEntity(
                    itemCode = "PT-1001",
                    desc = "Heavy Duty Angle Grinder 9 inch",
                    assetNo = "AST-9021",
                    materialCode = "MAT-PT-01",
                    size = "9 inch / 230mm",
                    brand = "Makita",
                    model = "GA9020",
                    serial = "MK-882910",
                    uom = "Nos",
                    issuedDate = "2026-08-15",
                    qty = 1,
                    empId = "E-104",
                    empName = "Tariq Mansoor",
                    empPosition = "Welding Specialist",
                    empContact = "+1 555-0211",
                    workLocation = "Pipe Shop Line B",
                    returnDate = "2026-08-26",
                    returnCondition = "Repair",
                    status = "Under Repair",
                    remarks = "Switch intermittent, sent to electrical maintenance",
                    receivedBy = "Store Operator B"
                )
            )
            dao.insertAllOutward(initialOutward)

            val initialCalibrations = listOf(
                CalibrationEntity(
                    itemCode = "MI-3010",
                    description = "Digital Vernier Caliper 300mm",
                    calibrationDate = "2026-06-15",
                    nextCalibrationDate = "2026-09-15",
                    calibratedBy = "ISO Calib Labs Inc",
                    certificateNo = "CERT-2026-8912",
                    remarks = "Passed all tolerance checks. Due within 30 days."
                ),
                CalibrationEntity(
                    itemCode = "MI-3022",
                    description = "Industrial Torque Wrench 40-200 Nm",
                    calibrationDate = "2025-08-10",
                    nextCalibrationDate = "2026-08-10",
                    calibratedBy = "National Metrology Bureau",
                    certificateNo = "CERT-2025-4421",
                    remarks = "Annual calibration expired! Recalibration required before critical pipe joints."
                )
            )
            dao.insertAllCalibrations(initialCalibrations)
        }
    }
}
