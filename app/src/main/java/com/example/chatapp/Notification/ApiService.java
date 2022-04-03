package com.example.chatapp.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA4Swc-oM:APA91bGZ8YDYPI9ECNVfBi_2MW0OQbfdVlR-4nizHZgqEpweEwQRZ8lb4Omm6G9eYH31Cw4WmbxLo8Y_RUB-AIGjr2htPR048b5WtVYgFUHQxGw_bDqcWtn0fg9tqs1Ya0Q1VsVAUDr7"
        }
    )
    @POST("fcm/send")
    Call<Response>sendNotification(@Body Sender body);

}
