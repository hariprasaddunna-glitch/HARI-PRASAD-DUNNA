package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.EmployeeEntity
import com.example.data.model.StockInwardEntity
import com.example.data.model.ToolsMasterItem
import com.example.ui.components.CalibrationPill
import com.example.ui.components.SearchInput
import com.example.ui.components.SectionCard
import com.example.ui.components.StatCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.SleekAmber
import com.example.ui.theme.SleekAmberLight
import com.example.ui.theme.SleekBlue
import com.example.ui.theme.SleekBlueLight
import com.example.ui.theme.SleekDivider
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekGreenDot
import com.example.ui.theme.SleekGreenLight
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekPurpleDark
import com.example.ui.theme.SleekPurpleLight
import com.example.ui.theme.SleekRed
import com.example.ui.theme.SleekRedAlert
import com.example.ui.theme.SleekRedLight
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.StatusDamageText
import com.example.ui.theme.StatusGoodText
import com.example.ui.theme.StatusRepairText
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsMasterDashboardScreen(
    rawItems: List<ToolsMasterItem>,
    filteredItems: List<ToolsMasterItem>,
    inwardList: List<StockInwardEntity>,
    employees: List<EmployeeEntity>,
    selectedType: String,
    onTypeChange: (String) -> Unit,
    selectedStatus: String,
    onStatusChange: (String) -> Unit,
    selectedEmployee: String,
    onEmployeeChange: (String) -> Unit,
    selectedLocation: String,
    onLocationChange: (String) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var detailItem by remember { mutableStateOf<ToolsMasterItem?>(null) }

    // Summary calculations
    val totalCount = rawItems.size
    val availableCount = rawItems.count { it.availableQty > 0 }
    val issuedUnits = rawItems.sumOf { it.issuedQty }
    val repairUnits = rawItems.sumOf { it.repairQty }
    val damagedUnits = rawItems.sumOf { it.damageLostQty }

    val distinctTypes = remember(rawItems) {
        listOf("All") + rawItems.map { it.type }.filter { it.isNotBlank() }.distinct().sorted()
    }
    val distinctLocations = remember(inwardList) {
        listOf("All") + inwardList.map { it.location }.filter { it.isNotBlank() }.distinct().sorted()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Sleek Interface Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TOOLSYNC PRO",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = SleekPurple,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Inventory Dashboard",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Live Pipe & Outfitting Tools Register — Synchronized with Microsoft Lists",
                    fontSize = 12.5.sp,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SleekPurpleLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User profile",
                    tint = SleekPurpleDark,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // 5 Sleek Stat Cards Grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                count = totalCount.toString(),
                label = "Total item codes",
                containerColor = SleekPurpleLight,
                accentColor = SleekPurpleDark,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                count = availableCount.toString(),
                label = "Available now",
                containerColor = SleekGreenLight,
                accentColor = SleekGreen,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                count = issuedUnits.toString(),
                label = "Units issued",
                containerColor = SleekBlueLight,
                accentColor = SleekBlue,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                count = repairUnits.toString(),
                label = "Under repair",
                containerColor = SleekAmberLight,
                accentColor = SleekAmber,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                count = damagedUnits.toString(),
                label = "Damaged / lost",
                containerColor = SleekRedLight,
                accentColor = SleekRed,
                modifier = Modifier.weight(1f)
            )
        }

        // Main Card with Filters and Data Table
        SectionCard(
            title = "Inventory Register",
            action = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(SleekGreenDot)
                    )
                    Text(
                        text = "Sync Active",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Filter Toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Filter: Type
                    FilterDropdown(
                        label = "Type",
                        selected = if (selectedType.isBlank()) "All" else selectedType,
                        options = distinctTypes,
                        onSelect = { onTypeChange(if (it == "All") "" else it) },
                        modifier = Modifier.weight(1.2f)
                    )

                    // Filter: Status
                    FilterDropdown(
                        label = "Status",
                        selected = if (selectedStatus.isBlank()) "All" else selectedStatus,
                        options = listOf("All", "Good", "Issued", "Repair", "Damage/Lost"),
                        onSelect = { onStatusChange(if (it == "All") "" else it) },
                        modifier = Modifier.weight(1.1f)
                    )

                    // Filter: Employee
                    val empOptions = remember(employees) {
                        listOf("All") + employees.map { "${it.empId} - ${it.name}" }
                    }
                    val selectedEmpDisplay = remember(selectedEmployee, employees) {
                        if (selectedEmployee.isBlank()) "All"
                        else employees.find { it.empId == selectedEmployee }?.let { "${it.empId} - ${it.name}" } ?: selectedEmployee
                    }
                    FilterDropdown(
                        label = "Holder",
                        selected = selectedEmpDisplay,
                        options = empOptions,
                        onSelect = { opt ->
                            if (opt == "All") onEmployeeChange("")
                            else {
                                val id = opt.substringBefore(" - ")
                                onEmployeeChange(id)
                            }
                        },
                        modifier = Modifier.weight(1.4f)
                    )

                    // Filter: Location
                    FilterDropdown(
                        label = "Location",
                        selected = if (selectedLocation.isBlank()) "All" else selectedLocation,
                        options = distinctLocations,
                        onSelect = { onLocationChange(if (it == "All") "" else it) },
                        modifier = Modifier.weight(1.2f)
                    )

                    // Live Search
                    SearchInput(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = "Search item code or description...",
                        modifier = Modifier.weight(2f)
                    )
                }

                // High-Density Data Table
                val hScrollState = rememberScrollState()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceLight)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(hScrollState)
                    ) {
                        // Header Row (Sleek Surface Variant)
                        Row(
                            modifier = Modifier
                                .background(SleekSurfaceVariant)
                                .border(androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder.copy(alpha = 0.5f)))
                                .padding(vertical = 11.dp, horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableCol("ITEM CODE", 110.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("TYPE", 130.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("DESCRIPTION", 220.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("SIZE / CAP", 120.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("BRAND", 100.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("MODEL", 100.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("UOM", 70.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("RCVD", 70.dp, Alignment.Center, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("ISSUED", 70.dp, Alignment.Center, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("REPAIR", 70.dp, Alignment.Center, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("DMG/LOST", 85.dp, Alignment.Center, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("AVAIL", 75.dp, Alignment.Center, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("STATUS", 110.dp, Alignment.Center, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("CURRENT HOLDER", 200.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                            TableCol("CALIBRATION", 110.dp, Alignment.Center, color = TextSecondary, fontWeight = FontWeight.Bold)
                        }

                        if (filteredItems.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp, horizontal = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No items match current filter criteria. Use Stock Inward to receive tools.",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(460.dp)
                            ) {
                                items(filteredItems) { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { detailItem = item }
                                            .background(if (item.availableQty <= 0) Color(0xFFFAFBFD) else SurfaceLight)
                                            .padding(vertical = 10.dp, horizontal = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TableCol(item.itemCode, 110.dp, fontWeight = FontWeight.Bold, color = SleekPurple)
                                        TableCol(item.type, 130.dp)
                                        TableCol(item.description, 220.dp)
                                        TableCol(item.size, 120.dp)
                                        TableCol(item.brand, 100.dp)
                                        TableCol(item.model, 100.dp)
                                        TableCol(item.uom, 70.dp)
                                        TableCol(item.receivedQty.toString(), 70.dp, Alignment.Center)
                                        TableCol(item.issuedQty.toString(), 70.dp, Alignment.Center)
                                        TableCol(item.repairQty.toString(), 70.dp, Alignment.Center)
                                        TableCol(item.damageLostQty.toString(), 85.dp, Alignment.Center)
                                        TableCol(
                                            item.availableQty.toString(),
                                            75.dp,
                                            Alignment.Center,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (item.availableQty > 0) StatusGoodText else StatusDamageText
                                        )
                                        Box(modifier = Modifier.width(110.dp), contentAlignment = Alignment.Center) {
                                            StatusPill(status = item.status)
                                        }
                                        TableCol(item.holderSummary ?: "—", 200.dp, color = if (item.holderSummary != null) TextPrimary else TextSecondary)
                                        Box(modifier = Modifier.width(110.dp), contentAlignment = Alignment.Center) {
                                            CalibrationPill(calibStatus = item.calibStatus)
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
    }

    // Detail Dialog
    detailItem?.let { item ->
        ItemDetailDialog(item = item, onDismiss = { detailItem = null })
    }
}

@Composable
private fun TableCol(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    alignment: Alignment = Alignment.CenterStart,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = TextPrimary
) {
    Box(
        modifier = Modifier.width(width),
        contentAlignment = alignment
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = fontWeight,
            color = color,
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceLight,
                unfocusedContainerColor = SleekSurfaceVariant,
                focusedBorderColor = SleekPurple,
                unfocusedBorderColor = SurfaceBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(14.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 12.5.sp) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ItemDetailDialog(
    item: ToolsMasterItem,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.itemCode,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekPurple
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusPill(status = item.status)
                        }
                        Text(
                            text = item.description,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = SleekDivider, modifier = Modifier.padding(vertical = 12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DetailRow("Tool Type", item.type)
                    DetailRow("Brand & Model", "${item.brand} ${item.model}".trim())
                    DetailRow("Asset No.", item.assetNo.ifBlank { "—" })
                    DetailRow("Material Code", item.materialCode.ifBlank { "—" })
                    DetailRow("Serial No.", item.serial.ifBlank { "—" })
                    DetailRow("PO / Supplier", "${item.poNo} / ${item.supplierName}".trim().removePrefix("/").removeSuffix("/"))
                    DetailRow("Default Store Location", item.location.ifBlank { "Main Store" })
                    DetailRow("Stock Quantities", "Received: ${item.receivedQty} | Issued: ${item.issuedQty} | Repair: ${item.repairQty} | Avail: ${item.availableQty} ${item.uom}")
                    DetailRow("Current Holder", item.holderSummary ?: "In store (Not currently issued)")
                    DetailRow("Calibration", "${item.calibStatus} (Next due: ${item.nextCalibrationDate ?: "Not scheduled"})")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
        Text(text = value, fontSize = 12.5.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
    }
}
