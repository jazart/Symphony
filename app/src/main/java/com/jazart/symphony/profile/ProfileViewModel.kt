package com.jazart.symphony.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jazart.data.repo.UserRepository
import com.jazart.symphony.common.BaseViewModel
import entities.User
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel  @Inject constructor(val userRepo: UserRepository, val id: String) : BaseViewModel(){
    private val _userLiveData : MutableLiveData<User> = MutableLiveData()

    val userLiveData: LiveData<User> = _userLiveData

    init {
        loadUserData()
    }
    private fun loadUserData(){
        viewModelScope.launch { _userLiveData.value = userRepo.getUserById(id)
        }

    }

}