package com.movies.dialog;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.movies.R;
import com.movies.utils.Global;
import com.movies.utils.ImageHandler;

/**
 * call to ScannerDialog.createInstance() and insert barcode
 * this dialog can show the body and make what action you want, send email,sms,  url, image and other
 */
public class ScannerDialog extends BaseDialogFragment {

    private static final String TAG_BARCODE = "TAG_BARCODE";
    private TextView titleTextView;

    private TextView bodyTextView;

    private Button actionButton;
    private ImageButton exitImageButton;

    private Barcode barcode;
    private String displayValue;
    private ImageHandler imageHandler;


    public static ScannerDialog createInstance(Barcode barcode) {
        ScannerDialog scannerDialog = new ScannerDialog();
        Bundle arguments = new Bundle();
        arguments.putParcelable(TAG_BARCODE, barcode);
        scannerDialog.setArguments(arguments);
        return scannerDialog;
    }

    @Override
    protected int getResLayout() {
        return R.layout.dialog_scanner;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View parent, @Nullable Bundle savedInstanceState) {
        initViews(parent);
        disableAllBodyView();
        initListeners();
        initVar();

        titleTextView.setText("Barcode");
        displayValue = barcode.displayValue;

        if (barcode.valueFormat == Barcode.TEXT) {
            setUiToTextMode();

        } else if (displayValue.startsWith("http")) {
            setUiToHttpMode();

        } else if (barcode.email != null) {
            setUiToEmailMode();

        } else if (barcode.sms != null) {
            setUiToSmsMode();

        } else {
            actionButton.setVisibility(View.GONE);
        }

    }


    private void setUiToHttpMode() {
        actionButton.setText(displayValue);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(displayValue));
                startActivity(i);
                dismiss();

            }
        });
    }

    private void setUiToSmsMode() {
        actionButton.setText(("Send Sms to : " + barcode.sms.phoneNumber));
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:" + barcode.sms.phoneNumber);
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                it.putExtra("sms_body", barcode.sms.message);
                startActivity(it);
                dismiss();

            }
        });
    }

    private void setUiToEmailMode() {
        actionButton.setText(displayValue);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, barcode.email.address);
                intent.putExtra(Intent.EXTRA_SUBJECT, barcode.email.subject);
                intent.putExtra(Intent.EXTRA_TEXT, barcode.email.body);
                startActivity(Intent.createChooser(intent, "Send Email"));
                dismiss();

            }
        });
    }

    private void setUiToTextMode() {
        bodyTextView.setVisibility(View.VISIBLE);
        bodyTextView.setText((getString(R.string.barcode_say) + "\n\n" + displayValue));
        actionButton.setText(getString(R.string.copy_text));
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.copyText(v.getContext(), displayValue);
                dismiss();
            }
        });
    }

    private void initVar() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            barcode = arguments.getParcelable(TAG_BARCODE);
        }
    }

    private void initListeners() {
        exitImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }


    private void initViews(View parent) {
        titleTextView = parent.findViewById(R.id.title_text_view);
        exitImageButton = parent.findViewById(R.id.exit_image_button);
        bodyTextView = parent.findViewById(R.id.body_text_view);
        actionButton = parent.findViewById(R.id.action_button);
    }

    private void disableAllBodyView() {
        bodyTextView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (imageHandler != null) {
            imageHandler.finish();
        }
        imageHandler = null;
    }
}
