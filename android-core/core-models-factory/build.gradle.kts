plugins {
    id("conferences4hall.android.library")
    id("conferences4hall.quality")
}

android {
    namespace = "com.paligot.confily.core.models.factory"
}

dependencies {
    api(projects.shared.models)
    implementation(libs.jetbrains.kotlinx.datetime)
}
