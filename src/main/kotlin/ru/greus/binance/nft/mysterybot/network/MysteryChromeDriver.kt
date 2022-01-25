package ru.greus.binance.nft.mysterybot.network

import ru.greus.binance.nft.mysterybot.config.ConfigurationService
import net.lightbody.bmp.BrowserMobProxyServer
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration
import kotlin.jvm.Throws

class MysteryChromeDriver(options: ChromeOptions): ChromeDriver(options) {
    private val config = ConfigurationService
    private var proxy: BrowserMobProxyServer? = null

    constructor(options: ChromeOptions, proxy: BrowserMobProxyServer?) : this(options) {
        this.proxy = proxy
    }

    @Throws(TimeoutException::class)
    fun waitForElement(by: By, timeToWait: Long = config.connection.elementWaitTimeout): WebElement {
        return WebDriverWait(this, Duration.ofSeconds(timeToWait)).until { d -> d.findElement(by) }
    }

    @Throws(TimeoutException::class)
    fun waitForElementClickable(by: By, timeToWait: Long = config.connection.elementWaitTimeout): WebElement {
        return WebDriverWait(this, Duration.ofSeconds(timeToWait))
            .until(ExpectedConditions.elementToBeClickable(by))
    }

    @Throws(TimeoutException::class)
    fun waitForElementVisibility(by: By, timeToWait: Long = config.connection.elementWaitTimeout): WebElement {
        return WebDriverWait(this, Duration.ofSeconds(timeToWait))
            .until(ExpectedConditions.visibilityOfElementLocated(by))
    }

    override fun quit() {
        super.quit()
        proxy?.stop()
    }
}