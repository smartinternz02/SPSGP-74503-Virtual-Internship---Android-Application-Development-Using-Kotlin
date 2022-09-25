package com.himanshu.android.di

import android.content.BroadcastReceiver
import com.himanshu.android.repository.local.RoomRepository
import com.himanshu.android.repository.local.RoomRepositoryImpl
import com.himanshu.android.repository.remote.GraphQLRepository
import com.himanshu.android.repository.remote.GraphQLRepositoryImpl
import com.himanshu.android.repository.services.FirebaseAuthRepository
import com.himanshu.android.repository.services.FirebaseAuthRepositoryImpl
import com.himanshu.android.repository.services.LocationRepository
import com.himanshu.android.repository.services.LocationRepositoryImpl
import com.himanshu.android.utils.SmsBroadcastReceiver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule {

    @Binds
    @ViewModelScoped
    abstract fun binRepository(repo: GraphQLRepositoryImpl): GraphQLRepository

    @Binds
    @ViewModelScoped
    abstract fun roomRepository(repo: RoomRepositoryImpl) : RoomRepository

    @Binds
    @ViewModelScoped
    abstract fun firebaseAuthRepository(repo: FirebaseAuthRepositoryImpl) : FirebaseAuthRepository

    @Binds
    @ViewModelScoped
    abstract fun smsBroadcastReceiver(broadcastReceiver: SmsBroadcastReceiver) : BroadcastReceiver

    @Binds
    @ViewModelScoped
    abstract fun locationRepository(repo: LocationRepositoryImpl) : LocationRepository
}