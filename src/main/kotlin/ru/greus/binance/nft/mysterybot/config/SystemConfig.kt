package ru.greus.binance.nft.mysterybot.config

object SystemConfig {
    const val TIME_CHECK_URL = "https://api.binance.com/api/v3/time"
    const val USER_CONFIG_PATH = "config/user.properties"
    // const val SELL_PAGE_URL = "https://www.binance.com/ru/nft/goods/sale/{PRODUCT_FOR_SALE_ID}?isBlindBox=1&isOpen=false"
    const val SPU = "aHR0cHM6Ly93d3cuYmluYW5jZS5jb20vcnUvbmZ0L2dvb2RzL3NhbGUve1BST0RVQ1RfRk9SX1NBTEVfSUR9P2lzQmxpbmRCb3g9MSZpc09wZW49ZmFsc2U="
    // const val FAKE_SELL_URL = "https://www.binance.com/bapi/nft/v1/private/nft/nft-trade/product-onsale"
    const val FSU = "aHR0cHM6Ly93d3cuYmluYW5jZS5jb20vYmFwaS9uZnQvdjEvcHJpdmF0ZS9uZnQvbmZ0LXRyYWRlL3Byb2R1Y3Qtb25zYWxl"
    // For items
    //const val PURCHASE_URL = "https://www.binance.com/bapi/nft/v1/private/nft/nft-trade/order-create"
    // For mystery boxes
    // const val PURCHASE_URL = "https://www.binance.com/bapi/nft/v1/private/nft/mystery-box/purchase"
    const val PU = "aHR0cHM6Ly93d3cuYmluYW5jZS5jb20vYmFwaS9uZnQvdjEvcHJpdmF0ZS9uZnQvbXlzdGVyeS1ib3gvcHVyY2hhc2U="
}