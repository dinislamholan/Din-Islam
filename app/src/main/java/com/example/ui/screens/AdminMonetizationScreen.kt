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
import androidx.compose.ui.unit.sp
import com.example.model.WithdrawalStatus
import com.example.viewmodel.MarketingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMonetizationScreen(viewModel: MarketingViewModel) {
    val monetization by viewModel.monetization.collectAsState()
    val withdrawals by viewModel.withdrawals.collectAsState()

    var showWithdrawDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showWithdrawDialog = true },
                icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Withdraw Revenue") },
                text = { Text("ব্যাংক উইথড্র রিকোয়েস্ট") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("admin_withdraw_fab")
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
                // AdMob Earnings Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "AdMob মনিটাইজেশন ও রেভিনিউ ওয়ালেট",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "বাধ্যতামূলক ইন্টারস্টিশিয়াল এড হতে অর্জিত রেভিনিউ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Surface(
                                color = Color(0xFF4CAF50),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "লাইভ আর্নিং",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "উইথড্রযোগ্য ব্যালেন্স (USD):",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                                Text(
                                    text = "$${String.format("%.2f", monetization.availableBalanceUsd)} USD",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "বাংলাদেশী টাকায় (BDT):",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                                val bdtVal = monetization.availableBalanceUsd * monetization.bdtExchangeRate
                                Text(
                                    text = "৳${String.format("%.2f", bdtVal)} BDT",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
            }

            // Key Monetization Metrics grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "মোট ইমপ্রেশন", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "${monetization.totalAdImpressions} views",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "AdMob eCPM Rate", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "$${monetization.ecpmUsd}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "মোট উইথড্রয়াল", style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = "$${String.format("%.2f", monetization.totalWithdrawnUsd)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E88E5)
                            )
                        }
                    }
                }
            }

            // Full Ad Revenue Analytics Dashboard
            item {
                FullAdRevenueAnalyticsSection(monetization = monetization)
            }

            // Bank Withdrawal Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ব্যাংক ও মোবাইল ব্যাংক উইথড্রয়াল হিস্ট্রি",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Button(
                        onClick = { showWithdrawDialog = true },
                        modifier = Modifier.testTag("request_payout_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("উইথড্র দিন")
                    }
                }
            }

            // Withdrawal History List
            if (withdrawals.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Box(
                            modifier = Modifier.padding(32.dp).fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "এখনও কোনো ব্যাংক উইথড্রয়াল রিকোয়েস্ট করা হয়নি।")
                        }
                    }
                }
            } else {
                items(withdrawals) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = item.paymentMethod,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Surface(
                                    color = when (item.status) {
                                        WithdrawalStatus.PAID -> Color(0xFF4CAF50)
                                        WithdrawalStatus.PENDING -> Color(0xFFFF9800)
                                        WithdrawalStatus.PROCESSING -> Color(0xFF2196F3)
                                        WithdrawalStatus.APPROVED -> Color(0xFF8BC34A)
                                        WithdrawalStatus.REJECTED -> Color(0xFFF44336)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = when (item.status) {
                                            WithdrawalStatus.PAID -> "পরিশোধিত (Paid)"
                                            WithdrawalStatus.PENDING -> "পেন্ডিং (Pending)"
                                            WithdrawalStatus.PROCESSING -> "প্রসেসিং"
                                            WithdrawalStatus.APPROVED -> "অনুমোদিত"
                                            WithdrawalStatus.REJECTED -> "বাতিল"
                                        },
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "একাউন্ট / ফোন নম্বর: ${item.accountNumber}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    if (item.bankRoutingNumber.isNotBlank()) {
                                        Text(
                                            text = "রাউটিং নম্বর: ${item.bankRoutingNumber}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "তারিখ: ${item.timestamp}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "$${String.format("%.2f", item.amountUsd)} USD",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "৳${String.format("%.2f", item.amountBdt)} BDT",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
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

    if (showWithdrawDialog) {
        var withdrawAmountText by remember { mutableStateOf("") }
        var selectedMethod by remember { mutableStateOf("Dutch-Bangla Bank (ডাচ-বাংলা ব্যাংক)") }
        var accountNumberText by remember { mutableStateOf("") }
        var routingNumberText by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }

        val bankOptions = listOf(
            "Dutch-Bangla Bank (ডাচ-বাংলা ব্যাংক)",
            "BRAC Bank (ব্র্যাক ব্যাংক)",
            "Islami Bank Bangladesh (ইসলামী ব্যাংক)",
            "City Bank (সিটি ব্যাংক)",
            "NCC Bank (এনসিসি ব্যাংক)",
            "bKash Personal/Merchant (বিকাশ)",
            "Nagad (নগদ)",
            "Rocket (রকেট)",
            "Upay (উপায়)",
            "International Wire Transfer (SWIFT)"
        )

        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ব্যাংক উইথড্র রিকোয়েস্ট")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "আপনার AdMob রেভিনিউ সরাসরি ব্যাংক বা মোবাইল ওয়ালেটে ট্রান্সফার করুন।",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "পেমেন্ট মেথড নির্বাচন করুন:",
                        style = MaterialTheme.typography.labelMedium
                    )

                    var expandedDropdown by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedDropdown,
                        onExpandedChange = { expandedDropdown = !expandedDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedMethod,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDropdown,
                            onDismissRequest = { expandedDropdown = false }
                        ) {
                            bankOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedMethod = option
                                        expandedDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = withdrawAmountText,
                        onValueChange = {
                            withdrawAmountText = it
                            errorMessage = ""
                        },
                        label = { Text("উইথড্র করার পরিমাণ (USD $)") },
                        modifier = Modifier.fillMaxWidth().testTag("withdraw_amount_input")
                    )

                    val reqVal = withdrawAmountText.toDoubleOrNull() ?: 0.0
                    val bdtConverted = reqVal * monetization.bdtExchangeRate
                    Text(
                        text = "সমপরিমাণ টাকা: ৳${String.format("%.2f", bdtConverted)} BDT (রেট: ১ USD = ৳১১৮ BDT)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF2E7D32)
                    )

                    OutlinedTextField(
                        value = accountNumberText,
                        onValueChange = {
                            accountNumberText = it
                            errorMessage = ""
                        },
                        label = { Text("একাউন্ট / মোবাইল নম্বর") },
                        modifier = Modifier.fillMaxWidth().testTag("withdraw_acc_input")
                    )

                    if (!selectedMethod.contains("bKash") && !selectedMethod.contains("Nagad")) {
                        OutlinedTextField(
                            value = routingNumberText,
                            onValueChange = { routingNumberText = it },
                            label = { Text("ব্যাংক রাউটিং নম্বর (ঐচ্ছিক)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (errorMessage.isNotBlank()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amountUsd = withdrawAmountText.toDoubleOrNull() ?: 0.0
                        if (amountUsd <= 0) {
                            errorMessage = "অনুগ্রহ করে সঠিক ডলার পরিমাণ লিখুন।"
                        } else if (amountUsd > monetization.availableBalanceUsd) {
                            errorMessage = "আপনার ওয়ালেটে পর্যপ্ত পরিমাণ ব্যালেন্স নেই।"
                        } else if (accountNumberText.isBlank()) {
                            errorMessage = "একাউন্ট নম্বর আবশ্যক।"
                        } else {
                            val success = viewModel.requestWithdrawal(
                                amountUsd = amountUsd,
                                method = selectedMethod,
                                accountNumber = accountNumberText,
                                routingNumber = routingNumberText
                            )
                            if (success) {
                                showWithdrawDialog = false
                            }
                        }
                    },
                    modifier = Modifier.testTag("confirm_withdraw_btn")
                ) {
                    Text("উইথড্র জমা দিন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun FullAdRevenueAnalyticsSection(monetization: com.example.model.MonetizationEarnings) {
    var selectedAnalyticsTab by remember { mutableIntStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ad_revenue_full_analytics_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Analytics,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "এড রেভিনিউ ফুল এনালাটিক্স",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "AdMob নেটওয়ার্ক পারফরম্যান্স ও ইনকাম ড্যাশবোর্ড",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Speed,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "লাইভ রিয়েলটাইম",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Overview Cards for Revenue Periods
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnalyticsPeriodBox(
                    label = "আজকের আয় (Today)",
                    usd = monetization.todayRevenueUsd,
                    bdt = monetization.todayRevenueUsd * monetization.bdtExchangeRate,
                    containerColor = Color(0xFFE8F5E9),
                    contentColor = Color(0xFF1B5E20),
                    modifier = Modifier.weight(1f)
                )
                AnalyticsPeriodBox(
                    label = "গতকাল (Yesterday)",
                    usd = monetization.yesterdayRevenueUsd,
                    bdt = monetization.yesterdayRevenueUsd * monetization.bdtExchangeRate,
                    containerColor = Color(0xFFE3F2FD),
                    contentColor = Color(0xFF0D47A1),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnalyticsPeriodBox(
                    label = "চলতি মাস (This Month)",
                    usd = monetization.thisMonthRevenueUsd,
                    bdt = monetization.thisMonthRevenueUsd * monetization.bdtExchangeRate,
                    containerColor = Color(0xFFFFF3E0),
                    contentColor = Color(0xFFE65100),
                    modifier = Modifier.weight(1f)
                )
                AnalyticsPeriodBox(
                    label = "গত মাস (Last Month)",
                    usd = monetization.lastMonthRevenueUsd,
                    bdt = monetization.lastMonthRevenueUsd * monetization.bdtExchangeRate,
                    containerColor = Color(0xFFF3E5F5),
                    contentColor = Color(0xFF4A148C),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Key Metrics Ratio Banner
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MetricBadge(label = "eCPM রেট", value = "$${monetization.ecpmUsd}")
                    MetricBadge(label = "এড ফিল রেট", value = "${monetization.fillRatePercent}%")
                    MetricBadge(label = "এভরেজ CTR", value = "${monetization.averageCtrPercent}%")
                    MetricBadge(label = "ইমপ্রেশন", value = "${monetization.totalAdImpressions}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs
            TabRow(selectedTabIndex = selectedAnalyticsTab) {
                Tab(
                    selected = selectedAnalyticsTab == 0,
                    onClick = { selectedAnalyticsTab = 0 },
                    text = { Text("দৈনিক রিপোর্ট", style = MaterialTheme.typography.labelMedium) },
                    modifier = Modifier.testTag("tab_daily_report")
                )
                Tab(
                    selected = selectedAnalyticsTab == 1,
                    onClick = { selectedAnalyticsTab = 1 },
                    text = { Text("এড ফরম্যাট", style = MaterialTheme.typography.labelMedium) },
                    modifier = Modifier.testTag("tab_format_report")
                )
                Tab(
                    selected = selectedAnalyticsTab == 2,
                    onClick = { selectedAnalyticsTab = 2 },
                    text = { Text("দেশ/জিগ্রাফি", style = MaterialTheme.typography.labelMedium) },
                    modifier = Modifier.testTag("tab_country_report")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedAnalyticsTab) {
                0 -> DailyAnalyticsView(monetization)
                1 -> AdFormatAnalyticsView(monetization)
                2 -> CountryAnalyticsView(monetization)
            }
        }
    }
}

@Composable
fun AnalyticsPeriodBox(
    label: String,
    usd: Double,
    bdt: Double,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$${String.format("%.2f", usd)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = "৳${String.format("%.2f", bdt)} BDT",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
fun MetricBadge(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun DailyAnalyticsView(monetization: com.example.model.MonetizationEarnings) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "📊 গত ৭ দিনের দৈনিক আর্নিং এবং ইমপ্রেশন গ্রাফ:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        // Visual Custom Bar Graph representation
        val maxEarning = (monetization.dailyRevenues.maxOfOrNull { it.earningsUsd } ?: 1.0).coerceAtLeast(0.1)

        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(100.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                monetization.dailyRevenues.take(7).reversed().forEach { daily ->
                    val barHeightRatio = (daily.earningsUsd / maxEarning).toFloat().coerceIn(0.15f, 1.0f)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "$${String.format("%.1f", daily.earningsUsd)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(18.dp)
                                .fillMaxHeight(barHeightRatio)
                                .background(
                                    if (daily.date.contains("আজ")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = daily.date.take(5),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Daily Detailed List
        monetization.dailyRevenues.forEach { daily ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = daily.date, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = "${daily.impressions} Views • ${daily.clicks} Clicks • CTR ${daily.ctrPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%.2f", daily.earningsUsd)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "eCPM $${daily.ecpmUsd}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun AdFormatAnalyticsView(monetization: com.example.model.MonetizationEarnings) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "🎯 এড ইউনিট টাইপ অনুযায়ী রেভিনিউ ব্রেকডাউন:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        monetization.adFormatPerformances.forEach { fmt ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fmt.displayName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$${String.format("%.2f", fmt.earningsUsd)} USD",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val totalEarn = monetization.adFormatPerformances.sumOf { it.earningsUsd }.coerceAtLeast(0.01)
                    val percentRatio = (fmt.earningsUsd / totalEarn).toFloat()

                    LinearProgressIndicator(
                        progress = { percentRatio },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "ইমপ্রেশন: ${fmt.impressions}", style = MaterialTheme.typography.labelSmall)
                        Text(text = "ক্লিক: ${fmt.clicks} (CTR ${fmt.ctrPercent}%)", style = MaterialTheme.typography.labelSmall)
                        Text(text = "eCPM: $${fmt.ecpmUsd}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CountryAnalyticsView(monetization: com.example.model.MonetizationEarnings) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "🌐 দেশ ভিত্তিক এড ট্রাফিক ও রেভিনিউ ব্রেকডাউন:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        monetization.countryBreakdowns.forEach { country ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = country.flagEmoji, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = country.countryName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = "ইমপ্রেশন: ${country.impressions} • eCPM $${country.ecpmUsd}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$${String.format("%.2f", country.earningsUsd)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    val bdt = country.earningsUsd * monetization.bdtExchangeRate
                    Text(
                        text = "৳${String.format("%.2f", bdt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
