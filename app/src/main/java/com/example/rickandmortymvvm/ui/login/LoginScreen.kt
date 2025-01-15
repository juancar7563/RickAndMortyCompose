package com.example.rickandmortymvvm.ui.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.rickandmortymvvm.R
import com.example.rickandmortymvvm.ui.Screen
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun LoginScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val colorNotificationBar = colorResource(R.color.soft_blue)
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var passwordVisible by remember { mutableStateOf(false) }
    val authState = loginViewModel.authState.observeAsState()
    val context = LocalContext.current

    SideEffect {
        systemUiController.setStatusBarColor(
            color = colorNotificationBar,
            darkIcons = true
        )
    }

    BackHandler {
        (context as? Activity)?.finishAndRemoveTask()
    }

    LaunchedEffect(authState.value) {
        when (val state = authState.value) {
            is LoginState.Authenticated -> {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            }

            is LoginState.RememberedUser -> {
                email = state.userMail
            }

            is LoginState.Error -> Toast.makeText(
                context,
                (authState.value as LoginState.Error).message, Toast.LENGTH_SHORT
            ).show()

            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.soft_blue)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.login_title),
            fontSize = 32.sp,
            color = colorResource(R.color.dark_green)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = stringResource(R.string.login_mail))
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(R.color.dark_green),
                unfocusedBorderColor = colorResource(R.color.dark_green),
                focusedLabelColor = colorResource(R.color.dark_green),
                backgroundColor = colorResource(R.color.green_morty)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.login_password)) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image =
                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(R.color.dark_green),
                unfocusedBorderColor = colorResource(R.color.dark_green),
                focusedLabelColor = colorResource(R.color.dark_green),
                backgroundColor = colorResource(R.color.green_morty)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                loginViewModel.login(email, password)
            },
            enabled = authState.value != LoginState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(64.dp, 0.dp), // Espaciado alrededor del botón
            shape = RoundedCornerShape(16.dp), // Bordes redondeados
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(R.color.dark_green),
                contentColor = colorResource(R.color.green_morty)
            )
        ) {
            Text(text = stringResource(R.string.login_subtitle))
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            navController.navigate(Screen.SignUp.route)
        }) {
            Text(
                text = stringResource(R.string.login_no_have_account),
                color = colorResource(R.color.dark_green)
            )
        }
    }

}