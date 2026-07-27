package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.AdSettingsConfig
import com.example.model.PublicUserProfile
import com.example.model.SystemNotice
import com.example.model.VideoCampaign
import com.example.viewmodel.MarketingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManagementScreen(viewModel: MarketingViewModel) {
    var selectedSectionTab by remember { mutableIntStateOf(0) } // 0: Users, 1: Points & Tasks, 2: Ad Control, 3: Notice Board

    val registeredUsers by viewModel.registeredUsers.collectAsState()
    val systemNotices by viewModel.systemNotices.collectAsState()
    val campaigns by viewModel.campaigns.collectAsState()
    val rewardPoints by viewModel.taskRewardPoints.collectAsState()
    val costPoints by viewModel.taskCostPoints.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Section Title Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "ইউজার, অ্যাড ও নোটিশ মেইনটেনেন্স",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "এডমিন প্যানেল হতে ইউজার, পয়েন্ট, অ্যাড কন্ট্রোল ও নোটিশ পরিচালনা করুন",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedSectionTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ) {
            Tab(
                selected = selectedSectionTab == 0,
                onClick = { selectedSectionTab = 0 },
                text = { Text("ইউজার", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall) },
                icon = { Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("admin_tab_users")
            )
            Tab(
                selected = selectedSectionTab == 1,
                onClick = { selectedSectionTab = 1 },
                text = { Text("টাস্ক ও পয়েন্ট", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall) },
                icon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("admin_tab_points_tasks")
            )
            Tab(
                selected = selectedSectionTab == 2,
                onClick = { selectedSectionTab = 2 },
                text = { Text("অ্যাড কন্ট্রোল", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall) },
                icon = { Icon(Icons.Default.AdsClick, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("admin_tab_ad_control")
            )
            Tab(
                selected = selectedSectionTab == 3,
                onClick = { selectedSectionTab = 3 },
                text = { Text("নোটিশ বোর্ড", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall) },
                icon = { Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("admin_tab_notices")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedSectionTab) {
            0 -> UserManagementTab(registeredUsers = registeredUsers, viewModel = viewModel)
            1 -> PointsAndTaskMaintenanceTab(
                campaigns = campaigns,
                rewardPoints = rewardPoints,
                costPoints = costPoints,
                viewModel = viewModel
            )
            2 -> AdControlMaintenanceTab(viewModel = viewModel)
            3 -> NoticeBoardMaintenanceTab(systemNotices = systemNotices, viewModel = viewModel)
        }
    }
}

// ----------------------------------------------------
// TAB 1: USER MAINTENANCE
// ----------------------------------------------------
@Composable
fun UserManagementTab(
    registeredUsers: List<PublicUserProfile>,
    viewModel: MarketingViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var userToEdit by remember { mutableStateOf<PublicUserProfile?>(null) }

    val filteredUsers = registeredUsers.filter { user ->
        user.fullName.contains(searchQuery, ignoreCase = true) ||
                user.mobileNumber.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("ইউজার খুঁজুন (নাম / ফোন নম্বর)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("admin_user_search_input")
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "মোট নিবন্ধিত ইউজার: ${filteredUsers.size} জন",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (filteredUsers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("কোনো ইউজার খুঁজে পাওয়া যায়নি।")
                    }
                }
            }
        } else {
            items(filteredUsers) { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(8.dp).size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = user.fullName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "📱 ${user.mobileNumber}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                color = if (user.isVerified) Color(0xFF2E7D32) else Color(0xFFE53935),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (user.isVerified) "ভেরিফাইড" else "আনভেরিফাইড",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = Color(0xFFFF9800),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "পয়েন্ট ব্যালেন্স: ${user.points} pt",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { userToEdit = user },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("edit_user_btn_${user.mobileNumber}")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("এডিট", style = MaterialTheme.typography.labelSmall)
                                }

                                IconButton(
                                    onClick = { viewModel.deleteUserByAdmin(user.mobileNumber) },
                                    modifier = Modifier.testTag("delete_user_btn_${user.mobileNumber}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }

    if (userToEdit != null) {
        val targetUser = userToEdit!!
        var editPointsText by remember { mutableStateOf(targetUser.points.toString()) }
        var isVerifiedState by remember { mutableStateOf(targetUser.isVerified) }
        var newPassText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { userToEdit = null },
            title = { Text("ইউজার প্রোফাইল টিউন করুন: ${targetUser.fullName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editPointsText,
                        onValueChange = { editPointsText = it },
                        label = { Text("পয়েন্ট সংখ্যা") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_user_points_input")
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isVerifiedState,
                            onCheckedChange = { isVerifiedState = it },
                            modifier = Modifier.testTag("admin_user_verify_checkbox")
                        )
                        Text("ইউজার ভেরিফাইড (Verified) স্ট্যাটাস")
                    }

                    OutlinedTextField(
                        value = newPassText,
                        onValueChange = { newPassText = it },
                        label = { Text("নতুন পাসওয়ার্ড (পরিবর্তন করতে চাইলে লিখুন)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_user_newpass_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pts = editPointsText.toIntOrNull() ?: targetUser.points
                        viewModel.updateUserProfileByAdmin(
                            mobileNumber = targetUser.mobileNumber,
                            newPoints = pts,
                            isVerified = isVerifiedState,
                            newPassword = if (newPassText.isNotBlank()) newPassText else null
                        )
                        userToEdit = null
                    },
                    modifier = Modifier.testTag("save_user_edit_btn")
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { userToEdit = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

// ----------------------------------------------------
// TAB 2: POINTS & TASK TIME MAINTENANCE
// ----------------------------------------------------
@Composable
fun PointsAndTaskMaintenanceTab(
    campaigns: List<VideoCampaign>,
    rewardPoints: Int,
    costPoints: Int,
    viewModel: MarketingViewModel
) {
    var editRewardText by remember { mutableStateOf(rewardPoints.toString()) }
    var editCostText by remember { mutableStateOf(costPoints.toString()) }
    var campaignToEdit by remember { mutableStateOf<VideoCampaign?>(null) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Point Rates Configuration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "সিস্টেম পয়েন্ট রেট মেইনটেনেন্স",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editRewardText,
                            onValueChange = { editRewardText = it },
                            label = { Text("টাস্ক বোনাস পয়েন্ট") },
                            modifier = Modifier.weight(1f).testTag("admin_reward_pts_input")
                        )

                        OutlinedTextField(
                            value = editCostText,
                            onValueChange = { editCostText = it },
                            label = { Text("বুস্ট চার্জ পয়েন্ট") },
                            modifier = Modifier.weight(1f).testTag("admin_cost_pts_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            val r = editRewardText.toIntOrNull() ?: rewardPoints
                            val c = editCostText.toIntOrNull() ?: costPoints
                            viewModel.updateTaskPointsConfig(r, c)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_point_config_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("পয়েন্ট রেট আপডেট করুন")
                    }
                }
            }
        }

        // Campaign & Task Time Maintenance Header
        item {
            Text(
                text = "ভিডিও টাস্কগুলোর সময় ও রিফ্রেশ ইন্টারভাল মেনটেনেন্স",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(campaigns) { campaign ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (campaign.isShorts) Icons.Default.SmartDisplay else Icons.Default.Movie,
                                contentDescription = null,
                                tint = if (campaign.isShorts) Color(0xFFE53935) else Color(0xFF1E88E5)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = campaign.videoTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Switch(
                            checked = campaign.isRunning,
                            onCheckedChange = { viewModel.toggleCampaignRunning(campaign.id) },
                            modifier = Modifier.testTag("admin_campaign_switch_${campaign.id}")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "⏱ ওয়াচ টাইম টার্গেট: ${campaign.targetWatchTimeMinutes} মিনিট",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "🔄 রিফ্রেশ ইন্টারভাল: ${campaign.autoRefreshIntervalSec} সে.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { campaignToEdit = campaign },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("edit_task_time_btn_${campaign.id}")
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("সময় ও সেটিংস টিউন", style = MaterialTheme.typography.labelSmall)
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { viewModel.deleteCampaign(campaign.id) },
                            modifier = Modifier.testTag("delete_task_btn_${campaign.id}")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }

    if (campaignToEdit != null) {
        val targetCam = campaignToEdit!!
        var watchTimeText by remember { mutableStateOf(targetCam.targetWatchTimeMinutes.toString()) }
        var refreshIntervalText by remember { mutableStateOf(targetCam.autoRefreshIntervalSec.toString()) }
        var targetViewsText by remember { mutableStateOf(targetCam.targetViews.toString()) }

        AlertDialog(
            onDismissRequest = { campaignToEdit = null },
            title = { Text("টাস্ক সময় ও সেটিংস এডিট: ${targetCam.videoTitle}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = watchTimeText,
                        onValueChange = { watchTimeText = it },
                        label = { Text("টার্গেট ওয়াচ টাইম (মিনিট)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_task_watchtime_input")
                    )

                    OutlinedTextField(
                        value = refreshIntervalText,
                        onValueChange = { refreshIntervalText = it },
                        label = { Text("অটো রিফ্রেশ সময় (সেকেন্ড)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_task_refresh_input")
                    )

                    OutlinedTextField(
                        value = targetViewsText,
                        onValueChange = { targetViewsText = it },
                        label = { Text("টার্গেট ভিউ সংখ্যা") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_task_targetviews_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val wt = watchTimeText.toIntOrNull() ?: targetCam.targetWatchTimeMinutes
                        val ref = refreshIntervalText.toIntOrNull() ?: targetCam.autoRefreshIntervalSec
                        val views = targetViewsText.toIntOrNull() ?: targetCam.targetViews

                        viewModel.updateCampaignTimeAndSettings(
                            campaignId = targetCam.id,
                            targetWatchTimeMinutes = wt,
                            autoRefreshSec = ref,
                            targetViews = views,
                            isRunning = targetCam.isRunning
                        )
                        campaignToEdit = null
                    },
                    modifier = Modifier.testTag("save_task_edit_btn")
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { campaignToEdit = null }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

// ----------------------------------------------------
// TAB 3: NOTICE BOARD MAINTENANCE
// ----------------------------------------------------
@Composable
fun NoticeBoardMaintenanceTab(
    systemNotices: List<SystemNotice>,
    viewModel: MarketingViewModel
) {
    var noticeTitleInput by remember { mutableStateOf("") }
    var noticeContentInput by remember { mutableStateOf("") }
    var isImportantToggle by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Create Notice Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Campaign,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "নতুন নোটিশ বোর্ডে প্রকাশ করুন",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedTextField(
                        value = noticeTitleInput,
                        onValueChange = { noticeTitleInput = it },
                        label = { Text("নোটিশ শিরোনাম") },
                        placeholder = { Text("যেমন: 🎉 বিশেষ অফার বা আপডেট নোটিশ") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_notice_title_input")
                    )

                    OutlinedTextField(
                        value = noticeContentInput,
                        onValueChange = { noticeContentInput = it },
                        label = { Text("নোটিশ বিবরণ") },
                        placeholder = { Text("ইউজারদের জানানো নোটিশ বার্তা লিখুন...") },
                        modifier = Modifier.fillMaxWidth().height(90.dp).testTag("admin_notice_content_input")
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = isImportantToggle,
                            onCheckedChange = { isImportantToggle = it },
                            modifier = Modifier.testTag("admin_notice_important_checkbox")
                        )
                        Text("গুরুত্বপূর্ণ নোটিশ (Important Red Tag)")
                    }

                    Button(
                        onClick = {
                            if (noticeTitleInput.isNotBlank() && noticeContentInput.isNotBlank()) {
                                viewModel.addSystemNotice(
                                    title = noticeTitleInput,
                                    content = noticeContentInput,
                                    isImportant = isImportantToggle
                                )
                                noticeTitleInput = ""
                                noticeContentInput = ""
                                isImportantToggle = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("publish_notice_btn")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("নোটিশ বোর্ড এ ব্রডকাস্ট করুন")
                    }
                }
            }
        }

        item {
            Text(
                text = "প্রকাশিত নোটিশ সমূহের তালিকা (${systemNotices.size} টি)",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (systemNotices.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(24.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("বর্তমানে কোনো নোটিশ নেই।")
                    }
                }
            }
        } else {
            items(systemNotices) { notice ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (notice.isImportant) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (notice.isImportant) Icons.Default.PriorityHigh else Icons.Default.Announcement,
                                    contentDescription = null,
                                    tint = if (notice.isImportant) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = notice.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = { viewModel.deleteSystemNotice(notice.id) },
                                modifier = Modifier.testTag("delete_notice_btn_${notice.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Notice", tint = MaterialTheme.colorScheme.error)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = notice.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "প্রকাশের সময়: ${notice.timestamp}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "লেখক: ${notice.author}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

// ----------------------------------------------------
// TAB 3: AD CONTROL SETTINGS MAINTENANCE
// ----------------------------------------------------
@Composable
fun AdControlMaintenanceTab(viewModel: MarketingViewModel) {
    val currentAdConfig by viewModel.adSettingsConfig.collectAsState()

    var appIdInput by remember(currentAdConfig) { mutableStateOf(currentAdConfig.appId) }
    var isMasterEnabled by remember(currentAdConfig) { mutableStateOf(currentAdConfig.isAdMobMasterEnabled) }

    var bannerUnitInput by remember(currentAdConfig) { mutableStateOf(currentAdConfig.bannerAdUnitId) }
    var isBannerEnabled by remember(currentAdConfig) { mutableStateOf(currentAdConfig.isBannerAdEnabled) }

    var interstitialUnitInput by remember(currentAdConfig) { mutableStateOf(currentAdConfig.interstitialAdUnitId) }
    var isInterstitialEnabled by remember(currentAdConfig) { mutableStateOf(currentAdConfig.isInterstitialAdEnabled) }

    var rewardedUnitInput by remember(currentAdConfig) { mutableStateOf(currentAdConfig.rewardedAdUnitId) }
    var isRewardedEnabled by remember(currentAdConfig) { mutableStateOf(currentAdConfig.isRewardedAdEnabled) }

    var nativeUnitInput by remember(currentAdConfig) { mutableStateOf(currentAdConfig.nativeAdUnitId) }
    var isNativeEnabled by remember(currentAdConfig) { mutableStateOf(currentAdConfig.isNativeAdEnabled) }

    var customTitleInput by remember(currentAdConfig) { mutableStateOf(currentAdConfig.customBannerTitle) }
    var customTargetUrlInput by remember(currentAdConfig) { mutableStateOf(currentAdConfig.customBannerTargetUrl) }
    var isCustomBannerEnabled by remember(currentAdConfig) { mutableStateOf(currentAdConfig.isCustomBannerEnabled) }

    var adRewardPtsInput by remember(currentAdConfig) { mutableStateOf(currentAdConfig.adRewardPoints.toString()) }
    var adIntervalSecInput by remember(currentAdConfig) { mutableStateOf(currentAdConfig.adAutoShowIntervalSec.toString()) }

    var showSavedMessage by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Info & Save Action Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AdsClick,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "অ্যাড কন্ট্রোল সেটিংস ড্যাশবোর্ড",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "অ্যাপের সকল অ্যাড টাইপ ও অ্যাড ইউনিট আইডি কন্ট্রোল করুন",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val pts = adRewardPtsInput.toIntOrNull() ?: currentAdConfig.adRewardPoints
                            val interval = adIntervalSecInput.toIntOrNull() ?: currentAdConfig.adAutoShowIntervalSec

                            val newConfig = currentAdConfig.copy(
                                appId = appIdInput.trim(),
                                isAdMobMasterEnabled = isMasterEnabled,
                                bannerAdUnitId = bannerUnitInput.trim(),
                                isBannerAdEnabled = isBannerEnabled,
                                interstitialAdUnitId = interstitialUnitInput.trim(),
                                isInterstitialAdEnabled = isInterstitialEnabled,
                                rewardedAdUnitId = rewardedUnitInput.trim(),
                                isRewardedAdEnabled = isRewardedEnabled,
                                nativeAdUnitId = nativeUnitInput.trim(),
                                isNativeAdEnabled = isNativeEnabled,
                                customBannerTitle = customTitleInput.trim(),
                                customBannerTargetUrl = customTargetUrlInput.trim(),
                                isCustomBannerEnabled = isCustomBannerEnabled,
                                adRewardPoints = pts,
                                adAutoShowIntervalSec = interval
                            )
                            viewModel.updateAdSettingsConfig(newConfig)
                            showSavedMessage = true
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_all_ad_settings_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("সকল অ্যাড সেটিংস সেভ করুন (Save All Settings)", fontWeight = FontWeight.Bold)
                    }

                    if (showSavedMessage) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFF2E7D32),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✅ অ্যাড সেটিংস সফলভাবে আপডেট ও অ্যাপজুড়ে সক্রিয় হয়েছে!",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp)
                            )
                        }
                    }
                }
            }
        }

        // 1. Master AdMob Switch & App ID
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AppSettingsAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("গুগল অ্যাডমব নেটওয়ার্ক (AdMob Master Switch)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Switch(
                            checked = isMasterEnabled,
                            onCheckedChange = { isMasterEnabled = it },
                            modifier = Modifier.testTag("admob_master_switch")
                        )
                    }

                    OutlinedTextField(
                        value = appIdInput,
                        onValueChange = { appIdInput = it },
                        label = { Text("AdMob App ID (ca-app-pub-xxx~xxx)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admob_app_id_input")
                    )
                }
            }
        }

        // 2. Banner Ad Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ViewStream, contentDescription = null, tint = Color(0xFF1976D2))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ব্যানার অ্যাড (Banner Ad Unit)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Switch(
                            checked = isBannerEnabled,
                            onCheckedChange = { isBannerEnabled = it },
                            modifier = Modifier.testTag("banner_ad_switch")
                        )
                    }

                    OutlinedTextField(
                        value = bannerUnitInput,
                        onValueChange = { bannerUnitInput = it },
                        label = { Text("Banner Ad Unit ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("banner_unit_id_input")
                    )
                }
            }
        }

        // 3. Interstitial Ad Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Fullscreen, contentDescription = null, tint = Color(0xFFD32F2F))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ইন্টারস্টিশিয়াল অ্যাড (Interstitial Ad Unit)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Switch(
                            checked = isInterstitialEnabled,
                            onCheckedChange = { isInterstitialEnabled = it },
                            modifier = Modifier.testTag("interstitial_ad_switch")
                        )
                    }

                    OutlinedTextField(
                        value = interstitialUnitInput,
                        onValueChange = { interstitialUnitInput = it },
                        label = { Text("Interstitial Ad Unit ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("interstitial_unit_id_input")
                    )
                }
            }
        }

        // 4. Rewarded Video Ad Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFF388E3C))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("রিওয়ার্ডেড ভিডিও অ্যাড (Rewarded Video Ad)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Switch(
                            checked = isRewardedEnabled,
                            onCheckedChange = { isRewardedEnabled = it },
                            modifier = Modifier.testTag("rewarded_ad_switch")
                        )
                    }

                    OutlinedTextField(
                        value = rewardedUnitInput,
                        onValueChange = { rewardedUnitInput = it },
                        label = { Text("Rewarded Ad Unit ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("rewarded_unit_id_input")
                    )
                }
            }
        }

        // 5. Native Ad Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Web, contentDescription = null, tint = Color(0xFF7B1FA2))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("নেটিভ অ্যাড (Native Advanced Ad Unit)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Switch(
                            checked = isNativeEnabled,
                            onCheckedChange = { isNativeEnabled = it },
                            modifier = Modifier.testTag("native_ad_switch")
                        )
                    }

                    OutlinedTextField(
                        value = nativeUnitInput,
                        onValueChange = { nativeUnitInput = it },
                        label = { Text("Native Ad Unit ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("native_unit_id_input")
                    )
                }
            }
        }

        // 6. Custom Promotional Banner Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFE65100))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("কাস্টম স্পন্সরড অফার ব্যানার", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Switch(
                            checked = isCustomBannerEnabled,
                            onCheckedChange = { isCustomBannerEnabled = it },
                            modifier = Modifier.testTag("custom_banner_switch")
                        )
                    }

                    OutlinedTextField(
                        value = customTitleInput,
                        onValueChange = { customTitleInput = it },
                        label = { Text("ব্যনার শিরোনাম/লেখা") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("custom_banner_title_input")
                    )

                    OutlinedTextField(
                        value = customTargetUrlInput,
                        onValueChange = { customTargetUrlInput = it },
                        label = { Text("ক্লিক করলে যাওয়ার লিংক (Target URL)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("custom_banner_url_input")
                    )
                }
            }
        }

        // 7. Ad Reward Points & Auto Interval
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("অ্যাড রিওয়ার্ড পয়েন্ট ও ইমপ্রেশন ফ্রিকোয়েন্সি", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = adRewardPtsInput,
                            onValueChange = { adRewardPtsInput = it },
                            label = { Text("অ্যাড দেখলে ইউজার পয়েন্ট পাবেন") },
                            modifier = Modifier.weight(1f).testTag("ad_reward_pts_input")
                        )

                        OutlinedTextField(
                            value = adIntervalSecInput,
                            onValueChange = { adIntervalSecInput = it },
                            label = { Text("অটো অ্যাড বিরতি (সেকেন্ড)") },
                            modifier = Modifier.weight(1f).testTag("ad_interval_sec_input")
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}
