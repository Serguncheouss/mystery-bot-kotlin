package ru.greus.binance.nft.mysterybot

import ru.greus.binance.nft.mysterybot.config.ConfigurationService
import ru.greus.binance.nft.mysterybot.models.LoginPage
import ru.greus.binance.nft.mysterybot.network.DriverFactory
import ru.greus.binance.nft.mysterybot.network.MysteryChromeDriver
import ru.greus.binance.nft.mysterybot.network.WebClient
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriverException
import kotlin.NoSuchElementException

class Bot {
    private val config = ConfigurationService
    private var driver: MysteryChromeDriver? = null

    val start: Unit
        get() {
            try {
                // Create WebClient
                val client = WebClient(true)

                // For time synchronization
                client.syncTimeWithServer(config.connection.timeCheckUrl)

                // Waiting for (saleTime - loginBeforeTime)
                Utils.waitFor(config.order.saleTime, config.auth.loginBeforeTime, true)

                // Create driver
                driver = DriverFactory.apply { addRequestFilter(client.headersListener) }.getDriver()

                try {
                    // Sign in
                    val loginPage = LoginPage(driver!!)

                    // Doing fake sell
                    val sellPage = loginPage.signIn(config.auth.login, config.auth.password)

                    // Waiting for (saleTime - prepareRequestBeforeTime)
                    Utils.waitFor(config.order.saleTime, config.connection.prepareRequestBeforeTime)

                    sellPage.sell()

                    // Waiting for sale starts (saleTime)
                    Utils.waitFor(config.order.saleTime)

                    println("Start buying up...")
                    client.send(
                        config.connection.purchaseUrl,
                        Utils.mapToJSON(
                            mapOf(
                                "number" to config.order.productCount,
                                "productId" to config.order.productId
                            )
                        ),
                        config.connection.requestsCount
                    )

                } catch (ex: Exception) {
                    when(ex) {
                        is TimeoutException ->
                            println("Waiting for element timeout. " +
                                    "You can increase limit by default in system config *_TIMEOUT. ${ex.message}")
                        is NoSuchElementException, is StaleElementReferenceException ->
                            print("May be page structure has been changed. Please check selectors. ${ex.message}")
                        else -> throw ex
                    }
                }
            } catch (e: WebDriverException) {
                println("Unable to load login page. Check your connection or may be you in ban list?. ${e.message}")
            } finally {
                driver?.quit()
                println("Buying finished. Press Enter to exit.")
                readLine()
            }
        }
}

fun main() {
    try {
        Bot().start
    } catch (e: ExceptionInInitializerError) {
        println("Please check the config file \"config\\user.properties\". Looks like not all params configured properly.")
    }
}