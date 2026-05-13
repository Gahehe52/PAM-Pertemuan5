package data.error

import io.ktor.client.plugins.ClientRequestException
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.SerializationException

sealed class AIError : Exception() {
    data class RateLimited(val retryAfter: Int) : AIError()
    data class Unauthorized(override val message: String) : AIError()
    data class ServerError(override val message: String) : AIError()
    data class NetworkError(override val message: String) : AIError()
    data class ParseError(override val message: String) : AIError()
    data class UnknownError(override val message: String) : AIError()
}

suspend fun <T> safeAICall(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: ClientRequestException) {
        when (e.response.status.value) {
            401 -> Result.failure(AIError.Unauthorized("Invalid API key. Check local.properties."))
            429 -> {
                val retryAfter = e.response.headers["Retry-After"]?.toIntOrNull() ?: 60
                Result.failure(AIError.RateLimited(retryAfter))
            }
            in 500..599 -> Result.failure(AIError.ServerError("Server Al sedang bermasalah."))
            else -> Result.failure(AIError.UnknownError(e.message ?: "Unknown Client Error"))
        }
    } catch (e: IOException) {
        Result.failure(AIError.NetworkError("Tidak ada koneksi internet."))
    } catch (e: SerializationException) {
        Result.failure(AIError.ParseError("Gagal memproses respons dari server."))
    } catch (e: Exception) {
        Result.failure(e)
    }
}