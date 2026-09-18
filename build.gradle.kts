plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.serialization)
    id("com.vanniktech.maven.publish") version "0.29.0"
}

group = "io.github.mrkefish"
version = "1.2.1"

kotlin {
    jvmToolchain(11)
    jvm()

    androidTarget {
        publishLibraryVariants("release", "debug")
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.bundles.ktor.client)
            api(libs.kotlinx.serialization.json)
            api(libs.kotlinx.coroutines.core)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.java)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.kotlinx.coroutines.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }


    }
}

android {
    namespace = "com.mrkefish.kspw_api"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    coordinates(group.toString(), "kspw-api", version.toString())

    pom {
        name.set("KSPW API")
        description.set("Kotlin Multiplatform, Java and Kotlin library for SpWorlds API")

            url.set("https://github.com/MrKefish/KSPW-Api")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://apache.org")
                }
            }
            developers {
                developer {
                    id.set("mrkefish")
                    name.set("mrkefish")
                    email.set("141611735+MrKefish@users.noreply.github.com")
                }
            }
            scm {
                connection.set("scm:git:://github.com")
                developerConnection.set("scm:git:ssh://://github.com")
                url.set("https://github.com/MrKefish/KSPW-Api")
            }
        }


    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}

extensions.configure<SigningExtension> {
    val signingKey = providers.environmentVariable("ORG_GRADLE_PROJECT_signingKey").orNull
    val signingKeyId = providers.environmentVariable("ORG_GRADLE_PROJECT_signingKeyId").orNull
    val signingPassword = providers.environmentVariable("ORG_GRADLE_PROJECT_signingPassword").orNull

    if (signingKey != null) {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    }

    sign(publishing.publications)
}
