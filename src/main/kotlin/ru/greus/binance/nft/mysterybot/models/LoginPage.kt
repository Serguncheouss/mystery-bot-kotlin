package ru.greus.binance.nft.mysterybot.models

import ru.greus.binance.nft.mysterybot.config.ConfigurationService
import ru.greus.binance.nft.mysterybot.network.MysteryChromeDriver
import org.openqa.selenium.By

class LoginPage(private val driver: MysteryChromeDriver) {
    private val emailBy: By = By.name("email")
    private val passwordBy: By = By.name("password")
    private val submitBy: By = By.id("click_login_submit")

    init {
        val config = ConfigurationService
        driver.get(config.auth.sellPageUrl)
    }

    fun signIn(email: String, password: String): SellPage {
        println("Preparing to login in...")
        with(driver) {
            waitForElementClickable(emailBy).sendKeys(email)
            waitForElementClickable(passwordBy).sendKeys(password)
            waitForElementClickable(submitBy).submit()
        }

        return SellPage(driver)
    }
}