package com.example.rickandmortymvvm.ui.login


sealed class LoginState{
    object Authenticated : LoginState()
    data class RememberedUser(val userMail: String) : LoginState()
    object Unauthenticated : LoginState()
    object Loading : LoginState()
    data class Error(val message : String) : LoginState()
}