package com.example.androidcameraapplication.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavHostController
import com.example.androidcameraapplication.camera.CameraAppBar
import com.example.androidcameraapplication.camera.PhotoViewModel
import com.example.androidcameraapplication.camera.PhotoViews.GalleryOrientation
import com.example.androidcameraapplication.camera.PhotoViews.VPhotoView
import com.example.androidcameraapplication.login.components.InputField

@Composable
fun SearchScreen(
    navController: NavHostController,
    title: String,
    description: String,
    viewModel: PhotoViewModel,
    onSearch: (String) -> Unit = {}
){
    Scaffold(topBar = {
        CameraAppBar(title = "Search Photos", navController = navController,
            icon = Icons.Default.ArrowBack){
            navController.popBackStack()
        }
    }){
        SearchForm(
            title,
            description,
            viewModel,
            navController,
            onSearch
        )
        it
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchForm(
    title: String,
    description: String,
    viewModel: PhotoViewModel,
    navController: NavHostController,
    onSearch: (String) -> Unit = {}
){
    Column() {
        val searchQuery = rememberSaveable { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQuery.value){
            searchQuery.value.trim().isNotEmpty()
        }

        InputField(valueState = searchQuery, labelId = "Search", enabled = true, onAction = KeyboardActions{
            if (!valid){
                return@KeyboardActions
            } else {
                onSearch(searchQuery.value.trim())
                searchQuery.value = ""
                keyboardController?.hide()
            }
        })

        VPhotoView(
            title = title,
            description = description,
            orientation = GalleryOrientation.Vertical,
            viewModel = viewModel,
            isSearch = true,
            navController = navController
        )
    }
}