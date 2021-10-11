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
 * Created by Charitha Ratnayake(charitha.r@eyepax.com) on 9/20/2021.
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
}
