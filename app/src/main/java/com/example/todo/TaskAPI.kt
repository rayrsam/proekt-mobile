    package com.example.todo

    import okhttp3.Response
    import retrofit2.Call
    import retrofit2.http.Body
    import retrofit2.http.DELETE
    import retrofit2.http.GET
    import retrofit2.http.POST
    import retrofit2.http.PUT
    import retrofit2.http.Path

    interface TaskAPI {

        @GET("GetList/{tags}")
        fun getList(@Path("tags") tags: String): Call<List<Task>>

        @POST("Create")
        fun createTask(@Body task: Task): Call<String>

        @PUT("Update")
        fun updateTask(@Body task: Task): Call<Task>

        @DELETE("Delete/{id}")
        fun deleteTask(@Path("id") id: String) : Call<Task>


    }