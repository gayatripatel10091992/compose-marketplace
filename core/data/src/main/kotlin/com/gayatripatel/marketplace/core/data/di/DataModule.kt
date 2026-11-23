package com.gayatripatel.marketplace.core.data.di

import android.content.Context
import androidx.room.Room
import com.gayatripatel.marketplace.core.data.local.CartDao
import com.gayatripatel.marketplace.core.data.local.MarketplaceDatabase
import com.gayatripatel.marketplace.core.data.local.ProductDao
import com.gayatripatel.marketplace.core.data.repository.OfflineCartRepository
import com.gayatripatel.marketplace.core.data.repository.OfflineProductRepository
import com.gayatripatel.marketplace.core.domain.CartRepository
import com.gayatripatel.marketplace.core.domain.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MarketplaceDatabase =
        Room.databaseBuilder(context, MarketplaceDatabase::class.java, "marketplace.db").build()

    @Provides
    fun provideProductDao(database: MarketplaceDatabase): ProductDao = database.productDao()

    @Provides
    fun provideCartDao(database: MarketplaceDatabase): CartDao = database.cartDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: OfflineProductRepository): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: OfflineCartRepository): CartRepository
}
