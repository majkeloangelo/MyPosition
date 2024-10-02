package com.example.myposition

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun LoginScreen(navController: NavHostController) {
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    var login by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter=  painterResource(id = R.drawable.theme),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
    Spacer(modifier = Modifier.height(36.dp))
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .background(Color(255, 255, 255, 0))
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.Absolute.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    ),
            ){
                Text(
                    text = "My Location",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        color = Color(15, 33, 68),
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = login,
                onValueChange = { login = it },
                label = { Text("Enter Login") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.login),
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter Password") },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                trailingIcon = {
                    val image = if (passwordVisibility) {
                        painterResource(id = R.drawable.pass_eye_off)
                    } else {
                        painterResource(id = R.drawable.pass_eye)
                    }
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(painter = image, contentDescription = null)
                    }
                },
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.lock), contentDescription = null)
                },
                shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                ),
            )
            Spacer(modifier = Modifier.height(72.dp))
            FloatingButton(onClick = { navController.navigate("map_screen") }, login, password)
            Text(
                text = "Login ",
                modifier = Modifier
                    .fillMaxSize(),
                style = TextStyle(
                    textAlign = TextAlign.Justify,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(15, 33, 68),

                    )
            )
        }
    }
}
fun Validate(login: String, password: String): Boolean{
    if (login.toLowerCase() == "admin" && password == "admin"){
        return true
    }else{
        return false
    }
}
@Composable
fun FloatingButton(onClick: () -> Unit, login: String, password: String) {
    var buttonColor by remember { mutableStateOf(Color(0xFFFFFFFF)) }
    FloatingActionButton(
        onClick = {
            if (Validate(login, password)) {
                onClick()
            }
        },
        containerColor = buttonColor
    ){
        Icon(painter = painterResource(id = R.drawable.phone), contentDescription = null)
    }
}