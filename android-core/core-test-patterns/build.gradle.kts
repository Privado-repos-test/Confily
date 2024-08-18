import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id("conferences4hall.android.library")
    id("conferences4hall.android.library.compose")
    id("conferences4hall.quality")
}

android {
    namespace = "org.gdglille.devfest.android.core.test.patterns"
}

dependencies {
    @OptIn(ExperimentalComposeLibrary::class)
    api(compose.uiTestJUnit4)
}
