package com.randomname.vkjokes.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.vk.sdk.VKSdk;

public class WallPostChooser extends DialogFragment {

    private static final String ITEMS_KEY = "items_key";

    private String[] items;
    private DialogInterface.OnClickListener mClickListener;

    public static WallPostChooser newInstance(String[] items) {
        WallPostChooser frag = new WallPostChooser();
        Bundle bundle = new Bundle();
        bundle.putStringArray(ITEMS_KEY, items);
        frag.setArguments(bundle);
        return frag;
    }

    public void setmClickListener(DialogInterface.OnClickListener click) {
        mClickListener = click;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items = getArguments().getStringArray(ITEMS_KEY);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setItems(items, mClickListener)
                .create();
    }
}
