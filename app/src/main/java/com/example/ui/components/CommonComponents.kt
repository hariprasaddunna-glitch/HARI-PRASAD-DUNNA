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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SyncStatus
import com.example.ui.theme.IndustrialBlue
import com.example.ui.theme.IndustrialNavy
import com.example.ui.theme.IndustrialNavyDark
import com.example.ui.theme.IndustrialSlate
import com.example.ui.theme.SleekActionCard
import com.example.ui.theme.SleekDivider
import com.example.ui.theme.SleekGreenDot
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekPurpleLight
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.StatusDamageBg
import com.example.ui.theme.StatusDamageText
import com.example.ui.theme.StatusGoodBg
import com.example.ui.theme.StatusGoodText
import com.example.ui.theme.StatusIssuedBg
import com.example.ui.theme.StatusIssuedText
import com.example.ui.theme.StatusMutedBg
import com.example.ui.theme.StatusMutedText
import com.example.ui.theme.StatusRepairBg
import com.example.ui.theme.StatusRepairText
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StatusPill(status: String, modifier: Modifier = Modifier) {
    val (bg, text) = when (status) {
        "Good", "Ready for Reissue" -> StatusGoodBg to StatusGoodText
        "Issued" -> StatusIssuedBg to StatusIssuedText
        "Repair", "Under Repair" -> StatusRepairBg to StatusRepairText
        "Damage/Lost", "Not available for Issue", "Damage", "Lost" -> StatusDamageBg to StatusDamageText
        else -> StatusMutedBg to StatusMutedText
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            color = text,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun CalibrationPill(calibStatus: String, modifier: Modifier = Modifier) {
    val (bg, text) = when (calibStatus) {
        "OK" -> StatusGoodBg to StatusGoodText
        "Due Soon" -> StatusRepairBg to StatusRepairText
        "Overdue" -> StatusDamageBg to StatusDamageText
        else -> StatusMutedBg to StatusMutedText
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = calibStatus,
            color = text,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun StatCard(
    count: String,
    label: String,
    modifier: Modifier = Modifier,
    accentColor: Color = TextPrimary,
    containerColor: Color = SurfaceLight
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = count,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                letterSpacing = 0.2.sp
            )
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        androidx.compose.foundation.BorderStroke(0.dp, Color.Transparent)
                    )
                    .background(SurfaceLight)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                action?.invoke()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SurfaceBorder.copy(alpha = 0.6f))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SearchInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(text = placeholder, fontSize = 13.sp, color = TextSecondary)
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
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
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    )
}

@Composable
fun SyncStatusBadge(
    status: SyncStatus,
    lastSyncTime: String?,
    modifier: Modifier = Modifier,
    isDarkBg: Boolean = false
) {
    val (dotColor, text, icon) = when (status) {
        SyncStatus.CONNECTED -> Triple(SleekGreenDot, "Synced to Microsoft Lists", Icons.Default.CloudDone)
        SyncStatus.SYNCING -> Triple(Color(0xFFE8C46B), "Syncing to Cloud...", Icons.Default.CloudSync)
        SyncStatus.OFFLINE -> Triple(Color(0xFFE8C46B), "Offline (Cached copy)", Icons.Default.CloudOff)
        SyncStatus.ERROR -> Triple(Color(0xFFF0938C), "Sync error (Saved locally)", Icons.Default.Cloud)
        SyncStatus.LOCAL -> Triple(if (isDarkBg) Color(0x88FFFFFF) else Color(0x88000000), "Local Storage Mode", Icons.Default.CloudOff)
    }

    val primaryTextColor = if (isDarkBg) Color(0xEEFFFFFF) else TextPrimary
    val secondaryTextColor = if (isDarkBg) Color(0x99FFFFFF) else TextSecondary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = text,
                fontSize = 11.5.sp,
                color = primaryTextColor,
                fontWeight = FontWeight.Medium
            )
            if (!lastSyncTime.isNullOrBlank() && status == SyncStatus.CONNECTED) {
                Text(
                    text = "Last sync: $lastSyncTime",
                    fontSize = 10.sp,
                    color = secondaryTextColor
                )
            }
        }
    }
}
