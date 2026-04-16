package com.example.questionaire.utils


sealed interface UIState<out T> {
    val isLoading: Boolean
    val errorMessages: List<ErrorMessage>

    data class NoData(
        override val isLoading: Boolean = false,
        override val errorMessages: List<ErrorMessage> = emptyList()
    ) : UIState<Nothing>

    data class HasData<out T>(
        val data: T,
        override val isLoading: Boolean = false,
        override val errorMessages: List<ErrorMessage> = emptyList()
    ) : UIState<T>
}

val UIState<*>.hasError get() = errorMessages.isNotEmpty()
val UIState<*>.isRefreshing get() = this is UIState.HasData && isLoading

sealed interface DataStatus<out T> {
    data object Empty: DataStatus<Nothing>
    data class Available<out T>(val data: T): DataStatus<T>
}


data class ViewModelState<T>(
    val status: DataStatus<T> = DataStatus.Empty,
    val isLoading: Boolean = false,
    val errorMessages: List<ErrorMessage> = emptyList()
) {
    fun loading() = copy(isLoading = true)

    fun idle() = copy(isLoading = false)

    fun onSuccess(data: T) = copy(
        status = DataStatus.Available(data),
        isLoading = false
    )

    fun onEmpty() = copy(
        status = DataStatus.Empty,
        isLoading = false
    )

    fun onError(error: ErrorMessage) = copy(
        isLoading = false,
        errorMessages = errorMessages + error
    )

    fun dismissError(id: Long) = copy(
        errorMessages = errorMessages.filterNot { it.id == id }
    )

    fun toUIState(): UIState<T> = when (status) {
        is DataStatus.Empty -> UIState.NoData(
            isLoading = isLoading,
            errorMessages = errorMessages
        )
        is DataStatus.Available -> UIState.HasData(
            data = status.data,
            isLoading = isLoading,
            errorMessages = errorMessages
        )
    }
}
