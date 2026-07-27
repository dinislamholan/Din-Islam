package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.ui.components.AdMobBannerAdView
import com.example.viewmodel.MarketingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicAuthScreen(
    viewModel: MarketingViewModel,
    onLoginSuccess: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Login, 1 = Register
    val adConfig by viewModel.adSettingsConfig.collectAsState()

    // Login fields
    var loginPhone by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginErrorMsg by remember { mutableStateOf("") }

    // Register fields
    var regName by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regErrorMsg by remember { mutableStateOf("") }

    // OTP Verification State
    var showOtpDialog by remember { mutableStateOf(false) }
    var generatedOtpCode by remember { mutableStateOf("") }
    var enteredOtpInput by remember { mutableStateOf("") }
    var otpErrorMsg by remember { mutableStateOf("") }

    // Forgot Password State
    var showForgotPassDialog by remember { mutableStateOf(false) }
    var forgotPhone by remember { mutableStateOf("") }
    var forgotOtpInput by remember { mutableStateOf("") }
    var generatedForgotOtp by remember { mutableStateOf<String?>(null) }
    var newPasswordInput by remember { mutableStateOf("") }
    var forgotErrorMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "পাবলিক ইউজার পোর্টাল",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "ভিডিও বুস্ট, সোশ্যাল শেয়ার এবং পয়েন্ট অর্জনের জন্য লগইন বা সাইন আপ করুন",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { 
                            selectedTab = 0 
                            loginErrorMsg = ""
                        },
                        text = { Text("লগইন") },
                        modifier = Modifier.testTag("tab_public_login")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { 
                            selectedTab = 1 
                            regErrorMsg = ""
                        },
                        text = { Text("নতুন অ্যাকাউন্ট (সাইন আপ)") },
                        modifier = Modifier.testTag("tab_public_signup")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (selectedTab == 0) {
                    // LOGIN FORM
                    OutlinedTextField(
                        value = loginPhone,
                        onValueChange = { 
                            loginPhone = it
                            loginErrorMsg = ""
                        },
                        label = { Text("মোবাইল নম্বর") },
                        placeholder = { Text("যেমন: 01711111111") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("public_login_phone_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { 
                            loginPassword = it
                            loginErrorMsg = ""
                        },
                        label = { Text("পাসওয়ার্ড") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("public_login_pass_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                forgotPhone = loginPhone
                                forgotOtpInput = ""
                                generatedForgotOtp = null
                                newPasswordInput = ""
                                forgotErrorMsg = ""
                                showForgotPassDialog = true
                            },
                            modifier = Modifier.testTag("public_forgot_pass_btn")
                        ) {
                            Text(
                                text = "পাসওয়ার্ড ভুলে গেছেন?",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (loginErrorMsg.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = loginErrorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (loginPhone.isBlank() || loginPassword.isBlank()) {
                                loginErrorMsg = "মোবাইল নম্বর ও পাসওয়ার্ড প্রদান করুন।"
                            } else {
                                val success = viewModel.loginPublicUser(loginPhone, loginPassword)
                                if (success) {
                                    onLoginSuccess()
                                } else {
                                    loginErrorMsg = "ভুল নম্বর বা পাসওয়ার্ড! পূর্বে অ্যাকাউন্ট তৈরি না করে থাকলে সাইন আপ করুন।"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("public_login_submit_btn")
                    ) {
                        Text("লগইন করুন")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "💡 ডেমো একাউন্ট: নম্বর 01711111111, পাসওয়ার্ড 123456",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                } else {
                    // SIGN UP FORM
                    OutlinedTextField(
                        value = regName,
                        onValueChange = { 
                            regName = it
                            regErrorMsg = ""
                        },
                        label = { Text("আপনার সম্পূর্ণ নাম") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("public_reg_name_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = regPhone,
                        onValueChange = { 
                            regPhone = it
                            regErrorMsg = ""
                        },
                        label = { Text("মোবাইল নম্বর") },
                        placeholder = { Text("যেমন: 01712345678") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("public_reg_phone_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = { 
                            regPassword = it
                            regErrorMsg = ""
                        },
                        label = { Text("নতুন পাসওয়ার্ড") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("public_reg_pass_input")
                    )

                    if (regErrorMsg.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = regErrorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (regName.isBlank() || regPhone.isBlank() || regPassword.isBlank()) {
                                regErrorMsg = "অনুগ্রহ করে সকল ফিল্ড পূরণ করুন।"
                            } else if (regPhone.length < 10) {
                                regErrorMsg = "সঠিক মোবাইল নম্বর প্রবেশ করান (কমপক্ষে ১০ ডিজিট)।"
                            } else {
                                generatedOtpCode = viewModel.sendRegistrationOtp(
                                    fullName = regName,
                                    mobileNumber = regPhone,
                                    password = regPassword
                                )
                                enteredOtpInput = ""
                                otpErrorMsg = ""
                                showOtpDialog = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("public_reg_submit_btn")
                    ) {
                        Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ওটিপি পাঠান এবং সাইন আপ করুন")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Persistent Bottom Banner Ad on Public Auth Screen
        if (adConfig.isAdMobMasterEnabled && adConfig.isBannerAdEnabled) {
            AdMobBannerAdView(
                adUnitId = adConfig.bannerAdUnitId,
                onAdClick = {
                    viewModel.recordAdImpression()
                    viewModel.addLog("পাবলিক অথ সাইন-ইন পেজে ব্যানার এড ক্লিকে ইমপ্রেশন রেকর্ড করা হলো!")
                }
            )
        }
    }

    // OTP VERIFICATION DIALOG
    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("মোবাইল ওটিপি ভেরিফিকেশন")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Realistic Simulated SMS Notification Box
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sms, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "📩 [SMS সিমুলেশন] ভেরিফিকেশন মেসেজ",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "আপনার মোবাইল নম্বর ($regPhone) এ পাঠানো ৪ ডিজিটের OTP কোড হলো: $generatedOtpCode",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }

                    Text(
                        text = "নিচের বক্সে SMS-এ প্রাপ্ত ৪ ডিজিটের OTP কোডটি বসিয়ে অ্যাকাউন্ট সক্রিয় করুন:",
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = enteredOtpInput,
                        onValueChange = { 
                            enteredOtpInput = it
                            otpErrorMsg = ""
                        },
                        label = { Text("৪ ডিজিটের OTP কোড") },
                        placeholder = { Text("যেমন: $generatedOtpCode") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("otp_code_input")
                    )

                    if (otpErrorMsg.isNotBlank()) {
                        Text(
                            text = otpErrorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val verified = viewModel.verifyRegistrationOtp(enteredOtpInput.trim())
                        if (verified) {
                            showOtpDialog = false
                            onLoginSuccess()
                        } else {
                            otpErrorMsg = "ভুল OTP কোড! সঠিক কোডটি প্রদান করুন ($generatedOtpCode)।"
                        }
                    },
                    modifier = Modifier.testTag("confirm_otp_btn")
                ) {
                    Text("ভেরিফাই ও সাইন আপ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // FORGOT PASSWORD DIALOG
    if (showForgotPassDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPassDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("পাসওয়ার্ড রিসেট করুন")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "আপনার রেজিস্টারকৃত মোবাইল নম্বর লিখুন। আমরা আপনাকে একটি OTP পাঠাব।",
                        style = MaterialTheme.typography.bodySmall
                    )

                    OutlinedTextField(
                        value = forgotPhone,
                        onValueChange = { 
                            forgotPhone = it
                            forgotErrorMsg = ""
                        },
                        label = { Text("রেজিস্টারকৃত মোবাইল নম্বর") },
                        placeholder = { Text("যেমন: 01711111111") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("forgot_phone_input")
                    )

                    if (generatedForgotOtp == null) {
                        Button(
                            onClick = {
                                if (forgotPhone.isBlank()) {
                                    forgotErrorMsg = "মোবাইল নম্বর লিখুন।"
                                } else {
                                    val code = viewModel.sendPublicResetOtp(forgotPhone)
                                    if (code != null) {
                                        generatedForgotOtp = code
                                        forgotErrorMsg = ""
                                    } else {
                                        forgotErrorMsg = "এই মোবাইল নম্বরটি সিস্টেমে খুঁজে পাওয়া যায়নি।"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("send_forgot_otp_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("OTP পাঠাও")
                        }
                    } else {
                        // Simulated SMS Notice
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "📩 [SMS] রিসেট OTP কোড: $generatedForgotOtp",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedTextField(
                            value = forgotOtpInput,
                            onValueChange = { 
                                forgotOtpInput = it
                                forgotErrorMsg = ""
                            },
                            label = { Text("প্রাপ্ত ৪ ডিজিটের OTP") },
                            placeholder = { Text("যেমন: $generatedForgotOtp") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("forgot_otp_input")
                        )

                        OutlinedTextField(
                            value = newPasswordInput,
                            onValueChange = { 
                                newPasswordInput = it
                                forgotErrorMsg = ""
                            },
                            label = { Text("নতুন পাসওয়ার্ড") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("forgot_new_pass_input")
                        )
                    }

                    if (forgotErrorMsg.isNotBlank()) {
                        Text(
                            text = forgotErrorMsg,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            },
            confirmButton = {
                if (generatedForgotOtp != null) {
                    Button(
                        onClick = {
                            if (forgotOtpInput.isBlank() || newPasswordInput.isBlank()) {
                                forgotErrorMsg = "OTP এবং নতুন পাসওয়ার্ড উভয়ই আবশ্যক।"
                            } else {
                                val success = viewModel.resetPublicPassword(
                                    mobileNumber = forgotPhone,
                                    enteredOtp = forgotOtpInput,
                                    newPassword = newPasswordInput
                                )
                                if (success) {
                                    showForgotPassDialog = false
                                    viewModel.loginPublicUser(forgotPhone, newPasswordInput)
                                    onLoginSuccess()
                                } else {
                                    forgotErrorMsg = "ভুল OTP কোড! সঠিক কোড দিন ($generatedForgotOtp)।"
                                }
                            }
                        },
                        modifier = Modifier.testTag("submit_reset_pass_btn")
                    ) {
                        Text("পাসওয়ার্ড আপডেট করুন")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPassDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
