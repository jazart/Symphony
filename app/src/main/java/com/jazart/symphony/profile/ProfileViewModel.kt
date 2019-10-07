package com.jazart.symphony.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import repo.UserRepository
import com.jazart.symphony.common.BaseViewModel
import entities.User
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class ProfileViewModel @Inject constructor(private val userRepo: UserRepository,
                                           @Named("uId") private val uId: String) : BaseViewModel() {
    private val _userLiveData: MutableLiveData<User> = MutableLiveData()
    val userLiveData: LiveData<User> = _userLiveData

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _userLiveData.value = userRepo.findUserById(uId)
        }
    }

}