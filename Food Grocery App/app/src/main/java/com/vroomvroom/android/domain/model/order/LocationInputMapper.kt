package com.himanshu.android.domain.model.order

import com.apollographql.apollo.api.toInput
import com.himanshu.android.domain.db.user.UserLocationEntity
import com.himanshu.android.domain.DomainMapper
import com.himanshu.android.type.LocationInput

class LocationInputMapper: DomainMapper<UserLocationEntity, LocationInput> {
    override fun mapToDomainModel(model: UserLocationEntity): LocationInput {
        return LocationInput(
            model.address.toInput(),
            model.city.toInput(),
            model.addInfo.toInput(),
            listOf(model.latitude, model.longitude)
        )
    }
}