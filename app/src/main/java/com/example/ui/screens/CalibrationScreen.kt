package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalibrationEntity
import com.example.data.model.ToolsMasterItem
import com.example.ui.ToolStoreViewModel
import com.example.ui.components.CalibrationPill
import com.example.ui.components.SectionCard
import com.example.ui.theme.SleekDivider
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.StatusDamageText
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalibrationScreen(
    masterItems: List<ToolsMasterItem>,
    calibrationList: List<CalibrationEntity>,
    onSubmitCalibration: (CalibrationEntity, () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val dueOrOverdue = remember(masterItems) {
        masterItems.filter { it.calibStatus == "Overdue" || it.calibStatus == "Due Soon" }
    }

    var selectedItem by remember { mutableStateOf<ToolsMasterItem?>(null) }
    var calibDate by remember { mutableStateOf(ToolStoreViewModel.todayStr()) }
    var nextCalibDate by remember { mutableStateOf("") }
    var calibratedBy by remember { mutableStateOf("") }
    var certificateNo by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    val resetForm = {
        selectedItem = null
        nextCalibDate = ""
        calibratedBy = ""
        certificateNo = ""
        remarks = ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Tool Calibration Register",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Track calibration certificates, expiry dates, and regulatory compliance for precision tools.",
            fontSize = 12.5.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Due soon or overdue warning card
        if (dueOrOverdue.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFF7ED))
                    .border(1.dp, Color(0xFFFED7AA), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFC2410C),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Calibration Attention Required (${dueOrOverdue.size} items)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9A3412)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    dueOrOverdue.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.itemCode} — ${item.description}",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Due: ${item.nextCalibrationDate ?: "Expired"}  ",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                CalibrationPill(calibStatus = item.calibStatus)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Log Calibration Form
        SectionCard(title = "Log Tool Calibration") {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Tool selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    var toolExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = toolExpanded,
                        onExpandedChange = { toolExpanded = !toolExpanded },
                        modifier = Modifier.weight(2f)
                    ) {
                        OutlinedTextField(
                            value = selectedItem?.let { "${it.itemCode} - ${it.description}" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("SELECT TOOL TO CALIBRATE *", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary) },
                            placeholder = { Text("Choose tool...", fontSize = 12.sp, color = TextSecondary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toolExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceLight,
                                unfocusedContainerColor = SleekSurfaceVariant,
                                focusedBorderColor = SleekPurple,
                                unfocusedBorderColor = SurfaceBorder
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = toolExpanded,
                            onDismissRequest = { toolExpanded = false }
                        ) {
                            masterItems.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text("${m.itemCode} — ${m.description} (${m.type})", fontSize = 12.5.sp) },
                                    onClick = {
                                        selectedItem = m
                                        toolExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    InputField(
                        value = calibDate,
                        onValueChange = { calibDate = it },
                        label = "Calibration Date",
                        placeholder = "YYYY-MM-DD",
                        modifier = Modifier.weight(1f)
                    )

                    InputField(
                        value = nextCalibDate,
                        onValueChange = { nextCalibDate = it },
                        label = "Next Due Date *",
                        placeholder = "YYYY-MM-DD",
                        modifier = Modifier.weight(1f),
                        testTag = "input_next_calib_date"
                    )
                }

                // Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InputField(
                        value = calibratedBy,
                        onValueChange = { calibratedBy = it },
                        label = "Calibrated By (Agency / Lab)",
                        placeholder = "e.g. National Metrology Lab",
                        modifier = Modifier.weight(1.5f)
                    )

                    InputField(
                        value = certificateNo,
                        onValueChange = { certificateNo = it },
                        label = "Certificate No.",
                        placeholder = "e.g. CERT-2026-9901",
                        modifier = Modifier.weight(1.5f)
                    )

                    InputField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = "Calibration Remarks",
                        placeholder = "e.g. Within 0.01mm tolerance",
                        modifier = Modifier.weight(2f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            val item = selectedItem
                            if (item != null) {
                                val entity = CalibrationEntity(
                                    itemCode = item.itemCode,
                                    description = item.description,
                                    calibrationDate = calibDate,
                                    nextCalibrationDate = nextCalibDate,
                                    calibratedBy = calibratedBy,
                                    certificateNo = certificateNo,
                                    remarks = remarks
                                )
                                onSubmitCalibration(entity, resetForm)
                            }
                        },
                        enabled = selectedItem != null && nextCalibDate.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPurple),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("btn_submit_calibration")
                    ) {
                        Text(
                            text = "Log Calibration",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Calibration History
        SectionCard(title = "Calibration Certificate History") {
            val hScroll = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(hScroll)
                ) {
                    Row(
                        modifier = Modifier
                            .background(SleekSurfaceVariant)
                            .padding(vertical = 10.dp, horizontal = 14.dp)
                    ) {
                        CalCol("ITEM CODE", 120.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        CalCol("DESCRIPTION", 220.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        CalCol("CALIBRATION DATE", 130.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        CalCol("NEXT DUE DATE", 130.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        CalCol("CALIBRATED BY", 180.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        CalCol("CERTIFICATE NO.", 150.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        CalCol("REMARKS", 240.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }

                    if (calibrationList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No calibration history logged yet.", color = TextSecondary, fontSize = 12.5.sp)
                        }
                    } else {
                        calibrationList.forEach { cal ->
                            Row(
                                modifier = Modifier
                                    .background(SurfaceLight)
                                    .padding(vertical = 9.dp, horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CalCol(cal.itemCode, 120.dp, fontWeight = FontWeight.Bold, color = SleekPurple)
                                CalCol(cal.description, 220.dp)
                                CalCol(cal.calibrationDate, 130.dp)
                                CalCol(cal.nextCalibrationDate, 130.dp, fontWeight = FontWeight.Bold)
                                CalCol(cal.calibratedBy, 180.dp)
                                CalCol(cal.certificateNo, 150.dp)
                                CalCol(cal.remarks, 240.dp)
                            }
                            HorizontalDivider(color = SleekDivider, thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalCol(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = TextPrimary
) {
    Box(modifier = Modifier.width(width)) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = fontWeight,
            color = color,
            maxLines = 1
        )
    }
}
