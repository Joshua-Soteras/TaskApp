/*
 * Based on https://github.com/android/architecture-samples/blob/main/app/src/main/java/com/example/android/architecture/blueprints/todoapp/data/ModelMappingExt.kt
 */
package com.example.quests.data

import com.example.quests.data.source.local.LocalTask

/**
 * Data model mapping extension functions.
 *
 * - Task: External model exposed to other layers in the architecture.
 * Obtained using `toExternal`.
 *
 * - LocalTask: Internal model used to represent a task stored locally in a database. Obtained
 * using `toLocal`.
 *
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

/**
 * TODO: when we figure out network stuff, converting between Local to Network stuff
 *   will be done here. Don't think we'll do Network to External, just let it go to Local
 *   then do Local to External.
 */