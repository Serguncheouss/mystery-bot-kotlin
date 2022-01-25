package ru.greus.binance.nft.mysterybot.config

import org.openqa.selenium.remote.CapabilityType
import kotlin.io.path.Path

object ChromeConfig {
    val SYSTEM_PROPERTIES = mapOf(
        "webdriver.chrome.driver" to "drivers/chromedriver.exe", // Set driver path
        "webdriver.chrome.silentOutput" to "true" // Disable info messages
    )
    val OPTIONS = listOf(
        "--no-sandbox",
        "--start-maximized",
//        "--ignore-certificate-errors",
        "user-data-dir=${Path("data").toAbsolutePath()}",
        "profile-directory=Binance"
    )
    val EXPERIMENTAL_OPTIONS = mapOf(
        "excludeSwitches" to listOf("enable-automation"), // Turn off automation
//        "useAutomationExtension" to false,
        "prefs" to mapOf( // Turn off credentials service
            "credentials_enable_service" to false,
            "profile.password_manager_enabled" to false
        )
    )
    val CAPABILITIES = mapOf(
        CapabilityType.PAGE_LOAD_STRATEGY to "eager"
    )
}