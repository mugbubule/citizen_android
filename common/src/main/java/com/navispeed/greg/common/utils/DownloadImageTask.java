package com.navispeed.greg.common.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * You have to add "<uses-permission android:name="android.permission.INTERNET" />" to the AndroidManifest.xml file.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private final Runnable onSuccess;
    private ImageView bmImage;
    private int roundCorners = 0;

    public DownloadImageTask(ImageView bmImage) {
        this(bmImage, () -> {}, 0);
    }
    public DownloadImageTask(ImageView bmImage, Runnable onSuccess) {
        this(bmImage, onSuccess, 0);
    }

    public DownloadImageTask(ImageView bmImage, Runnable onSuccess, int roundCorners) {
        this.bmImage = bmImage;
        this.onSuccess = onSuccess;
        this.roundCorners = roundCorners;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if (roundCorners != 0) {
            // the following calculations are for CENTER_CROP scaled images
            int dispW, dispH;
            int viewW = bmImage.getMeasuredWidth();
            int viewH = bmImage.getMeasuredHeight();
            int imgW = result.getWidth();
            int imgH = result.getHeight();
            // get scaling ratio
            double ratioH = (double)imgH / (double)viewH;
            double ratioW = (double)imgW / (double)viewW;
            // find out how image is rescaled in the imageView
            if (ratioH < ratioW) {
                dispH = (int)(imgH / ratioH);
                dispW = (int)(imgW / ratioH);
                // calculate the part of the image that is cut
                double cutRatio = (double)viewW / (double)dispW;
                // remove that part from the original image
                dispH = imgH;
                dispW = (int)((double)imgW * cutRatio);
            } else {
                dispH = (int)(imgH / ratioW);
                dispW = (int)(imgW / ratioW);
                // calculate the part of the image that is cut
                double cutRatio = (double)viewH / (double)dispH;
                // remove that part from the original image
                dispH = (int)((double)imgH * cutRatio);
                dispW = imgW;
            }
            bmImage.setImageBitmap(ImageUtils.getRoundedCornerBitmap(result, dispW, dispH, roundCorners));
        } else {
            bmImage.setImageBitmap(result);
        }
        onSuccess.run();

    }
}
