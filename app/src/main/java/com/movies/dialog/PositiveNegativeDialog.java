package com.movies.dialog;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.movies.R;

/**
 * contains 2 button, negative and positive , and if you don't need and just show message you don't need, is make it auto
 */
public class PositiveNegativeDialog extends BaseDialogFragment {

    private static final String TAG_BODY_MESSAGE = "TAG_BODY_MESSAGE";
    private static final String TAG_TEXT_NEGATIVE = "TAG_TEXT_NEGATIVE";
    private static final String TAG_TEXT_POSITIVE = "TAG_TEXT_POSITIVE";
    private static final String TAG_TITLE_TEXT = "TAG_TITLE_TEXT";
    private TextView titleTextView;
    private TextView bodyTextView;
    private Button positiveButton;
    private Button negativeButton;
    private Button exitButton;
    private View.OnClickListener onClickListenerPositive;
    private View.OnClickListener onClickListenerNegative;
    private String negativeText;
    private String positiveText;
    private String titleText;
    private String bodyMessage;

    public static PositiveNegativeDialog createInstance(String titleText, String bodyMessage, String negativeText, String positiveText,
                                                        View.OnClickListener listenerNegative, View.OnClickListener listenerPositive) {
        PositiveNegativeDialog positiveNegativeDialog = new PositiveNegativeDialog();
        positiveNegativeDialog.setCancelable(false);

        positiveNegativeDialog.onClickListenerNegative = listenerNegative;
        positiveNegativeDialog.onClickListenerPositive = listenerPositive;

        Bundle arguments = new Bundle();
        arguments.putString(TAG_TITLE_TEXT, titleText);
        arguments.putString(TAG_BODY_MESSAGE, bodyMessage);
        arguments.putString(TAG_TEXT_NEGATIVE, negativeText);
        arguments.putString(TAG_TEXT_POSITIVE, positiveText);
        positiveNegativeDialog.setArguments(arguments);
        return positiveNegativeDialog;

    }

    @Override
    protected int getResLayout() {
        return R.layout.dialog_positive_negative;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        initVar();
        disableButtons();

        bodyTextView.setText(bodyMessage);
        titleTextView.setText(titleText);


        boolean onlyShowMessage = onClickListenerPositive == null && onClickListenerNegative == null;
        if (onlyShowMessage) {
            exitButton.setVisibility(View.VISIBLE);
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } else {
            turnOnButtonIfExitsListener(negativeButton, negativeText, onClickListenerNegative);
            turnOnButtonIfExitsListener(positiveButton, positiveText, onClickListenerPositive);
        }

    }

    private void initVar() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            titleText = arguments.getString(TAG_TITLE_TEXT);
            bodyMessage = arguments.getString(TAG_BODY_MESSAGE);
            negativeText = arguments.getString(TAG_TEXT_NEGATIVE);
            positiveText = arguments.getString(TAG_TEXT_POSITIVE);
        }

    }

    private void initViews(View parent) {
        positiveButton = parent.findViewById(R.id.positive_button);
        negativeButton = parent.findViewById(R.id.negative_button);
        exitButton = parent.findViewById(R.id.exit_button);
        titleTextView = parent.findViewById(R.id.title_text_view);
        bodyTextView = parent.findViewById(R.id.body_text_view);

    }

    private void disableButtons() {
        positiveButton.setVisibility(View.GONE);
        negativeButton.setVisibility(View.GONE);
        exitButton.setVisibility(View.GONE);
    }


    private void turnOnButtonIfExitsListener(Button button, String text, View.OnClickListener listener) {
        if (listener != null) {
            button.setTag(this);
            button.setVisibility(View.VISIBLE);
            button.setText(text);
            button.setOnClickListener(listener);
        }
    }


}
