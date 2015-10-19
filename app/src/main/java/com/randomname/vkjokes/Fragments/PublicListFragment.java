package com.randomname.vkjokes.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.randomname.vkjokes.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PublicListFragment extends Fragment {
    @Bind(R.id.button)
    Button button;

    PublicListFragmentCallback publicListFragmentCallback;

    public PublicListFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a;

        if (context instanceof Activity){
            a = (Activity) context;

            try {
                publicListFragmentCallback = (PublicListFragmentCallback) a;
            } catch (ClassCastException e) {
                throw new ClassCastException(a.toString() + " must implement MainFragmentCallbacks");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.public_list_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick (R.id.button)
    public void buttonClick() {
        publicListFragmentCallback.onButtonClick();
    }

    public interface PublicListFragmentCallback {
        public void onButtonClick();
    }
}
