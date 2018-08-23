package com.movies.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.movies.R;

/**
 * Base of all dialogs, make tha animation and style when need for
 */
public abstract class BaseDialogFragment extends DialogFragment {

    public static <T extends BaseDialogFragment> T findDialogFragment(FragmentManager fragmentManager, String tag) {
        return (T) fragmentManager.findFragmentByTag(tag);
    }

    public static <T extends BaseDialogFragment> T findDialogFragment(FragmentManager fragmentManager) {
        return findDialogFragment(fragmentManager, getTagDialogFragment());
    }

    public static String getTagDialogFragment() {
        return "DialogFragment";
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (attributes != null) {
                attributes.windowAnimations = R.style.SimpleStyleDialogShow;
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_dialog);
            }
            window.setBackgroundDrawableResource(R.drawable.round_dialog);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getResLayout(), container, false);
    }

    @LayoutRes
    protected abstract int getResLayout();

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.findFragmentByTag(tag) == null) {
            super.show(manager, tag);
        }
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, getTagDialogFragment());
    }
}
