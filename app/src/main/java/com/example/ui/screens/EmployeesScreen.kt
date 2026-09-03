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
import androidx.compose.material3.HorizontalDivider
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
import com.example.data.model.EmployeeEntity
import com.example.data.model.StockOutwardEntity
import com.example.ui.components.SearchInput
import com.example.ui.components.SectionCard
import com.example.ui.theme.SleekDivider
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EmployeesScreen(
    employees: List<EmployeeEntity>,
    outwardList: List<StockOutwardEntity>,
    onSubmitEmployee: (EmployeeEntity, () -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var empId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    val resetForm = {
        empId = ""
        name = ""
        position = ""
        contact = ""
    }

    val filteredEmployees = remember(employees, searchQuery) {
        if (searchQuery.isBlank()) employees
        else {
            val q = searchQuery.trim().lowercase()
            employees.filter {
                it.empId.lowercase().contains(q) ||
                        it.name.lowercase().contains(q) ||
                        it.position.lowercase().contains(q) ||
                        it.contact.lowercase().contains(q)
            }
        }
    }

    // Map active tool issues per employee
    val activeIssuesCount = remember(outwardList) {
        outwardList.filter { it.returnDate.isBlank() }
            .groupBy { it.empId }
            .mapValues { it.value.sumOf { r -> r.qty } }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Employee Directory",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Manage registered personnel authorized to check out tools and equipment.",
            fontSize = 12.5.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Add Employee Form
        SectionCard(title = "Add Authorized Employee") {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InputField(
                        value = empId,
                        onValueChange = { empId = it },
                        label = "Employee ID *",
                        placeholder = "e.g. E-106",
                        modifier = Modifier.weight(1f),
                        testTag = "input_employee_id"
                    )

                    InputField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Full Name *",
                        placeholder = "e.g. James MacLeod",
                        modifier = Modifier.weight(1.5f),
                        testTag = "input_employee_name"
                    )

                    InputField(
                        value = position,
                        onValueChange = { position = it },
                        label = "Position / Trade",
                        placeholder = "e.g. Pipe Fitter, Rigger",
                        modifier = Modifier.weight(1.5f)
                    )

                    InputField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = "Phone / Extension",
                        placeholder = "e.g. +1 555-0199",
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            val entity = EmployeeEntity(
                                empId = empId.trim(),
                                name = name.trim(),
                                position = position.trim(),
                                contact = contact.trim()
                            )
                            onSubmitEmployee(entity, resetForm)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPurple),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("btn_submit_employee")
                    ) {
                        Text(
                            text = "Add Employee",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Employee Directory Table
        SectionCard(title = "Employee Directory (${filteredEmployees.size})") {
            SearchInput(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Search employee by ID, name, or trade...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

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
                        EmpCol("EMPLOYEE ID", 140.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        EmpCol("FULL NAME", 220.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        EmpCol("POSITION / TRADE", 200.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        EmpCol("CONTACT", 180.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                        EmpCol("CURRENT TOOLS ISSUED", 180.dp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    }

                    if (filteredEmployees.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No employees found.", color = TextSecondary, fontSize = 12.5.sp)
                        }
                    } else {
                        filteredEmployees.forEach { emp ->
                            val issuedQty = activeIssuesCount[emp.empId] ?: 0
                            Row(
                                modifier = Modifier
                                    .background(SurfaceLight)
                                    .padding(vertical = 9.dp, horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                EmpCol(emp.empId, 140.dp, fontWeight = FontWeight.Bold, color = SleekPurple)
                                EmpCol(emp.name, 220.dp, fontWeight = FontWeight.SemiBold)
                                EmpCol(emp.position, 200.dp)
                                EmpCol(emp.contact.ifBlank { "—" }, 180.dp)
                                EmpCol(
                                    if (issuedQty > 0) "$issuedQty tool(s) held" else "0 tools held",
                                    180.dp,
                                    color = if (issuedQty > 0) SleekPurple else TextSecondary,
                                    fontWeight = if (issuedQty > 0) FontWeight.Bold else FontWeight.Normal
                                )
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
private fun EmpCol(
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
