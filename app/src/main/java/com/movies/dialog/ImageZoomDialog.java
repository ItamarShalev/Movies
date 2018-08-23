package com.movies.dialog;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.movies.R;
import com.movies.utils.ImageHandler;
import com.movies.view.ZoomView;

/**
 * Image zoom dialog for make zoom to image
 * for use call to ImageZoomDialog.createInstance() and give String imageUrl and titleString
 */
public class ImageZoomDialog extends BaseDialogFragment {

    private static final String TAG_IMAGE_URL = "TAG_IMAGE_URL";
    private static final String TAG_TITLE_STRING = "TAG_TITLE_STRING";
    private String imageUrl;
    private String titleString;
    private ImageHandler imageHandler;
    private ZoomView zoomView;
    private ImageButton exitImageButton;
    private TextView titleTextView;

    public static ImageZoomDialog createInstance(String imageUrl,String titleString) {
        ImageZoomDialog imageZoomDialog = new ImageZoomDialog();
        Bundle args = new Bundle();
        args.putString(TAG_IMAGE_URL, imageUrl);
        args.putString(TAG_TITLE_STRING,titleString);
        imageZoomDialog.setArguments(args);
        return imageZoomDialog;
    }

    @Override
    protected int getResLayout() {
        return R.layout.dialog_image_zoom;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        imageHandler = new ImageHandler(context.getFilesDir().getPath());
    }

    @Override
    public void onViewCreated(@NonNull View parent, @Nullable Bundle savedInstanceState) {
        initVar();
        initViews(parent);
        initListeners();
        titleTextView.setText(titleString);
        imageHandler.loadImageAndSave(imageUrl, zoomView);
    }

    private void initVar() {
        Bundle arguments = getArguments();
        if (arguments != null){
            imageUrl = arguments.getString(TAG_IMAGE_URL);
            titleString = arguments.getString(TAG_TITLE_STRING);
        }

    }

    private void initViews(View parent) {
        zoomView = parent.findViewById(R.id.zoom_image_view);
        exitImageButton = parent.findViewById(R.id.exit_image_button);
        titleTextView = parent.findViewById(R.id.title_text_view);

    }

    private void initListeners() {
        exitImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHandler.finish();
                dismiss();
            }
        });
    }




}
