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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmployeeEntity
import com.example.data.model.StockOutwardEntity
import com.example.data.model.ToolsMasterItem
import com.example.ui.ToolStoreViewModel
import com.example.ui.components.SectionCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.SleekDivider
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.StatusGoodText
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockOutwardScreen(
    masterItems: List<ToolsMasterItem>,
    employees: List<EmployeeEntity>,
    outwardList: List<StockOutwardEntity>,
    onSubmitIssue: (ToolsMasterItem, EmployeeEntity, Int, String, String, String, () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val availableItems = remember(masterItems) {
        masterItems.filter { it.availableQty > 0 }
    }

    var selectedItem by remember { mutableStateOf<ToolsMasterItem?>(null) }
    var selectedEmployee by remember { mutableStateOf<EmployeeEntity?>(null) }

    var issuedDate by remember { mutableStateOf(ToolStoreViewModel.todayStr()) }
    var issueQty by remember { mutableStateOf("1") }
    var workLocation by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    val resetForm = {
        selectedItem = null
        selectedEmployee = null
        issueQty = "1"
        workLocation = ""
        remarks = ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Stock Outward",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Issue tools to an employee. Verifies available inventory in real-time.",
            fontSize = 12.5.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        SectionCard(title = "Issue a Tool") {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Selectors row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Item Selector
                    var itemExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = itemExpanded,
                        onExpandedChange = { itemExpanded = !itemExpanded },
                        modifier = Modifier.weight(1.5f)
                    ) {
                        OutlinedTextField(
                            value = selectedItem?.let { "${it.itemCode} - ${it.description} (${it.availableQty} avail)" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("SELECT TOOL TO ISSUE *", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary) },
                            placeholder = { Text("Choose available tool...", fontSize = 12.sp, color = TextSecondary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = itemExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("select_tool_dropdown"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceLight,
                                unfocusedContainerColor = SleekSurfaceVariant,
                                focusedBorderColor = SleekPurple,
                                unfocusedBorderColor = SurfaceBorder
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = itemExpanded,
                            onDismissRequest = { itemExpanded = false }
                        ) {
                            if (availableItems.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No tools currently available", fontSize = 12.sp, color = TextSecondary) },
                                    onClick = { itemExpanded = false }
                                )
                            } else {
                                availableItems.forEach { item ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "${item.itemCode} — ${item.description}",
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                    text = "${item.availableQty} ${item.uom}",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = StatusGoodText
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedItem = item
                                            itemExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Employee Selector
                    var empExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = empExpanded,
                        onExpandedChange = { empExpanded = !empExpanded },
                        modifier = Modifier.weight(1.5f)
                    ) {
                        OutlinedTextField(
                            value = selectedEmployee?.let { "${it.empId} - ${it.name} (${it.position})" } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("SELECT EMPLOYEE *", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary) },
                            placeholder = { Text("Choose employee...", fontSize = 12.sp, color = TextSecondary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = empExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("select_employee_dropdown"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SurfaceLight,
                                unfocusedContainerColor = SleekSurfaceVariant,
                                focusedBorderColor = SleekPurple,
                                unfocusedBorderColor = SurfaceBorder
                            ),
                            shape = RoundedCornerShape(14.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = empExpanded,
                            onDismissRequest = { empExpanded = false }
                        ) {
                            employees.forEach { emp ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${emp.empId} — ${emp.name} (${emp.position})",
                                            fontSize = 12.5.sp
                                        )
                                    },
                                    onClick = {
                                        selectedEmployee = emp
                                        empExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Auto-filled Tool Specs Row (Read-only)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReadOnlyField("Asset No.", selectedItem?.assetNo ?: "—", Modifier.weight(1f))
                    ReadOnlyField("Material Code", selectedItem?.materialCode ?: "—", Modifier.weight(1f))
                    ReadOnlyField("Brand & Model", "${selectedItem?.brand ?: ""} ${selectedItem?.model ?: ""}".trim().ifBlank { "—" }, Modifier.weight(1f))
                    ReadOnlyField("Available Qty", "${selectedItem?.availableQty ?: 0} ${selectedItem?.uom ?: "Nos"}", Modifier.weight(1f), isBold = true)
                }

                // Auto-filled Employee Details Row (Read-only)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ReadOnlyField("Employee Name", selectedEmployee?.name ?: "—", Modifier.weight(1f))
                    ReadOnlyField("Position", selectedEmployee?.position ?: "—", Modifier.weight(1f))
                    ReadOnlyField("Contact", selectedEmployee?.contact ?: "—", Modifier.weight(1f))
                }

                // Issue Form Inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InputField(
                        value = issuedDate,
                        onValueChange = { issuedDate = it },
                        label = "Issued Date",
                        placeholder = "YYYY-MM-DD",
                        modifier = Modifier.weight(1f)
                    )

                    InputField(
                        value = issueQty,
                        onValueChange = { issueQty = it },
                        label = "Qty to Issue *",
                        placeholder = "1",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.8f),
                        testTag = "input_outward_qty"
                    )

                    InputField(
                        value = workLocation,
                        onValueChange = { workLocation = it },
                        label = "Working Site / Location *",
                        placeholder = "e.g. Drydock Quay 2, Tank B",
                        modifier = Modifier.weight(1.5f),
                        testTag = "input_outward_location"
                    )

                    InputField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = "Remarks",
                        placeholder = "Optional notes",
                        modifier = Modifier.weight(1.5f)
                    )
                }

                // Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            val item = selectedItem
                            val emp = selectedEmployee
                            if (item != null && emp != null) {
                                val qty = issueQty.toIntOrNull() ?: 1
                                onSubmitIssue(item, emp, qty, workLocation, issuedDate, remarks, resetForm)
                            }
                        },
                        enabled = selectedItem != null && selectedEmployee != null,
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPurple),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("btn_submit_outward")
                    ) {
                        Text(
                            text = "Issue Tool",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Recent Outward Activity
        SectionCard(title = "Recent Outward Activity") {
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
                        OutCol("ITEM CODE", 120.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        OutCol("DESCRIPTION", 220.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        OutCol("ISSUED DATE", 110.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        OutCol("QTY", 70.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        OutCol("EMPLOYEE", 180.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        OutCol("LOCATION", 160.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        OutCol("RETURN DATE", 110.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        OutCol("CONDITION", 110.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        OutCol("STATUS", 130.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }

                    if (outwardList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No tools issued yet.", color = TextSecondary, fontSize = 12.5.sp)
                        }
                    } else {
                        outwardList.take(50).forEach { item ->
                            Row(
                                modifier = Modifier
                                    .background(SurfaceLight)
                                    .padding(vertical = 9.dp, horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutCol(item.itemCode, 120.dp, fontWeight = FontWeight.Bold, color = SleekPurple)
                                OutCol(item.desc, 220.dp)
                                OutCol(item.issuedDate, 110.dp)
                                OutCol("${item.qty} ${item.uom}", 70.dp)
                                OutCol("${item.empName.ifBlank { item.empId }} (${item.empPosition})", 180.dp)
                                OutCol(item.workLocation, 160.dp)
                                OutCol(item.returnDate.ifBlank { "—" }, 110.dp)
                                OutCol(item.returnCondition.ifBlank { "—" }, 110.dp)
                                Box(modifier = Modifier.width(130.dp)) {
                                    StatusPill(status = item.status)
                                }
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
private fun ReadOnlyField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isBold: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SleekSurfaceVariant.copy(alpha = 0.5f),
            unfocusedContainerColor = SleekSurfaceVariant.copy(alpha = 0.5f),
            focusedBorderColor = SurfaceBorder,
            unfocusedBorderColor = SurfaceBorder,
            focusedTextColor = if (isBold) SleekPurple else TextPrimary,
            unfocusedTextColor = if (isBold) SleekPurple else TextPrimary
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    )
}

@Composable
private fun OutCol(
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
