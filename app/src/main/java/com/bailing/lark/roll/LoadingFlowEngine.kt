package com.bailing.lark.roll

import android.content.Context
import com.bailing.lark.roll.data.LoadingFlowContext
import com.bailing.lark.roll.data.db.ScoreStorage
import com.bailing.lark.roll.event.FlowSignal
import com.bailing.lark.roll.event.LoadingDecision
import com.bailing.lark.roll.event.LoadingNode
import com.bailing.lark.roll.ui.components.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicReference

class LoadingFlowEngine(
    private val nodes: List<LoadingNode>
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun execute(
        context: Context,
        storage: ScoreStorage
    ): Flow<LoadingDecision> {

        val contextRef = AtomicReference(
            LoadingFlowContext(
                context = context.applicationContext,
                storage = storage
            )
        )

        return nodes
            .asFlow()

            .onStart {
                log("LoadingFlowEngine: started")
            }

            .flatMapConcat { node ->

                flow {

                    val currentContext = contextRef.get()

//                    log("LoadingFlowEngine: running ${node.name}")

                    val result = withContext(Dispatchers.IO) {
                        node.run(currentContext)
                    }

                    val updatedContext = result.first
                    val signal = result.second

                    contextRef.set(updatedContext)

                    emit(
                        NodeExecution(
                            nodeName = node.name,
                            signal = signal
                        )
                    )
                }
            }

            .onEach { execution ->

                when (val signal = execution.signal) {

                    FlowSignal.Continue -> {
                        log("LoadingFlowEngine: ${execution.nodeName} -> continue")
                    }

                    is FlowSignal.Finish -> {

                        log("LoadingFlowEngine: ${execution.nodeName} -> finish")

                        log("LoadingFlowEngine: decision = ${signal.decision}")
                    }
                }
            }

            .transformWhile { execution ->

                when (val signal = execution.signal) {

                    FlowSignal.Continue -> {
                        true
                    }

                    is FlowSignal.Finish -> {

                        emit(signal.decision)

                        false
                    }
                }
            }

            .catch { throwable ->

                log("LoadingFlowEngine: error = ${throwable.message}")

                emit(LoadingDecision.OpenOtherScreen(reason = "flow_error"))
            }

            .flowOn(Dispatchers.IO)
    }
}

private data class NodeExecution(
    val nodeName: String,
    val signal: FlowSignal
)