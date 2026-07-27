package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdminManagementScreen
import com.example.ui.screens.AdminMonetizationScreen
import com.example.ui.screens.AudienceEngagementScreen
import com.example.ui.screens.PublicUserScreen
import com.example.ui.screens.SocialMarketingScreen
import com.example.ui.screens.VideoGrowthScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MarketingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent()
            }
        }
    }
}

enum class AppRole {
    PUBLIC_USER, ADMIN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent() {
    val marketingViewModel: MarketingViewModel = viewModel()
    var currentRole by remember { mutableStateOf(AppRole.PUBLIC_USER) }
    var selectedAdminTab by remember { mutableIntStateOf(0) }

    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var adminPhoneInput by remember { mutableStateOf("") }
    var adminPasswordInput by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }
    var errorMessageText by remember { mutableStateOf("") }

    // Admin Forgot Password State
    var showAdminForgotPassDialog by remember { mutableStateOf(false) }
    var adminForgotPhoneInput by remember { mutableStateOf("") }
    var adminForgotOtpInput by remember { mutableStateOf("") }
    var generatedAdminForgotOtp by remember { mutableStateOf<String?>(null) }
    var newAdminPasswordInput by remember { mutableStateOf("") }
    var adminForgotErrorMsg by remember { mutableStateOf("") }

    val adminTabs = listOf(
        TabItem("সোশ্যাল পোস্ট", Icons.Default.Campaign),
        TabItem("ইউটিউব বুস্ট", Icons.Default.PlayCircle),
        TabItem("ইউজার ও নোটিশ", Icons.Default.AdminPanelSettings),
        TabItem("অডিয়েন্স বট", Icons.Default.Group),
        TabItem("মনিটাইজ ও উইথড্র", Icons.Default.AccountBalance)
    )

    var titleTapCount by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.clickable {
                            if (currentRole == AppRole.PUBLIC_USER) {
                                titleTapCount++
                                if (titleTapCount >= 5) {
                                    titleTapCount = 0
                                    adminPhoneInput = ""
                                    adminPasswordInput = ""
                                    loginError = false
                                    errorMessageText = ""
                                    showAdminLoginDialog = true
                                }
                            }
                        }
                    ) {
                        Text(
                            text = if (currentRole == AppRole.ADMIN) "অ্যাডমিন কন্ট্রোল প্যানেল" else "সোশ্যাল মিডিয়া বুস্টার",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (currentRole == AppRole.ADMIN) "সকল মার্কেটিং ফিচার নিয়ন্ত্রণ করুন" else "পাবলিক কন্টেন্ট অ্যান্ড ভিডিও সার্ভিস",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    if (currentRole == AppRole.ADMIN) {
                        IconButton(
                            onClick = { currentRole = AppRole.PUBLIC_USER },
                            modifier = Modifier.testTag("admin_logout_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Exit Admin Mode",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },

        bottomBar = {
            if (currentRole == AppRole.ADMIN) {
                NavigationBar {
                    adminTabs.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = selectedAdminTab == index,
                            onClick = { selectedAdminTab = index },
                            icon = { Icon(tab.icon, contentDescription = tab.title) },
                            label = { Text(tab.title) },
                            modifier = Modifier.testTag("admin_nav_tab_$index")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentRole == AppRole.PUBLIC_USER) {
                PublicUserScreen(viewModel = marketingViewModel)
            } else {
                when (selectedAdminTab) {
                    0 -> SocialMarketingScreen(viewModel = marketingViewModel)
                    1 -> VideoGrowthScreen(viewModel = marketingViewModel)
                    2 -> AdminManagementScreen(viewModel = marketingViewModel)
                    3 -> AudienceEngagementScreen(viewModel = marketingViewModel)
                    4 -> AdminMonetizationScreen(viewModel = marketingViewModel)
                }
            }
        }
    }

    // Admin Secure Mobile & Password Login Dialog
    if (showAdminLoginDialog) {
        AlertDialog(
            onDismissRequest = { showAdminLoginDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("অ্যাডমিন সিকিউরিটি লগইন")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "সংরক্ষিত এলাকা: এই সেকশনটি শুধুমাত্র অ্যাপ ওনার/অ্যাডমিনের ব্যবহারের জন্য। সাধারণ পাবলিক ইউজারদের প্রবেশের অনুমতি নেই।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    OutlinedTextField(
                        value = adminPhoneInput,
                        onValueChange = {
                            adminPhoneInput = it
                            loginError = false
                        },
                        label = { Text("অ্যাডমিন মোবাইল নম্বর") },
                        placeholder = { Text("যেমন: 01700000000") },
                        isError = loginError,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_phone_input")
                    )

                    OutlinedTextField(
                        value = adminPasswordInput,
                        onValueChange = {
                            adminPasswordInput = it
                            loginError = false
                        },
                        label = { Text("পাসওয়ার্ড / পিন কোড") },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = loginError,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_password_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                adminForgotPhoneInput = adminPhoneInput
                                adminForgotOtpInput = ""
                                generatedAdminForgotOtp = null
                                newAdminPasswordInput = ""
                                adminForgotErrorMsg = ""
                                showAdminForgotPassDialog = true
                            },
                            modifier = Modifier.testTag("admin_forgot_pass_btn")
                        ) {
                            Text(
                                text = "এডমিন পাসওয়ার্ড ভুলে গেছেন?",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = "ডিফল্ট এডমিন তথ্য: ফোন 01700000000, পাসওয়ার্ড 1234",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (loginError) {
                        Text(
                            text = errorMessageText,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmedPhone = adminPhoneInput.trim()
                        val trimmedPass = adminPasswordInput.trim()
                        
                        if (trimmedPhone.isBlank() || trimmedPass.isBlank()) {
                            loginError = true
                            errorMessageText = "মোবাইল নম্বর এবং পাসওয়ার্ড উভয়ই প্রদান করুন।"
                        } else if (marketingViewModel.validateAdminLogin(trimmedPhone, trimmedPass)) {
                            currentRole = AppRole.ADMIN
                            showAdminLoginDialog = false
                            marketingViewModel.addLog("অ্যাডমিন সিকিউরিটি মোবাইল লগইন সফল হয়েছে ($trimmedPhone)।")
                        } else {
                            loginError = true
                            errorMessageText = "ভুল মোবাইল নম্বর অথবা পাসওয়ার্ড! প্রবেশাধিকার অস্বীকৃত।"
                        }
                    },
                    modifier = Modifier.testTag("admin_login_submit_btn")
                ) {
                    Text("অ্যাডমিন লগইন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminLoginDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Admin Forgot Password Dialog
    if (showAdminForgotPassDialog) {
        AlertDialog(
            onDismissRequest = { showAdminForgotPassDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("অ্যাডমিন পাসওয়ার্ড রিসেট")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "অ্যাডমিন মোবাইল নম্বর লিখে সিকিউরিটি OTP পান:",
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = adminForgotPhoneInput,
                        onValueChange = {
                            adminForgotPhoneInput = it
                            adminForgotErrorMsg = ""
                        },
                        label = { Text("অ্যাডমিন মোবাইল নম্বর") },
                        placeholder = { Text("যেমন: 01700000000") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_forgot_phone_input")
                    )

                    if (generatedAdminForgotOtp == null) {
                        Button(
                            onClick = {
                                if (adminForgotPhoneInput.isBlank()) {
                                    adminForgotErrorMsg = "মোবাইল নম্বর প্রদান করুন।"
                                } else {
                                    val code = marketingViewModel.sendAdminResetOtp(adminForgotPhoneInput)
                                    if (code != null) {
                                        generatedAdminForgotOtp = code
                                        adminForgotErrorMsg = ""
                                    } else {
                                        adminForgotErrorMsg = "অ্যাডমিন নম্বর হিসাবে সনাক্ত করা যায়নি।"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("send_admin_forgot_otp_btn")
                        ) {
                            Text("এডমিন OTP পাঠাও")
                        }
                    } else {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "📩 [SMS Security] এডমিন রিসেট OTP: $generatedAdminForgotOtp",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedTextField(
                            value = adminForgotOtpInput,
                            onValueChange = {
                                adminForgotOtpInput = it
                                adminForgotErrorMsg = ""
                            },
                            label = { Text("প্রাপ্ত ৪ ডিজিটের OTP") },
                            placeholder = { Text("যেমন: $generatedAdminForgotOtp") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("admin_forgot_otp_input")
                        )

                        OutlinedTextField(
                            value = newAdminPasswordInput,
                            onValueChange = {
                                newAdminPasswordInput = it
                                adminForgotErrorMsg = ""
                            },
                            label = { Text("নতুন এডমিন পাসওয়ার্ড") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("admin_forgot_new_pass_input")
                        )
                    }

                    if (adminForgotErrorMsg.isNotBlank()) {
                        Text(
                            text = adminForgotErrorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            },
            confirmButton = {
                if (generatedAdminForgotOtp != null) {
                    Button(
                        onClick = {
                            if (adminForgotOtpInput.isBlank() || newAdminPasswordInput.isBlank()) {
                                adminForgotErrorMsg = "OTP এবং নতুন পাসওয়ার্ড উভয়ই আবশ্যক।"
                            } else {
                                val success = marketingViewModel.resetAdminPassword(
                                    mobileNumber = adminForgotPhoneInput,
                                    enteredOtp = adminForgotOtpInput,
                                    newPassword = newAdminPasswordInput
                                )
                                if (success) {
                                    showAdminForgotPassDialog = false
                                    adminPasswordInput = newAdminPasswordInput
                                    adminPhoneInput = adminForgotPhoneInput
                                    marketingViewModel.addLog("এডমিন পাসওয়ার্ড সফলভাবে রিসেট করা হয়েছে।")
                                } else {
                                    adminForgotErrorMsg = "ভুল OTP কোড! সঠিক কোড দিন ($generatedAdminForgotOtp)।"
                                }
                            }
                        },
                        modifier = Modifier.testTag("submit_admin_reset_pass_btn")
                    ) {
                        Text("এডমিন পাসওয়ার্ড আপডেট করুন")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminForgotPassDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

data class TabItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)


