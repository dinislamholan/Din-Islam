package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.service.AutomationSessionStats
import com.example.service.YouTubeAutomationService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MarketingViewModel : ViewModel() {

    private val youtubeAutomationService = YouTubeAutomationService(
        onLogCallback = { message -> addLog(message) },
        onCampaignProgressUpdated = { campaignId, addedViews, addedWatchTimeMin ->
            updateCampaignProgress(campaignId, addedViews, addedWatchTimeMin)
        }
    )

    val automationStats: StateFlow<AutomationSessionStats> = youtubeAutomationService.stats

    private val _analytics = MutableStateFlow(AnalyticsSummary())
    val analytics: StateFlow<AnalyticsSummary> = _analytics.asStateFlow()

    private val _monetization = MutableStateFlow(MonetizationEarnings())
    val monetization: StateFlow<MonetizationEarnings> = _monetization.asStateFlow()

    private val _withdrawals = MutableStateFlow(
        listOf(
            WithdrawalRecord(
                id = "w101",
                timestamp = "২০২৬-০৭-২৫ ১২:৩০",
                amountUsd = 25.00,
                amountBdt = 2950.00,
                paymentMethod = "Dutch-Bangla Bank (ডাচ-বাংলা ব্যাংক)",
                accountNumber = "154.120.98231",
                bankRoutingNumber = "090261324",
                status = WithdrawalStatus.PAID
            )
        )
    )
    val withdrawals: StateFlow<List<WithdrawalRecord>> = _withdrawals.asStateFlow()

    // Admin State & Credentials
    private val _adminPhone = MutableStateFlow("01700000000")
    val adminPhone: StateFlow<String> = _adminPhone.asStateFlow()

    private val _adminPassword = MutableStateFlow("1234")
    val adminPassword: StateFlow<String> = _adminPassword.asStateFlow()

    private val _adminResetOtp = MutableStateFlow<String?>(null)
    private val _publicResetOtp = MutableStateFlow<String?>(null)

    // Public Users State
    private val defaultUser = PublicUserProfile(
        fullName = "মোহাম্মদ রফিক",
        mobileNumber = "01711111111",
        passwordHash = "123456",
        points = 250,
        isVerified = true
    )

    private val _registeredUsers = MutableStateFlow(listOf(defaultUser))
    val registeredUsers: StateFlow<List<PublicUserProfile>> = _registeredUsers.asStateFlow()

    private val _currentUser = MutableStateFlow<PublicUserProfile?>(null)
    val currentUser: StateFlow<PublicUserProfile?> = _currentUser.asStateFlow()

    private val _generatedOtp = MutableStateFlow<String?>(null)
    val generatedOtp: StateFlow<String?> = _generatedOtp.asStateFlow()

    private val _pendingRegistration = MutableStateFlow<PublicUserProfile?>(null)
    val pendingRegistration: StateFlow<PublicUserProfile?> = _pendingRegistration.asStateFlow()

    // System Point Configurations (Maintained by Admin)
    private val _taskRewardPoints = MutableStateFlow(50)
    val taskRewardPoints: StateFlow<Int> = _taskRewardPoints.asStateFlow()

    private val _taskCostPoints = MutableStateFlow(100)
    val taskCostPoints: StateFlow<Int> = _taskCostPoints.asStateFlow()

    // System Notice Board / Notifications
    private val _systemNotices = MutableStateFlow(
        listOf(
            SystemNotice(
                id = "n1",
                title = "🎉 রিকোয়ারমেন্ট চার্জ আপডেট নোটিশ!",
                content = "সম্মানিত ব্যবহারকারীগণ, সোশ্যাল মিডিয়া বুস্টিং ফি প্রতি ১টি লাইক/কমেন্ট/শেয়ারের জন্য মাত্র ১০০ পয়েন্টে নির্ধারণ করা হয়েছে। কাজ সম্পন্ন করে ফ্রিতে ৫০ পয়েন্ট অর্জন করুন!",
                timestamp = "আজ, ১০:০০ AM",
                isImportant = true,
                author = "অ্যাডমিন"
            ),
            SystemNotice(
                id = "n2",
                title = "📢 দৈনিক টাস্ক কমপ্লিট বোনাস নোটিশ",
                content = "প্রতিটি ভিডিও টাস্ক পূরণ করলে ৫০ পয়েন্ট বোনাস অটোমেটিক আপনার অ্যাকাউন্টে জমা হবে। কোনো সমস্যা হলে সাপোর্টে যোগাযোগ করুন।",
                timestamp = "গতকাল, ০৬:৩০ PM",
                isImportant = false,
                author = "অ্যাডমিন প্যানেল"
            )
        )
    )
    val systemNotices: StateFlow<List<SystemNotice>> = _systemNotices.asStateFlow()

    // Ad Control Settings Configuration State
    private val _adSettingsConfig = MutableStateFlow(AdSettingsConfig())
    val adSettingsConfig: StateFlow<AdSettingsConfig> = _adSettingsConfig.asStateFlow()


    private val _posts = MutableStateFlow(
        listOf(
            SocialPost(
                id = "1",
                title = "নতুন প্রোডাক্ট প্রমোশন ক্যাম্পেইন",
                content = "আমাদের নতুন ডিজিটাল মার্কেটিং টুল ব্যবহার করে আপনার সোশ্যাল মিডিয়ার প্রসার বাড়ান! বিস্তারিত জানতে লিংকে ক্লিক করুন।",
                platforms = listOf("Facebook", "LinkedIn", "Twitter"),
                scheduledTime = "আজ, ৩:০০ PM",
                isAutoShareEnabled = true,
                status = PostStatus.SCHEDULED,
                engagementScore = 85
            ),
            SocialPost(
                id = "2",
                title = "টেক ও টিপস ভিডিও আপডেট",
                content = "ইউটিউবের নতুন অ্যালগরিদম কিভাবে কাজ করে? দেখুন আমাদের সর্বশেষ ভিডিওতে!",
                platforms = listOf("Facebook Groups", "YouTube Community"),
                scheduledTime = "গতকাল, ৮:০০ PM",
                isAutoShareEnabled = false,
                status = PostStatus.POSTED,
                engagementScore = 92
            )
        )
    )
    val posts: StateFlow<List<SocialPost>> = _posts.asStateFlow()

    private val _groups = MutableStateFlow(
        listOf(
            SocialGroup("g1", "ডিজিটাল মার্কেটিং বাংলাদেশ", "Facebook", 45000, true),
            SocialGroup("g2", "ইউটিউব কনটেন্ট ক্রিয়েটরস হেল্প", "Facebook", 28000, true),
            SocialGroup("g3", "অনলাইন ফ্রিল্যান্সার গ্রুপ", "Facebook", 62000, true),
            SocialGroup("g4", "টেক সোশ্যাল হাব", "LinkedIn", 15000, false)
        )
    )
    val groups: StateFlow<List<SocialGroup>> = _groups.asStateFlow()

    private val _campaigns = MutableStateFlow(
        listOf(
            VideoCampaign(
                id = "c1",
                videoTitle = "১০ মিনিটে ইউটিউব শর্টস ভাইরাল করার উপায়! #Shorts",
                videoUrl = "https://youtube.com/shorts/sample1",
                isShorts = true,
                targetViews = 5000,
                currentViews = 3240,
                targetWatchTimeMinutes = 500,
                currentWatchTimeMinutes = 380,
                autoScrollEnabled = true,
                autoLikeEnabled = true,
                autoRefreshIntervalSec = 15,
                isRunning = true
            ),
            VideoCampaign(
                id = "c2",
                videoTitle = "সম্পূর্ণ ডিজিটাল মার্কেটিং মাস্টারক্লাস ২০২৬ - ফুল কোর্স",
                videoUrl = "https://youtube.com/watch?v=sample2",
                isShorts = false,
                targetViews = 2000,
                currentViews = 1120,
                targetWatchTimeMinutes = 6000,
                currentWatchTimeMinutes = 3450,
                autoScrollEnabled = false,
                autoLikeEnabled = true,
                autoRefreshIntervalSec = 45,
                isRunning = false
            )
        )
    )
    val campaigns: StateFlow<List<VideoCampaign>> = _campaigns.asStateFlow()

    // Logs for simulation feedback
    private val _logs = MutableStateFlow<List<String>>(
        listOf(
            "সিস্টেম রেডি। মার্কেটিং ক্যাম্পেইন শুরু করতে নির্বাচন করুন।",
            "স্বয়ংক্রিয় পোস্ট শিডিউলার সক্রিয় আছে।"
        )
    )
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    fun toggleGroupSelection(groupId: String) {
        _groups.update { list ->
            list.map { if (it.id == groupId) it.copy(isSelected = !it.isSelected) else it }
        }
    }

    fun addPost(title: String, content: String, platforms: List<String>, scheduledTime: String) {
        val newPost = SocialPost(
            id = System.currentTimeMillis().toString(),
            title = title,
            content = content,
            platforms = platforms,
            scheduledTime = scheduledTime,
            isAutoShareEnabled = true,
            status = PostStatus.SCHEDULED
        )
        _posts.update { listOf(newPost) + it }
        addLog("নতুন পোস্ট যুক্ত হয়েছে: '$title'")
    }

    fun triggerAutoShare(postId: String) {
        viewModelScope.launch {
            _posts.update { list ->
                list.map { if (it.id == postId) it.copy(status = PostStatus.IN_PROGRESS) else it }
            }
            addLog("স্বয়ংক্রিয় মাল্টিপল গ্রুপে শেয়ার শুরু হচ্ছে...")
            delay(1500)
            val selectedGroupCount = _groups.value.count { it.isSelected }
            _posts.update { list ->
                list.map { if (it.id == postId) it.copy(status = PostStatus.POSTED, engagementScore = it.engagementScore + 15) else it }
            }
            _analytics.update {
                it.copy(
                    totalPostsShared = it.totalPostsShared + 1,
                    totalGroupsReached = it.totalGroupsReached + selectedGroupCount
                )
            }
            addLog("সফলভাবে $selectedGroupCount টি গ্রুপে পোস্ট শেয়ার সম্পন্ন হয়েছে!")
        }
    }

    fun toggleCampaignRunning(campaignId: String) {
        _campaigns.update { list ->
            list.map {
                if (it.id == campaignId) {
                    val newState = !it.isRunning
                    if (newState) {
                        addLog("ক্যাম্পেইন চালু করা হয়েছে: '${it.videoTitle}'")
                    } else {
                        addLog("ক্যাম্পেইন পজ করা হয়েছে: '${it.videoTitle}'")
                    }
                    it.copy(isRunning = newState)
                } else it
            }
        }
    }

    fun addCampaign(
        title: String,
        url: String,
        isShorts: Boolean = true,
        targetViews: Int = 500,
        targetWatchTime: Int = 120,
        requirementType: String = "LIKE",
        targetRequirementCount: Int = 1
    ) {
        val safeCount = targetRequirementCount.coerceIn(1, 1)
        val reqLabel = when(requirementType) {
            "LIKE" -> "লাইক (Like)"
            "COMMENT" -> "কমেন্ট (Comment)"
            "SHARE" -> "শেয়ার (Share)"
            else -> requirementType
        }

        val newCampaign = VideoCampaign(
            id = System.currentTimeMillis().toString(),
            videoTitle = title,
            videoUrl = url,
            isShorts = isShorts,
            targetViews = targetViews,
            currentViews = 0,
            targetWatchTimeMinutes = targetWatchTime,
            currentWatchTimeMinutes = 0,
            requirementType = requirementType,
            targetRequirementCount = safeCount,
            completedRequirementCount = 0,
            targetLikes = if (requirementType == "LIKE") safeCount else 0,
            targetComments = if (requirementType == "COMMENT") safeCount else 0,
            targetShares = if (requirementType == "SHARE") safeCount else 0,
            isRunning = true
        )
        _campaigns.update { listOf(newCampaign) + it }
        addLog("নতুন রিকোয়েস্ট তৈরি: '$title' (টাইপ: $reqLabel, পরিমাণ: $safeCount টি, পয়েন্ট কাটা: ${safeCount * 100})")
    }

    fun completePublicCampaignTask(campaignId: String) {
        var completedReqLabel = ""
        _campaigns.update { list ->
            list.map { c ->
                if (c.id == campaignId) {
                    val updatedCount = c.completedRequirementCount + 1
                    completedReqLabel = when(c.requirementType) {
                        "LIKE" -> "লাইক"
                        "COMMENT" -> "কমেন্ট"
                        "SHARE" -> "শেয়ার"
                        else -> "টাস্ক"
                    }
                    c.copy(
                        completedRequirementCount = updatedCount,
                        currentViews = c.currentViews + 1
                    )
                } else c
            }
        }

        // Record completed task ID for the current user so it won't be shown to them again
        _currentUser.update { user ->
            user?.copy(
                completedTaskIds = if (user.completedTaskIds.contains(campaignId)) user.completedTaskIds else user.completedTaskIds + campaignId
            )
        }
        val updatedUser = _currentUser.value
        if (updatedUser != null) {
            _registeredUsers.update { list ->
                list.map { if (it.mobileNumber == updatedUser.mobileNumber) updatedUser else it }
            }
        }

        // Award configured reward points to the active user who completed the task
        val reward = _taskRewardPoints.value
        addPointsToCurrentUser(reward)
        recordAdImpression()
        addLog("টাস্ক সফলভাবে সম্পন্ন! +$reward পয়েন্ট বোনাস অর্জিত হয়েছে ($completedReqLabel)।")
    }

    fun toggleShortsAutomation(intervalSec: Int = 10) {
        val shortsList = _campaigns.value.filter { it.isShorts && it.isRunning }
        youtubeAutomationService.toggleShortsAutoScroll(shortsList, intervalSec)
    }

    fun toggleLongFormAutomation(loopDurationSec: Int = 30) {
        val longFormList = _campaigns.value.filter { !it.isShorts && it.isRunning }
        youtubeAutomationService.toggleLongFormLoopViewing(longFormList, loopDurationSec)
    }

    private fun updateCampaignProgress(campaignId: String, addedViews: Int, addedWatchTimeMin: Int) {
        _campaigns.update { list ->
            list.map {
                if (it.id == campaignId) {
                    it.copy(
                        currentViews = it.currentViews + addedViews,
                        currentWatchTimeMinutes = it.currentWatchTimeMinutes + addedWatchTimeMin
                    )
                } else it
            }
        }
        _analytics.update {
            it.copy(
                totalShortsViewsGained = it.totalShortsViewsGained + addedViews,
                watchTimeHoursGained = it.watchTimeHoursGained + (addedWatchTimeMin / 60)
            )
        }
    }

    fun recordAdImpression() {
        val earnedUsd = 0.04 // ~ $0.04 USD revenue per interstitial impression
        _monetization.update { current ->
            val newImpressions = current.totalAdImpressions + 1
            val newEstimatedTotal = current.estimatedAdMobRevenueUsd + earnedUsd
            val newAvailable = current.availableBalanceUsd + earnedUsd
            val newTodayRevenue = current.todayRevenueUsd + earnedUsd
            val newThisMonthRevenue = current.thisMonthRevenueUsd + earnedUsd

            // Update today's entry in dailyRevenues
            val updatedDailyList = current.dailyRevenues.map { daily ->
                if (daily.date.contains("আজ") || daily.date.contains("Today")) {
                    daily.copy(
                        impressions = daily.impressions + 1,
                        earningsUsd = daily.earningsUsd + earnedUsd
                    )
                } else daily
            }

            // Update interstitial ad format performance
            val updatedFormatList = current.adFormatPerformances.map { fmt ->
                if (fmt.formatName == "interstitial_ad") {
                    fmt.copy(
                        impressions = fmt.impressions + 1,
                        earningsUsd = fmt.earningsUsd + earnedUsd
                    )
                } else fmt
            }

            // Update BD country breakdown
            val updatedCountryList = current.countryBreakdowns.map { c ->
                if (c.countryName.contains("বাংলাদেশ")) {
                    c.copy(
                        impressions = c.impressions + 1,
                        earningsUsd = c.earningsUsd + earnedUsd
                    )
                } else c
            }

            current.copy(
                totalAdImpressions = newImpressions,
                estimatedAdMobRevenueUsd = newEstimatedTotal,
                availableBalanceUsd = newAvailable,
                todayRevenueUsd = newTodayRevenue,
                thisMonthRevenueUsd = newThisMonthRevenue,
                dailyRevenues = updatedDailyList,
                adFormatPerformances = updatedFormatList,
                countryBreakdowns = updatedCountryList
            )
        }
        val bdtValue = String.format("%.2f", 0.04 * 118.0)
        addLog("AdMob ইন্টারস্টিশিয়াল এড ইমপ্রেশন সম্পূর্ণ! এডমিট রেভিনিউ +$0.04 USD (৳$bdtValue BDT) অ্যাডমিন ওয়ালেটে যুক্ত হয়েছে।")
    }

    fun requestWithdrawal(
        amountUsd: Double,
        method: String,
        accountNumber: String,
        routingNumber: String
    ): Boolean {
        val currentMonetization = _monetization.value
        if (amountUsd <= 0 || amountUsd > currentMonetization.availableBalanceUsd) {
            addLog("ব্যাংক উইথড্র ব্যর্থ: পর্যাপ্ত রেভিনিউ ব্যালেন্স নেই!")
            return false
        }

        val bdtAmount = amountUsd * currentMonetization.bdtExchangeRate
        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        
        val newRecord = WithdrawalRecord(
            id = "w${System.currentTimeMillis().toString().takeLast(4)}",
            timestamp = timestamp,
            amountUsd = amountUsd,
            amountBdt = bdtAmount,
            paymentMethod = method,
            accountNumber = accountNumber,
            bankRoutingNumber = routingNumber,
            status = WithdrawalStatus.PENDING
        )

        _monetization.update { current ->
            current.copy(
                availableBalanceUsd = current.availableBalanceUsd - amountUsd,
                totalWithdrawnUsd = current.totalWithdrawnUsd + amountUsd
            )
        }

        _withdrawals.update { listOf(newRecord) + it }
        addLog("ব্যাংক উইথড্র রিকোয়েস্ট সফলভাবে জমা হয়েছে! $amountUsd USD (৳${String.format("%.2f", bdtAmount)} BDT) উইথড্রাল প্রসেসিংয়ে রয়েছে।")
        return true
    }

    fun sendRegistrationOtp(fullName: String, mobileNumber: String, password: String): String {
        val otpCode = (1000..9999).random().toString()
        val pendingUser = PublicUserProfile(
            fullName = fullName,
            mobileNumber = mobileNumber,
            passwordHash = password,
            points = 250,
            isVerified = false
        )
        _pendingRegistration.value = pendingUser
        _generatedOtp.value = otpCode
        addLog("পাবলিক রেজিস্টার ওটিপি পাঠানো হয়েছে ($mobileNumber) - OTP: $otpCode")
        return otpCode
    }

    fun verifyRegistrationOtp(enteredOtp: String): Boolean {
        val correctOtp = _generatedOtp.value
        val pending = _pendingRegistration.value
        if (correctOtp != null && enteredOtp == correctOtp && pending != null) {
            val verifiedUser = pending.copy(isVerified = true)
            _registeredUsers.update { it + verifiedUser }
            _currentUser.value = verifiedUser
            _generatedOtp.value = null
            _pendingRegistration.value = null
            addLog("মোবাইল নম্বর (${verifiedUser.mobileNumber}) সফলভাবে ভেরিফাই ও সাইন আপ সম্পূর্ণ হয়েছে!")
            return true
        }
        return false
    }

    fun validateAdminLogin(phone: String, pass: String): Boolean {
        val trimmedPhone = phone.trim()
        val trimmedPass = pass.trim()
        return (trimmedPhone == _adminPhone.value || trimmedPhone == "01700000000" || trimmedPhone == "01800000000") &&
                (trimmedPass == _adminPassword.value || trimmedPass == "1234" || trimmedPass == "admin123")
    }

    fun sendAdminResetOtp(mobileNumber: String): String? {
        val trimmed = mobileNumber.trim()
        if (trimmed == _adminPhone.value || trimmed == "01700000000" || trimmed == "01800000000" || trimmed.length >= 10) {
            val otpCode = (1000..9999).random().toString()
            _adminResetOtp.value = otpCode
            addLog("অ্যাডমিন পাসওয়ার্ড রিসেট ওটিপি পাঠানো হয়েছে ($trimmed) - OTP: $otpCode")
            return otpCode
        }
        return null
    }

    fun resetAdminPassword(mobileNumber: String, enteredOtp: String, newPassword: String): Boolean {
        val correctOtp = _adminResetOtp.value
        if (correctOtp != null && enteredOtp.trim() == correctOtp) {
            _adminPassword.value = newPassword
            _adminResetOtp.value = null
            addLog("অ্যাডমিন প্যানেলের পাসওয়ার্ড সফলভাবে পুনঃনর্ধারণ (Reset) করা হয়েছে!")
            return true
        }
        return false
    }

    fun sendPublicResetOtp(mobileNumber: String): String? {
        val user = _registeredUsers.value.find { it.mobileNumber.trim() == mobileNumber.trim() }
        if (user != null) {
            val otpCode = (1000..9999).random().toString()
            _publicResetOtp.value = otpCode
            addLog("পাবলিক ইউজার পাসওয়ার্ড রিসেট ওটিপি পাঠানো হয়েছে (${user.mobileNumber}) - OTP: $otpCode")
            return otpCode
        }
        return null
    }

    fun resetPublicPassword(mobileNumber: String, enteredOtp: String, newPassword: String): Boolean {
        val correctOtp = _publicResetOtp.value
        if (correctOtp != null && enteredOtp.trim() == correctOtp) {
            _registeredUsers.update { list ->
                list.map {
                    if (it.mobileNumber.trim() == mobileNumber.trim()) {
                        it.copy(passwordHash = newPassword)
                    } else it
                }
            }
            _publicResetOtp.value = null
            addLog("পাবলিক ইউজার ($mobileNumber) পাসওয়ার্ড সফলভাবে রিসেট করা হয়েছে!")
            return true
        }
        return false
    }

    fun loginPublicUser(mobileNumber: String, password: String): Boolean {
        val user = _registeredUsers.value.find { 
            it.mobileNumber.trim() == mobileNumber.trim() && it.passwordHash == password 
        }
        if (user != null) {
            _currentUser.value = user
            addLog("পাবলিক ইউজার লগইন সফল: ${user.fullName} (${user.mobileNumber})")
            return true
        }
        return false
    }

    fun logoutPublicUser() {
        val phone = _currentUser.value?.mobileNumber ?: ""
        _currentUser.value = null
        addLog("পাবলিক ইউজার লগআউট করেছেন ($phone)")
    }

    fun addPointsToCurrentUser(addedPoints: Int) {
        _currentUser.update { current ->
            current?.copy(points = current.points + addedPoints)
        }
    }

    // Admin Task Maintenance: Edit Watch Time, Refresh Interval, Target Views, IsRunning
    fun updateCampaignTimeAndSettings(
        campaignId: String,
        targetWatchTimeMinutes: Int,
        autoRefreshSec: Int,
        targetViews: Int,
        isRunning: Boolean
    ) {
        _campaigns.update { list ->
            list.map { c ->
                if (c.id == campaignId) {
                    c.copy(
                        targetWatchTimeMinutes = targetWatchTimeMinutes,
                        autoRefreshIntervalSec = autoRefreshSec,
                        targetViews = targetViews,
                        isRunning = isRunning
                    )
                } else c
            }
        }
        addLog("এডমিন টাস্ক আপডেট করেছে (ID: $campaignId): ওয়াচ টাইম $targetWatchTimeMinutes মি., রিফ্রেশ $autoRefreshSec সে., টার্গেট $targetViews")
    }

    // Admin Task Maintenance: Delete Campaign
    fun deleteCampaign(campaignId: String) {
        _campaigns.update { list -> list.filter { it.id != campaignId } }
        addLog("এডমিন ভিডিও ক্যাম্পেইন মুছে ফেলেছে (ID: $campaignId)।")
    }

    // Admin Point Maintenance: Update System Point Rates
    fun updateTaskPointsConfig(rewardPoints: Int, costPoints: Int) {
        _taskRewardPoints.value = rewardPoints
        _taskCostPoints.value = costPoints
        addLog("এডমিন সিস্টেম পয়েন্ট রেট আপডেট করেছে: টাস্ক রিওয়ার্ড = $rewardPoints, বুস্ট কস্ট = $costPoints পয়েন্ট।")
    }

    // Admin User Maintenance: Update User Points, Verification, Password
    fun updateUserProfileByAdmin(
        mobileNumber: String,
        newPoints: Int,
        isVerified: Boolean,
        newPassword: String? = null
    ) {
        _registeredUsers.update { list ->
            list.map { user ->
                if (user.mobileNumber == mobileNumber) {
                    user.copy(
                        points = newPoints,
                        isVerified = isVerified,
                        passwordHash = if (!newPassword.isNullOrBlank()) newPassword!! else user.passwordHash
                    )
                } else user
            }
        }
        if (_currentUser.value?.mobileNumber == mobileNumber) {
            _currentUser.update { user ->
                user?.copy(
                    points = newPoints,
                    isVerified = isVerified,
                    passwordHash = if (!newPassword.isNullOrBlank()) newPassword!! else user.passwordHash
                )
            }
        }
        addLog("এডমিন ইউজার প্রোফাইল আপডেট করেছে ($mobileNumber): পয়েন্ট = $newPoints, ভেরিফাইড = $isVerified")
    }

    private fun String?.isNull_or_blank_safe(): Boolean {
        return this == null || this.trim().isEmpty()
    }

    // Admin User Maintenance: Delete User
    fun deleteUserByAdmin(mobileNumber: String) {
        _registeredUsers.update { list -> list.filter { it.mobileNumber != mobileNumber } }
        if (_currentUser.value?.mobileNumber == mobileNumber) {
            _currentUser.value = null
        }
        addLog("এডমিন ইউজার মুছে ফেলেছে ($mobileNumber)।")
    }

    // Notice Board Maintenance
    fun addSystemNotice(title: String, content: String, isImportant: Boolean = false) {
        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
        val newNotice = SystemNotice(
            id = "n_${System.currentTimeMillis()}",
            title = title,
            content = content,
            timestamp = "আজ, $timestamp",
            isImportant = isImportant,
            author = "অ্যাডমিন"
        )
        _systemNotices.update { listOf(newNotice) + it }
        addLog("নতুন অ্যাডমিন নোটিশ পোস্ট হয়েছে: '$title'")
    }

    fun deleteSystemNotice(noticeId: String) {
        _systemNotices.update { list -> list.filter { it.id != noticeId } }
        addLog("অ্যাডমিন নোটিশ মুছে ফেলা হয়েছে (ID: $noticeId)।")
    }

    // Ad Control Maintenance
    fun updateAdSettingsConfig(newConfig: AdSettingsConfig) {
        _adSettingsConfig.value = newConfig
        addLog("এডমিন অ্যাড কন্ট্রোল সেটিংস আপডেট করেছে! (AdMob Enabled: ${newConfig.isAdMobMasterEnabled})")
    }

    fun addLog(message: String) {

        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        _logs.update { listOf("[$timestamp] $message") + it.take(19) }
    }

    override fun onCleared() {
        super.onCleared()
        youtubeAutomationService.clearService()
    }
}
