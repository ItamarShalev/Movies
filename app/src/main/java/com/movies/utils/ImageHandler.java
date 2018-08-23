package com.movies.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Handle in image from url or storage, just call to ImageHandler#loadImageAndSave(String, ImageView, ProgressBar, int);
 * String url , for the first time is download from server and insert to image view and save to storage after this time if you call again  with this url
 * is just read for storage more faster,
 * progressbar is can be null if you dont have, just change visibale to gone where the bitmap is ready insert to image view
 * int idPosition for list view and other when you scroll fast and you dont want to insert image from other position
 * just give the right position and insert in real time in holder insert the position to image view with tag
 * <p>
 * <p>
 * this class can five you save to storage or read, and just download from imageUrl to bitmap and insert to image view(must call from background thread)
 */
public class ImageHandler {

    private Handler handlerMainThread;
    private Handler handlerBackgroundThread;
    private String parentPath;
    private HandlerThread handlerThread;

    public ImageHandler(String parentPath) {
        this.parentPath = parentPath;
        handlerMainThread = new Handler(Looper.getMainLooper());
        handlerThread = new HandlerThread(ImageHandler.class.getSimpleName());
        handlerThread.start();
        handlerBackgroundThread = new Handler(handlerThread.getLooper());
        createDirectory();
    }


    private void createDirectory() {
        File fileParentFromPath = getFileParentFromPath();
        if (!fileParentFromPath.exists()) {
            fileParentFromPath.mkdirs();
        }
    }

    private File getFileFromName(String fileName) {
        return new File(getFileParentFromPath(), fileName + ".png");
    }

    private File getFileParentFromPath() {
        String filePath = parentPath + "/Images";
        return new File(filePath);
    }


    private Bitmap loadImageFromUrl(String imageUrl, ImageView imageView, @Nullable ProgressBar progressBar) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            handlerMainThread.post(new InsertBitmapRunnable(imageView, bitmap, progressBar, imageUrl));
            return bitmap;
        } catch (IOException e) {
            return null;
        }

    }

    private void saveImageToStorage(String fileName, Bitmap bitmap) {
        try {
            File file = getFileFromName(fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ignore) {
        }
    }

    public Bitmap readImageFromStorage(String fileName) {
        try {
            File imageFile = getFileFromName(fileName);
            if (!imageFile.exists()) {
                return null;
            }
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            return BitmapFactory.decodeStream(fileInputStream);
        } catch (IOException ignore) {
            return null;
        }
    }

    private boolean isImageExists(String fileName) {
        File imageFile = getFileFromName(fileName);
        return imageFile.exists();
    }


    public void loadImageAndSave(String imageUrl, ImageView imageView, @Nullable ProgressBar progressBar) {
        imageView.setTag(imageUrl);
        handlerBackgroundThread.post(new HandleImageRunnable(imageUrl, imageView, progressBar, imageUrl));
    }


    public void loadImageAndSave(String imageUrl, ImageView imageView) {
        loadImageAndSave(imageUrl, imageView, null);
    }

    public String getNameFromUrl(String url) {
        return url.toLowerCase().replaceAll("[ /:-]", "");
    }

    public void finish() {
        handlerThread.quitSafely();
        handlerMainThread = null;
        handlerBackgroundThread = null;
    }

    private class InsertBitmapRunnable implements Runnable {

        private WeakReference<ImageView> referenceImageView;
        private WeakReference<ProgressBar> referenceProgressBar;
        private Bitmap bitmap;
        private Object tag;

        private InsertBitmapRunnable(ImageView imageView, Bitmap bitmap, ProgressBar progressBar, Object tag) {
            this.referenceImageView = new WeakReference<>(imageView);
            this.referenceProgressBar = new WeakReference<>(progressBar);
            this.bitmap = bitmap;
            this.tag = tag;
        }


        @Override
        public void run() {

            if (referenceImageView.get() != null && bitmap != null && !bitmap.isRecycled()) {
                if (tag == null || tag.equals(referenceImageView.get().getTag())) {
                    referenceImageView.get().setImageBitmap(bitmap);
                }
            }
            if (referenceProgressBar.get() != null) {
                referenceProgressBar.get().setVisibility(View.GONE);
            }
            bitmap = null;
            referenceImageView.clear();
            referenceProgressBar.clear();
        }
    }

    private class HandleImageRunnable implements Runnable {


        private String imageUrl;
        private WeakReference<ImageView> referenceImageView;
        private WeakReference<ProgressBar> referenceProgressBar;
        private Object tag;

        private HandleImageRunnable(String imageUrl, ImageView imageView, ProgressBar progressBar, Object tag) {
            this.imageUrl = imageUrl;
            referenceImageView = new WeakReference<>(imageView);
            referenceProgressBar = new WeakReference<>(progressBar);
            this.tag = tag;
        }


        @Override
        public void run() {
            String fileName = getNameFromUrl(imageUrl);
            if (isImageExists(fileName)) {
                Bitmap bitmap = readImageFromStorage(fileName);
                handlerMainThread.post(new InsertBitmapRunnable(referenceImageView.get(), bitmap, referenceProgressBar.get(), tag));
            } else {
                Bitmap bitmap = loadImageFromUrl(imageUrl, referenceImageView.get(), referenceProgressBar.get());
                saveImageToStorage(fileName, bitmap);
            }
        }
    }

}
