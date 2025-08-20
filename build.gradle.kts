import io.izzel.taboolib.gradle.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("io.izzel.taboolib") version "2.0.23"
    kotlin("jvm") version "1.9.21"
}

taboolib {
    env {
        // 安装模块
        install(Basic,
            Bukkit,
            BukkitHook,
            BukkitUtil,
            BukkitNMSUtil,
            Database,
            PtcObject
        )
    }
    version {
        taboolib = "6.2.3-5297ae6"
        coroutines = "1.8.1"
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms.core:v12104:12104-minimize:mapped@jar")
    compileOnly("ink.ptms.core:v12104:12104-minimize:universal@jar")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
