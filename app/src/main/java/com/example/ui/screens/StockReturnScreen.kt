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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.StockOutwardEntity
import com.example.ui.ToolStoreViewModel
import com.example.ui.components.SectionCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.SleekDivider
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekPurpleLight
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockReturnScreen(
    outwardList: List<StockOutwardEntity>,
    onSubmitReturn: (StockOutwardEntity, String, String, String, String, () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingReturns = remember(outwardList) {
        outwardList.filter { it.returnDate.isBlank() }
    }
    val completedReturns = remember(outwardList) {
        outwardList.filter { it.returnDate.isNotBlank() }
    }

    var selectedOutward by remember { mutableStateOf<StockOutwardEntity?>(null) }
    var returnDate by remember { mutableStateOf(ToolStoreViewModel.todayStr()) }
    val conditionOptions = listOf("Good", "Repair", "Damage", "Lost")
    var selectedCondition by remember { mutableStateOf(conditionOptions[0]) }
    var receivedBy by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    val resetForm = {
        selectedOutward = null
        selectedCondition = conditionOptions[0]
        receivedBy = ""
        remarks = ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Stock Return",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Receive a tool back from an employee and record its condition. Returns update live inventory status immediately.",
            fontSize = 12.5.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Return Action Card
        SectionCard(title = "Receive a Tool Back") {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Read-only inspection details of selected item
                if (selectedOutward == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SleekSurfaceVariant, RoundedCornerShape(16.dp))
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Select an issued tool from the 'Awaiting Return' table below to process return.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    val r = selectedOutward!!
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReturnReadOnlyField("Selected Tool", "${r.itemCode} — ${r.desc}", Modifier.weight(1.5f))
                        ReturnReadOnlyField("Issued Date", r.issuedDate, Modifier.weight(1f))
                        ReturnReadOnlyField("Issued Qty", "${r.qty} ${r.uom}", Modifier.weight(1f))
                        ReturnReadOnlyField("Issued To", "${r.empName} (${r.empId})", Modifier.weight(1.5f))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ReturnReadOnlyField("Work Location", r.workLocation, Modifier.weight(1.5f))
                        ReturnReadOnlyField("Contact", r.empContact.ifBlank { "—" }, Modifier.weight(1f))
                        ReturnReadOnlyField("Asset / Serial", "${r.assetNo} / ${r.serial}".removePrefix("/").removeSuffix("/"), Modifier.weight(1.5f))
                    }

                    // Return Parameters Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InputField(
                            value = returnDate,
                            onValueChange = { returnDate = it },
                            label = "Return Date",
                            placeholder = "YYYY-MM-DD",
                            modifier = Modifier.weight(1f)
                        )

                        var condExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = condExpanded,
                            onExpandedChange = { condExpanded = !condExpanded },
                            modifier = Modifier.weight(1.2f)
                        ) {
                            OutlinedTextField(
                                value = selectedCondition,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("RETURN CONDITION *", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = condExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .testTag("select_return_condition"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SurfaceLight,
                                    unfocusedContainerColor = SleekSurfaceVariant,
                                    focusedBorderColor = SleekPurple,
                                    unfocusedBorderColor = SurfaceBorder
                                ),
                                shape = RoundedCornerShape(14.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = condExpanded,
                                onDismissRequest = { condExpanded = false }
                            ) {
                                conditionOptions.forEach { cond ->
                                    DropdownMenuItem(
                                        text = { Text(cond, fontSize = 12.5.sp) },
                                        onClick = {
                                            selectedCondition = cond
                                            condExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        InputField(
                            value = receivedBy,
                            onValueChange = { receivedBy = it },
                            label = "Received By",
                            placeholder = "Store keeper name",
                            modifier = Modifier.weight(1.2f),
                            testTag = "input_received_by"
                        )

                        InputField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            label = "Inspection Remarks",
                            placeholder = "e.g. Good condition, case included",
                            modifier = Modifier.weight(1.6f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { selectedOutward = null },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(text = "Cancel", fontSize = 12.sp, color = TextSecondary)
                        }

                        Button(
                            onClick = {
                                onSubmitReturn(r, returnDate, selectedCondition, receivedBy, remarks, resetForm)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SleekPurple),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("btn_submit_return")
                        ) {
                            Text(
                                text = "Receive Tool into Store",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Awaiting Return List
        SectionCard(title = "Tools Awaiting Return (${pendingReturns.size})") {
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
                            .padding(vertical = 10.dp, horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RetCol("ITEM CODE", 110.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        RetCol("DESCRIPTION", 220.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        RetCol("ISSUED DATE", 110.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        RetCol("QTY", 70.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        RetCol("EMPLOYEE", 180.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        RetCol("SITE LOCATION", 160.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        RetCol("REMARKS", 180.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        RetCol("ACTION", 110.dp, Alignment.Center, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }

                    if (pendingReturns.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("All issued tools have been returned.", color = TextSecondary, fontSize = 12.5.sp)
                        }
                    } else {
                        pendingReturns.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .background(if (selectedOutward?.id == item.id) SleekPurpleLight else SurfaceLight)
                                    .padding(vertical = 8.dp, horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RetCol(item.itemCode, 110.dp, fontWeight = FontWeight.Bold, color = SleekPurple)
                                RetCol(item.desc, 220.dp)
                                RetCol(item.issuedDate, 110.dp)
                                RetCol("${item.qty} ${item.uom}", 70.dp)
                                RetCol("${item.empName.ifBlank { item.empId }} (${item.empPosition})", 180.dp)
                                RetCol(item.workLocation, 160.dp)
                                RetCol(item.remarks.ifBlank { "—" }, 180.dp)
                                Box(modifier = Modifier.width(110.dp), contentAlignment = Alignment.Center) {
                                    OutlinedButton(
                                        onClick = { selectedOutward = item },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = SleekPurple
                                        ),
                                        modifier = Modifier.testTag("btn_select_return_${item.itemCode}")
                                    ) {
                                        Text(text = "Select", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            HorizontalDivider(color = SleekDivider, thickness = 1.dp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Recently Received Returns History
        SectionCard(title = "Recently Received Returns Log") {
            val hScroll2 = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(hScroll2)
                ) {
                    Row(
                        modifier = Modifier
                            .background(SleekSurfaceVariant)
                            .padding(vertical = 10.dp, horizontal = 14.dp)
                    ) {
                        RetCol("ITEM CODE", 110.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        RetCol("DESCRIPTION", 220.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        RetCol("EMPLOYEE", 180.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        RetCol("RETURN DATE", 110.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        RetCol("CONDITION", 110.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        RetCol("RECEIVED BY", 140.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        RetCol("STATUS", 140.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }

                    if (completedReturns.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No returned tools logged yet.", color = TextSecondary, fontSize = 12.5.sp)
                        }
                    } else {
                        completedReturns.take(50).forEach { item ->
                            Row(
                                modifier = Modifier
                                    .background(SurfaceLight)
                                    .padding(vertical = 9.dp, horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RetCol(item.itemCode, 110.dp, fontWeight = FontWeight.Bold, color = SleekPurple)
                                RetCol(item.desc, 220.dp)
                                RetCol("${item.empName} (${item.empId})", 180.dp)
                                RetCol(item.returnDate, 110.dp)
                                RetCol(item.returnCondition, 110.dp, fontWeight = FontWeight.SemiBold)
                                RetCol(item.receivedBy.ifBlank { "—" }, 140.dp)
                                Box(modifier = Modifier.width(140.dp)) {
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
private fun ReturnReadOnlyField(label: String, value: String, modifier: Modifier = Modifier) {
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
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    )
}

@Composable
private fun RetCol(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    alignment: Alignment = Alignment.CenterStart,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = TextPrimary
) {
    Box(modifier = Modifier.width(width), contentAlignment = alignment) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = fontWeight,
            color = color,
            maxLines = 1
        )
    }
}
