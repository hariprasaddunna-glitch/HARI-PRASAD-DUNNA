package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekPurpleLight
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.StatusDamageText
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CloudSyncDialog(
    isOpen: Boolean,
    currentUrl: String,
    onUrlChange: (String) -> Unit,
    onSaveAndSync: (String) -> Unit,
    onDisconnect: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var copiedNotice by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .height(600.dp)
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SleekPurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = null,
                                tint = SleekPurple,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Power Automate & Microsoft Lists Setup",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Enterprise Cloud Sync & Automated Workflows",
                                fontSize = 11.5.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    if (copiedNotice != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE6F4EA))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = SleekGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(copiedNotice!!, fontSize = 11.sp, color = SleekGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SleekSurfaceVariant,
                    contentColor = SleekPurple,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = SleekPurple
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Link, null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Connection", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FormatListBulleted, null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Step-by-Step Guide", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Code, null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("JSON Schemas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Content Area (Scrollable)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        0 -> ConnectionTabContent(
                            currentUrl = currentUrl,
                            onUrlChange = onUrlChange
                        )
                        1 -> StepByStepGuideTabContent(
                            onCopyText = { label, text ->
                                clipboardManager.setText(AnnotatedString(text))
                                copiedNotice = "$label Copied!"
                            }
                        )
                        2 -> JsonSchemasTabContent(
                            onCopyText = { label, text ->
                                clipboardManager.setText(AnnotatedString(text))
                                copiedNotice = "$label Copied!"
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SurfaceBorder, thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentUrl.isNotBlank()) {
                        OutlinedButton(
                            onClick = onDisconnect,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusDamageText),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF8E7E5)),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("btn_disconnect_flow")
                        ) {
                            Text(text = "Disconnect", fontSize = 12.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("btn_cancel_flow_dialog")
                    ) {
                        Text(text = "Close", fontSize = 12.sp, color = TextPrimary)
                    }

                    Button(
                        onClick = { onSaveAndSync(currentUrl) },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_save_sync_flow")
                    ) {
                        Text(
                            text = if (currentUrl.isBlank()) "Save" else "Save & Sync Now",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectionTabContent(
    currentUrl: String,
    onUrlChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "FLOW HTTP POST TRIGGER URL",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = currentUrl,
            onValueChange = onUrlChange,
            placeholder = {
                Text(
                    text = "https://prod-XX.westus.logic.azure.com/workflows/...",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = SleekSurfaceVariant,
                focusedBorderColor = SleekPurple,
                unfocusedBorderColor = SurfaceBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_flow_url")
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SleekSurfaceVariant)
                .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SleekPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "How the Connection Works",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = SleekPurple
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "1. ToolSync Pro functions offline-first using its high-speed Room SQLite local database.\n" +
                            "2. Whenever you submit an Inward, Outward, Return, Employee, or Calibration, the app automatically emits an asynchronous HTTP POST payload to this Power Automate flow.\n" +
                            "3. Clicking 'Sync now' triggers 'getAll' to download the latest state from your 4 Microsoft Lists.\n" +
                            "4. Multiple desktop operators share the same live Microsoft 365 SharePoint backend.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun StepByStepGuideTabContent(
    onCopyText: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GuideStepCard(
            stepNumber = "1",
            title = "Create the 4 Microsoft Lists",
            description = "In Microsoft 365 SharePoint or Microsoft Lists, create 4 lists with the exact names below:",
            content = {
                Text(
                    text = "• StockInward: Title (or ItemCode), AssetNo, PONo, SupplierName, MaterialCode, Description, Size, Brand, Model, Serial, ReceivedDate, UOM, ReceivedQty (Number), Location, Type\n" +
                            "• StockOutward: Title (or ItemCode), Desc, AssetNo, MaterialCode, Size, Brand, Model, Serial, UOM, IssuedDate, Qty (Number), EmpID, EmpName, EmpPosition, EmpContact, WorkLocation, ReturnDate, ReturnCondition, Status, Remarks, ReceivedBy\n" +
                            "• Employees: Title (or EmpID), Name, Position, Contact\n" +
                            "• Calibration: Title (or ItemCode), Description, CalibrationDate, NextCalibrationDate, CalibratedBy, CertificateNo, Remarks",
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        )

        GuideStepCard(
            stepNumber = "2",
            title = "Create an Automated Cloud Flow in Power Automate",
            description = "Go to make.powerautomate.com > Create > Instant Cloud Flow > Trigger: 'When an HTTP request is received'. Set 'Who can trigger the flow' to 'Anyone' (or Organization).",
            content = {
                Text(
                    text = "In 'Request Body JSON Schema', switch to the 'JSON Schemas' tab in this dialog and copy the trigger schema.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        )

        GuideStepCard(
            stepNumber = "3",
            title = "Add a 'Switch' Action on body('action')",
            description = "Add a Control > Switch step evaluating the 'action' property from the HTTP request body:",
            content = {
                Text(
                    text = "• Case 'getAll': Add 4 'Get items' actions (one for each list), then add a 'Response' (Status 200) returning JSON with ok: true and data: { stockInward, stockOutward, employees, calibration }.\n" +
                            "• Case 'addInward': Add 'Create item' in StockInward list using triggerBody()?['row'] properties.\n" +
                            "• Case 'addOutward': Add 'Create item' in StockOutward list using triggerBody()?['row'] properties.\n" +
                            "• Case 'updateOutward': Add 'Get items' (filter by ItemCode eq '@{triggerBody()?['row']?['ItemCode']}') and 'Update item' setting ReturnDate, ReturnCondition, Status, ReceivedBy.\n" +
                            "• Case 'addEmployee': Add 'Create item' in Employees list.\n" +
                            "• Case 'addCalibration': Add 'Create item' in Calibration list.",
                    fontSize = 11.5.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        )

        GuideStepCard(
            stepNumber = "4",
            title = "Add Response Action & Copy HTTP POST URL",
            description = "Add a 'Response' action at the end of each branch with Status Code 200. Save the flow, then copy the generated 'HTTP POST URL' and paste it into the Connection tab!",
            content = null
        )
    }
}

@Composable
private fun GuideStepCard(
    stepNumber: String,
    title: String,
    description: String,
    content: (@Composable () -> Unit)?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SleekSurfaceVariant)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(SleekPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = stepNumber, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = description, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)

            if (content != null) {
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}

@Composable
private fun JsonSchemasTabContent(
    onCopyText: (String, String) -> Unit
) {
    val triggerSchema = """{
  "type": "object",
  "properties": {
    "action": { "type": "string" },
    "row": { "type": "object" }
  },
  "required": ["action"]
}"""

    val getAllResponseSample = """{
  "ok": true,
  "data": {
    "stockInward": [
      {
        "Type": "Pipe",
        "ItemCode": "PIPE-CS-001",
        "AssetNo": "AST-8821",
        "PONo": "PO-2026-091",
        "SupplierName": "Vallourec Tubes",
        "MaterialCode": "CS-A106-GRB",
        "Description": "6\" Carbon Steel Seamless Pipe",
        "Size": "6\" Sch 40",
        "Brand": "Vallourec",
        "Model": "A106",
        "Serial": "SN-88219",
        "ReceivedDate": "2026-08-15",
        "UOM": "Mtrs",
        "ReceivedQty": 120,
        "Location": "Rack B-01"
      }
    ],
    "stockOutward": [],
    "employees": [],
    "calibration": []
  }
}"""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Trigger Schema Card
        SchemaCard(
            title = "HTTP Request Body JSON Schema (Power Automate Trigger)",
            subtitle = "Paste this in the 'Request Body JSON Schema' field of 'When an HTTP request is received':",
            code = triggerSchema,
            onCopy = { onCopyText("Trigger Schema", triggerSchema) }
        )

        // getAll Response Sample Card
        SchemaCard(
            title = "Expected Response JSON for 'getAll' Action",
            subtitle = "In the 'Response' action of the 'getAll' switch case, configure this JSON body format:",
            code = getAllResponseSample,
            onCopy = { onCopyText("getAll Response", getAllResponseSample) }
        )
    }
}

@Composable
private fun SchemaCard(
    title: String,
    subtitle: String,
    code: String,
    onCopy: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SleekSurfaceVariant)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                OutlinedButton(
                    onClick = onCopy,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SleekPurple),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 11.5.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
                    .padding(10.dp)
            ) {
                Text(
                    text = code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
