package com.bailing.lark.roll.nodes

import com.bailing.lark.roll.data.LoadingFlowContext
import com.bailing.lark.roll.event.FlowSignal
import com.bailing.lark.roll.event.LoadingDecision
import com.bailing.lark.roll.event.LoadingNode

class BrokenScoreNode : LoadingNode {
    override val name: String = "BrokenScoreNode"

    override suspend fun run(ctx: LoadingFlowContext): Pair<LoadingFlowContext, FlowSignal> {

        val brokenScore = ctx.storage.getBrokenScore()

        return if (!brokenScore.isNullOrBlank()) {
//            log("$name: broken score found = $brokenScore")
            ctx.put("broken_score", brokenScore) to FlowSignal.Finish(
                LoadingDecision.OpenOtherScreen("broken_score_exists")
            )
        } else {
//            log("$name: broken score empty")
            ctx to FlowSignal.Continue
        }
    }
}