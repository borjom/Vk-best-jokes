package com.randomname.vkjokes.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.vk.sdk.VKSdk;

public class VkLoginAlert extends DialogFragment {

    public static VkLoginAlert newInstance() {
        VkLoginAlert frag = new VkLoginAlert();
        return frag;
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
        Effectstype effect = Effectstype.Sidefill;
//Fadein,Slideright,Slideleft,Slidetop,SlideBottom,Newspager,Fall,Sidefill,Fliph,Flipv,RotateBottom,RotateLeft,Slit,Shake;
        dialogBuilder
                .withTitle("Вход")
                .withTitleColor("#3E75B6")
                .withDividerColor("#3E75B6")
                .withMessage("Для того что бы ставить лайки, Вам нужно войти!")
                .withMessageColor("#ffffff")
                .withDialogColor("#3E75B6")
                 //.withIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_login, null))
                .withDuration(350)
                .withEffect(effect)
                .withButton1Text("Да")
                .withButton2Text("Нет")
                .show();

        return dialogBuilder
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VKSdk.login(getActivity(), "wall");
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                });
    }
}