package com.example.tregoapp.customer.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.tregoapp.customer.model.GetRequestById;
import com.example.tregoapp.customer.model.Location;
import com.example.tregoapp.customer.model.SOSRequest;
import com.example.tregoapp.customer.model.ServiceDetail;
import com.example.tregoapp.customer.model.ShopDetail;
import com.example.tregoapp.customer.model.VehicleDetail;
import com.example.tregoapp.customer.model.auth.AuthState;
import com.example.tregoapp.customer.api.ApiService;
import com.example.tregoapp.customer.api.RetrofitClient;
import com.example.tregoapp.customer.manager.SessionManager;
//import com.example.tregoapp.customer.model.CreateRequest;
import com.example.tregoapp.customer.model.MechanicShop;
//import com.example.tregoapp.customer.model.NearbyRequest;
import com.example.tregoapp.customer.model.ServiceRequest;
import com.example.tregoapp.customer.model.auth.Login;
import com.example.tregoapp.customer.model.auth.Register;
import com.example.tregoapp.customer.model.map.Route;
import com.example.tregoapp.customer.model.map.RouteResponse;
import com.example.tregoapp.customer.model.response.ApiResponse;
import com.example.tregoapp.customer.model.response.User;
import com.example.tregoapp.customer.network.Resource;
import com.example.tregoapp.customer.model.FCMTokenRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private final SessionManager sessionManager;
    private final ApiService api;
    private final ApiService api2;
    private final Gson gson = new Gson();

    public Repository(Context context) {
        this.sessionManager = new SessionManager(context);
        this.api = RetrofitClient.getInstance().create(ApiService.class);
        this.api2 = RetrofitClient.getOSRMInstance().create(ApiService.class);
    }

    // ================= COMMON ERROR PARSER =================
    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() != null) {

                String errorJson = response.errorBody().string();
                Log.d("API_ERROR_BODY", errorJson);

                // Try parsing JSON
                if (errorJson.startsWith("{")) {
                    com.example.tregoapp.mechanic.model.response.ApiResponse<?> errorResponse =
                            gson.fromJson(errorJson, com.example.tregoapp.mechanic.model.response.ApiResponse.class);

                    if (errorResponse != null && errorResponse.getMessage() != null) {
                        return errorResponse.getMessage();
                    }
                }

                // If not JSON return raw message
                return errorJson;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Something went wrong";
    }

    // ================= LOGIN =================
    public void customerLogin(Login user,
                              MutableLiveData<Resource<User>> authState) {

        authState.postValue(Resource.loading(null));

        api.customerLogin(user).enqueue(new Callback<ApiResponse<User>>() {

            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                   Response<ApiResponse<User>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<User> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        if (body.getData() != null) {
                            sessionManager.saveUser(body.getData());
                            authState.postValue(Resource.success(body.getData()));
                        } else {
                            authState.postValue(Resource.error("Login successful but user data is missing", null));
                        }

                    } else {
                        authState.postValue(Resource.error(body.getMessage(), null));
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(Resource.error(msg, null));
            }
        });
    }

    // ================= REGISTER =================
    public void customerRegister(Register user,
                                 MutableLiveData<Resource<User>> authState) {

        authState.postValue(Resource.loading(null));

        api.customerRegister(user).enqueue(new Callback<ApiResponse<User>>() {

            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                   Response<ApiResponse<User>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<User> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        if (body.getData() != null) {
                            sessionManager.saveUser(body.getData());
                            authState.postValue(Resource.success(body.getData()));
                        } else {
                            authState.postValue(Resource.error("Registration successful but user data is missing", null));
                        }

                    } else {
                        authState.postValue(Resource.error(body.getMessage(), null));
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(Resource.error(msg, null));
            }
        });
    }

    // ================= REGISTER VEHICLE =================
    public void registerVehicle(VehicleDetail vehicleDetail,
                                MutableLiveData<Resource<VehicleDetail>> authState) {
        authState.postValue(Resource.loading(null));
        api.registerVehicle(vehicleDetail).enqueue(new Callback<ApiResponse<VehicleDetail>>() {
            @Override
            public void onResponse(Call<ApiResponse<VehicleDetail>> call, Response<ApiResponse<VehicleDetail>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<VehicleDetail> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        authState.postValue(Resource.success(body.getData()));
                    } else {
                        authState.postValue(Resource.error(body.getMessage(), null));
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<VehicleDetail>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(Resource.error(msg, null));
            }
        });
    }

    // ================= GET NEARBY SHOPS =================
    public void getNearbyShops(Location location,
                               MutableLiveData<Resource<List<ShopDetail>>> nearbyShopsList) {
        nearbyShopsList.postValue(Resource.loading(null));
        api.getNearbyShops(location).enqueue(new Callback<ApiResponse<List<ShopDetail>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ShopDetail>>> call, Response<ApiResponse<List<ShopDetail>>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<List<ShopDetail>> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        if (body.getData() != null) {
                            nearbyShopsList.postValue(Resource.success(body.getData()));
                        } else {
                            nearbyShopsList.postValue(Resource.error("No shops found", null));
                        }
                    } else {
                        nearbyShopsList.postValue(Resource.error(body.getMessage(), null));
                    }

                } else {
                    Log.d("API_SUCCESS", "RESPONSE IS FAILED");
                    String errorMsg = parseError(response);
                    nearbyShopsList.postValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ShopDetail>>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                nearbyShopsList.postValue(Resource.error(msg, null));
            }
        });
    }

    // ================= GET VEHICLE =================
    public void getVehicles(GetRequestById request,
                            MutableLiveData<Resource<List<VehicleDetail>>> vehicleList) {
        vehicleList.postValue(Resource.loading(null));
        api.getVehicles(request).enqueue(new Callback<ApiResponse<List<VehicleDetail>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<VehicleDetail>>> call, Response<ApiResponse<List<VehicleDetail>>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<List<VehicleDetail>> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        vehicleList.postValue(Resource.success(body.getData()));
                    } else {
                        vehicleList.postValue(Resource.error(body.getMessage(), null));
                    }

                } else {
                    String errorMsg = parseError(response);
                    vehicleList.postValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<VehicleDetail>>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                vehicleList.postValue(Resource.error(msg, null));
            }
        });
    }

    // ================= GET SERVICES =================
    public void getShopServices(GetRequestById request,
                            MutableLiveData<Resource<List<ServiceDetail>>> servicesList) {
        servicesList.postValue(Resource.loading(null));
        api.getShopServices(request).enqueue(new Callback<ApiResponse<List<ServiceDetail>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ServiceDetail>>> call, Response<ApiResponse<List<ServiceDetail>>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<List<ServiceDetail>> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        servicesList.postValue(Resource.success(body.getData()));
                    } else {
                        servicesList.postValue(Resource.error(body.getMessage(), null));
                    }

                } else {
                    String errorMsg = parseError(response);
                    servicesList.postValue(Resource.error(errorMsg, null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<ServiceDetail>>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                servicesList.postValue(Resource.error(msg, null));
            }
        });
    }



    // ================= CREATE SERVICE REQUEST =================
    public void createServiceRequest(ServiceRequest request,
                                     MutableLiveData<Resource<ServiceRequest>> serviceRequestLiveData) {

        serviceRequestLiveData.postValue(Resource.loading(null));

        api.createRequest(request).enqueue(new Callback<ApiResponse<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<ServiceRequest>> call, Response<ApiResponse<ServiceRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<ServiceRequest> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        serviceRequestLiveData.postValue(Resource.success(body.getData()));
                    } else {
                        serviceRequestLiveData.postValue(Resource.error(body.getMessage(), null));
                    }

                } else {
                    String errorMsg = parseError(response);
                    serviceRequestLiveData.postValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ServiceRequest>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                serviceRequestLiveData.postValue(Resource.error(msg, null));
            }
        });
    }

    // ================= SEND SOS SERVICE REQUEST =================
    public void sendSOS(SOSRequest request,
                        MutableLiveData<Resource<GetRequestById>> state) {

        state.postValue(Resource.loading(null));

        api.sendSOS(request).enqueue(new Callback<ApiResponse<GetRequestById>>() {
            @Override
            public void onResponse(Call<ApiResponse<GetRequestById>> call, Response<ApiResponse<GetRequestById>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<GetRequestById> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        state.postValue(Resource.success(body.getData()));
                    } else {
                        state.postValue(Resource.error(body.getMessage(), null));
                    }

                } else {
                    String errorMsg = parseError(response);
                    state.postValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<GetRequestById>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                state.postValue(Resource.error(msg, null));
            }
        });
    }

    // ================= GET SERVICE REQUEST BY ID =================
    public void getServiceRequest(GetRequestById request,
                                     MutableLiveData<Resource<ServiceRequest>> serviceRequestLiveData) {

        serviceRequestLiveData.postValue(Resource.loading(null));

        api.getServiceRequest(request).enqueue(new Callback<ApiResponse<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<ServiceRequest>> call, Response<ApiResponse<ServiceRequest>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<ServiceRequest> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        serviceRequestLiveData.postValue(Resource.success(body.getData()));
                    } else {
                        serviceRequestLiveData.postValue(Resource.error(body.getMessage(), null));
                    }

                } else {
                    String errorMsg = parseError(response);
                    serviceRequestLiveData.postValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ServiceRequest>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                serviceRequestLiveData.postValue(Resource.error(msg, null));
            }
        });
    }


    // ================= GET SERVICE REQUEST HISTORY BY ID =================
    public void getServiceRequestHistory(GetRequestById request,
                                  MutableLiveData<Resource<List<ServiceRequest>>> serviceRequestHistoryLiveData) {

        serviceRequestHistoryLiveData.postValue(Resource.loading(null));

        api.getServiceRequestHistory(request).enqueue(new Callback<ApiResponse<List<ServiceRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ServiceRequest>>> call, Response<ApiResponse<List<ServiceRequest>>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<List<ServiceRequest>> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        serviceRequestHistoryLiveData.postValue(Resource.success(body.getData()));
                    } else {
                        serviceRequestHistoryLiveData.postValue(Resource.error(body.getMessage(), null));
                    }

                } else {
                    String errorMsg = parseError(response);
                    serviceRequestHistoryLiveData.postValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ServiceRequest>>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                serviceRequestHistoryLiveData.postValue(Resource.error(msg, null));
            }
        });
    }


    // ================= GET LIVE REQUESTED REQUEST =================
    public void getLiveRequestedRequest(GetRequestById request,
                                     MutableLiveData<Resource<List<ServiceRequest>>> liveRequestLiveData) {

        liveRequestLiveData.postValue(Resource.loading(null));

        api.getLiveRequestedRequest(request).enqueue(new Callback<ApiResponse<List<ServiceRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ServiceRequest>>> call, Response<ApiResponse<List<ServiceRequest>>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<List<ServiceRequest>> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        liveRequestLiveData.postValue(Resource.success(body.getData()));
                    } else {
                        liveRequestLiveData.postValue(Resource.error(body.getMessage(), null));
                    }

                } else {
                    String errorMsg = parseError(response);
                    liveRequestLiveData.postValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ServiceRequest>>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                liveRequestLiveData.postValue(Resource.error(msg, null));
            }
        });
    }

    // ================= CANCEL SERVICE REQUEST =================
    public void cancelRequestedService(GetRequestById request,
                                       MutableLiveData<Resource<Void>> authState) {

        authState.postValue(Resource.loading(null));

        api.cancelRequestedService(request).enqueue(new Callback<ApiResponse<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<ServiceRequest>> call, Response<ApiResponse<ServiceRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<ServiceRequest> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        authState.postValue(Resource.success(null));
                    } else {
                        authState.postValue(Resource.error(body.getMessage(), null));
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ServiceRequest>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(Resource.error(msg, null));
            }
        });
    }


    // ================= CONFIRM SERVICE COMPLETION =================
    public void confirmServiceCompletion(GetRequestById request,
                                       MutableLiveData<Resource<Void>> authState) {

        authState.postValue(Resource.loading(null));

        api.confirmServiceCompletion(request).enqueue(new Callback<ApiResponse<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<ServiceRequest>> call, Response<ApiResponse<ServiceRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<ServiceRequest> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        authState.postValue(Resource.success(null));
                    } else {
                        authState.postValue(Resource.error(body.getMessage(), null));
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ServiceRequest>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(Resource.error(msg, null));
            }
        });
    }


    // ================= GET CURRENT USER =================

//    public void getNearbyMechanics(double lat,
//                                   double lng,
//                                   MutableLiveData<List<MechanicShop>> listLiveData) {
//
//        api.getNearbyMechanics(new NearbyRequest(lat, lng))
//                .enqueue(new Callback<ApiResponse<List<MechanicShop>>>() {
//                    @Override
//                    public void onResponse(Call<ApiResponse<List<MechanicShop>>> call,
//                                           Response<ApiResponse<List<MechanicShop>>> response) {
//
//                        if (response.isSuccessful() && response.body() != null) {
//                            listLiveData.postValue(response.body().getData());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ApiResponse<List<MechanicShop>>> call,
//                                          Throwable t) {
//                    }
//                });
//    }

    // ================= ROUTER OSRM =================
    public void getRoutes(String start, String end,
                          MutableLiveData<Resource<Route>> routeLiveData) {
        Log.d("ENTER ROUTES CALL", "-------------------CALL REQUEST SEND---------------------");
        routeLiveData.postValue(Resource.loading(null));
        api2.getRoute(start, end, "full", "geojson").enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Log.d("ROUTE_API_SUCCESS", response.message());

                    if(response.body().getRoutes() != null &&
                            !response.body().getRoutes().isEmpty()){
                        Route route = response.body().getRoutes().get(0);

                        Log.d("ROUTE_API_SUCCESS", response.body().toString());
                        routeLiveData.postValue(Resource.success(route));
                    } else {
                        routeLiveData.postValue(Resource.error("No routes found", null));
                    }

                } else {
                    String errorMsg = parseError(response);
                    routeLiveData.postValue(Resource.error(errorMsg, null));
                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("ROUTE_API_FAILURE", msg);
                routeLiveData.postValue(Resource.error(msg, null));
            }
        });
    }

    // ================= UPDATE CUSTOMER FCM TOKEN =================
    public void updateCustomerFcmToken(FCMTokenRequest request, Runnable onComplete) {
        api.updateCustomerFcmToken(request).enqueue(new Callback<ApiResponse<FCMTokenRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<FCMTokenRequest>> call, Response<ApiResponse<FCMTokenRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<FCMTokenRequest> body = response.body();

                    if (body.getSuccess()) {
                        Log.d("API_FCM_SUCCESS", body.getMessage());
                    } else {
                        Log.d("API_FCM_FAILURE", body.getMessage());
                    }

                } else {
                    String errorMsg = parseError(response);
                    Log.d("API_FCM_FAILURE", errorMsg);
                }
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onFailure(Call<ApiResponse<FCMTokenRequest>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FCM_FAILURE", msg);
                if (onComplete != null) onComplete.run();
            }
        });
    }

    // ================= DETAILS INTEGRATION =================
    public void getCustomerDetails(String id, MutableLiveData<Resource<User>> liveData) {
        liveData.postValue(Resource.loading(null));
        api.getCustomerDetails(new GetRequestById(id)).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> body = response.body();
                    if (body.getSuccess() && body.getData() != null) {
                        sessionManager.saveUser(body.getData()); // Update local session
                        liveData.postValue(Resource.success(body.getData()));
                    } else {
                        liveData.postValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.postValue(Resource.error(parseError(response), null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                liveData.postValue(Resource.error(t.getMessage(), null));
            }
        });
    }

    public void getMechanicDetails(String id, MutableLiveData<Resource<User>> liveData) {
        liveData.postValue(Resource.loading(null));
        api.getMechanicDetails(new GetRequestById(id)).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(Resource.success(response.body().getData()));
                } else {
                    liveData.postValue(Resource.error(parseError(response), null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                liveData.postValue(Resource.error(t.getMessage(), null));
            }
        });
    }

    public void getShopDetails(String id, MutableLiveData<Resource<ShopDetail>> liveData) {
        liveData.postValue(Resource.loading(null));
        api.getShopDetails(new GetRequestById(id)).enqueue(new Callback<ApiResponse<ShopDetail>>() {
            @Override
            public void onResponse(Call<ApiResponse<ShopDetail>> call, Response<ApiResponse<ShopDetail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(Resource.success(response.body().getData()));
                } else {
                    liveData.postValue(Resource.error(parseError(response), null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<ShopDetail>> call, Throwable t) {
                liveData.postValue(Resource.error(t.getMessage(), null));
            }
        });
    }

    public void getServiceDetails(String id, MutableLiveData<Resource<ServiceDetail>> liveData) {
        liveData.postValue(Resource.loading(null));
        api.getServiceDetails(new GetRequestById(id)).enqueue(new Callback<ApiResponse<ServiceDetail>>() {
            @Override
            public void onResponse(Call<ApiResponse<ServiceDetail>> call, Response<ApiResponse<ServiceDetail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(Resource.success(response.body().getData()));
                } else {
                    liveData.postValue(Resource.error(parseError(response), null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<ServiceDetail>> call, Throwable t) {
                liveData.postValue(Resource.error(t.getMessage(), null));
            }
        });
    }

    // ================= SESSION =================
    public User getSavedUser() {
        return sessionManager.getUser();
    }

    public String getSavedUserId() {
        return sessionManager.getUserId();
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public void clearSession() {
        sessionManager.clearPref();
    }
}
