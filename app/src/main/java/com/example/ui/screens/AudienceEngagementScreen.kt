package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AudienceProfile
import com.example.viewmodel.MarketingViewModel

@Composable
fun AudienceEngagementScreen(viewModel: MarketingViewModel) {
    var profiles by remember {
        mutableStateOf(
            listOf(
                AudienceProfile("p1", "রাফি আহমেদ", null, "Facebook", true),
                AudienceProfile("p2", "সামিয়া খান", null, "Facebook", true),
                AudienceProfile("p3", "তানভীর হোসেন", null, "LinkedIn", true),
                AudienceProfile("p4", "সাদিয়া ইসলাম", null, "YouTube", false)
            )
        )
    }

    var commentTemplate by remember { mutableStateOf("অসাধারণ একটি ইনফরমেটিভ ভিডিও! ধন্যবাদ শেয়ার করার জন্য।") }
    var autoCommentDelaySec by remember { mutableStateOf("15") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "অডিয়েন্স এনগেজমেন্ট অ্যান্ড কমেন্ট অটোমেশন",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "সোশ্যাল প্রোফাইল এবং কমেন্ট এনগেজমেন্ট প্ল্যানিং সেন্টার",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Configuration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "স্বয়ংসক্রিয় কমেন্ট ও এনগেজমেন্ট সেটিংস",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = commentTemplate,
                        onValueChange = { commentTemplate = it },
                        label = { Text("ডিফল্ট কমেন্ট টেমপ্লেট") },
                        modifier = Modifier.fillMaxWidth().testTag("comment_template_input"),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = autoCommentDelaySec,
                        onValueChange = { autoCommentDelaySec = it },
                        label = { Text("কমেন্ট ফ্রিকোয়েন্সি ব্যবধান (সেকেন্ড)") },
                        modifier = Modifier.fillMaxWidth().testTag("comment_delay_input")
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সংযুক্ত প্রোফাইল অ্যাকাউন্টসমূহ",
                    style = MaterialTheme.typography.titleLarge
                )
                Button(
                    onClick = {
                        val newId = "p${profiles.size + 1}"
                        profiles = profiles + AudienceProfile(newId, "প্রোফাইল অ্যাকাউন্ট #${profiles.size + 1}", null, "Facebook", true)
                        viewModel.addLog("নতুন অ্যাকাউন্ট সংযোগ সফল হয়েছে।")
                    },
                    modifier = Modifier.testTag("add_profile_btn")
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("অ্যাকাউন্ট যোগ করুন")
                }
            }
        }

        items(profiles) { profile ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = profile.name, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "${profile.platform} • " + if (profile.isActive) "সক্রিয়" else "নিষ্ক্রিয়",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (profile.isActive) Color(0xFF2E7D32) else Color.Gray
                        )
                    }
                    Switch(
                        checked = profile.isActive,
                        onCheckedChange = { active ->
                            profiles = profiles.map { if (it.id == profile.id) it.copy(isActive = active) else it }
                        },
                        modifier = Modifier.testTag("profile_switch_${profile.id}")
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
