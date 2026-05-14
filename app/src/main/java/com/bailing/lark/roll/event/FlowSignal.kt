package com.bailing.lark.roll.event

sealed class FlowSignal {
    data object Continue : FlowSignal()
    data class Finish(val decision: LoadingDecision) : FlowSignal()
}