package com.example.androidcameraapplication.navigation

enum class CameraScreens {
    ImportView,
    CaptureView,
    HomeView,
    PhotoView,
    SplashScreen,
    Login,
    Search;

    companion object {
        fun fromRoute(route: String): CameraScreens
                = when(route?.substringBefore("/")){

            ImportView.name -> ImportView
            CaptureView.name -> CaptureView
            HomeView.name-> HomeView
            PhotoView.name -> PhotoView
            SplashScreen.name -> SplashScreen
            Login.name -> Login
            Search.name -> Search
            null -> PhotoView
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }
}