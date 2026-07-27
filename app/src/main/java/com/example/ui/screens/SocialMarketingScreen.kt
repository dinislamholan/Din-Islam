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
import com.example.model.PostStatus
import com.example.model.SocialPost
import com.example.viewmodel.MarketingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialMarketingScreen(viewModel: MarketingViewModel) {
    val posts by viewModel.posts.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val analytics by viewModel.analytics.collectAsState()
    val logs by viewModel.logs.collectAsState()

    var showAddPostDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddPostDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Post") },
                text = { Text("নতুন পোস্ট শিডিউল") },
                modifier = Modifier.testTag("add_post_fab")
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
                    text = "সোশ্যাল মিডিয়া মার্কেটিং ড্যাশবোর্ড",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "মাল্টিপল গ্রুপ শেয়ারিং এবং অটো-পোস্টিং ম্যানেজার",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Overview statistics
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    com.example.ui.components.AnalyticsCard(
                        title = "মোট পোস্ট শেয়ার",
                        value = "${analytics.totalPostsShared}",
                        icon = Icons.Default.Share,
                        accentColor = Color(0xFF1E88E5),
                        modifier = Modifier.weight(1f)
                    )
                    com.example.ui.components.AnalyticsCard(
                        title = "গ্রুপ কভারেজ",
                        value = "${analytics.totalGroupsReached}",
                        icon = Icons.Default.Groups,
                        accentColor = Color(0xFF43A047),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Target Groups Selection Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "টার্গেটেড সোশ্যাল গ্রুপস",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Badge {
                                Text("${groups.count { it.isSelected }} টি সিলেক্টেড")
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        groups.forEach { group ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = group.isSelected,
                                    onCheckedChange = { viewModel.toggleGroupSelection(group.id) },
                                    modifier = Modifier.testTag("group_checkbox_${group.id}")
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = group.name,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${group.platform} • ${group.memberCount} সদস্য",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Active Posts & Campaigns List
            item {
                Text(
                    text = "ক্যাম্পেইন ও পোস্ট শিডিউল",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(posts) { post ->
                PostCardItem(post = post, onAutoShare = { viewModel.triggerAutoShare(post.id) })
            }

            // Logs output window
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Terminal,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "লাইভ মার্কেটিং অ্যাক্টিভিটি লগ",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        logs.take(5).forEach { log ->
                            Text(
                                text = log,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showAddPostDialog) {
        AddPostDialog(
            onDismiss = { showAddPostDialog = false },
            onSubmit = { title, content, time ->
                viewModel.addPost(
                    title = title,
                    content = content,
                    platforms = listOf("Facebook Groups", "LinkedIn"),
                    scheduledTime = time
                )
                showAddPostDialog = false
            }
        )
    }
}

@Composable
fun PostCardItem(post: SocialPost, onAutoShare: () -> Unit) {
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
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                StatusChip(status = post.status)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সময়: ${post.scheduledTime}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Button(
                    onClick = onAutoShare,
                    enabled = post.status != PostStatus.IN_PROGRESS,
                    modifier = Modifier.testTag("auto_share_btn_${post.id}")
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (post.status == PostStatus.IN_PROGRESS) "শেয়ার হচ্ছে..." else "মাল্টি-গ্রুপ অটো শেয়ার")
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: PostStatus) {
    val (bgColor, textColor, label) = when (status) {
        PostStatus.SCHEDULED -> Triple(Color(0xFFE3F2FD), Color(0xFF1976D2), "শিডিউলড")
        PostStatus.IN_PROGRESS -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "প্রসেসিং")
        PostStatus.POSTED -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "সম্পন্ন")
        PostStatus.FAILED -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "ব্যর্থ")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun AddPostDialog(onDismiss: () -> Unit, onSubmit: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var scheduledTime by remember { mutableStateOf("আজ, ৬:০০ PM") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("নতুন পোস্ট মার্কেটিং শিডিউল") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("পোস্টের শিরোনাম") },
                    modifier = Modifier.fillMaxWidth().testTag("post_title_input")
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("পোস্টের বিষয়বস্তু") },
                    modifier = Modifier.fillMaxWidth().testTag("post_content_input"),
                    minLines = 3
                )
                OutlinedTextField(
                    value = scheduledTime,
                    onValueChange = { scheduledTime = it },
                    label = { Text("শেয়ার করার সময়") },
                    modifier = Modifier.fillMaxWidth().testTag("post_time_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) onSubmit(title, content, scheduledTime)
                },
                modifier = Modifier.testTag("submit_post_btn")
            ) {
                Text("সংরক্ষণ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
