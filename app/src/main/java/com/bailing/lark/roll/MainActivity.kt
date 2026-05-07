package com.bailing.lark.roll

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bailing.lark.roll.data.AppRepository
import com.bailing.lark.roll.data.UtilityBill
import com.bailing.lark.roll.data.UtilityKind
import com.bailing.lark.roll.data.UtilityRecord
import com.bailing.lark.roll.ui.components.BottomNavBar
import kotlinx.coroutines.flow.drop
import com.bailing.lark.roll.ui.components.NavTab
import com.bailing.lark.roll.ui.screens.ActivityScreen
import com.bailing.lark.roll.ui.screens.CalculatorScreen
import com.bailing.lark.roll.ui.screens.OverviewScreen
import com.bailing.lark.roll.ui.screens.UtilityDetailsOverlay
import com.bailing.lark.roll.ui.theme.BeigeBackground
import com.bailing.lark.roll.ui.theme.TracksyHomeTheme

class MainActivity : ComponentActivity() {
    private var multiTouchDetected = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()
        setContent {
            TracksyHomeTheme {
                MainScreen()
            }
        }
    }

    private fun hideSystemBars() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.pointerCount > 1) {
            if (!multiTouchDetected) {
                multiTouchDetected = true
                val cancelEvent = MotionEvent.obtain(ev)
                cancelEvent.action = MotionEvent.ACTION_CANCEL
                super.dispatchTouchEvent(cancelEvent)
                cancelEvent.recycle()
            }
            return true
        }
        if (multiTouchDetected) {
            if (ev.actionMasked == MotionEvent.ACTION_UP ||
                ev.actionMasked == MotionEvent.ACTION_CANCEL
            ) {
                multiTouchDetected = false
            }
            return true
        }
        return super.dispatchTouchEvent(ev)
    }
}

@Composable
private fun MainScreen() {
    val context = LocalContext.current
    val repository = remember { AppRepository.get(context) }

    var selectedTab by remember { mutableStateOf(NavTab.Overview) }
    val bills = remember {
        mutableStateListOf<UtilityBill>().apply { addAll(repository.loadBillsOrDefault()) }
    }
    val records = remember {
        mutableStateListOf<UtilityRecord>().apply { addAll(repository.loadRecordsOrDefault()) }
    }
    val expandedBillId = remember { mutableStateOf<Int?>(null) }
    var detailsKind by remember { mutableStateOf<UtilityKind?>(null) }

    LaunchedEffect(repository) {
        snapshotFlow { bills.toList() }
            .drop(1)
            .collect { repository.saveBills(it) }
    }
    LaunchedEffect(repository) {
        snapshotFlow { records.toList() }
            .drop(1)
            .collect { repository.saveRecords(it) }
    }

    BackHandler(enabled = detailsKind != null) {
        detailsKind = null
    }
    BackHandler(enabled = detailsKind == null && selectedTab != NavTab.Overview) {
        selectedTab = NavTab.Overview
    }

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BeigeBackground)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BeigeBackground,
            bottomBar = {
                BottomNavBar(
                    selected = selectedTab,
                    onSelect = { selectedTab = it }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BeigeBackground)
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        val forward = targetState.ordinal > initialState.ordinal
                        val dir = if (forward) 1 else -1
                        (slideInVertically(tween(280)) { it / 12 * dir } + fadeIn(tween(280))) togetherWith
                            (slideOutVertically(tween(220)) { -(it / 12) * dir } + fadeOut(tween(220)))
                    },
                    label = "tab"
                ) { tab ->
                    when (tab) {
                        NavTab.Overview -> OverviewScreen(
                            bills = bills,
                            records = records,
                            expandedId = expandedBillId,
                            onOpenDetails = { kind -> detailsKind = kind },
                            contentPadding = padding
                        )
                        NavTab.Calculator -> CalculatorScreen(
                            records = records,
                            onUpdate = { updated ->
                                val pos = records.indexOfFirst { it.kind == updated.kind }
                                if (pos >= 0) records[pos] = updated
                            },
                            contentPadding = padding
                        )
                        NavTab.Activity -> ActivityScreen(
                            records = records,
                            contentPadding = padding
                        )
                    }
                }
            }
        }

        UtilityDetailsOverlay(
            visible = detailsKind != null,
            record = detailsKind?.let { kind -> records.firstOrNull { it.kind == kind } },
            onClose = { detailsKind = null },
            onUpdate = { updated ->
                val pos = records.indexOfFirst { it.kind == updated.kind }
                if (pos >= 0) records[pos] = updated
            }
        )
    }
}

