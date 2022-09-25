package com.himanshu.android.utils

import android.content.Intent
import com.himanshu.android.MerchantQuery
import com.himanshu.android.OrdersQuery
import com.himanshu.android.view.state.ViewState


interface OnProductClickListener {
    fun onClick(product: MerchantQuery.Product?)
}

interface OnOrderProductClickListener {
    fun onClick(product: OrdersQuery.Product?)
}

interface SmsBroadcastReceiverListener {
    fun onIntent(intent: ViewState<Intent>)
}