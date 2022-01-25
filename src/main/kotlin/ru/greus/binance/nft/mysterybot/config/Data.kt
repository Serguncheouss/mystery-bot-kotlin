package ru.greus.binance.nft.mysterybot.config

data class Auth(
    val sellPageUrl: String,
    val login: String,
    val password: String,
    val loginTimeout: Long,
    val loginBeforeTime: Long
)

data class Connection(
    val productForSaleId: Long,
    val requestsCount: Int,
    val prepareRequestBeforeTime: Long,
    val elementWaitTimeout: Long,
    val fakeSellUrl: String,
    val purchaseUrl: String,
    val captchaHeadersTimeout: Long,
    val timeCheckUrl: String
)

data class Order(
    val productId: Long,
    val productCount: Int,
    val saleTime: Long
)