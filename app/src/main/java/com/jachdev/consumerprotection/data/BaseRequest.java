package com.jachdev.consumerprotection.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Asanka on 9/28/2021.
 */
public class BaseRequest {

    private List<MultipartBody.Part> parts = new ArrayList<>();

    public void setImageParts(String key, File file){
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(key, file.getName(), requestFile);
        parts.add(body);
    }

    public void setTextParts(String key, String value){
        MultipartBody.Part body =
                MultipartBody.Part.createFormData(key, value);
        parts.add(body);
    }

    public List<MultipartBody.Part> getMultiParts() {
        return parts;
    }
}
