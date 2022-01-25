package ru.greus.binance.nft.mysterybot.config

import org.apache.log4j.Level
import org.apache.log4j.LogManager
import java.io.FileInputStream
import java.util.*

object ConfigurationService {
    val auth: Auth
    val connection: Connection
    val order: Order

    init {
        // Turn off loggers
        LogManager.getRootLogger().level = Level.OFF

        val properties = Properties()

        FileInputStream(SystemConfig.USER_CONFIG_PATH).use { fis ->
            properties.load(fis)

            connection = Connection(
                productForSaleId = properties.getProperty("connection.PRODUCT_FOR_SALE_ID").toLong(),
                requestsCount = properties.getProperty("connection.REQUEST_COUNT").toInt(),
                prepareRequestBeforeTime = properties.getProperty("connection.PREPARE_REQUEST_BEFORE_TIME").toLong(),
                elementWaitTimeout = properties.getProperty("connection.ELEMENT_WAIT_TIMEOUT").toLong(),
                fakeSellUrl = Base64.getDecoder().decode(SystemConfig.FSU).toString(Charsets.UTF_8),
                purchaseUrl = Base64.getDecoder().decode(SystemConfig.PU).toString(Charsets.UTF_8),
                captchaHeadersTimeout = properties.getProperty("connection.CAPTCHA_HEADERS_WAIT_TIMEOUT").toLong(),
                timeCheckUrl = SystemConfig.TIME_CHECK_URL
            )

            auth = Auth(
                sellPageUrl = Base64.getDecoder().decode(SystemConfig.SPU).toString(Charsets.UTF_8).replace(
                    "{PRODUCT_FOR_SALE_ID}",
                    connection.productForSaleId.toString()
                ),
                login = properties.getProperty("auth.LOGIN"),
                password = properties.getProperty("auth.PASSWORD"),
                loginTimeout = properties.getProperty("auth.LOGIN_TIMEOUT").toLong(),
                loginBeforeTime = properties.getProperty("auth.LOGIN_BEFORE_TIME").toLong()
            )

            order = Order(
                productId = properties.getProperty("order.PRODUCT_ID").toLong(),
                productCount = properties.getProperty("order.PRODUCTS_COUNT").toInt(),
                saleTime = properties.getProperty("order.PRODUCT_SALE_TIME").toLong()
            )
        }
    }
}