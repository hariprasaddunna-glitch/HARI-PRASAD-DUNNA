package com.example.data.remote

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.CalibrationEntity
import com.example.data.model.EmployeeEntity
import com.example.data.model.StockInwardEntity
import com.example.data.model.StockOutwardEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class PowerAutomateSyncService(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("tool_store_cloud_prefs", Context.MODE_PRIVATE)

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

    var flowUrl: String
        get() = prefs.getString("power_automate_flow_url", "") ?: ""
        set(value) {
            prefs.edit().putString("power_automate_flow_url", value.trim()).apply()
        }

    fun isConfigured(): Boolean = flowUrl.isNotBlank()

    data class RemoteDataBundle(
        val stockInward: List<StockInwardEntity>,
        val stockOutward: List<StockOutwardEntity>,
        val employees: List<EmployeeEntity>,
        val calibrations: List<CalibrationEntity>
    )

    suspend fun fetchAllRemote(): Result<RemoteDataBundle> = withContext(Dispatchers.IO) {
        val url = flowUrl
        if (url.isBlank()) {
            return@withContext Result.failure(IllegalStateException("No Power Automate Flow URL configured"))
        }

        try {
            val payload = JSONObject().apply {
                put("action", "getAll")
            }
            val requestBody = payload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP error ${response.code}: ${response.message}"))
                }
                val bodyStr = response.body?.string() ?: ""
                val json = JSONObject(bodyStr)
                if (!json.optBoolean("ok", true) && json.has("error")) {
                    return@withContext Result.failure(Exception(json.optString("error", "Unknown flow error")))
                }

                val dataObj = json.optJSONObject("data") ?: JSONObject()

                val inwardList = mutableListOf<StockInwardEntity>()
                val inwardArr = dataObj.optJSONArray("stockInward") ?: JSONArray()
                for (i in 0 until inwardArr.length()) {
                    val o = inwardArr.optJSONObject(i) ?: continue
                    inwardList.add(
                        StockInwardEntity(
                            type = o.optString("Type", o.optString("type", "")),
                            itemCode = o.optString("ItemCode", o.optString("itemCode", "")),
                            assetNo = o.optString("AssetNo", o.optString("assetNo", "")),
                            poNo = o.optString("PONo", o.optString("poNo", "")),
                            supplierName = o.optString("SupplierName", o.optString("supplierName", "")),
                            materialCode = o.optString("MaterialCode", o.optString("materialCode", "")),
                            description = o.optString("Description", o.optString("description", "")),
                            size = o.optString("Size", o.optString("size", "")),
                            brand = o.optString("Brand", o.optString("brand", "")),
                            model = o.optString("Model", o.optString("model", "")),
                            serial = o.optString("Serial", o.optString("serial", "")),
                            receivedDate = o.optString("ReceivedDate", o.optString("receivedDate", "")),
                            uom = o.optString("UOM", o.optString("uom", "Nos")),
                            receivedQty = o.optInt("ReceivedQty", o.optInt("receivedQty", 0)),
                            location = o.optString("Location", o.optString("location", ""))
                        )
                    )
                }

                val outwardList = mutableListOf<StockOutwardEntity>()
                val outwardArr = dataObj.optJSONArray("stockOutward") ?: JSONArray()
                for (i in 0 until outwardArr.length()) {
                    val o = outwardArr.optJSONObject(i) ?: continue
                    outwardList.add(
                        StockOutwardEntity(
                            itemCode = o.optString("ItemCode", o.optString("itemCode", "")),
                            desc = o.optString("Desc", o.optString("desc", o.optString("Description", ""))),
                            assetNo = o.optString("AssetNo", o.optString("assetNo", "")),
                            materialCode = o.optString("MaterialCode", o.optString("materialCode", "")),
                            size = o.optString("Size", o.optString("size", "")),
                            brand = o.optString("Brand", o.optString("brand", "")),
                            model = o.optString("Model", o.optString("model", "")),
                            serial = o.optString("Serial", o.optString("serial", "")),
                            uom = o.optString("UOM", o.optString("uom", "Nos")),
                            issuedDate = o.optString("IssuedDate", o.optString("issuedDate", "")),
                            qty = o.optInt("Qty", o.optInt("qty", 1)),
                            empId = o.optString("EmpID", o.optString("empId", "")),
                            empName = o.optString("EmpName", o.optString("empName", "")),
                            empPosition = o.optString("EmpPosition", o.optString("empPosition", "")),
                            empContact = o.optString("EmpContact", o.optString("empContact", "")),
                            workLocation = o.optString("WorkLocation", o.optString("workLocation", "")),
                            returnDate = o.optString("ReturnDate", o.optString("returnDate", "")),
                            returnCondition = o.optString("ReturnCondition", o.optString("returnCondition", "")),
                            status = o.optString("Status", o.optString("status", "Issued")),
                            remarks = o.optString("Remarks", o.optString("remarks", "")),
                            receivedBy = o.optString("ReceivedBy", o.optString("receivedBy", ""))
                        )
                    )
                }

                val empList = mutableListOf<EmployeeEntity>()
                val empArr = dataObj.optJSONArray("employees") ?: JSONArray()
                for (i in 0 until empArr.length()) {
                    val o = empArr.optJSONObject(i) ?: continue
                    val empId = o.optString("EmpID", o.optString("empId", ""))
                    if (empId.isNotBlank()) {
                        empList.add(
                            EmployeeEntity(
                                empId = empId,
                                name = o.optString("Name", o.optString("name", "")),
                                position = o.optString("Position", o.optString("position", "")),
                                contact = o.optString("Contact", o.optString("contact", ""))
                            )
                        )
                    }
                }

                val calibList = mutableListOf<CalibrationEntity>()
                val calibArr = dataObj.optJSONArray("calibration") ?: JSONArray()
                for (i in 0 until calibArr.length()) {
                    val o = calibArr.optJSONObject(i) ?: continue
                    calibList.add(
                        CalibrationEntity(
                            itemCode = o.optString("ItemCode", o.optString("itemCode", "")),
                            description = o.optString("Description", o.optString("description", "")),
                            calibrationDate = o.optString("CalibrationDate", o.optString("calibrationDate", "")),
                            nextCalibrationDate = o.optString("NextCalibrationDate", o.optString("nextCalibrationDate", "")),
                            calibratedBy = o.optString("CalibratedBy", o.optString("calibratedBy", "")),
                            certificateNo = o.optString("CertificateNo", o.optString("certificateNo", "")),
                            remarks = o.optString("Remarks", o.optString("remarks", ""))
                        )
                    )
                }

                Result.success(
                    RemoteDataBundle(
                        stockInward = inwardList,
                        stockOutward = outwardList,
                        employees = empList,
                        calibrations = calibList
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun pushAction(action: String, rowJson: JSONObject): Result<Unit> = withContext(Dispatchers.IO) {
        val url = flowUrl
        if (url.isBlank()) {
            return@withContext Result.success(Unit) // local-only mode
        }

        try {
            val payload = JSONObject().apply {
                put("action", action)
                put("row", rowJson)
            }
            val requestBody = payload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("HTTP error ${response.code}"))
                }
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
