package com.example.rickandmortymvvm.ui.login

import android.widget.Toast
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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.rickandmortymvvm.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SignUpScreen(
    navController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    val authState = loginViewModel.authState.observeAsState()
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val colorNotificationBar = colorResource(R.color.soft_blue)

    SideEffect {
        systemUiController.setStatusBarColor(
            color = colorNotificationBar,
            darkIcons = true
        )
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is LoginState.Authenticated -> navController.navigate("home")
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
            text = stringResource(R.string.sign_up_title),
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
            onValueChange = {
                password = it
            },
            label = {
                Text(text = stringResource(R.string.login_password))
            },
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
                loginViewModel.signup(email, password)
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
            Text(text = stringResource(R.string.sign_up_create_account))
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            navController.navigate("login")
        }) {
            Text(
                text = stringResource(R.string.sign_up_have_account),
                color = colorResource(R.color.dark_green)
            )
        }

    }
}