package com.picpay.desafio.android.ui.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.android.data.local.user.UserLocalData
import com.picpay.desafio.android.data.repository.IUserRepository
import com.picpay.desafio.android.utils.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userRepository: IUserRepository,
    val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _usersData: MutableStateFlow<List<UserLocalData>> = MutableStateFlow(listOf())
    val usersData: StateFlow<List<UserLocalData>> = _usersData

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error: MutableSharedFlow<ResourceState.Error> = MutableSharedFlow()
    val error: SharedFlow<ResourceState.Error> = _error


    fun getUsers() {
        viewModelScope.launch {
            withContext(dispatcher) {
                _isLoading.emit(true)
                when(val usersResponse = userRepository.getUsers()) {
                    is ResourceState.Success -> {
                        _isLoading.emit(false)
                        _usersData.emit(usersResponse.data)
                    }
                    is ResourceState.Error -> {
                        _isLoading.emit(false)
                        _error.emit(usersResponse)
                    }
                }
            }
        }
    }
}