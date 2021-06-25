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

package com.zeoflow.depot.solver.query.result

import com.zeoflow.depot.ext.N
import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec

/**
 * helper class to create correct transaction code.
 */
interface TransactionWrapper {
    fun beginTransactionWithControlFlow()
    fun commitTransaction()
    fun endTransactionWithControlFlow()
}

fun MethodSpec.Builder.transactionWrapper(dbField: FieldSpec) = object : TransactionWrapper {
    override fun beginTransactionWithControlFlow() {
        addStatement("$N.beginTransaction()", dbField)
        beginControlFlow("try")
    }

    override fun commitTransaction() {
        addStatement("$N.setTransactionSuccessful()", dbField)
    }

    override fun endTransactionWithControlFlow() {
        nextControlFlow("finally")
        addStatement("$N.endTransaction()", dbField)
        endControlFlow()
    }
}

fun CodeBlock.Builder.transactionWrapper(dbField: FieldSpec) = object : TransactionWrapper {
    override fun beginTransactionWithControlFlow() {
        addStatement("$N.beginTransaction()", dbField)
        beginControlFlow("try")
    }

    override fun commitTransaction() {
        addStatement("$N.setTransactionSuccessful()", dbField)
    }

    override fun endTransactionWithControlFlow() {
        nextControlFlow("finally")
        addStatement("$N.endTransaction()", dbField)
        endControlFlow()
    }
}
