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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.StockInwardEntity
import com.example.ui.ToolStoreViewModel
import com.example.ui.components.SectionCard
import com.example.ui.theme.SleekDivider
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekPurpleDark
import com.example.ui.theme.SleekPurpleLight
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInwardScreen(
    inwardList: List<StockInwardEntity>,
    onSubmitInward: (StockInwardEntity, () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val typeOptions = listOf("Power Tool", "Mechanical", "Measuring Instrument", "Rigging", "Safety Equipment", "Other")
    val uomOptions = listOf("Nos", "Set", "Pcs", "Mtr", "Kg")

    var selectedType by remember { mutableStateOf(typeOptions[0]) }
    var itemCode by remember { mutableStateOf("") }
    var assetNo by remember { mutableStateOf("") }
    var poNo by remember { mutableStateOf("") }
    var supplierName by remember { mutableStateOf("") }
    var materialCode by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var serial by remember { mutableStateOf("") }
    var receivedDate by remember { mutableStateOf(ToolStoreViewModel.todayStr()) }
    var selectedUom by remember { mutableStateOf(uomOptions[0]) }
    var receivedQty by remember { mutableStateOf("1") }
    var location by remember { mutableStateOf("") }

    val resetForm = {
        itemCode = ""
        assetNo = ""
        poNo = ""
        supplierName = ""
        materialCode = ""
        description = ""
        size = ""
        brand = ""
        model = ""
        serial = ""
        receivedQty = "1"
        location = ""
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Page Head
        Text(
            text = "Stock Inward",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Log new tools and equipment received into store. Synced to Microsoft Lists Stock Inward.",
            fontSize = 12.5.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Inward Form
        SectionCard(title = "Tools Received Entry") {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Row 1: 4 columns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    var typeExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = !typeExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("TYPE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
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
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false }
                        ) {
                            typeOptions.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t, fontSize = 12.5.sp) },
                                    onClick = { selectedType = t; typeExpanded = false }
                                )
                            }
                        }
                    }

                    InputField(
                        value = itemCode,
                        onValueChange = { itemCode = it },
                        label = "Item Code *",
                        placeholder = "e.g. PT-1002",
                        modifier = Modifier.weight(1f),
                        testTag = "input_inward_item_code"
                    )

                    InputField(
                        value = assetNo,
                        onValueChange = { assetNo = it },
                        label = "Asset No.",
                        placeholder = "e.g. AST-9025",
                        modifier = Modifier.weight(1f)
                    )

                    InputField(
                        value = poNo,
                        onValueChange = { poNo = it },
                        label = "PO No.",
                        placeholder = "e.g. PO-2026-101",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: 4 columns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InputField(
                        value = supplierName,
                        onValueChange = { supplierName = it },
                        label = "Supplier Name",
                        placeholder = "e.g. Gulf Tools LLC",
                        modifier = Modifier.weight(1f)
                    )

                    InputField(
                        value = materialCode,
                        onValueChange = { materialCode = it },
                        label = "Material Code",
                        placeholder = "e.g. MAT-PT-02",
                        modifier = Modifier.weight(1f)
                    )

                    InputField(
                        value = description,
                        onValueChange = { description = it },
                        label = "Tool Description",
                        placeholder = "e.g. 1/2 inch Pneumatic Impact Wrench",
                        modifier = Modifier.weight(2f)
                    )
                }

                // Row 3: 4 columns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InputField(
                        value = size,
                        onValueChange = { size = it },
                        label = "Size / Capacity",
                        placeholder = "e.g. 1/2\" Drive, 650Nm",
                        modifier = Modifier.weight(1f)
                    )

                    InputField(
                        value = brand,
                        onValueChange = { brand = it },
                        label = "Brand",
                        placeholder = "e.g. Chicago Pneumatic",
                        modifier = Modifier.weight(1f)
                    )

                    InputField(
                        value = model,
                        onValueChange = { model = it },
                        label = "Model No.",
                        placeholder = "e.g. CP7748",
                        modifier = Modifier.weight(1f)
                    )

                    InputField(
                        value = serial,
                        onValueChange = { serial = it },
                        label = "Serial No.",
                        placeholder = "e.g. CP-991204",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 4: 4 columns
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InputField(
                        value = receivedDate,
                        onValueChange = { receivedDate = it },
                        label = "Received Date",
                        placeholder = "YYYY-MM-DD",
                        modifier = Modifier.weight(1f)
                    )

                    var uomExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = uomExpanded,
                        onExpandedChange = { uomExpanded = !uomExpanded },
                        modifier = Modifier.weight(0.8f)
                    ) {
                        OutlinedTextField(
                            value = selectedUom,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("UOM", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = uomExpanded) },
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
                            expanded = uomExpanded,
                            onDismissRequest = { uomExpanded = false }
                        ) {
                            uomOptions.forEach { u ->
                                DropdownMenuItem(
                                    text = { Text(u, fontSize = 12.5.sp) },
                                    onClick = { selectedUom = u; uomExpanded = false }
                                )
                            }
                        }
                    }

                    InputField(
                        value = receivedQty,
                        onValueChange = { receivedQty = it },
                        label = "Received Qty *",
                        placeholder = "1",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.8f),
                        testTag = "input_inward_qty"
                    )

                    InputField(
                        value = location,
                        onValueChange = { location = it },
                        label = "Store Location",
                        placeholder = "e.g. Store Rack A2",
                        modifier = Modifier.weight(1.4f)
                    )
                }

                // Submit Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            val qty = receivedQty.toIntOrNull() ?: 1
                            val entity = StockInwardEntity(
                                type = selectedType,
                                itemCode = itemCode.trim(),
                                assetNo = assetNo.trim(),
                                poNo = poNo.trim(),
                                supplierName = supplierName.trim(),
                                materialCode = materialCode.trim(),
                                description = description.trim(),
                                size = size.trim(),
                                brand = brand.trim(),
                                model = model.trim(),
                                serial = serial.trim(),
                                receivedDate = receivedDate.trim(),
                                uom = selectedUom,
                                receivedQty = qty,
                                location = location.trim()
                            )
                            onSubmitInward(entity, resetForm)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPurple),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("btn_submit_inward")
                    ) {
                        Text(
                            text = "Add to Stock Inward",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Recently Received Table
        SectionCard(title = "Recently Received Log") {
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
                        InwardCol("DATE", 110.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        InwardCol("ITEM CODE", 120.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        InwardCol("TYPE", 140.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        InwardCol("DESCRIPTION", 240.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        InwardCol("SUPPLIER", 180.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        InwardCol("QTY", 70.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        InwardCol("UOM", 70.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        InwardCol("LOCATION", 140.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }

                    if (inwardList.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No stock inward logged yet.", color = TextSecondary, fontSize = 12.5.sp)
                        }
                    } else {
                        inwardList.take(50).forEach { item ->
                            Row(
                                modifier = Modifier
                                    .background(SurfaceLight)
                                    .padding(vertical = 9.dp, horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                InwardCol(item.receivedDate, 110.dp)
                                InwardCol(item.itemCode, 120.dp, fontWeight = FontWeight.Bold, color = SleekPurple)
                                InwardCol(item.type, 140.dp)
                                InwardCol(item.description, 240.dp)
                                InwardCol(item.supplierName, 180.dp)
                                InwardCol(item.receivedQty.toString(), 70.dp, fontWeight = FontWeight.Bold)
                                InwardCol(item.uom, 70.dp)
                                InwardCol(item.location, 140.dp)
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
private fun InwardCol(
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

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary) },
        placeholder = { Text(placeholder, fontSize = 12.sp, color = TextSecondary) },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SurfaceLight,
            unfocusedContainerColor = SleekSurfaceVariant,
            focusedBorderColor = SleekPurple,
            unfocusedBorderColor = SurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.testTag(testTag)
    )
}
