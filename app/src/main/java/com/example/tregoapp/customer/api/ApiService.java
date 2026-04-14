package com.example.tregoapp.customer.api;

//import com.example.tregoapp.customer.model.CreateRequest;
import com.example.tregoapp.customer.model.GetRequestById;
import com.example.tregoapp.customer.model.Location;
import com.example.tregoapp.customer.model.MechanicShop;
//import com.example.tregoapp.customer.model.NearbyRequest;
import com.example.tregoapp.customer.model.SOSRequest;
import com.example.tregoapp.customer.model.ServiceDetail;
import com.example.tregoapp.customer.model.ServiceRequest;
import com.example.tregoapp.customer.model.ShopDetail;
import com.example.tregoapp.customer.model.VehicleDetail;
import com.example.tregoapp.customer.model.auth.Login;
import com.example.tregoapp.customer.model.auth.Register;
import com.example.tregoapp.customer.model.map.RouteResponse;
import com.example.tregoapp.customer.model.response.ApiResponse;
import com.example.tregoapp.customer.model.response.User;
import com.example.tregoapp.customer.model.FCMTokenRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Customer
    @POST("customer/auth/register")
    Call<ApiResponse<User>> customerRegister(
            @Body Register request
    );

    @POST("customer/auth/login")
    Call<ApiResponse<User>> customerLogin(
            @Body Login request
    );

    @POST("customer/auth/nearbyShops")
    Call<ApiResponse<List<ShopDetail>>> getNearbyShops(
            @Body Location location
    );

    @POST("customer/auth/createVehicle")
    Call<ApiResponse<VehicleDetail>> registerVehicle(
            @Body VehicleDetail vehicleDetail
    );

    @POST("customer/auth/getVehicles")
    Call<ApiResponse<List<VehicleDetail>>> getVehicles(
            @Body GetRequestById request
    );

    @POST("customer/auth/getShopServices")
    Call<ApiResponse<List<ServiceDetail>>> getShopServices(
            @Body GetRequestById request
    );

    @POST("customer/getCurrentUser")
    Call<ApiResponse<User>> getCurrentUser(
            @Body GetRequestById request
    );

    @POST("customer/auth/createRequest")
    Call<ApiResponse<ServiceRequest>> createRequest(
            @Body ServiceRequest request
    );

    @POST("customer/auth/sendSOS")
    Call<ApiResponse> sendSOS(@Body SOSRequest request);

    @POST("customer/auth/getServiceRequest")
    Call<ApiResponse<ServiceRequest>> getServiceRequest(
            @Body GetRequestById request
    );

    @POST("customer/auth/cancelRequestedService")
    Call<ApiResponse<ServiceRequest>> cancelRequestedService(
            @Body GetRequestById request
    );

    @POST("customer/auth/confirmServiceCompletion")
    Call<ApiResponse<ServiceRequest>> confirmServiceCompletion(
            @Body GetRequestById request
    );

    @POST("customer/auth/getLiveRequestedRequest")
    Call<ApiResponse<List<ServiceRequest>>> getLiveRequestedRequest(
            @Body GetRequestById request
    );

    @POST("customer/auth/getCustomerDetails")
    Call<ApiResponse<User>> getCustomerDetails(@Body GetRequestById request);

    @POST("customer/auth/getMechanicDetails")
    Call<ApiResponse<User>> getMechanicDetails(@Body GetRequestById request);

    @POST("customer/auth/getShopDetails")
    Call<ApiResponse<ShopDetail>> getShopDetails(@Body GetRequestById request);

    @POST("customer/auth/getServiceDetails")
    Call<ApiResponse<ServiceDetail>> getServiceDetails(@Body GetRequestById request);

    @PUT("customer/auth/updateCustomerFcmToken")
    Call<ApiResponse<FCMTokenRequest>> updateCustomerFcmToken(
            @Body FCMTokenRequest request
    );

    // For Map
    @GET("route/v1/driving/{start};{end}")
    Call<RouteResponse> getRoute(
            @Path("start") String start,
            @Path("end") String end,
            @Query("overview") String overview,
            @Query("geometries") String geometries
    );


//    @POST("mechanic/nearby-mechanics")
//    Call<ApiResponse<List<MechanicShop>>> getNearbyMechanics(
//            @Body NearbyRequest request
//    );
}
