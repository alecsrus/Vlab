package com.virtuallabs.premium

import android.app.Activity
import android.content.Context

/**
 * Заготовка под Google Play Billing.
 *
 * В прототипе мы НЕ выполняем реальную покупку (для этого нужен релиз в Google Play и настроенные товары).
 * Но класс оставлен, чтобы архитектурно было куда “включить” оплату.
 */
class BillingBridge(
    private val context: Context
) {
    // TODO: подключить BillingClient и продукты (sku/productId)
    // Реализация зависит от того:
    // - подписка или единоразовая покупка,
    // - сколько уровней premium,
    // - нужна ли семейная библиотека и т.д.

    fun launchPremiumPurchaseFlow(activity: Activity) {
        // TODO: реальная покупка через BillingClient
        // Пока — ничего.
    }
}
