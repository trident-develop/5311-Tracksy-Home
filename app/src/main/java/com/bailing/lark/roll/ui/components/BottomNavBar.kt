package com.bailing.lark.roll.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bailing.lark.roll.ui.icons.UtilityIcon
import com.bailing.lark.roll.ui.icons.UtilityIconKind
import com.bailing.lark.roll.ui.theme.BeigeBorder
import com.bailing.lark.roll.ui.theme.PrimaryAccent
import com.bailing.lark.roll.ui.theme.PrimaryAccentMuted
import com.bailing.lark.roll.ui.theme.TextSecondary

enum class NavTab(val title: String, val icon: UtilityIconKind) {
    Overview("Overview", UtilityIconKind.Overview),
    Calculator("Calculator", UtilityIconKind.Calculator),
    Activity("Activity", UtilityIconKind.Activity)
}

@Composable
fun BottomNavBar(
    selected: NavTab,
    onSelect: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = PrimaryAccent.copy(alpha = 0.18f),
                    spotColor = PrimaryAccent.copy(alpha = 0.18f)
                )
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, BeigeBorder, RoundedCornerShape(24.dp))
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab.values().forEach { tab ->
                NavBarItem(
                    tab = tab,
                    selected = tab == selected,
                    onClick = { onSelect(tab) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    tab: NavTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg by animateColorAsState(
        targetValue = if (selected) PrimaryAccentMuted.copy(alpha = 0.55f)
        else MaterialTheme.colorScheme.surface,
        animationSpec = tween(220),
        label = "bg"
    )
    val tint by animateColorAsState(
        targetValue = if (selected) PrimaryAccent else TextSecondary,
        animationSpec = tween(220),
        label = "tint"
    )
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        UtilityIcon(
            kind = tab.icon,
            tint = tint,
            size = 22.dp,
            strokeWidth = if (selected) 2.0.dp else 1.6.dp,
            filled = false
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = tab.title,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
