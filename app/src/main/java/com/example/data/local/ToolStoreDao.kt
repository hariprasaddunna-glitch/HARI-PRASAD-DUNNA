package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.CalibrationEntity
import com.example.data.model.EmployeeEntity
import com.example.data.model.StockInwardEntity
import com.example.data.model.StockOutwardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolStoreDao {

    // Stock Inward
    @Query("SELECT * FROM stock_inward ORDER BY id DESC")
    fun getAllInward(): Flow<List<StockInwardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInward(item: StockInwardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllInward(items: List<StockInwardEntity>)

    @Query("DELETE FROM stock_inward")
    suspend fun clearInward()

    // Stock Outward
    @Query("SELECT * FROM stock_outward ORDER BY id DESC")
    fun getAllOutward(): Flow<List<StockOutwardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutward(item: StockOutwardEntity): Long

    @Update
    suspend fun updateOutward(item: StockOutwardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllOutward(items: List<StockOutwardEntity>)

    @Query("DELETE FROM stock_outward")
    suspend fun clearOutward()

    // Employees
    @Query("SELECT * FROM employees ORDER BY empId ASC")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEmployees(employees: List<EmployeeEntity>)

    @Query("DELETE FROM employees")
    suspend fun clearEmployees()

    // Calibrations
    @Query("SELECT * FROM calibrations ORDER BY id DESC")
    fun getAllCalibrations(): Flow<List<CalibrationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalibration(calibration: CalibrationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCalibrations(calibrations: List<CalibrationEntity>)

    @Query("DELETE FROM calibrations")
    suspend fun clearCalibrations()

    @Transaction
    suspend fun replaceAllData(
        inward: List<StockInwardEntity>,
        outward: List<StockOutwardEntity>,
        employees: List<EmployeeEntity>,
        calibrations: List<CalibrationEntity>
    ) {
        clearInward()
        insertAllInward(inward)
        clearOutward()
        insertAllOutward(outward)
        clearEmployees()
        insertAllEmployees(employees)
        clearCalibrations()
        insertAllCalibrations(calibrations)
    }
}
