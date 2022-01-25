import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
    // For exe with JRE distribution
    //id("org.beryx.runtime") version "1.12.7"
    // For exe with JRE distribution
    //id("edu.sc.seis.launch4j") version "2.5.1"
}

group = "ru.greus"
version = "1.1.0"

repositories {
    mavenCentral()
}

// For exe with JRE distribution
//runtime {
//    addOptions("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
//}

//tasks.runtime {
//    doLast {
//        copy {
//            from("src/main/resources")
//            into("$buildDir/image/bin")
//        }
//    }
//}

//tasks.create<Copy>("addJreToDistributable")  {
//    from(zipTree("C:\\Users\\Strel\\Downloads\\openjdk-11.0.2_windows-x64_bin.zip"))
//    destinationDir = file("$buildDir/launch4j")
//}
//
//tasks.createExe {
//    dependsOn("addJreToDistributable")
//}

tasks.installDist {
    doLast {
        copy {
            from(tasks.startScripts)
            into(destinationDir)
        }
        copy {
            from(tasks.jar.get().outputs.files)
            into("$destinationDir/lib")
        }
        copy {
            from("config")
            into("$destinationDir/config")
        }
        copy {
            from("drivers")
            into("$destinationDir/drivers")
        }
        copy {
            from("data")
            into("$destinationDir/data")
        }
        copy {
            from("assets")
            into("$destinationDir/assets")
        }
    }
    eachFile {
        if (path.contains("bin")) {
            exclude()
        }
    }
}

tasks.distZip {
    eachFile {
        if (path.contains("bin")) {
            exclude()
        }
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ru.greus.binance.nft.mysterybot.BotKt"
        attributes["Class-Path"] = configurations["runtimeClasspath"].joinToString(" ") { it.name }
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile)) {
            exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
        }
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
//    configurations["runtimeClasspath"].map { if (it.isDirectory) it else zipTree(it) }
}

// For bat without JRE distribution, fix classpath in bat file
tasks.startScripts {
    executableDir = ""
    classpath = tasks.jar.get().outputs.files
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

dependencies {
    implementation("org.seleniumhq.selenium:selenium-java:4.1.0")
    implementation("org.seleniumhq.selenium:selenium-chrome-driver:4.1.0")
    implementation("org.json:json:20211205")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.qaprosoft:carina-proxy:7.2.14")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.0")
}

application {
    mainClass.set("ru.greus.binance.nft.mysterybot.BotKt")
}
// For exe with JRE distribution
//launch4j {
//    mainClassName = "ru.greus.mysteryparser.BotKt"
//    icon = "${projectDir}/src/main/resources/images/logo.ico"
//    bundledJrePath = "jre"
//    jreMinVersion = "11"
//}