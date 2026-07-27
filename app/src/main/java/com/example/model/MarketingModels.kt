package com.example.model

data class SocialPost(
    val id: String,
    val title: String,
    val content: String,
    val platforms: List<String>,
    val scheduledTime: String,
    val isAutoShareEnabled: Boolean = true,
    val status: PostStatus = PostStatus.SCHEDULED,
    val engagementScore: Int = 0
)

enum class PostStatus {
    SCHEDULED, POSTED, IN_PROGRESS, FAILED
}

data class SocialGroup(
    val id: String,
    val name: String,
    val platform: String,
    val memberCount: Int,
    val isSelected: Boolean = true
)

data class AudienceProfile(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val platform: String,
    val isActive: Boolean = true
)

data class VideoCampaign(
    val id: String,
    val videoTitle: String,
    val videoUrl: String,
    val isShorts: Boolean,
    val targetViews: Int,
    val currentViews: Int,
    val targetWatchTimeMinutes: Int,
    val currentWatchTimeMinutes: Int,
    val requirementType: String = "LIKE", // "LIKE", "COMMENT", "SHARE"
    val targetRequirementCount: Int = 5, // 1 to 5
    val completedRequirementCount: Int = 0,
    val targetLikes: Int = 5,
    val targetComments: Int = 5,
    val targetShares: Int = 5,
    val autoScrollEnabled: Boolean = true,
    val autoLikeEnabled: Boolean = true,
    val autoRefreshIntervalSec: Int = 30,
    val isRunning: Boolean = false
)

data class AnalyticsSummary(
    val totalPostsShared: Int = 142,
    val totalGroupsReached: Int = 38,
    val totalShortsViewsGained: Int = 12500,
    val watchTimeHoursGained: Float = 48.5f,
    val engagementRate: String = "+24.8%"
)

data class DailyAdRevenue(
    val date: String,
    val impressions: Int,
    val clicks: Int,
    val ctrPercent: Double,
    val ecpmUsd: Double,
    val earningsUsd: Double
)

data class AdFormatPerformance(
    val formatName: String,
    val displayName: String,
    val impressions: Int,
    val clicks: Int,
    val ctrPercent: Double,
    val ecpmUsd: Double,
    val earningsUsd: Double
)

data class CountryRevenueBreakdown(
    val countryName: String,
    val flagEmoji: String,
    val impressions: Int,
    val ecpmUsd: Double,
    val earningsUsd: Double
)

data class MonetizationEarnings(
    val totalAdImpressions: Int = 420,
    val estimatedAdMobRevenueUsd: Double = 16.20,
    val todayRevenueUsd: Double = 1.48,
    val yesterdayRevenueUsd: Double = 2.10,
    val thisMonthRevenueUsd: Double = 16.20,
    val lastMonthRevenueUsd: Double = 42.60,
    val ecpmUsd: Double = 3.50,
    val averageCtrPercent: Double = 4.2,
    val fillRatePercent: Double = 98.5,
    val bdtExchangeRate: Double = 118.0,
    val availableBalanceUsd: Double = 16.20,
    val totalWithdrawnUsd: Double = 0.0,
    val dailyRevenues: List<DailyAdRevenue> = listOf(
        DailyAdRevenue("আজ (Today)", 42, 3, 4.8, 3.80, 1.48),
        DailyAdRevenue("গতকাল (Yesterday)", 58, 4, 4.3, 3.60, 2.10),
        DailyAdRevenue("২৪ জুলাই", 65, 5, 4.6, 3.55, 2.30),
        DailyAdRevenue("২৩ জুলাই", 50, 3, 4.0, 3.40, 1.70),
        DailyAdRevenue("২২ জুলাই", 72, 6, 5.1, 3.75, 2.70),
        DailyAdRevenue("২১ জুলাই", 40, 2, 3.8, 3.30, 1.32),
        DailyAdRevenue("২০ জুলাই", 93, 8, 5.2, 3.90, 3.70)
    ),
    val adFormatPerformances: List<AdFormatPerformance> = listOf(
        AdFormatPerformance("interstitial_ad", "ইন্টারস্টিশিয়াল এড (Interstitial)", 260, 21, 5.2, 4.20, 10.92),
        AdFormatPerformance("rewarded_video", "রিওয়ার্ডেড ভিডিও (Rewarded)", 85, 11, 7.2, 5.50, 4.67),
        AdFormatPerformance("banner_ad", "ব্যানার এড (Banner)", 75, 2, 1.1, 0.80, 0.61)
    ),
    val countryBreakdowns: List<CountryRevenueBreakdown> = listOf(
        CountryRevenueBreakdown("বাংলাদেশ (BD)", "🇧🇩", 310, 3.20, 9.92),
        CountryRevenueBreakdown("যুক্তরাষ্ট্র (USA)", "🇺🇸", 45, 8.50, 3.82),
        CountryRevenueBreakdown("সৌদি আরব (KSA)", "🇸🇦", 40, 4.10, 1.64),
        CountryRevenueBreakdown("অন্যান্য (Others)", "🌐", 25, 2.90, 0.82)
    )
)

data class WithdrawalRecord(
    val id: String,
    val timestamp: String,
    val amountUsd: Double,
    val amountBdt: Double,
    val paymentMethod: String, // e.g. "Bank Transfer (Dutch-Bangla Bank)", "bKash", "Nagad"
    val accountNumber: String,
    val bankRoutingNumber: String = "",
    val status: WithdrawalStatus = WithdrawalStatus.PENDING
)

enum class WithdrawalStatus {
    PENDING, PROCESSING, APPROVED, PAID, REJECTED
}

data class PublicUserProfile(
    val fullName: String,
    val mobileNumber: String,
    val passwordHash: String,
    val points: Int = 250,
    val isVerified: Boolean = true,
    val completedTaskIds: List<String> = emptyList()
)

data class SystemNotice(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: String,
    val isImportant: Boolean = false,
    val author: String = "অ্যাডমিন"
)

data class AdSettingsConfig(
    val appId: String = "ca-app-pub-3940256099942544~3347511713",
    val isAdMobMasterEnabled: Boolean = true,
    val bannerAdUnitId: String = "ca-app-pub-3940256099942544/6300978111",
    val isBannerAdEnabled: Boolean = true,
    val interstitialAdUnitId: String = "ca-app-pub-3940256099942544/1033173712",
    val isInterstitialAdEnabled: Boolean = true,
    val rewardedAdUnitId: String = "ca-app-pub-3940256099942544/5224354917",
    val isRewardedAdEnabled: Boolean = true,
    val nativeAdUnitId: String = "ca-app-pub-3940256099942544/2247696110",
    val isNativeAdEnabled: Boolean = true,
    val customBannerTitle: String = "বিশেষ স্পন্সরড অফার: ভিডিও বুস্টে ৫০% ডিসকাউন্ট!",
    val customBannerTargetUrl: String = "https://google.com",
    val isCustomBannerEnabled: Boolean = true,
    val adRewardPoints: Int = 50,
    val adAutoShowIntervalSec: Int = 30
)

