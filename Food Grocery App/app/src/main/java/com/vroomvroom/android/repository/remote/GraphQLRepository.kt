package com.himanshu.android.repository.remote

import com.himanshu.android.*
import com.himanshu.android.domain.db.user.UserLocationEntity
import com.himanshu.android.domain.model.merchant.Merchants
import com.himanshu.android.domain.model.order.OrderResponse
import com.himanshu.android.type.LocationInput
import com.himanshu.android.type.OrderInput
import com.himanshu.android.type.ReviewInput
import com.himanshu.android.view.state.ViewState

interface GraphQLRepository {

    suspend fun queryCategory(type: String): ViewState<CategoryQuery.Data>?

    suspend fun queryMerchants(query: String, filter: String?): ViewState<Merchants>?
    suspend fun queryMerchant(merchantId: String): ViewState<MerchantQuery.Data>?
    suspend fun mutationFavorite(merchantId: String, direction: Int): ViewState<FavoriteMutation.Data>?
    suspend fun mutationReview(reviewInput: ReviewInput): ViewState<ReviewMutation.Data>?

    suspend fun queryOrders(): ViewState<OrdersQuery.Data>?
    suspend fun queryOrdersByStatus(status: String): ViewState<List<OrderResponse?>>?
    suspend fun queryOrder(orderId: String): ViewState<OrderResponse>?
    suspend fun queryOrdersStatus(): ViewState<OrdersStatusQuery.Data>?
    suspend fun mutationCreateOrder(orderInput: OrderInput): ViewState<CreateOrderMutation.Data>?
    suspend fun mutationUpdateDeliveryAddress(orderId: String, locationInput: LocationInput): ViewState<UpdateDeliveryAddressMutation.Data>?
    suspend fun mutationUpdateOrderNotified(orderId: String): ViewState<UpdateOrderNotifiedMutation.Data>?
    suspend fun mutationCancelOrder(orderId: String, reason: String): ViewState<CancelOrderMutation.Data>?

    suspend fun queryUser(): ViewState<UserQuery.Data>?
    suspend fun mutationRegister(): ViewState<RegisterMutation.Data>?
    suspend fun mutationUpdateName(name: String): ViewState<UpdateNameMutation.Data>?
    suspend fun mutationUpdateUserLocation(locationEntity: UserLocationEntity): ViewState<UpdateUserLocationMutation.Data>?

    suspend fun mutationVerifyMobileNumber(number:String): ViewState<VerifyMobileNumberMutation.Data>?
    suspend fun mutationOtpVerification(otp: String): ViewState<OtpVerificationMutation.Data>?
}