package com.alberoneramos.workout.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class UrlBuilder {

    public Uri buildWorkoutUrl(String workoutPlanId){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").authority("workout-a4d9c.firebaseio.com")
                .appendQueryParameter("USER_ID",FirebaseAuth.getInstance().getCurrentUser().getUid())
                .appendQueryParameter("WORKOUT_ID",workoutPlanId);
        return createDynamicUri(builder.build());
    }

    private Uri createDynamicUri(Uri uri){
        return FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(uri)
                .setDynamicLinkDomain("v8z88.app.goo.gl")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink().getUri();
    }

    public static Bitmap generateQRCode(String uri,int dimension){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Bitmap bitmap = null;
        Canvas canvas = null;
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(uri, BarcodeFormat.QR_CODE,dimension,dimension);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
