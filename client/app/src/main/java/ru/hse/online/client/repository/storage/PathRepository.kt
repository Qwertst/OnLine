package ru.hse.online.client.repository.storage

import android.util.Log
import kotlinx.coroutines.flow.first
import ru.hse.online.client.repository.networking.api_data.PathRequest
import ru.hse.online.client.repository.networking.api_data.PathResult
import ru.hse.online.client.repository.networking.api_service.PathApiService
import java.util.UUID

class PathRepository(
    private val pathApiService: PathApiService,
    private val appDataStore: AppDataStore
) {

    suspend fun getPaths(friendId: UUID? = null): PathResult {
        val token: String = appDataStore.getValueFlow(AppDataStore.USER_TOKEN, "").first()
        val userId: UUID = appDataStore.getUserIdFlow().first()
        return try {
            val response = pathApiService.getPaths("Bearer $token", friendId ?: userId)
            when (response.code()) {
                200 -> PathResult.Success(
                    paths = response.body(),
                    code = response.code()
                )
                else -> PathResult.Failure(
                    code = response.code(),
                    message = response.message()
                )
            }
        } catch (e: Exception) {
            PathResult.Failure(message = e.localizedMessage)
        }
    }

    suspend fun createPath(path: PathRequest): PathResult {
        val token: String = appDataStore.getValueFlow(AppDataStore.USER_TOKEN, "").first()
        return try {
            val response = pathApiService.createPath("Bearer $token", path)
            when (response.code()) {
                204 -> PathResult.Success(code = response.code())
                else -> PathResult.Failure(
                    code = response.code(),
                    message = response.message()
                )
            }
        } catch (e: Exception) {
            PathResult.Failure(message = e.localizedMessage)
        }
    }

    suspend fun deletePath(token: String, userId: UUID, pathId: UUID): PathResult {
        return try {
            val response = pathApiService.deletePath("Bearer $token", userId, pathId)
            when (response.code()) {
                204 -> PathResult.Success(code = response.code())
                else -> PathResult.Failure(
                    code = response.code(),
                    message = response.message()
                )
            }
        } catch (e: Exception) {
            PathResult.Failure(message = e.localizedMessage)
        }
    }
}