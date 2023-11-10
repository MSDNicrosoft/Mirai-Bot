import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    val kotlinVersion = "1.9.20"
    kotlin("jvm").version(kotlinVersion)
    kotlin("plugin.serialization").version(kotlinVersion)

    val detektVersion = "1.23.1"
    id("io.gitlab.arturbosch.detekt").version(detektVersion)

    id("org.ajoberstar.grgit").version("5.2.1")
    id("net.mamoe.mirai-console").version("2.16.0")
    id("net.kyori.blossom").version("2.1.0")
    id("com.github.johnrengelman.shadow").version("8.1.1")  // FIXME
}

base {
    group = "${properties["maven_group"]}"
    archivesName = "${properties["archives_base_name"]}"
    version = "${properties["version"]}+${
        if (grgit.status().isClean()) {
            grgit.head().abbreviatedId
        } else {
            "dev"
        }
    }"
}

repositories {
    if (System.getenv("CI")?.toBoolean() != true) {
        maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    }
    mavenCentral()
}

dependencies {
    val gson_version: String by project
    implementation("com.google.code.gson:gson:$gson_version")

    val kotlinx_serialization_version: String by project
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version")

    val ktor_version: String by project
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-cio-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    val quartz_version: String by project
    implementation("org.quartz-scheduler:quartz:$quartz_version")

    val sentry_version: String by project
    implementation("io.sentry:sentry:$sentry_version")

    val detektVersion = "1.23.1"
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${detektVersion}")
}

mirai {
    jvmTarget = JavaVersion.VERSION_17
}

detekt {
    parallel = true
    config.setFrom(file("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = false
}

sourceSets {
    main {
        blossom {
            resources {
                property("version", version.toString())
            }
        }
    }
}

tasks {
    val jvmVersion = "17"

    withType<Detekt>().configureEach {
        jvmTarget = jvmVersion
    }

    withType<DetektCreateBaselineTask>().configureEach {
        jvmTarget = jvmVersion
    }

    compileKotlin {
        dependsOn("detekt")
        kotlinOptions {
            jvmTarget = jvmVersion
        }
    }

    compileJava {
        enabled = false
    }

    processTestResources {
        enabled = false
    }
}
