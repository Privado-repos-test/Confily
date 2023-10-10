plugins {
    id("conferences4hall.android.library")
    id("conferences4hall.android.library.compose")
    id("conferences4hall.quality")
}

android {
    namespace = "org.gdglille.devfest.android.theme.m3.features"
}

dependencies {
    api(projects.themeM3.ui)
    api(projects.themeM3.schedules.schedulesUi)
    api(projects.themeM3.speakers.speakersUi)
    api(projects.themeM3.style)
    implementation(projects.themeM3.schedules.schedulesFeature)
    implementation(projects.themeM3.navigation)
    implementation(projects.uiResources)
    implementation(projects.androidData)
    implementation(projects.shared)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.lifecycle)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.navigation)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.pager)

    implementation(libs.kotlinx.collections)
}
