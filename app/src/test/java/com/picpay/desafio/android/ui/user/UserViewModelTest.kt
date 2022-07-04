package com.picpay.desafio.android.ui.user

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.picpay.desafio.android.data.local.user.UserLocalData
import com.picpay.desafio.android.data.repository.FakeUserRepository
import com.picpay.desafio.android.utils.ResourceState
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class UserViewModelTest {

    private lateinit var userViewModel: UserViewModel
    private lateinit var userRepository: FakeUserRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {

        userRepository = FakeUserRepository(Dispatchers.IO)

        userViewModel = UserViewModel(
            savedStateHandle = SavedStateHandle(),
            userRepository = userRepository,
            dispatcher = testDispatcher
        )
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenLoadingUpdate_Expected_Success() = runTest {
        userViewModel.isLoading.test {
            val first = awaitItem()
            assertEquals(false, first)

            userViewModel.getUsers()

            val second = awaitItem()
            assertEquals(true, second)

            val last = awaitItem()
            assertEquals(false, last)
        }
    }

    @Test
    fun whenUsersNotFetched_Expected_Error() = runTest {
        val code = 404
        val message = "Api error test"
        userRepository.apply {
            setReturnError(true)
            setCode(code)
            setMessage(message)
        }

        userViewModel.error.test {
            userViewModel.getUsers()

            val errorStatus = awaitItem()

            assert(errorStatus is ResourceState.Error.ApiError)
            assertEquals(code, errorStatus.code)
            assertEquals(message, errorStatus.message)
        }
    }

    @Test
    fun whenThrowException_Expected_Error() = runTest {
        val message = "Exception test error"
        userRepository.apply {
            setThrowException(true)
            setMessage(message)
        }

        userViewModel.error.test {
            userViewModel.getUsers()

            val errorStatus = awaitItem()

            assert(errorStatus is ResourceState.Error.ExceptionError)
            assertEquals(message, errorStatus.message)
            assert(errorStatus.code == null)
        }
    }

    @Test
    fun whenDataFetched_Expected_Success() = runTest {
        val data = listOf(
            UserLocalData(id = 1, name = "user1", img = "", username = "user1_username"),
            UserLocalData(id = 2, name = "user2", img = "", username = "user2_username"),
            UserLocalData(id = 3, name = "user3", img = "", username = "user3_username")
        )
        userRepository.insertUsers(data)

        userViewModel.usersData.test {
            val firstData = awaitItem()
            assert(firstData.isEmpty())

            userViewModel.getUsers()
            val usersData = awaitItem()
            assertEquals(data, usersData)
        }
    }
}