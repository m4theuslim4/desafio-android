package com.picpay.desafio.android.ui.user

import androidx.lifecycle.SavedStateHandle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.picpay.desafio.android.R
import com.picpay.desafio.android.utils.RecyclerViewMatchers
import com.picpay.desafio.android.data.local.user.UserLocalData
import com.picpay.desafio.android.data.respository.FakeAndroidTestUserRepository
import com.picpay.desafio.android.utils.launchFragmentInHiltContainer
import com.picpay.desafio.android.utils.CountingIdlingResourceSingleton
import com.picpay.desafio.android.utils.MockHelper
import com.picpay.desafio.android.utils.MockHelper.ERROR_CODE
import com.picpay.desafio.android.utils.MockHelper.ERROR_MESSAGE
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class UserFragmentTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private var userRepository: FakeAndroidTestUserRepository = FakeAndroidTestUserRepository()

    @BindValue
    @JvmField
    val userViewModel: UserViewModel = UserViewModel(SavedStateHandle(), userRepository, Dispatchers.IO)

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(CountingIdlingResourceSingleton.countingIdlingResource)
    }

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun usersFetchSuccess_validateUsersList() {
        val data = MockHelper.generateUsersLocalDataMock(3)
        userRepository.insertUsers(data)

        launchFragmentInHiltContainer<UserFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.title)).check(matches(isDisplayed()))
        onView(withId(R.id.user_list_progress_bar)).check(matches(not(isDisplayed())))

        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        RecyclerViewMatchers.checkRecyclerViewItem(R.id.recyclerView, 0, withText(data[0].username))
        RecyclerViewMatchers.checkRecyclerViewItem(R.id.recyclerView, 1, withText(data[1].username))
        RecyclerViewMatchers.checkRecyclerViewItem(R.id.recyclerView, 2, withText(data[2].username))
    }

    @Test
    fun usersFetchError_validateErrorMessage() {
        userRepository.apply {
            setReturnError(true)
            setCode(ERROR_CODE)
            setMessage(ERROR_MESSAGE)
        }

        launchFragmentInHiltContainer<UserFragment>(themeResId = R.style.AppTheme)

        onView(withId(R.id.title)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerView)).check(matches(not(isDisplayed())))
        onView(withId(R.id.user_list_progress_bar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun usersFetchException_validateErrorMessage() {
        userRepository.apply {
            setThrowException(true)
            setMessage(ERROR_MESSAGE)
        }
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(CountingIdlingResourceSingleton.countingIdlingResource)
    }

}