package com.example.service

import com.example.model.VideoCampaign
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AutomationSessionStats(
    val totalShortsScrolled: Int = 0,
    val totalShortsLiked: Int = 0,
    val totalLongFormLoops: Int = 0,
    val totalWatchTimeMinutesAccrued: Int = 0,
    val currentActiveCampaignTitle: String = "কোনো ভিডিও প্লে হচ্ছে না",
    val isShortsEngineActive: Boolean = false,
    val isLongFormEngineActive: Boolean = false,
    val autoLikeEnabled: Boolean = true,
    val autoScrollIntervalSec: Int = 10,
    val currentLoopIteration: Int = 0
)

/**
 * Dedicated Service Layer handling YouTube Video Automation:
 * 1. Shorts auto-scrolling & engagement logic
 * 2. Long-form video loop viewing & watch time generation logic
 */
class YouTubeAutomationService(
    private val onLogCallback: (String) -> Unit,
    private val onCampaignProgressUpdated: (campaignId: String, newViews: Int, addedWatchTimeMin: Int) -> Unit
) {
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var shortsJob: Job? = null
    private var longFormJob: Job? = null

    private val _stats = MutableStateFlow(AutomationSessionStats())
    val stats: StateFlow<AutomationSessionStats> = _stats.asStateFlow()

    /**
     * Starts or stops YouTube Shorts Auto-Scrolling engine.
     * Automatically scrolls between active Shorts campaigns, triggers auto-likes, and increments views.
     */
    fun toggleShortsAutoScroll(
        shortsCampaigns: List<VideoCampaign>,
        intervalSec: Int = 10,
        autoLike: Boolean = true
    ) {
        if (_stats.value.isShortsEngineActive) {
            stopShortsEngine()
            onLogCallback("ইউটিউব শর্টস অটো-স্ক্রলিং ইঞ্জিন বন্ধ করা হয়েছে।")
        } else {
            if (shortsCampaigns.isEmpty()) {
                onLogCallback("ত্রুটি: অটো-স্ক্রল করার জন্য কোনো ইউটিউব শর্টস ক্যাম্পেইন পাওয়া যায়নি!")
                return
            }
            startShortsEngine(shortsCampaigns, intervalSec, autoLike)
            onLogCallback("ইউটিউব শর্টস অটো-স্ক্রলিং ও অটো-লাইক ইঞ্জিন সক্রিয় করা হয়েছে ($intervalSec সেকেন্ড পর পর স্ক্রল)।")
        }
    }

    private fun startShortsEngine(
        campaigns: List<VideoCampaign>,
        intervalSec: Int,
        autoLike: Boolean
    ) {
        shortsJob?.cancel()
        _stats.update {
            it.copy(
                isShortsEngineActive = true,
                autoScrollIntervalSec = intervalSec,
                autoLikeEnabled = autoLike
            )
        }

        shortsJob = serviceScope.launch {
            var index = 0
            while (isActive) {
                if (campaigns.isEmpty()) break
                val currentCampaign = campaigns[index % campaigns.size]
                index++

                _stats.update {
                    it.copy(
                        currentActiveCampaignTitle = "▶ [Shorts] ${currentCampaign.videoTitle}",
                        totalShortsScrolled = it.totalShortsScrolled + 1,
                        totalShortsLiked = if (autoLike) it.totalShortsLiked + 1 else it.totalShortsLiked
                    )
                }

                val autoLikeLog = if (autoLike) " (অটো-লাইক প্রদত্ত 👍)" else ""
                onLogCallback("অটো-স্ক্রল শর্টস: '${currentCampaign.videoTitle}' প্লে হচ্ছে$autoLikeLog")
                
                // Trigger view increment
                onCampaignProgressUpdated(currentCampaign.id, 1, 1)

                delay(intervalSec * 1000L)
            }
        }
    }

    fun stopShortsEngine() {
        shortsJob?.cancel()
        shortsJob = null
        _stats.update { it.copy(isShortsEngineActive = false, currentActiveCampaignTitle = "শর্টস ইঞ্জিন নিষ্ক্রিয়") }
    }

    /**
     * Starts or stops Long-Form Video Loop Viewing engine.
     * Simulates continuous loop viewing of long-form videos to boost target watch time hours and views.
     */
    fun toggleLongFormLoopViewing(
        longFormCampaigns: List<VideoCampaign>,
        loopDurationSec: Int = 30
    ) {
        if (_stats.value.isLongFormEngineActive) {
            stopLongFormEngine()
            onLogCallback("লং-ফর্ম ভিডিও লুপ ভিউয়িং ইঞ্জিন পজ করা হয়েছে।")
        } else {
            if (longFormCampaigns.isEmpty()) {
                onLogCallback("ত্রুটি: লুপ করার জন্য কোনো লং-ফর্ম ভিডিও পাওয়া যায়নি!")
                return
            }
                startLongFormEngine(longFormCampaigns, loopDurationSec)
                onLogCallback("ইউটিউব লং-ফর্ম ভিডিও লুপ অটো-ভিউয়িং সক্রিয় করা হয়েছে।")
        }
    }

    private fun startLongFormEngine(campaigns: List<VideoCampaign>, loopDurationSec: Int) {
        longFormJob?.cancel()
        _stats.update { it.copy(isLongFormEngineActive = true) }

        longFormJob = serviceScope.launch {
            var loopIndex = 0
            while (isActive) {
                if (campaigns.isEmpty()) break
                val currentCampaign = campaigns[loopIndex % campaigns.size]
                loopIndex++

                _stats.update {
                    it.copy(
                        currentActiveCampaignTitle = "🔁 [লুপ ভিডিও] ${currentCampaign.videoTitle}",
                        currentLoopIteration = it.currentLoopIteration + 1,
                        totalLongFormLoops = it.totalLongFormLoops + 1,
                        totalWatchTimeMinutesAccrued = it.totalWatchTimeMinutesAccrued + 3
                    )
                }

                onLogCallback("লুপ ভিউ চালিত হচ্ছে: '${currentCampaign.videoTitle}' (লুপ #${_stats.value.currentLoopIteration})")
                
                // Add 1 view and 3 minutes watch time per loop iteration
                onCampaignProgressUpdated(currentCampaign.id, 1, 3)

                delay(loopDurationSec * 1000L)
            }
        }
    }

    fun stopLongFormEngine() {
        longFormJob?.cancel()
        longFormJob = null
        _stats.update { it.copy(isLongFormEngineActive = false) }
    }

    fun clearService() {
        shortsJob?.cancel()
        longFormJob?.cancel()
        serviceScope.cancel()
    }
}
