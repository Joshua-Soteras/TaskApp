/*
 * Based on https://github.com/android/architecture-samples/blob/main/app/src/main/java/com/example/android/architecture/blueprints/todoapp/data/ModelMappingExt.kt
 */
package com.example.quests.data

import com.example.quests.data.source.local.LocalTask
import com.example.quests.data.source.network.model.NetworkTask

/**
 * Data model mapping extension functions.
 *
 * - Task: External model exposed to other layers in the architecture.
 * Obtained using `toExternal`.
 *
 * - LocalTask: Internal model used to represent a task stored locally in a database. Obtained
 * using `toLocal`.
 *
 * - NetworkTask: Internal model used to represent a task from the network. Obtained
 * using `toNetwork`.
 */

// External to local
fun Task.toLocal() = LocalTask(
    id = id,
    title = title,
    description = description,
    completionDate = completionDate,
)

fun List<Task>.toLocal() = map(Task::toLocal)

// Local to external
fun LocalTask.toExternal() = Task(
    id = id,
    title = title,
    description = description,
    completionDate = completionDate,
)

// Note: JvmName is used to provide a unique name for each extension function with the same name.
// Without this, type erasure will cause compiler errors because these methods will have the same
// signature on the JVM.
@JvmName("localToExternal")
fun List<LocalTask>.toExternal() = map(LocalTask::toExternal)

// Network to local
fun NetworkTask.toLocal() = LocalTask(
    id = id,
    title = title,
    description = description,
    completionDate = completionDate
)

@JvmName("networkToLocal")
fun List<NetworkTask>.toLocal() = map(NetworkTask::toLocal)

// Local to Network
fun LocalTask.toNetwork() = NetworkTask(
    id = id,
    title = title,
    description = description,
    completionDate = completionDate
)

fun List<LocalTask>.toNetwork() = map(LocalTask::toNetwork)

// External to Network
fun Task.toNetwork() = toLocal().toNetwork()

@JvmName("externalToNetwork")
fun List<Task>.toNetwork() = map(Task::toNetwork)

// Network to External
fun NetworkTask.toExternal() = toLocal().toExternal()

@JvmName("networkToExternal")
fun List<NetworkTask>.toExternal() = map(NetworkTask::toExternal)