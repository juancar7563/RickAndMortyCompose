package com.example.rickandmortymvvm.ui.login

import android.app.Application
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.util.StringUtils
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class LoginViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val mutableAuthState = MutableLiveData<LoginState>()
    val authState: LiveData<LoginState> = mutableAuthState

    init {
        checkAuthStatus()
    }


    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            mutableAuthState.value = LoginState.Unauthenticated
        } else {
            mutableAuthState.value = auth.currentUser?.email?.let { LoginState.RememberedUser(it) }
        }
    }

    fun login(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            mutableAuthState.value = LoginState.Error(application.getString(R.string.generic_credentials_empty))
            return
        }
        mutableAuthState.value = LoginState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mutableAuthState.value = LoginState.Authenticated
                } else {
                    mutableAuthState.value =
                        LoginState.Error(
                            task.exception?.message ?: application.getString(R.string.generic_error)
                        )
                }
            }
    }

    fun signup(email: String, password: String) {

        if (email.isEmpty() || password.isEmpty()) {
            mutableAuthState.value = LoginState.Error(application.getString(R.string.generic_credentials_empty))
            return
        } else if (!StringUtils.validateEmail(email)) {
            mutableAuthState.value = LoginState.Error(application.getString(R.string.generic_mail_error))
            return
        } else if (!StringUtils.validatePassword(password)) {
            mutableAuthState.value = LoginState.Error(application.getString(R.string.generic_password_error))
            return
        }
        mutableAuthState.value = LoginState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mutableAuthState.value = LoginState.Authenticated
                } else {
                    mutableAuthState.value =
                        LoginState.Error(task.exception?.message ?: application.getString(R.string.generic_error))
                }
            }
    }
}