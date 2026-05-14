package com.bailing.lark.roll.nodes

import android.os.Build
import com.bailing.lark.roll.data.LoadingFlowContext
import com.bailing.lark.roll.event.FlowSignal
import com.bailing.lark.roll.event.LoadingNode
import java.util.Locale

class DeviceNode : LoadingNode {
    override val name: String = "DeviceNode"

    override suspend fun run(ctx: LoadingFlowContext): Pair<LoadingFlowContext, FlowSignal> {
        val device = getDeviceString()

        return ctx.put("device", device) to FlowSignal.Continue
    }

    fun getDeviceString(): String {
        return try {
            buildString {
                val brand = Build.BRAND.replaceFirstChar { it.titlecase(Locale.getDefault()) }
                append(brand).append(' ').append(Build.MODEL)
            }
        } catch (_: Throwable) {
            "unknown_device"
        }
    }
}