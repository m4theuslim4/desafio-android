package com.picpay.desafio.android.data.repository


import com.nhaarman.mockitokotlin2.*
import com.picpay.desafio.android.data.local.user.User
import com.picpay.desafio.android.data.local.user.UserDao
import com.picpay.desafio.android.data.local.user.UserLocalData
import com.picpay.desafio.android.data.network.PicPayService
import com.picpay.desafio.android.utils.MockHelper
import com.picpay.desafio.android.utils.MockHelper.ERROR_CODE
import com.picpay.desafio.android.utils.MockHelper.ERROR_MESSAGE
import com.picpay.desafio.android.utils.MockHelper.ERROR_MESSAGE_ESCAPED
import com.picpay.desafio.android.utils.ResourceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class UserRepositoryTest {

    private lateinit var userRepository: IUserRepository

    private val userDao: UserDao = mock()
    private val picPayService: PicPayService = mock()

    @Before
    fun setUp() {
        userRepository = UserRepository(
            userDao = userDao,
            picPayService = picPayService,
            dispatcher = Dispatchers.IO
        )
    }

    @Test
    fun whenHasUsersInDatabase_Expected_Success() = runTest {
        val usersLocalData = MockHelper.generateUsersLocalDataMock(3)
        `when`(userDao.getUsers()).thenReturn(usersLocalData)

        assertEquals(usersLocalData, (userRepository.getUsers() as ResourceState.Success).data)
    }

    @Test
    fun whenFetchUsersFromApi_Expected_Success() = runTest {
        val usersFromApi = MockHelper.generateUsersMock(3)
        `when`(userDao.getUsers()).thenReturn(listOf())
        `when`(picPayService.fetchUsers()).thenReturn(Response.success(usersFromApi))

        val response = userRepository.getUsers()

        assertTrue(response is ResourceState.Success)
        assertEquals(
            usersFromApi[0].username,
            (userRepository.getUsers() as ResourceState.Success).data[0].username
        )
        assertEquals(
            usersFromApi[1].username,
            (userRepository.getUsers() as ResourceState.Success).data[1].username
        )
        assertEquals(
            usersFromApi[2].username,
            (userRepository.getUsers() as ResourceState.Success).data[2].username
        )
    }

    @Test
    fun whenFetchUsersFromApi_Expected_Error() = runTest {
        `when`(userDao.getUsers()).thenReturn(listOf())
        `when`(picPayService.fetchUsers()).thenReturn(
            Response.error(ERROR_CODE, ERROR_MESSAGE_ESCAPED.toResponseBody()))

        val response = userRepository.getUsers()

        assertTrue(response is ResourceState.Error.ApiError)
        assertEquals(ERROR_MESSAGE, (response as ResourceState.Error).message)
        assertEquals(ERROR_CODE, response.code)
    }

    @Test
    fun whenApiThrowException_Expected_Error() = runTest {
        val throwable = Throwable(ERROR_MESSAGE)
        `when`(userDao.getUsers()).thenReturn(listOf())
        given(picPayService.fetchUsers()).willAnswer {
            throw throwable
        }

        val response = userRepository.getUsers()

        assertTrue(response is ResourceState.Error.ExceptionError)
        assertEquals(throwable.message, (response as ResourceState.Error).message)
        assertTrue(response.code == null)
    }
}