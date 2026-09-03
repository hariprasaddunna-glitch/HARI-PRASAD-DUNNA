package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.SleekPurple
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

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = SleekPurple,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "Connect to Power Automate & Microsoft Lists",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Paste the HTTP POST URL of your Power Automate flow with the 'When an HTTP request is received' trigger. This single flow syncs with your 4 Microsoft Lists (Stock Inward, Stock Outward, Employees, Calibration) and automates email/Teams notifications seamlessly.",
                    fontSize = 12.5.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "FLOW HTTP POST URL",
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

                // Guide Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SleekSurfaceVariant, RoundedCornerShape(14.dp))
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = SleekPurple,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = "Automated Flow Actions Supported",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SleekPurple
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• getAll: Loads all 4 Microsoft Lists into local high-speed cache\n" +
                                    "• addInward: New tool receipt recorded to Stock Inward list\n" +
                                    "• addOutward: Tool issue recorded to Stock Outward list\n" +
                                    "• updateOutward: Tool return logged and condition updated\n" +
                                    "• addCalibration & addEmployee: Live records synced",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentUrl.isNotBlank()) {
                        OutlinedButton(
                            onClick = onDisconnect,
                            shape = RoundedCornerShape(14.dp),
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
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("btn_cancel_flow_dialog")
                    ) {
                        Text(text = "Cancel", fontSize = 12.sp, color = TextPrimary)
                    }

                    Button(
                        onClick = { onSaveAndSync(currentUrl) },
                        colors = ButtonDefaults.buttonColors(containerColor = SleekPurple),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("btn_save_sync_flow")
                    ) {
                        Text(
                            text = "Connect & Sync",
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
