package com.himanshu.android.domain.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.himanshu.android.domain.db.cart.CartItemChoiceEntity
import com.himanshu.android.domain.db.cart.CartItemDAO
import com.himanshu.android.domain.db.cart.CartItemEntity
import com.himanshu.android.domain.db.search.SearchDao
import com.himanshu.android.domain.db.search.SearchEntity
import com.himanshu.android.domain.db.user.UserDao
import com.himanshu.android.domain.db.user.UserEntity
import com.himanshu.android.domain.db.user.UserLocationEntity

@Database(
    entities = [
        CartItemEntity::class,
        CartItemChoiceEntity::class,
        UserEntity::class,
        UserLocationEntity::class,
        SearchEntity::class
               ],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun cartItemDao(): CartItemDAO
    abstract fun userDao(): UserDao
    abstract fun searchDao(): SearchDao

}