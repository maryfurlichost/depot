/*
 * Copyright (C) 2021 ZeoFlow SRL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zeoflow.depot.solver.shortcut.binderprovider

import com.zeoflow.depot.compiler.processing.XType
import com.zeoflow.depot.ext.GuavaUtilConcurrentTypeNames
import com.zeoflow.depot.ext.L
import com.zeoflow.depot.ext.N
import com.zeoflow.depot.ext.DepotGuavaTypeNames
import com.zeoflow.depot.ext.T
import com.zeoflow.depot.processor.Context
import com.zeoflow.depot.processor.ProcessorErrors
import com.zeoflow.depot.solver.shortcut.binder.CallableInsertMethodBinder.Companion.createInsertBinder
import com.zeoflow.depot.solver.shortcut.binder.InsertMethodBinder
import com.zeoflow.depot.vo.ShortcutQueryParameter

/**
 * Provider for Guava ListenableFuture binders.
 */
class GuavaListenableFutureInsertMethodBinderProvider(
    private val context: Context
) : InsertMethodBinderProvider {

    private val hasGuavaDepot by lazy {
        context.processingEnv.findTypeElement(DepotGuavaTypeNames.GUAVA_DEPOT) != null
    }

    override fun matches(declared: XType): Boolean =
        declared.typeArguments.size == 1 &&
            declared.rawType.typeName == GuavaUtilConcurrentTypeNames.LISTENABLE_FUTURE

    override fun provide(
        declared: XType,
        params: List<ShortcutQueryParameter>
    ): InsertMethodBinder {
        if (!hasGuavaDepot) {
            context.logger.e(ProcessorErrors.MISSING_DEPOT_GUAVA_ARTIFACT)
        }

        val typeArg = declared.typeArguments.first()
        val adapter = context.typeAdapterStore.findInsertAdapter(typeArg, params)
        return createInsertBinder(typeArg, adapter) { callableImpl, dbField ->
            addStatement(
                "return $T.createListenableFuture($N, $L, $L)",
                DepotGuavaTypeNames.GUAVA_DEPOT,
                dbField,
                "true", // inTransaction
                callableImpl
            )
        }
    }
}