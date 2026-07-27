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
import androidx.compose.ui.unit.dp
import com.example.model.VideoCampaign
import com.example.viewmodel.MarketingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoGrowthScreen(viewModel: MarketingViewModel) {
    val campaigns by viewModel.campaigns.collectAsState()
    val analytics by viewModel.analytics.collectAsState()
    val automationStats by viewModel.automationStats.collectAsState()
    var showAddCampaignDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddCampaignDialog = true },
                icon = { Icon(Icons.Default.VideoCall, contentDescription = "New Campaign") },
                text = { Text("নতুন ভিডিও ক্যাম্পেইন") },
                modifier = Modifier.testTag("add_campaign_fab")
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
                Text(
                    text = "ইউটিউব গ্রোথ অ্যান্ড ভিডিও বুস্টার",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "শর্টস ভিউ, অটো-স্ক্রলিং, লাইকিং ও লং ভিডিও ওয়াচ টাইম অ্যানালিটিক্স",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Automation Service Control Widget
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AutoMode,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ইউটিউব অটোমেশন সার্ভিস কন্ট্রোল",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Surface(
                                color = if (automationStats.isShortsEngineActive || automationStats.isLongFormEngineActive) Color(0xFF4CAF50) else Color.Gray,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = if (automationStats.isShortsEngineActive || automationStats.isLongFormEngineActive) "সক্রিয়" else "বন্ধ",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "প্লেয়িং: ${automationStats.currentActiveCampaignTitle}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.toggleShortsAutomation(intervalSec = 10) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (automationStats.isShortsEngineActive) Color(0xFFE53935) else MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.weight(1f).testTag("toggle_shorts_auto_btn")
                            ) {
                                Text(if (automationStats.isShortsEngineActive) "শর্টস পজ" else "শর্টস অটো-স্ক্রল")
                            }

                            Button(
                                onClick = { viewModel.toggleLongFormAutomation(loopDurationSec = 20) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (automationStats.isLongFormEngineActive) Color(0xFF1E88E5) else MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.weight(1f).testTag("toggle_longform_loop_btn")
                            ) {
                                Text(if (automationStats.isLongFormEngineActive) "লুপ পজ" else "লং ভিডিও লুপ")
                            }
                        }
                    }
                }
            }

            // Stats summary
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    com.example.ui.components.AnalyticsCard(
                        title = "শর্টস ভিউ অর্জিত",
                        value = "${analytics.totalShortsViewsGained}",
                        icon = Icons.Default.PlayCircle,
                        accentColor = Color(0xFFE53935),
                        modifier = Modifier.weight(1f)
                    )
                    com.example.ui.components.AnalyticsCard(
                        title = "ওয়াচ টাইম (ঘণ্টা)",
                        value = "${analytics.watchTimeHoursGained}h",
                        icon = Icons.Default.HourglassTop,
                        accentColor = Color(0xFFFB8C00),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "সক্রিয় ভিডিও ক্যাম্পেইন তালিকা",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(campaigns) { campaign ->
                CampaignCardItem(
                    campaign = campaign,
                    onToggleRunning = { viewModel.toggleCampaignRunning(campaign.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showAddCampaignDialog) {
        AddCampaignDialog(
            onDismiss = { showAddCampaignDialog = false },
            onSubmit = { title, url, isShorts, targetViews, targetWatchTime ->
                viewModel.addCampaign(
                    title = title,
                    url = url,
                    isShorts = isShorts,
                    targetViews = targetViews,
                    targetWatchTime = targetWatchTime
                )
                showAddCampaignDialog = false
            }
        )
    }
}

@Composable
fun CampaignCardItem(campaign: VideoCampaign, onToggleRunning: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Switch(
                    checked = campaign.isRunning,
                    onCheckedChange = { onToggleRunning() },
                    modifier = Modifier.testTag("campaign_switch_${campaign.id}")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Indicators
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "টার্গেট ভিউ অর্জন:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${campaign.currentViews} / ${campaign.targetViews}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                LinearProgressIndicator(
                    progress = { (campaign.currentViews.toFloat() / campaign.targetViews.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Automation parameters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (campaign.isShorts) {
                    LabelBadge(
                        text = "অটো-স্ক্রলিং: " + if (campaign.autoScrollEnabled) "সক্রিয়" else "বন্ধ",
                        icon = Icons.Default.SwapVert
                    )
                    LabelBadge(
                        text = "অটো-লাইকিং: " + if (campaign.autoLikeEnabled) "সক্রিয়" else "বন্ধ",
                        icon = Icons.Default.Favorite
                    )
                } else {
                    LabelBadge(
                        text = "রিফ্রেশ ইন্টারভাল: ${campaign.autoRefreshIntervalSec}s",
                        icon = Icons.Default.Refresh
                    )
                    LabelBadge(
                        text = "ওয়াচ টাইম লুপ: সক্রিয়",
                        icon = Icons.Default.Repeat
                    )
                }
            }
        }
    }
}

@Composable
fun LabelBadge(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AddCampaignDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, Boolean, Int, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var isShorts by remember { mutableStateOf(true) }
    var targetViews by remember { mutableStateOf("1000") }
    var targetWatchTime by remember { mutableStateOf("300") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন ইউটিউব গ্রোথ ক্যাম্পেইন") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("ভিডিওর শিরোনাম") },
                    modifier = Modifier.fillMaxWidth().testTag("campaign_title_input")
                )
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("ইউটিউব লিংক") },
                    modifier = Modifier.fillMaxWidth().testTag("campaign_url_input")
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = isShorts,
                        onClick = { isShorts = true }
                    )
                    Text("ইউটিউব শর্টস (Shorts)")
                    Spacer(modifier = Modifier.width(12.dp))
                    RadioButton(
                        selected = !isShorts,
                        onClick = { isShorts = false }
                    )
                    Text("লং ভিডিও")
                }
                OutlinedTextField(
                    value = targetViews,
                    onValueChange = { targetViews = it },
                    label = { Text("টার্গেট ভিউ সংখ্যা") },
                    modifier = Modifier.fillMaxWidth().testTag("campaign_views_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSubmit(
                            title,
                            url,
                            isShorts,
                            targetViews.toIntOrNull() ?: 1000,
                            targetWatchTime.toIntOrNull() ?: 300
                        )
                    }
                },
                modifier = Modifier.testTag("submit_campaign_btn")
            ) {
                Text("ক্যাম্পেইন তৈরি করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
