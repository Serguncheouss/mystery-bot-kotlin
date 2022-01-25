package ru.greus.binance.nft.mysterybot.models

import ru.greus.binance.nft.mysterybot.config.ConfigurationService
import ru.greus.binance.nft.mysterybot.network.MysteryChromeDriver
import org.openqa.selenium.By
import java.lang.Thread.sleep

class SellPage(private val driver: MysteryChromeDriver) {
    private val sellPrice = "999999999" // Must have type String
    private val priceBy: By = By.xpath("/html/body/div[1]/div/div[2]/main/div/div/div[5]/div[2]/div/div[1]/input")
    private val confirmBy: By = By.xpath("/html/body/div[1]/div/div[2]/main/div/div/div[8]/button[2]")
    private val createOrderBy: By = By.xpath("/html/body/div[5]/div/div/div[7]/button[2]")

    init {
        val config = ConfigurationService

        driver.waitForElement(confirmBy, config.auth.loginTimeout)
        println("Logged in.")

        if (driver.currentUrl != config.auth.sellPageUrl) {
            throw IllegalStateException("This is not Sale Page. Current page is: ${driver.currentUrl}")
        }
    }

    fun sell() {
        with(driver) {
            waitForElementClickable(priceBy).apply {
                while (Regex("[^0-9]").replace(getAttribute("value"), "") != sellPrice) {
                    clear()
                    sellPrice.forEach { char ->
                        click()
                        sendKeys(char.toString())
                        sleep(250)
                    }
                }
            }
            waitForElementClickable(confirmBy).click()
            waitForElementClickable(createOrderBy).click()
        }
    }
}