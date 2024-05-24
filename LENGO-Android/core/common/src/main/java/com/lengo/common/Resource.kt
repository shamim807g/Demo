package com.lengo.common


/**
 * Status of a resource that is provided to the UI.
 *
 *
 * These are usually created by the Repository classes where they return
 * `LiveData<Resource<T>>` to pass back the latest data to the UI with its fetch status.
 */
enum class DataStatus {
    SUCCESS,
    ERROR,
    LOADING
}


data class Resource<out T>(val status: DataStatus, val data: T?, val message: String?, val error: Throwable? = null) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(DataStatus.SUCCESS, data, null)
        }

        fun <T> error(error: Throwable): Resource<T> {
            return Resource(DataStatus.ERROR, null, null, error)
        }

        fun <T> loading(): Resource<T> {
            return Resource(DataStatus.LOADING, null, null)
        }

        /*fun <T : Any> handleException(e: Throwable): Resource<T> {
            return when (e) {
                is IOException -> Resource.error("No network connected!")
                else -> Resource.error(e.localizedMessage ?: "Something went wrong")
            }
        }*/
    }
}