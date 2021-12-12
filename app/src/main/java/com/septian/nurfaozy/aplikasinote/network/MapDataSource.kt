package com.septian.nurfaozy.aplikasinote.network



import com.septian.nurfaozy.aplikasinote.models.DirectionResponses
import retrofit2.Response

class MapDataSource {

    private val retrofit = ClientRequest.retrofitInstance!!
    private val mapService: MapService = retrofit.create(MapService::class.java)

    suspend fun getDirection(
        origin: String, destination: String, apiKey: String
    ): Result<DirectionResponses> {
        return getResponse(
            request = {
                mapService.getDirection(
                    origin = origin, destination = destination, apiKey = apiKey
                )
            },
            defaultErrorMessage = "Error get direction")
    }

    private suspend fun <T> getResponse(
        request: suspend () -> Response<T>,
        defaultErrorMessage: String
    ): Result<T> {
        return try {
            println("I'm working in thread ${Thread.currentThread().name}")
            val result = request.invoke()
            if (result.isSuccessful) {
                return Result.success(result.body())
            } else {
                val errorResponse = ErrorUtils.parseError(result, retrofit)
                Result.error(errorResponse?.status_message ?: defaultErrorMessage, errorResponse)
            }
        } catch (e: Throwable) {
            Result.error("Unknown Error", null)
        }
    }
}