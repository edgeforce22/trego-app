package com.example.tregoapp.mechanic.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.tregoapp.mechanic.model.CancelRequestById;
import com.example.tregoapp.mechanic.model.GetRequestByTwoId;
import com.example.tregoapp.mechanic.model.map.Route;
import com.example.tregoapp.mechanic.model.map.RouteResponse;
import com.example.tregoapp.mechanic.model.AcceptServiceRequest;
import com.example.tregoapp.mechanic.model.FCMTokenRequest;
import com.example.tregoapp.mechanic.model.GetRequestById;
import com.example.tregoapp.mechanic.model.ServiceRequest;
import com.example.tregoapp.mechanic.api.ApiService;
import com.example.tregoapp.mechanic.api.RetrofitClient;
import com.example.tregoapp.mechanic.manager.SessionManager;
import com.example.tregoapp.mechanic.model.RegisterShopResponse;
import com.example.tregoapp.mechanic.model.ServiceDetail;
import com.example.tregoapp.mechanic.model.ShopDetail;
import com.example.tregoapp.mechanic.model.StatusUpdate;
import com.example.tregoapp.mechanic.model.auth.AuthState;
import com.example.tregoapp.customer.network.Resource;
import com.example.tregoapp.mechanic.model.auth.Login;
import com.example.tregoapp.mechanic.model.auth.Register;
import com.example.tregoapp.mechanic.model.response.ApiResponse;
import com.example.tregoapp.mechanic.model.response.User;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    private final SessionManager mSessionManager;
    private final ApiService api;
    private final ApiService api2;
    private final Gson gson = new Gson();

    public Repository(Context context) {
        mSessionManager = new SessionManager(context);
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
                    ApiResponse<?> errorResponse =
                            gson.fromJson(errorJson, ApiResponse.class);

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
    public void mechanicLogin(Login user,
                              MutableLiveData<AuthState> authState,
                              MutableLiveData<User> currentUser) {

        api.mechanicLogin(user).enqueue(new Callback<ApiResponse<User>>() {

            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                   Response<ApiResponse<User>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<User> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());
                        Log.d("API_SUCCESS_LOGIN", body.getData().toString());

                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );

                        if (body.getData() != null) {
                            currentUser.postValue(body.getData());
                            mSessionManager.saveUser(body.getData());
                            if (body.getData().getShopId() != null && !body.getData().getShopId().isEmpty()) {
                                fetchAndSaveShopDetails(body.getData().getShopId());
                            }
                        }

                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }

    // ================= REGISTER =================
    public void mechanicRegister(Register user,
                                 MutableLiveData<AuthState> authState,
                                 MutableLiveData<User> currentUser) {

        api.mechanicRegister(user).enqueue(new Callback<ApiResponse<User>>() {

            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                   Response<ApiResponse<User>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<User> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );

                        if (body.getData() != null) {
                            currentUser.postValue(body.getData());
                            mSessionManager.saveUser(body.getData());
                            if (body.getData().getShopId() != null && !body.getData().getShopId().isEmpty()) {
                                fetchAndSaveShopDetails(body.getData().getShopId());
                            }
                        }

                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }

    // ================= SHOP REGISTER =================
    public void registerShop(ShopDetail shop,
                             MutableLiveData<ShopDetail> shopDetail,
                             MutableLiveData<User> currentUser,
                             MutableLiveData<AuthState> authState) {

        api.registerShop(shop).enqueue(new Callback<ApiResponse<RegisterShopResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<RegisterShopResponse>> call, Response<ApiResponse<RegisterShopResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<RegisterShopResponse> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        if (body.getData() != null) {
                            currentUser.postValue(body.getData().getMechanic());
                            mSessionManager.saveUser(body.getData().getMechanic());
                            shopDetail.postValue(body.getData().getShop());
                            mSessionManager.saveShopDetails(body.getData().getShop());
                        }
                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );

                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RegisterShopResponse>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }


    // ================= WORKER SHOP REGISTER =================
    public void workerShopRegister(GetRequestByTwoId request,
                                   MutableLiveData<AuthState> authState,
                                   MutableLiveData<User> currentUser) {

        api.workerShopRegister(request).enqueue(new Callback<ApiResponse<RegisterShopResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<RegisterShopResponse>> call, Response<ApiResponse<RegisterShopResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<RegisterShopResponse> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        if (body.getData() != null) {
                            Log.d("API_MECHANIC_DATA", new Gson().toJson(response.body()));
                            currentUser.postValue(body.getData().getMechanic());
                            mSessionManager.saveUser(body.getData().getMechanic());
                            mSessionManager.saveShopDetails(body.getData().getShop());
                        }
                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );

                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RegisterShopResponse>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }


    // ================= CREATE SERVICE =================
    public void createService(ServiceDetail service,
                             MutableLiveData<AuthState> authState) {

        api.createService(service).enqueue(new Callback<ApiResponse<ServiceDetail>>() {
            @Override
            public void onResponse(Call<ApiResponse<ServiceDetail>> call, Response<ApiResponse<ServiceDetail>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<ServiceDetail> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );

                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ServiceDetail>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }

    // ================= UPDATE STATUS =================
    public void statusUpdate(StatusUpdate statusUpdate,
                             MutableLiveData<AuthState> authState,
                             MutableLiveData<User> currentUser) {

        api.updateStatus(statusUpdate).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<User> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );

                        if (body.getData() != null) {
                            currentUser.postValue(body.getData());
                            mSessionManager.updateStatus(body.getData().getStatus());
                        }

                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }


    // ================= GET SERVICE REQUEST BY SHOP ID =================
    public void getShopServiceRequests(GetRequestByTwoId request,
                                  MutableLiveData<AuthState> authState,
                                  MutableLiveData<List<ServiceRequest>> serviceRequestLiveData) {

        api.getServiceRequest(request).enqueue(new Callback<ApiResponse<List<ServiceRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ServiceRequest>>> call, Response<ApiResponse<List<ServiceRequest>>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<List<ServiceRequest>> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        serviceRequestLiveData.postValue(body.getData());
                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );
                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ServiceRequest>>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }

    // ================= GET ACTIVE SERVICE REQUEST BY SHOP ID =================
    public void getActiveServiceRequests(GetRequestByTwoId request,
                                          MutableLiveData<AuthState> authState,
                                          MutableLiveData<List<ServiceRequest>> activeServiceRequestLiveData) {

        api.getActiveServiceRequests(request).enqueue(new Callback<ApiResponse<List<ServiceRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ServiceRequest>>> call, Response<ApiResponse<List<ServiceRequest>>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<List<ServiceRequest>> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        activeServiceRequestLiveData.postValue(body.getData());
                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );
                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ServiceRequest>>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }


    // ================= GET ACCEPTED SERVICE REQUEST BY SERVICE REQUEST ID =================
    public void getAcceptedServiceRequest(GetRequestById request,
                                       MutableLiveData<AuthState> authState,
                                       MutableLiveData<ServiceRequest> acceptedServiceRequestLiveData) {

        api.getAcceptedServiceRequest(request).enqueue(new Callback<ApiResponse<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<ServiceRequest>> call, Response<ApiResponse<ServiceRequest>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<ServiceRequest> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        acceptedServiceRequestLiveData.postValue(body.getData());
                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );
                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ServiceRequest>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }


    // ================= ACCEPT SERVICE REQUEST  =================
    public void acceptServiceRequest(AcceptServiceRequest request,
                                     MutableLiveData<AuthState> authState,
                                     MutableLiveData<ServiceRequest> acceptServiceRequest) {
        api.acceptServiceRequest(request).enqueue(new Callback<ApiResponse<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<ServiceRequest>> call, Response<ApiResponse<ServiceRequest>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<ServiceRequest> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        acceptServiceRequest.postValue(body.getData());
                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );
                    } else {
                        acceptServiceRequest.postValue(null);
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    acceptServiceRequest.postValue(null);
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ServiceRequest>> call, Throwable t) {
                acceptServiceRequest.postValue(null);
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }


    // ================= CANCEL SERVICE REQUEST  =================
    public void cancelServiceRequest(CancelRequestById request,
                                     MutableLiveData<AuthState> authState,
                                     MutableLiveData<List<ServiceRequest>> serviceRequests) {
        api.cancelServiceRequest(request).enqueue(new Callback<ApiResponse<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<ServiceRequest>> call, Response<ApiResponse<ServiceRequest>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<ServiceRequest> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );
                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ServiceRequest>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }


    // ================= START SERVICE REQUEST  =================
    public void startServiceRequest(GetRequestById request,
                                     MutableLiveData<AuthState> authState,
                                     MutableLiveData<List<ServiceRequest>> serviceRequests) {
        api.startServiceRequest(request).enqueue(new Callback<ApiResponse<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<ServiceRequest>> call, Response<ApiResponse<ServiceRequest>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<ServiceRequest> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );
                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ServiceRequest>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }

    // ================= COMPLETE SERVICE REQUEST  =================
    public void completeServiceRequest(GetRequestById request,
                                    MutableLiveData<AuthState> authState,
                                    MutableLiveData<ServiceRequest> acceptedServiceRequest) {
        api.completeServiceRequest(request).enqueue(new Callback<ApiResponse<ServiceRequest>>() {
            @Override
            public void onResponse(Call<ApiResponse<ServiceRequest>> call, Response<ApiResponse<ServiceRequest>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<ServiceRequest> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());
                        acceptedServiceRequest.postValue(body.getData());

                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );
                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ServiceRequest>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }


    // ================= GET SERVICE REQUESTS HISTORY =================
    public void getShopHistoryServiceRequests(GetRequestById request,
                                              MutableLiveData<AuthState> authState,
                                              MutableLiveData<List<ServiceRequest>> shopServiceRequestHistory) {
        api.getShopHistoryServiceRequests(request).enqueue(new Callback<ApiResponse<List<ServiceRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ServiceRequest>>> call, Response<ApiResponse<List<ServiceRequest>>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<List<ServiceRequest>> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        shopServiceRequestHistory.postValue(body.getData());
                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );
                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ServiceRequest>>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }

    // ================= GET SERVICE REQUESTS HISTORY =================
    public void getWorkerHistoryServiceRequests(GetRequestByTwoId request,
                                              MutableLiveData<AuthState> authState,
                                              MutableLiveData<List<ServiceRequest>> shopServiceRequestHistory) {
        api.getWorkerHistoryServiceRequests(request).enqueue(new Callback<ApiResponse<List<ServiceRequest>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ServiceRequest>>> call, Response<ApiResponse<List<ServiceRequest>>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<List<ServiceRequest>> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        shopServiceRequestHistory.postValue(body.getData());
                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );
                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ServiceRequest>>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }

    // ================= GET SERVICE REQUESTS HISTORY =================
    public void getShopWorkers(GetRequestById request,
                              MutableLiveData<AuthState> authState,
                              MutableLiveData<List<User>> workersList) {
        api.getShopWorkers(request).enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<List<User>> body = response.body();

                    if (body.getSuccess()) {

                        Log.d("API_SUCCESS", body.getMessage());

                        workersList.postValue(body.getData());
                        authState.postValue(
                                new AuthState(true, body.getMessage())
                        );
                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }


    // ================= UPDATE SHOP FCM TOKEN =================
    public void updateMechanicFcmToken(FCMTokenRequest request, Runnable onComplete) {
        api.updateMechanicFcmToken(request).enqueue(new Callback<ApiResponse<FCMTokenRequest>>() {
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


        // ================= GET CURRENT USER =================
    public void getCurrentUser(String uid,
                               MutableLiveData<User> currentUser,
                               MutableLiveData<AuthState> authState) {

        api.getCurrentUser(new GetRequestById(uid)).enqueue(new Callback<ApiResponse<User>>() {

            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                   Response<ApiResponse<User>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ApiResponse<User> body = response.body();

                    if (body.getSuccess() && body.getData() != null) {

                        currentUser.postValue(body.getData());
                        mSessionManager.saveUser(body.getData());

                    } else {
                        authState.postValue(
                                new AuthState(false, body.getMessage())
                        );
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                authState.postValue(new AuthState(false, msg));
            }
        });
    }


    public void getRoutes(String start, String end,
                          MutableLiveData<AuthState> authState,
                          MutableLiveData<Route> routeLiveData) {
        Log.d("ENTER ROUTES CALL", "-------------------CALL REQUEST SEND---------------------");
        api2.getRoute(start, end, "full", "geojson").enqueue(new Callback<RouteResponse>() {
            @Override
            public void onResponse(Call<RouteResponse> call, Response<RouteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Log.d("ROUTE_API_SUCCESS", response.message());

                    if(response.body().getRoutes() != null &&
                            !response.body().getRoutes().isEmpty()){
                        Route route = response.body().getRoutes().get(0);

                        Log.d("ROUTE_API_SUCCESS", response.body().toString());
                        routeLiveData.postValue(route);
                        authState.postValue(new AuthState(true, response.message()));
                    }

                } else {
                    String errorMsg = parseError(response);
                    authState.postValue(new AuthState(false, errorMsg));
                }
            }

            @Override
            public void onFailure(Call<RouteResponse> call, Throwable t) {
                String msg = t.getMessage() != null ? t.getMessage() : "Network error";
                Log.d("ROUTE_API_FAILURE", msg);
                authState.postValue(new AuthState(false, msg));
            }
        });
    }

    // ================= DETAILS INTEGRATION =================
    public void getCustomerDetails(String id, MutableLiveData<User> liveData) {
        api.getCustomerDetails(new GetRequestById(id)).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body().getData());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {}
        });
    }

    public void getMechanicDetails(String id, MutableLiveData<User> liveData) {
        api.getMechanicDetails(new GetRequestById(id)).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body().getData());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {}
        });
    }

    public void getShopDetails(String id, MutableLiveData<Resource<ShopDetail>> liveData) {
        liveData.postValue(Resource.loading(null));
        api.getShopDetails(new GetRequestById(id)).enqueue(new Callback<ApiResponse<ShopDetail>>() {
            @Override
            public void onResponse(Call<ApiResponse<ShopDetail>> call, Response<ApiResponse<ShopDetail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ShopDetail> body = response.body();
                    if (body.getSuccess()) {
                        liveData.postValue(Resource.success(body.getData()));
                    } else {
                        liveData.postValue(Resource.error(body.getMessage(), null));
                    }
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
                    ApiResponse<ServiceDetail> body = response.body();
                    if (body.getSuccess()) {
                        Log.d("API_SUCESS", "Got service details" + body.getData().toString());
                        liveData.postValue(Resource.success(body.getData()));
                    } else {
                        Log.d("API_SUCESS", "Error in service details" + body.getData().toString());
                        liveData.postValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    Log.d("API_SUCESS", "Error in service details" + response.body().getData().toString());
                    liveData.postValue(Resource.error(parseError(response), null));
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<ServiceDetail>> call, Throwable t) {
                Log.d("API_SUCESS", "Error in service details" + t.getMessage());
                liveData.postValue(Resource.error(t.getMessage(), null));
            }
        });
    }

    // ================= SESSION =================
    public User getSavedUser() {
        return mSessionManager.getUser();
    }

    public String getSavedUserId() {
        return mSessionManager.getUserId();
    }

    public String getSavedUserShopId() {
        return mSessionManager.getShopId();
    }

    public String getRole() {
        return mSessionManager.getUserRole();
    }
    public boolean isLoggedIn() {
        return mSessionManager.isLoggedIn();
    }

    public void clearSession() {
        mSessionManager.clearPref();
    }

    public ShopDetail getSavedShopDetails() {
        return mSessionManager.getShopDetails();
    }

    public void fetchAndSaveShopDetails(String shopId) {
        api.getShopDetails(new GetRequestById(shopId)).enqueue(new Callback<ApiResponse<ShopDetail>>() {
            @Override
            public void onResponse(Call<ApiResponse<ShopDetail>> call, Response<ApiResponse<ShopDetail>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                    mSessionManager.saveShopDetails(response.body().getData());
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<ShopDetail>> call, Throwable t) {
                Log.e("Repository", "Failed to fetch shop details on login: " + t.getMessage());
            }
        });
    }
}
