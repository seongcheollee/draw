import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gpsdraw.data.LoginBody
import com.example.gpsdraw.data.SignInResultDto
import com.example.gpsdraw.data.retrofit.ClientErrorBody
import com.example.gpsdraw.data.retrofit.LoginService
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginServiceCreator {

    private val BASE_URL = "https://2b92-222-112-208-80.ngrok-free.app"
    private val client: LoginService

    private val gson = GsonBuilder().setLenient().create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    init {
        client = retrofit.create(LoginService::class.java)
    }


        fun requestLogin(uid: String, password: String): LiveData<String> {
            val liveData = MutableLiveData<String>()
            val body = LoginBody(uid, password)
            val call = client.requestLogin(body)

            call.enqueue(object : Callback<SignInResultDto> { // Specify the correct type here
                override fun onFailure(call: Call<SignInResultDto>, t: Throwable) {
                    Log.e("이메일 error", "${t.message}")
                    liveData.value = "server error"
                }

                override fun onResponse(
                    call: Call<SignInResultDto>,
                    response: Response<SignInResultDto>
                ) {
                    if (response.isSuccessful) {
                        val signInResultDto = response.body() // 서버로부터 받은 SignInResultDto 객체
                        val token = signInResultDto?.token // 토큰 값을 가져옴
                        Log.d("요청 성공!", "onResponse is Successful! Token: $token")

                        liveData.value = token!! // 또는 원하는 다른 응답 메시지
                    } else {
                        Log.e("요청 실패", "response is not Successful")

                        try {
                            val errorBody = response.errorBody()
                            val errorConverter = retrofit.responseBodyConverter<ClientErrorBody>(
                                ClientErrorBody::class.java,
                                ClientErrorBody::class.java.annotations
                            )

                            val convertedBody = errorConverter.convert(errorBody)
                            Log.e("errorBody() message is ", "${convertedBody?.message}")

                            when (convertedBody?.message) {
                                "가입되지 않은 E-MAIL 입니다." -> {
                                    liveData.value = "not registered email"
                                }
                                "잘못된 비밀번호입니다." -> {
                                    liveData.value = "incorrect password"
                                }
                                else -> {
                                    liveData.value = "server error"
                                }
                            }
                        } catch (e: Exception) {
                            liveData.value = "server error"
                            e.printStackTrace()
                        }
                    }
                }
            })

            return liveData
        }

}
