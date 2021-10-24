/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.idea.perf.tracer

import com.google.idea.perf.agent.TracerHook
import com.google.idea.perf.tracer.TracerConfig.getMethodTraceData
import com.google.idea.perf.tracer.TracerConfig.getMethodTracepoint
import com.google.idea.perf.tracer.TracerConfig.shouldInstrumentClass
import java.util.TreeMap

// TODO(baskakov): update description
/**
 * [TracerUserConfig] essentially keeps track of which methods should be traced.
 * See also [TracerConfigUtil].
 *
 * When the user adds a new trace request, it ends up here. [TracerClassFileTransformer]
 * finds out about the request by calling [shouldInstrumentClass] and [getMethodTraceData].
 *
 * [TracerUserConfig] also keeps track of an integer method ID for each traced method.
 * The method ID is injected into the method bytecode and passed to the [TracerHook],
 * which then uses [getMethodTracepoint] to retrieve the corresponding tracepoint.
 * It is important for [getMethodTracepoint] to be fast and lock free.
 *
 * Note: there may be multiple tracing requests which apply to a given method.
 * For simplicity, only the most recent applicable trace request is honored.
 */
object TracerUserConfig {

    private val userTraceRequests = TreeMap<String, TraceTarget.Method>()

    fun cloneUserTraceRequests(): List<TraceTarget.Method> {
        return userTraceRequests.values.toList()
    }

    fun addUserTraceRequest(entry: TraceTarget.Method) {
        val sortKey = concatClassAndMethod(entry)
        userTraceRequests[sortKey] = entry
    }

    fun addUserUntraceRequest(entry: TraceTarget.Method) {
        val sortKey = concatClassAndMethod(entry)
        userTraceRequests.remove(sortKey)
    }

    private fun concatClassAndMethod(entry: TraceTarget.Method): String {
        return "${entry.className}#${entry.methodName ?: ""}"
    }

}
