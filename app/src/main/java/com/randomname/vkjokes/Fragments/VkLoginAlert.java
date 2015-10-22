package com.randomname.vkjokes.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.vk.sdk.VKSdk;

public class VkLoginAlert extends DialogFragment {

    public static VkLoginAlert newInstance() {
        VkLoginAlert frag = new VkLoginAlert();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setMessage("Для того что бы ставить лайки, Вам нужно войти в ВК.")
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .setPositiveButton("Войти", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        VKSdk.login(getActivity(), "wall, video");
                    }
                })
                .create();
    }
}
