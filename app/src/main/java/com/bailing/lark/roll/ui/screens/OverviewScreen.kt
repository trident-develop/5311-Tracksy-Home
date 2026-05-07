package com.bailing.lark.roll.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.bailing.lark.roll.data.BillStatus
import com.bailing.lark.roll.data.UtilityBill
import com.bailing.lark.roll.data.UtilityRecord
import com.bailing.lark.roll.data.currentYearMonthKey
import com.bailing.lark.roll.ui.components.ScreenHeader
import com.bailing.lark.roll.ui.components.SectionHeader
import com.bailing.lark.roll.ui.components.SummaryCard
import com.bailing.lark.roll.ui.components.UtilityCard
import com.bailing.lark.roll.ui.theme.TextSecondary

@Composable
fun OverviewScreen(
    bills: SnapshotStateList<UtilityBill>,
    records: SnapshotStateList<UtilityRecord>,
    expandedId: MutableState<Int?>,
    onOpenDetails: (com.bailing.lark.roll.data.UtilityKind) -> Unit,
    contentPadding: PaddingValues
) {
    val billsSnapshot = bills.toList()
    val dueSoon = remember(billsSnapshot) { billsSnapshot.count { it.status == BillStatus.DueSoon } }
    val overdue = remember(billsSnapshot) { billsSnapshot.count { it.status == BillStatus.Overdue } }
    val paid = remember(billsSnapshot) { billsSnapshot.count { it.status == BillStatus.Paid } }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding())
    ) {
        ScreenHeader(
            eyebrow = "Good day at home",
            title = "Home bills overview"
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 4.dp,
                bottom = contentPadding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                val msg = when {
                    overdue > 0 && dueSoon > 0 -> "$dueSoon bills due soon · $overdue overdue"
                    overdue > 0 -> "$overdue overdue"
                    dueSoon > 0 -> "$dueSoon bills due soon"
                    else -> "Everything is up to date"
                }
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                SummaryCard(
                    dueSoon = dueSoon,
                    overdue = overdue,
                    paidThisMonth = paid
                )
            }
            item {
                SectionHeader(title = "Your utilities", action = "All")
            }
            itemsIndexed(bills) { index, bill ->
                val anim by animateFloatAsState(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = tween(durationMillis = 420, delayMillis = index * 60),
                    label = "card-$index"
                )
                UtilityCard(
                    bill = bill,
                    record = records.firstOrNull { it.kind == bill.kind },
                    expanded = expandedId.value == bill.id,
                    onToggleExpanded = {
                        expandedId.value = if (expandedId.value == bill.id) null else bill.id
                    },
                    onMarkPaid = {
                        val pos = bills.indexOfFirst { it.id == bill.id }
                        if (pos >= 0) {
                            bills[pos] = bills[pos].copy(
                                status = BillStatus.Paid,
                                dueDate = "Paid · just now",
                                paidForMonth = currentYearMonthKey()
                            )
                        }
                    },
                    onViewDetails = { onOpenDetails(bill.kind) },
                    modifier = Modifier
                        .alpha(anim)
                        .graphicsLayer {
                            translationY = (1f - anim) * 24.dp.toPx()
                        }
                )
            }
        }
    }
}
