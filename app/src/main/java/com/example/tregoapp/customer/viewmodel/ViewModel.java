package com.example.tregoapp.customer.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

//import com.example.tregoapp.customer.model.CreateRequest;
import com.example.tregoapp.customer.model.GetRequestById;
import com.example.tregoapp.customer.model.Location;
import com.example.tregoapp.customer.model.SOSRequest;
import com.example.tregoapp.customer.model.ServiceDetail;
import com.example.tregoapp.customer.model.ServiceRequest;
import com.example.tregoapp.customer.model.ShopDetail;
import com.example.tregoapp.customer.model.VehicleDetail;
import com.example.tregoapp.customer.model.auth.AuthState;
import com.example.tregoapp.customer.model.MechanicShop;
import com.example.tregoapp.customer.model.auth.Login;
import com.example.tregoapp.customer.model.auth.Register;
import com.example.tregoapp.customer.model.map.Route;
import com.example.tregoapp.customer.model.response.User;
import com.example.tregoapp.customer.repository.Repository;
import com.example.tregoapp.customer.model.FCMTokenRequest;

import org.jspecify.annotations.NonNull;

import java.util.List;

import com.example.tregoapp.customer.network.Resource;

public class ViewModel extends AndroidViewModel {
    private MutableLiveData<Resource<User>> authResource = new MutableLiveData<>();
    private MutableLiveData<Resource<List<ShopDetail>>> nearbyShopsResource = new MutableLiveData<>();
    private MutableLiveData<Resource<List<VehicleDetail>>> vehicleListResource = new MutableLiveData<>();
    private MutableLiveData<Resource<List<ServiceDetail>>> servicesListResource = new MutableLiveData<>();
    private MutableLiveData<Resource<ServiceRequest>> serviceRequestResource = new MutableLiveData<>();
    private MutableLiveData<Resource<List<ServiceRequest>>> serviceRequestHistory = new MutableLiveData<>();
    private MutableLiveData<Resource<List<ServiceRequest>>> liveRequestResource = new MutableLiveData<>();
    private MutableLiveData<Resource<Route>> routeResource = new MutableLiveData<>();
    private MutableLiveData<Resource<GetRequestById>> sosActionResource = new MutableLiveData<>();
    private MutableLiveData<Resource<Void>> genericActionResource = new MutableLiveData<>();
    private MutableLiveData<Resource<VehicleDetail>> vehicleRegistrationResource = new MutableLiveData<>();
    private MutableLiveData<Resource<User>> customerDetails = new MutableLiveData<>();
    private MutableLiveData<Resource<User>> mechanicDetails = new MutableLiveData<>();
    private MutableLiveData<Resource<ShopDetail>> shopDetails = new MutableLiveData<>();
    private MutableLiveData<Resource<ServiceDetail>> serviceDetails = new MutableLiveData<>();

    private Repository repository;

    public ViewModel(@NonNull Application application) {
        super(application);
        this.repository = new Repository(application.getApplicationContext());
    }

    public MutableLiveData<Resource<User>> getAuthResource() {
        return authResource;
    }

    public MutableLiveData<Resource<List<ShopDetail>>> getNearbyShopsResource() {
        return nearbyShopsResource;
    }

    public MutableLiveData<Resource<List<VehicleDetail>>> getVehicleListResource() {
        return vehicleListResource;
    }

    public MutableLiveData<Resource<List<ServiceDetail>>> getServicesListResource() {
        return servicesListResource;
    }

    public MutableLiveData<Resource<List<ServiceRequest>>> getServiceRequestHistory() {
        return serviceRequestHistory;
    }

    public MutableLiveData<Resource<ServiceRequest>> getServiceRequestResource() {
        return serviceRequestResource;
    }

    public MutableLiveData<Resource<List<ServiceRequest>>> getLiveRequestResource() {
        return liveRequestResource;
    }

    public MutableLiveData<Resource<Route>> getRouteResource() {
        return routeResource;
    }

    public MutableLiveData<Resource<GetRequestById>> getSosActionResource() {
        return sosActionResource;
    }

    public MutableLiveData<Resource<Void>> getGenericActionResource() {
        return genericActionResource;
    }

    public MutableLiveData<Resource<VehicleDetail>> getVehicleRegistrationResource() {
        return vehicleRegistrationResource;
    }

    public MutableLiveData<Resource<User>> getCustomerDetailsLiveData() { return customerDetails; }
    public MutableLiveData<Resource<User>> getMechanicDetailsLiveData() { return mechanicDetails; }
    public MutableLiveData<Resource<ShopDetail>> getShopDetailsLiveData() { return shopDetails; }
    public MutableLiveData<Resource<ServiceDetail>> getServiceDetailsLiveData() { return serviceDetails; }

    public void login(String phoneNumber, String password) {
        repository.customerLogin(new Login(phoneNumber, password), authResource);
    }

    public void register(String name, String phoneNumber, String address, String password, double latitude, double longitude) {
        repository.customerRegister(new Register(name, phoneNumber, address, password, latitude, longitude), authResource);
    }

    public void getNearbyShops(double latitude, double longitude) {
        repository.getNearbyShops(new Location(latitude, longitude, null), nearbyShopsResource);
    }

    public void registerVehicle(String customerId, String vehicleType, String vehicleBrand, String vehicleModel, String registrationNumber) {
        repository.registerVehicle(new VehicleDetail(customerId, vehicleType, vehicleBrand, vehicleModel, registrationNumber), vehicleRegistrationResource);
    }

    public void getVehicles(String customer) {
        repository.getVehicles(new GetRequestById(customer), vehicleListResource);
    }

    public void getShopServices(String shopId) {
        repository.getShopServices(new GetRequestById(shopId), servicesListResource);
    }

    public void getRoute(String start, String end) {
        repository.getRoutes(start, end, routeResource);
    }

    public void createServiceRequest(String customerId, String shopId, String vehicleId, String serviceId, String problemDescription, String address, double lat, double lng, double totalPrice, double totalDistance, double totalDuration) {
        Location location = new Location(lat, lng, address);
        ServiceRequest serviceRequest = new ServiceRequest(customerId, shopId, vehicleId, serviceId, problemDescription, location, totalPrice, totalDistance, totalDuration);
        repository.createServiceRequest(serviceRequest, serviceRequestResource);
    }

    public void sendSOS(String customerId, double latitude, double longitude, String address, List<String> problemTypes) {
        repository.sendSOS(new SOSRequest(customerId, latitude, longitude, address, problemTypes), sosActionResource);
    }

    public void getServiceRequest(String requestId) {
        repository.getServiceRequest(new GetRequestById(requestId), serviceRequestResource);
    }

    public void getServiceRequestHistory(String customerId) {
        repository.getServiceRequestHistory(new GetRequestById(customerId), serviceRequestHistory);
    }

    public void getLiveRequestedRequest(String customerId) {
        repository.getLiveRequestedRequest(new GetRequestById(customerId), liveRequestResource);
    }

    public void cancelRequestedService(String requestId) {
        repository.cancelRequestedService(new GetRequestById(requestId), genericActionResource);
    }

    public void confirmServiceCompletion(String requestId) {
        repository.confirmServiceCompletion(new GetRequestById(requestId), genericActionResource);
    }


    public void fetchCustomerDetails(String id) { repository.getCustomerDetails(id, customerDetails); }
    public void fetchMechanicDetails(String id) { repository.getMechanicDetails(id, mechanicDetails); }
    public void fetchShopDetails(String id) { repository.getShopDetails(id, shopDetails); }
    public void fetchServiceDetails(String id) { repository.getServiceDetails(id, serviceDetails); }

    public void updateCustomerFcmToken(String userId, String token) {
        updateCustomerFcmToken(userId, token, null);
    }

    public void updateCustomerFcmToken(String userId, String token, Runnable onComplete) {
        FCMTokenRequest fcmTokenRequest = new FCMTokenRequest(userId, token);
        repository.updateCustomerFcmToken(fcmTokenRequest, onComplete);
    }

    public String getUserId() {
        return repository.getSavedUserId();
    }

    public void loadSavedUser() {
        authResource.setValue(Resource.success(repository.getSavedUser()));
    }

    public void logout() {
        updateCustomerFcmToken(getUserId(), null, () -> {
            repository.clearSession();
        });
    }

    // Utilities

}
