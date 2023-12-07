/*
 * Copyright 2023 The Android Open Source Project
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
 *
 * Changes:
 * - Changed members to match with our LocalTask
 * - Removed `status` member
 * - Made the class serializable
 */

package com.example.quests.data.source.network.model

import kotlinx.serialization.Serializable

/**
 * Internal model for the results of converting JSON to a Kotlin class. This is what
 * we convert to JSON to send to the backend.
 *
 * See ModelMappingExt.kt for mapping functions used to convert this model to other
 * models.
 */
@Serializable
data class NetworkTask(
    val id: String,
    val title: String = "",
    val description: String = "",
    var completionDate: Long = 0L,
    var dueDate: Long = 0L
)