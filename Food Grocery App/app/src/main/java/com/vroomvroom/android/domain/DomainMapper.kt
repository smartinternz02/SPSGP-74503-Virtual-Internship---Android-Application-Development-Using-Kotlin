package com.himanshu.android.domain

interface DomainMapper<T, DomainModel> {
    fun mapToDomainModel(model: T): DomainModel
}