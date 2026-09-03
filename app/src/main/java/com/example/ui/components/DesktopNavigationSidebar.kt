package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentReturned
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Input
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SyncStatus
import com.example.ui.AppScreen
import com.example.ui.UserRole
import com.example.ui.theme.SleekActionCard
import com.example.ui.theme.SleekBlue
import com.example.ui.theme.SleekBlueLight
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekPurpleDark
import com.example.ui.theme.SleekPurpleLight
import com.example.ui.theme.SleekRed
import com.example.ui.theme.SleekRedLight
import com.example.ui.theme.SleekSurfaceVariant
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DesktopNavigationSidebar(
    currentScreen: AppScreen,
    userRole: UserRole,
    syncStatus: SyncStatus,
    lastSyncTime: String?,
    pendingReturnsCount: Int,
    calibrationAlertCount: Int,
    isCollapsed: Boolean = false,
    onToggleCollapse: () -> Unit = {},
    onSelectScreen: (AppScreen) -> Unit,
    onToggleRole: () -> Unit,
    onOpenCloudSettings: () -> Unit,
    onManualSync: () -> Unit,
    onResetSampleData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sidebarWidth = if (isCollapsed) 72.dp else 260.dp

    Column(
        modifier = modifier
            .width(sidebarWidth)
            .fillMaxHeight()
            .background(SleekSurfaceVariant)
            .border(
                width = 1.dp,
                color = SurfaceBorder,
                shape = RoundedCornerShape(0.dp)
            )
            .padding(if (isCollapsed) 10.dp else 16.dp),
        horizontalAlignment = if (isCollapsed) Alignment.CenterHorizontally else Alignment.Start
    ) {
        // Brand Header (Sleek Interface style)
        if (!isCollapsed) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SleekPurpleLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Handyman,
                            contentDescription = null,
                            tint = SleekPurpleDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "TOOLSYNC PRO",
                            color = SleekPurple,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Tools & Equipment",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pipe & Outfitting",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = onToggleCollapse,
                    modifier = Modifier.size(32.dp).testTag("btn_collapse_sidebar")
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Collapse sidebar",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Role Selector Banner (Admin / Operator)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SleekActionCard)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                    .clickable { onToggleRole() }
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = SleekPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (userRole == UserRole.ADMIN) "Admin Mode" else "Operator Mode",
                        color = TextPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "SWITCH",
                    color = SleekPurple,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        } else {
            // Collapsed Header
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SleekPurpleLight)
                    .clickable { onToggleCollapse() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Expand sidebar",
                    tint = SleekPurpleDark,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (userRole == UserRole.ADMIN) SleekPurpleLight else SleekActionCard)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                    .clickable { onToggleRole() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Toggle Role",
                    tint = SleekPurple,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Items
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = if (isCollapsed) Alignment.CenterHorizontally else Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            NavItem(
                label = AppScreen.DASHBOARD.label,
                icon = Icons.Default.Dashboard,
                isActive = currentScreen == AppScreen.DASHBOARD,
                isCollapsed = isCollapsed,
                onClick = { onSelectScreen(AppScreen.DASHBOARD) },
                testTag = "nav_dashboard"
            )

            NavItem(
                label = AppScreen.INWARD.label,
                icon = Icons.Default.Input,
                isActive = currentScreen == AppScreen.INWARD,
                isCollapsed = isCollapsed,
                onClick = { onSelectScreen(AppScreen.INWARD) },
                testTag = "nav_inward"
            )

            NavItem(
                label = AppScreen.OUTWARD.label,
                icon = Icons.Default.Output,
                isActive = currentScreen == AppScreen.OUTWARD,
                isCollapsed = isCollapsed,
                onClick = { onSelectScreen(AppScreen.OUTWARD) },
                testTag = "nav_outward"
            )

            NavItem(
                label = AppScreen.RETURN.label,
                icon = Icons.Default.AssignmentReturned,
                isActive = currentScreen == AppScreen.RETURN,
                isCollapsed = isCollapsed,
                badgeCount = pendingReturnsCount,
                badgeBg = SleekBlueLight,
                badgeTextColor = SleekBlue,
                onClick = { onSelectScreen(AppScreen.RETURN) },
                testTag = "nav_return"
            )

            NavItem(
                label = AppScreen.CALIBRATION.label,
                icon = Icons.Default.Speed,
                isActive = currentScreen == AppScreen.CALIBRATION,
                isCollapsed = isCollapsed,
                badgeCount = calibrationAlertCount,
                badgeBg = if (calibrationAlertCount > 0) SleekRedLight else null,
                badgeTextColor = if (calibrationAlertCount > 0) SleekRed else null,
                onClick = { onSelectScreen(AppScreen.CALIBRATION) },
                testTag = "nav_calibration"
            )

            NavItem(
                label = AppScreen.EMPLOYEES.label,
                icon = Icons.Default.Group,
                isActive = currentScreen == AppScreen.EMPLOYEES,
                isCollapsed = isCollapsed,
                onClick = { onSelectScreen(AppScreen.EMPLOYEES) },
                testTag = "nav_employees"
            )
        }

        // Footer Section: Cloud Sync & Controls
        HorizontalDivider(
            color = SurfaceBorder,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (!isCollapsed) {
            SyncStatusBadge(status = syncStatus, lastSyncTime = lastSyncTime, isDarkBg = false)

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onOpenCloudSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_cloud_settings"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SleekActionCard,
                    contentColor = TextPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudQueue,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = SleekPurple
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Cloud connection", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onManualSync,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_manual_sync"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SleekPurple,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Sync now", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            if (userRole == UserRole.ADMIN) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onResetSampleData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_reset_sample"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Reset sample data", fontSize = 11.5.sp)
                }
            }
        } else {
            // Collapsed footer icons
            IconButton(
                onClick = onOpenCloudSettings,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SleekActionCard)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                    .testTag("btn_cloud_settings")
            ) {
                Icon(
                    imageVector = Icons.Default.CloudQueue,
                    contentDescription = "Cloud connection",
                    tint = SleekPurple,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            IconButton(
                onClick = onManualSync,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SleekPurple)
                    .testTag("btn_manual_sync")
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Sync now",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (userRole == UserRole.ADMIN) {
                Spacer(modifier = Modifier.height(8.dp))
                IconButton(
                    onClick = onResetSampleData,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekActionCard)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                        .testTag("btn_reset_sample")
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset sample data",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCollapsed: Boolean = false,
    badgeCount: Int = 0,
    badgeBg: Color? = null,
    badgeTextColor: Color? = null,
    testTag: String = ""
) {
    val bg = if (isActive) SleekPurpleLight else Color.Transparent
    val contentColor = if (isActive) SleekPurpleDark else TextSecondary

    if (!isCollapsed) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(99.dp))
                .background(bg)
                .clickable { onClick() }
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag(testTag),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    color = contentColor,
                    fontSize = 13.5.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
                )
            }

            if (badgeCount > 0 && badgeBg != null && badgeTextColor != null) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(badgeBg)
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = badgeTextColor,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    } else {
        Box(
            modifier = modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bg)
                .clickable { onClick() }
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )

            if (badgeCount > 0 && badgeBg != null && badgeTextColor != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(badgeTextColor)
                )
            }
        }
    }
}
