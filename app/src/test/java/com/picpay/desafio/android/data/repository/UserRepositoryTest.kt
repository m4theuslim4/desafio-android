package com.picpay.desafio.android.data.repository


import com.nhaarman.mockitokotlin2.*
import com.picpay.desafio.android.data.local.user.User
import com.picpay.desafio.android.data.local.user.UserDao
import com.picpay.desafio.android.data.local.user.UserLocalData
import com.picpay.desafio.android.data.network.PicPayService
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
        val user1 = UserLocalData(id = 1, name = "user1", img = "", username = "user1_username")
        val user2 = UserLocalData(id = 2, name = "user2", img = "", username = "user2_username")
        val user3 = UserLocalData(id = 3, name = "user3", img = "", username = "user3_username")
        val usersLocalData = listOf(user1, user2, user3)
        `when`(userDao.getUsers()).thenReturn(usersLocalData)

        assertEquals(usersLocalData, (userRepository.getUsers() as ResourceState.Success).data)
    }

    @Test
    fun whenFetchUsersFromApi_Expected_Success() = runTest {
        val user1 = User(id = 1, name = "user1", img = "", username = "user1_username")
        val user2 = User(id = 2, name = "user2", img = "", username = "user2_username")
        val user3 = User(id = 3, name = "user3", img = "", username = "user3_username")
        val usersFromApi = listOf(user1, user2, user3)
        `when`(userDao.getUsers()).thenReturn(listOf())
        `when`(picPayService.fetchUsers()).thenReturn(Response.success(usersFromApi))

        val response = userRepository.getUsers()

        assertTrue(response is ResourceState.Success)
        assertEquals(usersFromApi[0].username,(userRepository.getUsers() as ResourceState.Success).data[0].username)
        assertEquals(usersFromApi[1].username,(userRepository.getUsers() as ResourceState.Success).data[1].username)
        assertEquals(usersFromApi[2].username,(userRepository.getUsers() as ResourceState.Success).data[2].username)
    }

    @Test
    fun whenFetchUsersFromApi_Expected_Error() = runTest {
        `when`(userDao.getUsers()).thenReturn(listOf())
        `when`(picPayService.fetchUsers()).thenReturn(Response.error(404, "\"Not found\"".toResponseBody()))

        val response = userRepository.getUsers()

        assertTrue(response is ResourceState.Error.ApiError)
        assertEquals("Not found", (response as ResourceState.Error).message)
        assertEquals(404, (response as ResourceState.Error).code)
    }

    @Test
    fun whenApiThrowException_Expected_Error() = runTest {
        val throwable = Throwable("Test Throwable error message")
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