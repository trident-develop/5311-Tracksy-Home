package com.bailing.lark.roll.nodes

import com.bailing.lark.roll.data.LoadingFlowContext
import com.bailing.lark.roll.event.FlowSignal
import com.bailing.lark.roll.event.LoadingDecision
import com.bailing.lark.roll.event.LoadingNode

class CachedScoreNode : LoadingNode {
    override val name: String = "CachedScoreNode"

    override suspend fun run(ctx: LoadingFlowContext): Pair<LoadingFlowContext, FlowSignal> {

        val score = ctx.storage.getSavedScore()

        return if (!score.isNullOrBlank()) {
//            log("$name: cached score found = $score")
            ctx to FlowSignal.Finish(
                LoadingDecision.OpenWebView(score)
            )
        } else {
//            log("$name: cached score empty")
            ctx to FlowSignal.Continue
        }
    }
}