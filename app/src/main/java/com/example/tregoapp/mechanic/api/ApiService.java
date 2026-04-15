package com.example.tregoapp.mechanic.api;

import com.example.tregoapp.mechanic.model.GetRequestByTwoId;
import com.example.tregoapp.mechanic.model.map.RouteResponse;
import com.example.tregoapp.mechanic.model.AcceptServiceRequest;
import com.example.tregoapp.mechanic.model.FCMTokenRequest;
import com.example.tregoapp.mechanic.model.GetRequestById;
import com.example.tregoapp.mechanic.model.ServiceRequest;
import com.example.tregoapp.mechanic.model.RegisterShopResponse;
import com.example.tregoapp.mechanic.model.ServiceDetail;
import com.example.tregoapp.mechanic.model.ShopDetail;
import com.example.tregoapp.mechanic.model.StatusUpdate;
import com.example.tregoapp.mechanic.model.auth.Login;
import com.example.tregoapp.mechanic.model.auth.Register;
import com.example.tregoapp.mechanic.model.response.ApiResponse;
import com.example.tregoapp.mechanic.model.response.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Mechanic
    @POST("mechanic/auth/register")
    Call<ApiResponse<User>> mechanicRegister(
            @Body Register request
    );

    @POST("mechanic/auth/login")
    Call<ApiResponse<User>> mechanicLogin(
            @Body Login request
    );

    @POST("mechanic/auth/registerShop")
    Call<ApiResponse<RegisterShopResponse>> registerShop(
            @Body ShopDetail shopDetail
    );

    @POST("mechanic/auth/workerShopRegister")
    Call<ApiResponse<RegisterShopResponse>> workerShopRegister(
            @Body GetRequestByTwoId request
    );

    @POST("mechanic/auth/createService")
    Call<ApiResponse<ServiceDetail>> createService(
            @Body ServiceDetail serviceDetail
    );

    @POST("mechanic/auth/updateStatus")
    Call<ApiResponse<User>> updateStatus(
            @Body StatusUpdate statusUpdate
    );

    @POST("mechanic/auth/getShopServiceRequests")
    Call<ApiResponse<List<ServiceRequest>>> getServiceRequest(
            @Body GetRequestByTwoId request
    );

    @POST("mechanic/auth/getActiveServiceRequests")
    Call<ApiResponse<List<ServiceRequest>>> getActiveServiceRequests(
            @Body GetRequestByTwoId request
    );

    @POST("mechanic/auth/getAcceptedServiceRequest")
    Call<ApiResponse<ServiceRequest>> getAcceptedServiceRequest(
            @Body GetRequestById request
    );

    @POST("mechanic/auth/acceptServiceRequest")
    Call<ApiResponse<ServiceRequest>> acceptServiceRequest(
            @Body AcceptServiceRequest request
    );

    @POST("mechanic/auth/cancelServiceRequest")
    Call<ApiResponse<ServiceRequest>> cancelServiceRequest(
            @Body GetRequestById request
    );

    @POST("mechanic/auth/startServiceRequest")
    Call<ApiResponse<ServiceRequest>> startServiceRequest(
            @Body GetRequestById request
    );

    @POST("mechanic/auth/completeServiceRequest")
    Call<ApiResponse<ServiceRequest>> completeServiceRequest(
            @Body GetRequestById request
    );

    @POST("mechanic/auth/getShopHistoryServiceRequests")
    Call<ApiResponse<List<ServiceRequest>>> getShopHistoryServiceRequests(
            @Body GetRequestById request
    );

    @POST("mechanic/auth/getShopWorkers")
    Call<ApiResponse<List<User>>> getShopWorkers(
            @Body GetRequestById request
    );

    @POST("mechanic/auth/getWorkerHistoryServiceRequests")
    Call<ApiResponse<List<ServiceRequest>>> getWorkerHistoryServiceRequests(
            @Body GetRequestByTwoId request
    );

    @PUT("mechanic/auth/updateMechanicFcmToken")
    Call<ApiResponse<FCMTokenRequest>> updateMechanicFcmToken(
            @Body FCMTokenRequest request
    );

    @POST("mechanic/auth/getCurrentUser")
    Call<ApiResponse<User>> getCurrentUser(
            @Body GetRequestById request
    );

    @POST("mechanic/auth/getCustomerDetails")
    Call<ApiResponse<User>> getCustomerDetails(@Body GetRequestById request);

    @POST("mechanic/auth/getMechanicDetails")
    Call<ApiResponse<User>> getMechanicDetails(@Body GetRequestById request);

    @POST("mechanic/auth/getShopDetails")
    Call<ApiResponse<ShopDetail>> getShopDetails(@Body GetRequestById request);

    @POST("mechanic/auth/getServiceDetails")
    Call<ApiResponse<ServiceDetail>> getServiceDetails(@Body GetRequestById request);

    // For Map
    @GET("route/v1/driving/{start};{end}")
    Call<RouteResponse> getRoute(
            @Path("start") String start,
            @Path("end") String end,
            @Query("overview") String overview,
            @Query("geometries") String geometries
    );
}
