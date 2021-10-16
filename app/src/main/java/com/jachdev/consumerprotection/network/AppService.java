package com.jachdev.consumerprotection.network;

import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.AppConstant;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Asanka on 9/20/2021.
 */
public class AppService {

    private static final String BASE_URL = AppConstant.BASE_URL;
    public static volatile AppService service;
    private Retrofit server;

    public AppService() {
        server = provideRetrofit();
    }

    /**
     * this is Singleton class
     * @return : application class
     */
    public static AppService getInstance() {
        //Double check locking pattern
        if (service == null) { //Check for the first time
            synchronized (AppService.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (service == null)
                    service = new AppService();
            }
        }
        return service;
    }

    private Retrofit provideRetrofit(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(HttpLoggingInterceptor.Level.BODY);

        Interceptor queryInterceptor1 = new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder().build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        };
        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(queryInterceptor1)
                .build();


        return new Retrofit.Builder().baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

    }

    private ApiInterface provideRetrofitService(Retrofit retrofit){
        return retrofit.create(ApiInterface.class);
    }

    public ApiInterface getServer() {
        return provideRetrofitService(server);
    }
}
