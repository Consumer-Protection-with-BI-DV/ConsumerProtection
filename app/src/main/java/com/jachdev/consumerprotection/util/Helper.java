package com.jachdev.consumerprotection.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import com.jachdev.commonlibs.utils.DateTimeUtil;
import com.jachdev.consumerprotection.BuildConfig;
import com.pd.chocobar.ChocoBar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.zelory.compressor.Compressor;

/**
 * Created by Asanka on 9/20/2021.
 */
public class Helper {

    public static File compressToFile(Context context, File file) {
        Compressor compressor = new Compressor(context);
        compressor.setQuality(75);
        File compressedFile = null;
        try{
            compressedFile = compressor.compressToFile(file);
        }catch (Exception e){
            e.printStackTrace();
        }

        return compressedFile;
    }

    public static void loadImageByFilePath(ImageView view, String path) {
        if (path == null)
            return;

        File f = new File(path);
        Picasso.get().load(f).into(view);
    }

    public static void loadImageByUrl(ImageView view, String path) {
        if (path == null )
            return;

        File file = new File(path);

        if (file.isFile()) {
            Picasso.get()
                    .load(file)
                    .into(view);
        } else {
            Picasso.get()
                    .load(path)
                    .into(view);
        }

        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public static void call(Context context, String phone) {

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    public static long dateTimeToUnix(String date) {
        if (date == null || date.isEmpty())
            date = DateTimeUtil.convertUnixToCustomFormat(DateTimeUtil.DATE_FORMAT_3);
        try {
            DateFormat dateFormat = new SimpleDateFormat(DateTimeUtil.DATE_FORMAT_3, Locale.getDefault());
            Date dateObject = dateFormat.parse(date);

            return (dateObject.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return System.currentTimeMillis();
        }
    }

    public static String distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if(dist < 1){
            double distkm = milesTokm(dist);

            if(distkm < 1){
                return com.jachdev.commonlibs.utils.Helper.toCurrencyFormat(kmTom(distkm)) + "meters";
            }

            return com.jachdev.commonlibs.utils.Helper.toCurrencyFormat(milesTokm(dist)) + "km";
        } else{
            return com.jachdev.commonlibs.utils.Helper.toCurrencyFormat(dist) + "miles";
        }
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private static double milesTokm(double distanceInMiles) {
        return distanceInMiles * 1.60934;
    }

    private static double kmTom(double distanceInKm) {
        return distanceInKm / 1000.0;
    }
}
