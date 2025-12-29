package com.toyou.core.domain.model

sealed class DomainResult<out T> {
    data class Success<T>(val data: T) : DomainResult<T>()
    data class Error(
        val code: Int = -1,
        val message: String = "Unknown error"
    ) : DomainResult<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = (this as? Success)?.data
    fun getOrThrow(): T = (this as? Success)?.data ?: throw IllegalStateException("Result is error")

    inline fun <R> map(transform: (T) -> R): DomainResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
    }

    inline fun onSuccess(action: (T) -> Unit): DomainResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Error) -> Unit): DomainResult<T> {
        if (this is Error) action(this)
        return this
    }
}
