package com.example.rickandmortymvvm.util

object StringUtils {
    fun validateEmail(email: String): Boolean{
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailRegex.toRegex())
    }

    fun validatePassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"
        return password.matches(passwordRegex.toRegex())
    }
}