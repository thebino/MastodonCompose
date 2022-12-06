enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name="DodoForMastodon"

include(":di")
include(":app-android")
include(":app-desktop")
//include(":app-iOS")

include(":ui:timeline")
//include(":ui:messages")
//include(":ui:notifications")
//include(":ui:search")
//include(":ui:settings")
include(":ui:common")
include(":ui:root")
include(":ui:signed-in")
include(":ui:signed-out")

include(":domain:timeline")
include(":domain:authentication")

//include(":data-timeline")
include(":data:persistence")
include(":data:network")
include(":data:repository")
