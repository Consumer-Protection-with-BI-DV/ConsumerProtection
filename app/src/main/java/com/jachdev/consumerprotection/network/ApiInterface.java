package com.jachdev.consumerprotection.network;

import com.jachdev.consumerprotection.AppConstant;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.PredictionRequest;
import com.jachdev.consumerprotection.data.Report;
import com.jachdev.consumerprotection.data.Shop;
import com.jachdev.consumerprotection.data.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Charitha Ratnayake(charitha.r@eyepax.com) on 9/20/2021.
 */
public interface ApiInterface {

    @POST(AppConstant.PATH_SIGN_UP)
    Single<AppResponse> signUp(@Body User user);

    @POST(AppConstant.PATH_LOGIN)
    Single<AppResponse> login(@Body User user);

    @GET(AppConstant.PATH_GET_CATEGORIES)
    Single<AppResponse> getCategories();

    @Multipart
    @POST(AppConstant.PATH_ADD_ORGANIZATION)
    Single<AppResponse> addOrganization(@Part List<MultipartBody.Part> file);

    @GET(AppConstant.PATH_GET_ORGANIZATION)
    Single<AppResponse> getOrganization(@Query("user_id") long user_id);

    @POST(AppConstant.PATH_ADD_SHOP)
    Single<AppResponse> addShop(@Body Shop shop);

    @GET(AppConstant.PATH_GET_SHOP)
    Single<AppResponse> getShops(@Query("organization_id") long organization_id);

    @POST(AppConstant.PATH_GET_ESSENTIALS)
    Single<AppResponse> getEssentials(@Body PredictionRequest request);

    @POST(AppConstant.PATH_COMPLAINTS)
    Single<AppResponse> complaints(@Body Report report);

    @GET(AppConstant.PATH_GET_ALL_SHOPS)
    Single<AppResponse> getAllShops();
}
