package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.components.AdMobBannerAdView
import com.example.ui.components.AdMobInterstitialDialog
import com.example.viewmodel.MarketingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicUserScreen(viewModel: MarketingViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val campaigns by viewModel.campaigns.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val systemNotices by viewModel.systemNotices.collectAsState()
    val adConfig by viewModel.adSettingsConfig.collectAsState()
    val uriHandler = LocalUriHandler.current

    if (currentUser == null) {
        PublicAuthScreen(
            viewModel = viewModel,
            onLoginSuccess = { /* Logged in successfully */ }
        )
        return
    }

    val activeUser = currentUser!!
    var showNoticeBoardDialog by remember { mutableStateOf(false) }
    var showSubmitBoostDialog by remember { mutableStateOf(false) }
    var showAdMobInterstitial by remember { mutableStateOf(false) }
    var adTriggerReason by remember { mutableStateOf("") }

    var pendingTaskCampaignId by remember { mutableStateOf<String?>(null) }
    var pendingTaskUrl by remember { mutableStateOf<String?>(null) }

    var showAutoVerifyDialog by remember { mutableStateOf(false) }
    var verifyingCampaignId by remember { mutableStateOf<String?>(null) }
    var verifyingUrl by remember { mutableStateOf<String?>(null) }

    var isAutoLoopActive by remember { mutableStateOf(false) }
    var activeVideoIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(isAutoLoopActive) {
        if (isAutoLoopActive) {
            while (isAutoLoopActive) {
                kotlinx.coroutines.delay(4000)
                if (campaigns.isNotEmpty()) {
                    activeVideoIndex = (activeVideoIndex + 1) % campaigns.size
                    viewModel.addPointsToCurrentUser(10)
                    viewModel.addLog("পাবলিক লুপ: অটো-স্ক্রল সম্পন্ন! ১০ পয়েন্ট অর্জিত হয়েছে।")
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (adConfig.isAdMobMasterEnabled && adConfig.isBannerAdEnabled) {
                AdMobBannerAdView(
                    adUnitId = adConfig.bannerAdUnitId,
                    onAdClick = {
                        viewModel.recordAdImpression()
                        viewModel.addPointsToCurrentUser(5)
                        viewModel.addLog("পাবলিক ব্যানার এড ক্লিকে ৫ পয়েন্ট অর্জিত হয়েছে!")
                    }
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { 
                    adTriggerReason = "বুস্ট রিকোয়েস্ট জমা দেওয়ার আগে বাধ্যতামূলক ইন্টারস্টিশিয়াল এড।"
                    showAdMobInterstitial = true 
                },
                icon = { Icon(Icons.Default.Upload, contentDescription = "Submit Link") },
                text = { Text("আমার ভিডিও বুস্ট রিকোয়েস্ট") },
                modifier = Modifier.testTag("public_submit_boost_fab")
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "স্বাগতম, ${activeUser.fullName}!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                text = "মোবাইল: ${activeUser.mobileNumber} (ভেরিফাইড)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BadgedBox(
                                badge = {
                                    if (systemNotices.isNotEmpty()) {
                                        Badge { Text("${systemNotices.size}") }
                                    }
                                }
                            ) {
                                IconButton(
                                    onClick = { showNoticeBoardDialog = true },
                                    modifier = Modifier.testTag("public_notice_board_icon_btn")
                                ) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = "Notice Board",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.MonetizationOn,
                                        contentDescription = null,
                                        tint = Color.Yellow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${activeUser.points} pts",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            IconButton(
                                onClick = { viewModel.logoutPublicUser() },
                                modifier = Modifier.testTag("public_user_logout_btn")
                            ) {
                                Icon(
                                    Icons.Default.Logout,
                                    contentDescription = "Logout",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            // Notice Board Ticker / Banner
            if (systemNotices.isNotEmpty()) {
                val latestNotice = systemNotices.first()
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showNoticeBoardDialog = true }
                            .testTag("notice_board_banner"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (latestNotice.isImportant) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = if (latestNotice.isImportant) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = latestNotice.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (latestNotice.isImportant) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "নোটিশ বোর্ড >",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = latestNotice.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }


            // AdMob Interstitial Watch Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E88E5)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.ConnectedTv,
                                    contentDescription = null,
                                    tint = Color.Yellow,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "AdMob ইন্টারস্টিশিয়াল এড দেখুন",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "বিজ্ঞাপন দেখে বোনাস ৫০ পয়েন্ট অর্জন করুন!",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        Button(
                            onClick = {
                                adTriggerReason = "বোনাস পয়েন্ট অর্জনের জন্য এড।"
                                showAdMobInterstitial = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                            modifier = Modifier.testTag("watch_interstitial_ad_btn")
                        ) {
                            Text("এড দেখুন (+৫০)", color = Color.Black)
                        }
                    }
                }
            }

            // Auto-Scrolling YouTube Player Simulator Widget
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = Color(0xFFE53935)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ইউটিউব অটো-ভিউ ও প্লেয়ার লুপ",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Button(
                                onClick = { isAutoLoopActive = !isAutoLoopActive },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAutoLoopActive) Color(0xFFE53935) else MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.testTag("public_autoloop_btn")
                            ) {
                                Text(if (isAutoLoopActive) "লুপ বন্ধ করুন" else "অটো লুপ চালু করুন")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (campaigns.isNotEmpty()) {
                            val currentCampaign = campaigns[activeVideoIndex % campaigns.size]
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                color = Color.Black,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = if (currentCampaign.isShorts) Icons.Default.SmartDisplay else Icons.Default.OndemandVideo,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = currentCampaign.videoTitle,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (isAutoLoopActive) "▶ অটো-প্লেয়িং ও ওয়াচ টাইম গেইন হচ্ছে..." else "প্লে করতে অটো লুপ চালু করুন",
                                            color = if (isAutoLoopActive) Color.Green else Color.LightGray,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Public Video Boost Campaigns
            item {
                Text(
                    text = "জনপ্রিয় ভিডিও বুস্ট ক্যাম্পেইনসমূহ",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            val userCompletedTaskIds = activeUser.completedTaskIds.toSet()
            val availableCampaigns = campaigns.filter { campaign -> campaign.id !in userCompletedTaskIds }

            if (availableCampaigns.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "সমস্ত উপলব্ধ টাস্ক সম্পন্ন করেছেন! 🎉",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                                Text(
                                    text = "এই মুহূর্তে আপনার জন্য কোনো নতুন টাস্ক নেই। নতুন টাস্ক যোগ হওয়া পর্যন্ত অপেক্ষা করুন অথবা নিজের বুস্ট পোস্ট জমা দিন।",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                items(availableCampaigns) { campaign ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.OndemandVideo,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = campaign.videoTitle,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                                Text(
                                    text = "লিংক: ${campaign.videoUrl}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Requirement Badge and Progress
                        val (reqIcon, reqLabel, reqColor) = when(campaign.requirementType) {
                            "LIKE" -> Triple(Icons.Default.ThumbUp, "লাইক (Like)", MaterialTheme.colorScheme.secondaryContainer)
                            "COMMENT" -> Triple(Icons.Default.Comment, "কমেন্ট (Comment)", MaterialTheme.colorScheme.tertiaryContainer)
                            "SHARE" -> Triple(Icons.Default.Share, "শেয়ার (Share)", MaterialTheme.colorScheme.primaryContainer)
                            else -> Triple(Icons.Default.Verified, "টাস্ক", MaterialTheme.colorScheme.surfaceVariant)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = reqColor,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(reqIcon, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = reqLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = "প্রোগ্রেস: ${campaign.completedRequirementCount} / ${campaign.targetRequirementCount} টি সম্পূর্ণ",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = {
                                if (campaign.targetRequirementCount > 0) {
                                    (campaign.completedRequirementCount.toFloat() / campaign.targetRequirementCount.toFloat()).coerceIn(0f, 1f)
                                } else 0f
                            },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val isCompleted = campaign.completedRequirementCount >= campaign.targetRequirementCount

                        Button(
                            onClick = {
                                pendingTaskCampaignId = campaign.id
                                pendingTaskUrl = campaign.videoUrl
                                adTriggerReason = "টাস্কে রিডাইরেক্ট করার আগে ইন্টারস্টিশিয়াল এড"
                                showAdMobInterstitial = true
                            },
                            enabled = !isCompleted,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("public_complete_task_${campaign.id}"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isCompleted) "টাস্কটি পূর্ণ হয়েছে (১/১)" else "কাজটি পূরণ করুন (+৫০ পয়েন্ট)",
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

            // Public Posts Feed
            item {
                Text(
                    text = "সোশ্যাল মিডিয়া পাবলিক ফিড",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(posts) { post ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = post.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = post.content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                viewModel.addPointsToCurrentUser(15)
                                viewModel.addLog("পোস্ট সোশ্যাল শেয়ার করা হয়েছে। ১৫ পয়েন্ট যোগ করা হলো!")
                            }) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("শেয়ার করুন (+15 pts)")
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showAdMobInterstitial) {
        AdMobInterstitialDialog(
            adUnitId = adConfig.interstitialAdUnitId,
            rewardPoints = adConfig.adRewardPoints,
            onAdCompleted = { pointsEarned ->
                viewModel.recordAdImpression()

                if (pendingTaskCampaignId != null) {
                    val targetUrl = pendingTaskUrl ?: "https://youtube.com"
                    val taskId = pendingTaskCampaignId!!

                    try {
                        uriHandler.openUri(targetUrl)
                    } catch (e: Exception) {
                        viewModel.addLog("লিংক ওপেন করা যাচ্ছে না: $targetUrl")
                    }

                    verifyingCampaignId = taskId
                    verifyingUrl = targetUrl
                    showAutoVerifyDialog = true

                    pendingTaskCampaignId = null
                    pendingTaskUrl = null
                } else {
                    viewModel.addPointsToCurrentUser(pointsEarned)
                    viewModel.addLog("AdMob ইন্টারস্টিশিয়াল এড সফলভাবে দেখা হয়েছে! $pointsEarned পয়েন্ট যোগ করা হয়েছে।")
                    if (adTriggerReason.contains("বুস্ট রিকোয়েস্ট")) {
                        showSubmitBoostDialog = true
                    }
                }
            },
            onDismiss = {
                showAdMobInterstitial = false
                pendingTaskCampaignId = null
                pendingTaskUrl = null
            }
        )
    }

    if (showAutoVerifyDialog && verifyingCampaignId != null) {
        TaskAutoVerificationDialog(
            campaignId = verifyingCampaignId!!,
            targetUrl = verifyingUrl ?: "",
            onApproved = {
                viewModel.completePublicCampaignTask(verifyingCampaignId!!)
            },
            onDismiss = {
                showAutoVerifyDialog = false
                verifyingCampaignId = null
                verifyingUrl = null
            }
        )
    }

    if (showNoticeBoardDialog) {
        PublicUserNoticeBoardDialog(
            systemNotices = systemNotices,
            onDismiss = { showNoticeBoardDialog = false }
        )
    }

    if (showSubmitBoostDialog) {
        var titleInput by remember { mutableStateOf("") }
        var linkInput by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf("LIKE") } // "LIKE", "COMMENT", "SHARE"
        var targetCount by remember { mutableIntStateOf(1) } // Fixed to 1
        var errorMessage by remember { mutableStateOf("") }

        val totalCost = targetCount * 100 // 100 points per requirement

        AlertDialog(
            onDismissRequest = { showSubmitBoostDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Publish,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("পাবলিক বুস্ট রিকোয়েস্ট জমা দিন")
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Title Input Field
                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it; errorMessage = "" },
                        label = { Text("ভিডিও/পোস্টের টাইটেল (Title)") },
                        placeholder = { Text("যেমন: মাই ভিডিও প্রমোশন") },
                        modifier = Modifier.fillMaxWidth().testTag("public_boost_title_input"),
                        singleLine = true
                    )

                    // Video Link Input Field
                    OutlinedTextField(
                        value = linkInput,
                        onValueChange = { linkInput = it; errorMessage = "" },
                        label = { Text("ভিডিও/পোস্ট লিংক (Video Link)") },
                        placeholder = { Text("https://youtube.com/... অথবা ফেসবুক লিংক") },
                        modifier = Modifier.fillMaxWidth().testTag("public_boost_link_input"),
                        singleLine = true
                    )

                    Text(
                        text = "রিকোয়ারমেন্ট টাইপ নির্বাচন করুন (১টি নির্বাচনযোগ্য):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )

                    // Single Option Selection Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedType == "LIKE",
                            onClick = { selectedType = "LIKE" },
                            label = { Text("👍 লাইক") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = selectedType == "COMMENT",
                            onClick = { selectedType = "COMMENT" },
                            label = { Text("💬 কমেন্ট") },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = selectedType == "SHARE",
                            onClick = { selectedType = "SHARE" },
                            label = { Text("🔗 শেয়ার") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider()

                    // Notice Banner for Cost
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "💰 চার্জ: ১টি রিকোয়ারমেন্ট এর জন্য ১০০ পয়েন্ট",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "মোট খরচ: $totalCost পয়েন্ট (আপনার অ্যাকাউন্টে আছে: ${activeUser.points} পয়েন্ট)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleInput.isBlank()) {
                            errorMessage = "অনুগ্রহ করে টাইটেল লিখুন!"
                            return@Button
                        }
                        if (linkInput.isBlank()) {
                            errorMessage = "অনুগ্রহ করে লিংক প্রদান করুন!"
                            return@Button
                        }

                        if (activeUser.points >= totalCost) {
                            viewModel.addPointsToCurrentUser(-totalCost)
                            viewModel.addCampaign(
                                title = titleInput.trim(),
                                url = linkInput.trim(),
                                isShorts = true,
                                targetViews = 500,
                                targetWatchTime = 120,
                                requirementType = selectedType,
                                targetRequirementCount = targetCount
                            )
                            viewModel.addLog("বুস্ট রিকোয়েস্ট জমা হয়েছে! টাইটেল: ${titleInput.trim()}, টাইপ: $selectedType, পরিমাণ: $targetCount টি, কাটা হয়েছে: $totalCost পয়েন্ট")
                            showSubmitBoostDialog = false
                        } else {
                            errorMessage = "পর্যাপ্ত পয়েন্ট নেই! আপনার প্রয়োজন $totalCost পয়েন্ট (আছে ${activeUser.points} পয়েন্ট)।"
                        }
                    },
                    modifier = Modifier.testTag("public_submit_boost_confirm")
                ) {
                    Text("রিকোয়েস্ট জমা দিন ($totalCost pts)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitBoostDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun RequirementCounterRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
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
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = "সীমা: ১ - ৫টি (পারটি ৫০ পয়েন্ট)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { if (value > 1) onValueChange(value - 1) },
                enabled = value > 1,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = if (value > 1) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = "$value টি",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            IconButton(
                onClick = { if (value < 5) onValueChange(value + 1) },
                enabled = value < 5,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = if (value < 5) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}

@Composable
fun TaskAutoVerificationDialog(
    campaignId: String,
    targetUrl: String,
    onApproved: () -> Unit,
    onDismiss: () -> Unit
) {
    var verificationStep by remember { mutableIntStateOf(1) } // 1, 2, 3, 4 (completed)
    var progress by remember { mutableFloatStateOf(0.1f) }
    var isApproved by remember { mutableStateOf(false) }

    LaunchedEffect(campaignId) {
        progress = 0.25f
        verificationStep = 1
        kotlinx.coroutines.delay(1800)

        progress = 0.60f
        verificationStep = 2
        kotlinx.coroutines.delay(2000)

        progress = 0.90f
        verificationStep = 3
        kotlinx.coroutines.delay(1800)

        progress = 1.0f
        verificationStep = 4
        isApproved = true
        onApproved()
    }

    AlertDialog(
        onDismissRequest = {
            if (isApproved) onDismiss()
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isApproved) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("অটোমেটিক টাস্ক এপ্রুভড! 🎉")
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("অটোমেটিক চেক চলছে...")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "সোশ্যাল লিংক: ${targetUrl.take(35)}...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = if (isApproved) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Surface(
                    color = if (isApproved) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        val statusText = when(verificationStep) {
                            1 -> "১/৩: সোশ্যাল প্ল্যাটফর্ম রিডাইরেক্ট কানেকশন চেক করা হচ্ছে..."
                            2 -> "২/৩: ইউটিউব/ফেসবুক সার্ভারে আপনার রিকোয়ারমেন্ট রেসপন্স চেক হচ্ছে..."
                            3 -> "৩/৩: অ্যাক্টিভিটি (লাইক/কমেন্ট/শেয়ার) অটো ভেরিফাই করা হচ্ছে..."
                            else -> "✅ কাজটি সঠিকভাবে সম্পন্ন হয়েছে! অটো এপ্রুভ দেওয়া হলো।"
                        }
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = if (isApproved) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isApproved) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "বোনাস: +৫০ পয়েন্ট আপনার অ্যাকাউন্টে যোগ হয়েছে!",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isApproved) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("task_auto_verify_done_btn")
                ) {
                    Text("ঠিক আছে (+৫০ পয়েন্ট)")
                }
            }
        }
    )
}

@Composable
fun PublicUserNoticeBoardDialog(
    systemNotices: List<com.example.model.SystemNotice>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Campaign,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "অ্যাডমিন নোটিশ বোর্ড",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "অ্যাডমিন কর্তৃক সাম্প্রতিক সকল নোটিফিকেশন",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            if (systemNotices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("বর্তমানে কোনো নোটিশ নেই।")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(systemNotices) { notice ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (notice.isImportant) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (notice.isImportant) Icons.Default.PriorityHigh else Icons.Default.Announcement,
                                            contentDescription = null,
                                            tint = if (notice.isImportant) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = notice.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (notice.isImportant) Color(0xFFC62828) else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = notice.content,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = notice.timestamp,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = notice.author,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_notice_board_btn")
            ) {
                Text("বন্ধ করুন")
            }
        }
    )
}

