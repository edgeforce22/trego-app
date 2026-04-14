package com.example.tregoapp.mechanic.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.tregoapp.mechanic.model.GetRequestByTwoId;
import com.example.tregoapp.mechanic.model.map.Route;
import com.example.tregoapp.mechanic.model.Location;
import com.example.tregoapp.mechanic.model.AcceptServiceRequest;
import com.example.tregoapp.mechanic.model.FCMTokenRequest;
import com.example.tregoapp.mechanic.model.GetRequestById;
import com.example.tregoapp.mechanic.model.ServiceDetail;
import com.example.tregoapp.mechanic.model.ServiceRequest;
import com.example.tregoapp.mechanic.model.ShopDetail;
import com.example.tregoapp.mechanic.model.StatusUpdate;
import com.example.tregoapp.mechanic.model.auth.AuthState;
import com.example.tregoapp.mechanic.model.auth.Login;
import com.example.tregoapp.mechanic.model.auth.Register;
import com.example.tregoapp.mechanic.model.response.User;
import com.example.tregoapp.mechanic.repository.Repository;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import kotlinx.coroutines.Job;

public class ViewModel extends AndroidViewModel {
    private MutableLiveData<AuthState> authState = new MutableLiveData<>();
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<ShopDetail> shopDetail = new MutableLiveData<>();
    private MutableLiveData<List<ServiceRequest>> shopServiceRequestsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ServiceRequest>> activeServiceRequestsLiveData = new MutableLiveData<>();
    private MutableLiveData<ServiceRequest> acceptedServiceRequestLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ServiceRequest>> shopServiceRequestHistoryLiveData = new MutableLiveData<>();
    private MutableLiveData<List<User>> workersListLiveData = new MutableLiveData<>();
    private MutableLiveData<ServiceRequest> acceptServiceRequestLiveData = new MutableLiveData<>();
    private MutableLiveData<Route> routeLiveData = new MutableLiveData<>();
    private MutableLiveData<User> customerDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<User> mechanicDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<ShopDetail> shopDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<ServiceDetail> serviceDetailsLiveData = new MutableLiveData<>();

    private Job pollingJob;

    private Repository repository;
    private Register RegisterUser;
    private Login LoginUser;

    public ViewModel(@NonNull Application application) {
        super(application);
        this.repository = new Repository(application.getApplicationContext());
    }

    public MutableLiveData<AuthState> getAuthState() {
        return authState;
    }

    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }
    public MutableLiveData<ShopDetail> getShopDetail() {
        return shopDetail;
    }
    public MutableLiveData<List<ServiceRequest>> getShopServiceRequestsLiveData() {
        return shopServiceRequestsLiveData;
    }
    public MutableLiveData<List<ServiceRequest>> getActiveServiceRequestsLiveData() {
        return activeServiceRequestsLiveData;
    }
    public MutableLiveData<ServiceRequest> getAcceptedServiceRequestLiveData() {
        return acceptedServiceRequestLiveData;
    }
    public MutableLiveData<Route> getRouteLiveData() {
        return routeLiveData;
    }
    public MutableLiveData<User> getCustomerDetailsLiveData() { return customerDetailsLiveData; }
    public MutableLiveData<User> getMechanicDetailsLiveData() { return mechanicDetailsLiveData; }
    public MutableLiveData<ShopDetail> getShopDetailsLiveData() { return shopDetailsLiveData; }
    public MutableLiveData<ServiceDetail> getServiceDetailsLiveData() { return serviceDetailsLiveData; }

    public MutableLiveData<List<ServiceRequest>> getShopServiceRequestHistoryLiveData() {
        return shopServiceRequestHistoryLiveData;
    }

    public MutableLiveData<ServiceRequest> getAcceptServiceRequestLiveData() {
        return acceptServiceRequestLiveData;
    }

    public MutableLiveData<List<User>> getWorkersListLiveData() {
        return workersListLiveData;
    }

    public void login(String phoneNumber, String password) {
        LoginUser = new Login(phoneNumber, password);
        repository.mechanicLogin(LoginUser, authState, currentUser);
    }

    public void register(String name, String phoneNumber, String role, String address, String password, double latitude, double longitude) {
        RegisterUser = new Register(name, phoneNumber, role, address, password, latitude, longitude);
        repository.mechanicRegister(RegisterUser, authState, currentUser);
    }

    public void registerShop(String ownerId, String shopName, String shopContactNumber, String address, double latitude, double longitude, String shopOpeningTime, String shopClosingTime) {
        ShopDetail shop = new ShopDetail(ownerId, shopName, shopContactNumber, address, latitude, longitude, shopOpeningTime, shopClosingTime);
        repository.registerShop(shop, shopDetail, currentUser, authState);
    }

    public void createService(String shopId, String service, String serviceDescription, double servicePrice) {
        ServiceDetail serviceDetail = new ServiceDetail(shopId, service, serviceDescription, servicePrice);
        repository.createService(serviceDetail, authState);
    }

    public void updateStatus(String mechanicId, String status) {
        StatusUpdate statusUpdate = new StatusUpdate(mechanicId, status);
        repository.statusUpdate(statusUpdate, authState, currentUser);
    }

    public void getShopServiceRequests(String mechanicId, String shopId) {
        GetRequestByTwoId request = new GetRequestByTwoId(mechanicId, shopId);
        repository.getShopServiceRequests(request, authState, shopServiceRequestsLiveData);
    }

    public void getActiveServiceRequests(String mechanicId, String shopId) {
        GetRequestByTwoId request = new GetRequestByTwoId(mechanicId, shopId);
        repository.getActiveServiceRequests(request, authState, activeServiceRequestsLiveData);
    }

    public void getAcceptedServiceRequest(String shopId) {
        GetRequestById request = new GetRequestById(shopId);
        repository.getAcceptedServiceRequest(request, authState, acceptedServiceRequestLiveData);
    }

    public void acceptServiceRequest(String requestId, String mechanicId, double latitude, double longitude, String address) {
        Location location = new Location(latitude, longitude, address);
        AcceptServiceRequest request = new AcceptServiceRequest(requestId, mechanicId, location);
        repository.acceptServiceRequest(request, authState, acceptServiceRequestLiveData);
    }

    public void cancelServiceRequest(String requestId) {
        GetRequestById request = new GetRequestById(requestId);
        repository.cancelServiceRequest(request, authState, shopServiceRequestsLiveData);
    }

    public void startServiceRequest(String requestId) {
        GetRequestById request = new GetRequestById(requestId);
        repository.startServiceRequest(request, authState, shopServiceRequestsLiveData);
    }

    public void completeServiceRequest(String requestId) {
        GetRequestById request = new GetRequestById(requestId);
        repository.completeServiceRequest(request, authState, acceptServiceRequestLiveData);
    }

    public void updateMechanicFcmToken(String userId, String token) {
        updateMechanicFcmToken(userId, token, null);
    }

    public void updateMechanicFcmToken(String userId, String token, Runnable onComplete) {
        FCMTokenRequest fcmTokenRequest = new FCMTokenRequest(userId, token);
        repository.updateMechanicFcmToken(fcmTokenRequest, onComplete);
    }

    public void getRoute(String start, String end) {
        repository.getRoutes(start, end, authState, routeLiveData);
    }

    public void getShopHistoryServiceRequests(String shopId) {
        GetRequestById request = new GetRequestById(shopId);
        repository.getShopHistoryServiceRequests(request, authState, shopServiceRequestHistoryLiveData);
    }

    public void getShopWorkers(String shopId) {
        GetRequestById request = new GetRequestById(shopId);
        repository.getShopWorkers(request, authState, workersListLiveData);
    }

    // Workers
    public void workerShopRegister(String  workerId, String shopId) {
        GetRequestByTwoId request = new GetRequestByTwoId(workerId, shopId);
        repository.workerShopRegister(request, authState, currentUser);
    }

    public void getWorkerHistoryServiceRequests(String mechanicId, String shopId) {
        GetRequestByTwoId request = new GetRequestByTwoId(mechanicId, shopId);
        repository.getWorkerHistoryServiceRequests(request, authState, shopServiceRequestHistoryLiveData);
    }

    public void fetchCustomerDetails(String id) { repository.getCustomerDetails(id, customerDetailsLiveData); }
    public void fetchMechanicDetails(String id) { repository.getMechanicDetails(id, mechanicDetailsLiveData); }
    public void fetchShopDetails(String id) { repository.getShopDetails(id, shopDetailsLiveData); }
    public void fetchServiceDetails(String id) { repository.getServiceDetails(id, serviceDetailsLiveData); }

    public String getUserId() {
        return repository.getSavedUserId();
    }
    public String getShopId() {
        return repository.getSavedUserShopId();
    }
    public String getRole() {
        return repository.getRole();
    }
    public void loadSavedUser() {
        currentUser.setValue(repository.getSavedUser());
    }

    public void logout() {
        updateMechanicFcmToken(getUserId(), null, () -> {
            repository.clearSession();
        });
    }


    // Socket Implementation
    public void removeRequest(String requestId) {

        List<ServiceRequest> currentList = shopServiceRequestsLiveData.getValue();
        if (currentList == null) return;

        List<ServiceRequest> updatedList = new ArrayList<>(currentList);

        updatedList.removeIf(item -> item.getId().equals(requestId));

        shopServiceRequestsLiveData.setValue(updatedList);
    }

    public void addRequest(ServiceRequest newRequest) {

        List<ServiceRequest> current = shopServiceRequestsLiveData.getValue();
        List<ServiceRequest> updated = current != null ? new ArrayList<>(current) : new ArrayList<>();

        // 🔥 prevent duplicates by ID
        for (int i = 0; i < updated.size(); i++) {
            if (updated.get(i).getId().equals(newRequest.getId())) {
                // If it exists, update it instead of adding (optional, but safer)
                updated.set(i, newRequest);
                shopServiceRequestsLiveData.setValue(updated);
                return;
            }
        }

        updated.add(0, newRequest); // add on top
        shopServiceRequestsLiveData.setValue(updated);
    }

    public void clearAcceptServiceRequest() {
        acceptServiceRequestLiveData.setValue(null);
    }

}
