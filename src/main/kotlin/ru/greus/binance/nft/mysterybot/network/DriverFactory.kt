package ru.greus.binance.nft.mysterybot.network

import ru.greus.binance.nft.mysterybot.config.ChromeConfig
import net.lightbody.bmp.BrowserMobProxyServer
import net.lightbody.bmp.client.ClientUtil
import net.lightbody.bmp.filters.RequestFilter
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.CapabilityType

object DriverFactory {
    private val options = ChromeOptions()
    private val proxy = BrowserMobProxyServer()

    init {
        // Start the proxy
        proxy.start()
        // Get the Selenium proxy object
        val seleniumProxy = ClientUtil.createSeleniumProxy(proxy)
        options.setCapability(CapabilityType.PROXY, seleniumProxy)

        // Configure driver
        ChromeConfig.SYSTEM_PROPERTIES.forEach{ (n, v) -> System.setProperty(n, v) }
        ChromeConfig.OPTIONS.forEach { v -> options.addArguments(v) }
        ChromeConfig.EXPERIMENTAL_OPTIONS.forEach { (n, v) -> options.setExperimentalOption(n, v) }
        ChromeConfig.CAPABILITIES.forEach { (n, v) -> options.setCapability(n, v) }
    }

    fun addRequestFilter(listener: RequestFilter) {
        proxy.addRequestFilter(listener)
    }

    fun getDriver(): MysteryChromeDriver {
        val driver = MysteryChromeDriver(options, proxy)
        // Disable selenium identification
        driver.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})")
        driver.executeCdpCommand("Network.setUserAgentOverride", mapOf("userAgent" to
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/96.0.4664.93 Safari/537.36")
        )
        return driver
    }
}