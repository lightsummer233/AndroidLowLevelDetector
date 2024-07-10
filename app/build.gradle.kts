import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import org.gradle.configurationcache.extensions.capitalized
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")

    id("kotlin-android")

    kotlin("plugin.serialization")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = Versions.AndroidBuild.compileSdk
    // compileSdkExtension = Versions.AndroidBuild.compileSdkExtension
    // compileSdkPreview = Versions.AndroidBuild.compileSdkPreview
    buildToolsVersion = Versions.AndroidBuild.buildTools

    defaultConfig {
        namespace = "net.imknown.android.forefrontinfo"

        versionCode = Versions.AndroidBuild.versionCode
        versionName = Versions.AndroidBuild.versionName

        val currentDatetime = getCurrentDatetime()
        val currentGitBranchName = providers.execute("git", "rev-parse", "--abbrev-ref", "HEAD")
        base.archivesName.set("lld-$versionName-$versionCode-$currentDatetime-$currentGitBranchName")

        minSdk = Versions.AndroidBuild.minSdk
        targetSdk = Versions.AndroidBuild.targetSdk
        // targetSdkPreview = Versions.AndroidBuild.targetSdkPreview

        resourceConfigurations.addAll(listOf("zh-rCN", "zh-rTW", "fr-rFR"))

        buildConfigField("String", "GIT_BRANCH", "\"$currentGitBranchName\"")

        ndkVersion = Versions.AndroidBuild.ndk

        externalNativeBuild {
            cmake {
                arguments += listOf("-DANDROID_ARM_NEON=TRUE", "-DANDROID_TOOLCHAIN=clang")

                cFlags += listOf("-D__STDC_FORMAT_MACROS")

                cppFlags += listOf("-fexceptions", "-frtti", "-std=c++17")
            }
        }
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = file("$rootDir/local.properties")
            val keystoreProperties = Properties().apply {
                load(FileInputStream(keystorePropertiesFile))
            }

            storeFile = file(keystoreProperties["storeFile"].toString())
            storePassword = keystoreProperties["storePassword"].toString()
            keyAlias = keystoreProperties["keyAlias"].toString()
            keyPassword = keystoreProperties["keyPassword"].toString()

            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }

        getByName("debug") {
            storeFile = file("$rootDir/keys/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"

            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    flavorDimensions += IssueTracker::class.simpleName.toString()

    productFlavors {
        create(IssueTracker.foss.name)
        create(IssueTracker.firebase.name)
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            signingConfig = signingConfigs.getByName(name)

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "proguard-firebase-rules.pro",
                "proguard-rules-kotlinx-serialization-json.pro"
            )

            configure<CrashlyticsExtension> {
                nativeSymbolUploadEnabled = true
            }
        }

        debug {
            isDebuggable = true
            isJniDebuggable = true

            signingConfig = signingConfigs.getByName(name)

            applicationIdSuffix = ".$name"
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    externalNativeBuild {
        cmake {
            path("CMakeLists.txt")
            version = Versions.AndroidBuild.cmake
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    lint {
        checkDependencies = true
    }
}

tasks.whenTaskAdded {
    val flavorNone = IssueTracker.foss.name.capitalized()
    if (name.startsWith("process$flavorNone") && name.endsWith("GoogleServices")) {
        println("Task $name disabled.")
        enabled = false
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(project(":base"))

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${Versions.desugarJdkLibs}")

    // region [AndroidX]
    implementation("androidx.cardview:cardview:${Versions.AndroidX.cardView}")

    implementation("androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraintLayout}")

    implementation("androidx.coordinatorlayout:coordinatorlayout:${Versions.AndroidX.coordinatorLayout}")

    implementation("androidx.recyclerview:recyclerview:${Versions.AndroidX.recyclerView}")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:${Versions.AndroidX.swipeRefreshLayout}")
    implementation("androidx.webkit:webkit:${Versions.AndroidX.webkit}")
    // endregion [AndroidX]

    // region [3rd Parties]
    debugImplementation("com.squareup.leakcanary:leakcanary-android:${Versions.ThirdParties.leakCanary}")
    // implementation ("com.squareup.leakcanary:plumber-android:${Versions.ThirdParties.leakCanary}")
    // endregion [3rd Parties]

    val firebaseImplementation = IssueTracker.firebase.name + "Implementation"
    firebaseImplementation(platform("com.google.firebase:firebase-bom:${Versions.Firebase.billOfMaterials}"))
    firebaseImplementation("com.google.firebase:firebase-analytics-ktx")
    firebaseImplementation("com.google.firebase:firebase-crashlytics-ndk")
}