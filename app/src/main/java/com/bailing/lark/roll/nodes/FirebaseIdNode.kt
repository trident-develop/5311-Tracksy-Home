package com.bailing.lark.roll.nodes

import com.bailing.lark.roll.data.LoadingFlowContext
import com.bailing.lark.roll.event.FlowSignal
import com.bailing.lark.roll.event.LoadingNode
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.tasks.await

class FirebaseIdNode : LoadingNode {

    override val name: String = "FirebaseIdNode"

    override suspend fun run(
        ctx: LoadingFlowContext
    ): Pair<LoadingFlowContext, FlowSignal> {

        val firebaseId = loadFirebaseId()

        return ctx.put("firebase_id", firebaseId) to FlowSignal.Continue
    }

    private suspend fun loadFirebaseId(): String {
        return runCatching {
            Firebase.analytics.appInstanceId.await()
        }.getOrNull() ?: "null"
    }
}