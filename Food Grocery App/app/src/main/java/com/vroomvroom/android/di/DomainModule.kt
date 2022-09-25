package com.himanshu.android.di

import com.himanshu.android.domain.model.merchant.MerchantsMapper
import com.himanshu.android.domain.model.order.*
import com.himanshu.android.domain.model.product.ProductMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Singleton
    @Provides
    fun provideMerchantsMapper(): MerchantsMapper {
        return MerchantsMapper()
    }

    @Singleton
    @Provides
    fun provideProductMapper(): ProductMapper {
        return ProductMapper()
    }

    @Singleton
    @Provides
    fun provideOrderInputMapper(): OrderInputMapper {
        return OrderInputMapper()
    }

    @Singleton
    @Provides
    fun provideOrdersResponseMapper(): OrdersResponseMapper {
        return OrdersResponseMapper()
    }
    @Singleton
    @Provides
    fun provideOrderResponseMapper(): OrderResponseMapper {
        return OrderResponseMapper()
    }

    @Singleton
    @Provides
    fun provideLocationInputMapper(): LocationInputMapper {
        return LocationInputMapper()
    }

    @Singleton
    @Provides
    fun provideOrderBuilder(): OrderInputBuilder {
        return OrderInputBuilder()
    }

}