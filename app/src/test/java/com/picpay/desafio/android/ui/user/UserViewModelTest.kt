package com.picpay.desafio.android.ui.user

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.picpay.desafio.android.data.repository.FakeTestUserRepository
import com.picpay.desafio.android.utils.MockHelper
import com.picpay.desafio.android.utils.MockHelper.ERROR_CODE
import com.picpay.desafio.android.utils.MockHelper.ERROR_MESSAGE
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
    private lateinit var userRepository: FakeTestUserRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {

        userRepository = FakeTestUserRepository(Dispatchers.IO)

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
        userRepository.apply {
            setReturnError(true)
            setCode(ERROR_CODE)
            setMessage(ERROR_MESSAGE)
        }

        userViewModel.error.test {
            userViewModel.getUsers()

            val errorStatus = awaitItem()

            assert(errorStatus is ResourceState.Error.ApiError)
            assertEquals(ERROR_CODE, errorStatus.code)
            assertEquals(ERROR_MESSAGE, errorStatus.message)
        }
    }

    @Test
    fun whenThrowException_Expected_Error() = runTest {
        userRepository.apply {
            setThrowException(true)
            setMessage(ERROR_MESSAGE)
        }

        userViewModel.error.test {
            userViewModel.getUsers()

            val errorStatus = awaitItem()

            assert(errorStatus is ResourceState.Error.ExceptionError)
            assertEquals(ERROR_MESSAGE, errorStatus.message)
            assert(errorStatus.code == null)
        }
    }

    @Test
    fun whenDataFetched_Expected_Success() = runTest {
        val data = MockHelper.generateUsersLocalDataMock(3)
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