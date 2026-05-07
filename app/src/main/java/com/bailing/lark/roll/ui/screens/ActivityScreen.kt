package com.bailing.lark.roll.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bailing.lark.roll.data.UtilityRecord
import com.bailing.lark.roll.ui.components.ScreenHeader
import com.bailing.lark.roll.ui.components.SectionHeader
import com.bailing.lark.roll.ui.components.UtilityTrendCard
import com.bailing.lark.roll.ui.theme.BeigeBorderStrong
import com.bailing.lark.roll.ui.theme.paletteFor

@Composable
fun ActivityScreen(
    records: SnapshotStateList<UtilityRecord>,
    contentPadding: PaddingValues
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val pageCount = records.size
    val pagerState = rememberPagerState(pageCount = { pageCount })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding())
    ) {
        ScreenHeader(
            eyebrow = "Last 6 months",
            title = "Activity"
        )
        if (pageCount == 0) {
            Spacer(modifier = Modifier.weight(1f))
        } else {
            val currentRecord = records.getOrNull(pagerState.currentPage)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Per utility",
                    modifier = Modifier.weight(1f)
                )
                if (currentRecord != null) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(paletteFor(currentRecord.kind).soft.copy(alpha = 0.55f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        androidx.compose.material3.Text(
                            text = "${pagerState.currentPage + 1} / $pageCount",
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                            color = paletteFor(currentRecord.kind).primary,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                    }
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp),
                pageSpacing = 14.dp
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp)
                ) {
                    UtilityTrendCard(
                        record = records[page],
                        visible = visible,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            PagerIndicator(
                pageCount = pageCount,
                currentPage = pagerState.currentPage,
                records = records,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
        Spacer(
            modifier = Modifier.height(contentPadding.calculateBottomPadding() + 12.dp)
        )
    }
}

@Composable
private fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    records: SnapshotStateList<UtilityRecord>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until pageCount) {
            val isActive = i == currentPage
            val palette = paletteFor(records[i].kind)
            val width by animateDpAsState(
                targetValue = if (isActive) 24.dp else 8.dp,
                animationSpec = tween(260),
                label = "indicator-w-$i"
            )
            val height by animateDpAsState(
                targetValue = if (isActive) 8.dp else 8.dp,
                animationSpec = tween(260),
                label = "indicator-h-$i"
            )
            val color by animateColorAsState(
                targetValue = if (isActive) palette.primary else BeigeBorderStrong,
                animationSpec = tween(260),
                label = "indicator-c-$i"
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .width(width)
                    .height(height)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
