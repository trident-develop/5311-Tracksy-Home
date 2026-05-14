package com.bailing.lark.roll

import com.bailing.lark.roll.nodes.BrokenScoreNode
import com.bailing.lark.roll.nodes.CachedScoreNode
import com.bailing.lark.roll.nodes.DeviceNode
import com.bailing.lark.roll.nodes.FirebaseIdNode
import com.bailing.lark.roll.nodes.FirstInstNode
import com.bailing.lark.roll.nodes.GadidNode
import com.bailing.lark.roll.nodes.ProbeNode
import com.bailing.lark.roll.nodes.ReferrerNode
import com.bailing.lark.roll.nodes.ScoreAssembleNode
import com.bailing.lark.roll.ui.theme.ScoreBuilder

object LoadingFlowFactory {

    fun create(baseUrl: String): LoadingFlowEngine {
        return LoadingFlowEngine(
            nodes = listOf(
                CachedScoreNode(),
                BrokenScoreNode(),
                ReferrerNode(),
                GadidNode(),
                DeviceNode(),
                ProbeNode(),
                FirstInstNode(),
                FirebaseIdNode(),
                ScoreAssembleNode(
                    scoreBuilder = ScoreBuilder(baseUrl)
                )
            )
        )
    }
}