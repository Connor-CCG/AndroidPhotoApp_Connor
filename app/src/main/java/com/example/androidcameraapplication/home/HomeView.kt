package com.example.androidcameraapplication.home

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.androidcameraapplication.AppComponents.BottomMenuItem
import com.example.androidcameraapplication.camera.CameraAppBar
import com.example.androidcameraapplication.camera.PhotoViewModel
import com.example.androidcameraapplication.camera.PhotoViews.GalleryOrientation
import com.example.androidcameraapplication.navigation.CameraScreens
import com.example.samplemodule.MyClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeView(navController: NavController, viewModel: PhotoViewModel){
    val title = "This is a sample title"
    val desc = "Sample description"
    val orientation = GalleryOrientation.Horizontal
    val keyboardController = LocalSoftwareKeyboardController.current
    val auth: FirebaseAuth = Firebase.auth
    LaunchedEffect(auth.currentUser?.uid) {
        viewModel.getLivePhotos()
    }

    Scaffold(
        topBar = {
            CameraAppBar(title = "Photo App", navController = navController){
                navController.popBackStack()
            }
        },
        bottomBar = {
            BottomNavigation(
                navController = navController,
                desc = desc
            )
        },

        modifier = Modifier.fillMaxSize()
    ){
        Box(contentAlignment = Alignment.BottomEnd){
            FloatingActionButton(modifier = Modifier.offset(x= (-20).dp, y = (-80).dp),
                onClick = {
                    // FAB onClick
                    navController.navigate(CameraScreens.PhotoView.name +
                            "/$title/${viewModel.photoDescription}/${viewModel.galleryOrientation}")
                }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp)
            ) {
                Text("Reading will appear here once extracted.",
                    modifier = Modifier.padding(bottom = 10.dp))

                Box(contentAlignment = Alignment.CenterStart){
                    OutlinedTextField(
                        value = viewModel.imageText,
                        onValueChange = { text ->
                            viewModel.imageText = text
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            color = MaterialTheme.colors.onBackground
                        ),
                        modifier = Modifier
                            .padding(top = 20.dp, bottom = 20.dp, start = 40.dp, end = 40.dp)
                            .fillMaxWidth(),
                        enabled = true,
                        keyboardActions  = KeyboardActions {
                            keyboardController?.hide()
                        }
                    )
                    if (viewModel.isExtractingText) {
                        Row(modifier = Modifier.padding(60.dp)){
                            CircularProgressAnimated(modifier = Modifier
                                .size(25.dp)
                                .padding(50.dp))
                            Text("Extracting text from image...", modifier = Modifier.padding(start = 10.dp))
                        }
                    }
                }

                OutlinedTextField(
                    value = viewModel.photoDescription,
                    onValueChange = { text ->
                        viewModel.photoDescription = text
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        color = MaterialTheme.colors.onBackground
                    ),
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 20.dp, start = 40.dp, end = 40.dp)
                        .fillMaxWidth(),
                    enabled = true,
                    label = { Text("Photo Description")},
                    keyboardActions  = KeyboardActions {
                        keyboardController?.hide()
                    }
                )

                val verticalGrid = remember { mutableStateOf(true) }
                val horizontalGrid = remember { mutableStateOf(false) }

                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ){
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start) {
                        Text("Vertical Photo Gallery")

                        Checkbox(
                            checked = viewModel.galleryOrientation == GalleryOrientation.Vertical,
                            onCheckedChange = {
                                verticalGrid.value = it
                                horizontalGrid.value = !it
                                viewModel.toggleGalleryOrientation()

                            }
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start){
                        Text("Horizontal Photo Gallery")

                        Checkbox(
                            checked = viewModel.galleryOrientation == GalleryOrientation.Horizontal,
                            onCheckedChange = {
                                viewModel.toggleGalleryOrientation()
                            }
                        )
                    }

                }

            }


        }
        it

    }

}

//Determinate (based on input)
@Composable
private fun CircularProgressAnimated(modifier: Modifier = Modifier){
    val progressValue = 1.00f
    val infiniteTransition = rememberInfiniteTransition()

    val progressAnimationValue by infiniteTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = progressValue,animationSpec = infiniteRepeatable(animation = tween(1500)))

    CircularProgressIndicator(progress = progressAnimationValue, modifier = Modifier.size(25.dp))
}

@Composable
fun BottomNavigation(
    navController: NavController,
    desc: String
) {
    fun prepareBottomMenu(): List<BottomMenuItem> {
        val bottomMenuItemsList = arrayListOf<BottomMenuItem>()

        // add menu items
        bottomMenuItemsList.add(BottomMenuItem(label = "Home", icon = Icons.Filled.Home))
        bottomMenuItemsList.add(BottomMenuItem(label = "Search", icon = Icons.Filled.Search))

        return bottomMenuItemsList
    }
    // items list
    val bottomMenuItemsList = prepareBottomMenu()

    val contextForToast = LocalContext.current.applicationContext

    var selectedItem by remember {
        mutableStateOf("Home")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BottomNavigation(
            modifier = Modifier.align(alignment = Alignment.BottomCenter)
        ) {
            bottomMenuItemsList.forEach { menuItem ->
                BottomNavigationItem(
                    selected = (selectedItem == menuItem.label),
                    onClick = {
                        selectedItem = menuItem.label
                        if(menuItem.label == "Home"){
                            navController.navigate(CameraScreens.HomeView.name)
                        } else if (menuItem.label == "Search"){
                            navController.navigate(CameraScreens.Search.name + "/$desc")
                        }

                    },
                    icon = {
                        Icon(
                            imageVector = menuItem.icon,
                            contentDescription = menuItem.label
                        )
                    },
                    label = {
                        Text(text = menuItem.label)
                    },
                    enabled = true
                )
            }
        }
    }
}
