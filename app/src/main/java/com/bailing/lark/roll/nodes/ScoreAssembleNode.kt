package com.bailing.lark.roll.nodes

import com.bailing.lark.roll.data.LoadingFlowContext
import com.bailing.lark.roll.event.FlowSignal
import com.bailing.lark.roll.event.LoadingDecision
import com.bailing.lark.roll.event.LoadingNode
import com.bailing.lark.roll.ui.theme.ScoreBuilder

class ScoreAssembleNode(
    private val scoreBuilder: ScoreBuilder
) : LoadingNode {

    override val name: String = "LinkAssembleNode"

    override suspend fun run(ctx: LoadingFlowContext): Pair<LoadingFlowContext, FlowSignal> {

        val url = scoreBuilder.build(ctx.data)

//        log("$name: link ready = $url")

        return ctx.put("final_url", url) to FlowSignal.Finish(
            LoadingDecision.OpenWebView(url)
        )
    }
}