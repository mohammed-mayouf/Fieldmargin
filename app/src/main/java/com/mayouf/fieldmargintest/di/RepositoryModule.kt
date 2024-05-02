package com.mayouf.fieldmargintest.di

import com.mayouf.fieldmargintest.data.repo.CertificatesRepositoryImpl
import com.mayouf.fieldmargintest.domain.repo.CertificatesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {
    @Binds
    abstract fun bindCertificatesRepository(itemsRepositoryImpl: CertificatesRepositoryImpl): CertificatesRepository
}