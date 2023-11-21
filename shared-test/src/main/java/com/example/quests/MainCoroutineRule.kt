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