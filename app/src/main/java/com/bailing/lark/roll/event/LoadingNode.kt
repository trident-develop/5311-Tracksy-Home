package com.bailing.lark.roll.event

import com.bailing.lark.roll.data.LoadingFlowContext

interface LoadingNode {
    val name: String
    suspend fun run(ctx: LoadingFlowContext): Pair<LoadingFlowContext, FlowSignal>
}