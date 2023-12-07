/*
 * Copyright 2019 The Android Open Source Project
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
 * - Removed @ExperimentalCoroutinesApi annotation because nothing is
 *   experimental anymore
 */

package com.example.quests

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Sets up the coroutine dispatcher to be used for unit testing.
 *
 * `viewModelScope` uses `Dispatchers.Main` which is unavailable in local tests,
 * so we need to `setMain()` to modify `Dispatchers.Main` to use a [TestDispatcher].
 *
 * Declare it as a JUnit Rule:
 * ```
 * @get:Rule
 * val mainCoroutineRule = MainCoroutineRule()
 * ```
 * Then, use `runTest` to execute your tests.
 *
 * See
 * https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-survey#3
 * (a bit outdated, but still helpful).
 */
class MainCoroutineRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}