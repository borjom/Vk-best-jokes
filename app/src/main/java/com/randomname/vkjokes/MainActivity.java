package com.randomname.vkjokes;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.randomname.vkjokes.Fragments.FullscreenPhotoFragment;
import com.randomname.vkjokes.Fragments.PublicListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements PublicListFragment.PublicListFragmentCallback {

    private final static String FULLSCREEN_FRAGMENT_TAG = "full_screen_tag";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private MaterialMenuDrawable materialMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();

        FragmentManager fm = getSupportFragmentManager();

        if (fm.getFragments() == null) {
            Fragment fragment = fm.findFragmentByTag("PublicListFragment");
            if (fragment == null) {
                FragmentTransaction ft = fm.beginTransaction();
                fragment = new PublicListFragment();
                ft.replace(R.id.main_frame, fragment, "PublicListFragment");
                ft.commit();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        closeFullscreen(false);
        super.onBackPressed();
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFullscreen(true);
            }
        });

        toolbar.setNavigationIcon(materialMenu);
    }

    private void closeFullscreen(boolean toClose) {
        Fragment frag = getSupportFragmentManager().findFragmentByTag(FULLSCREEN_FRAGMENT_TAG);
        if (frag != null) {
            animateToolbar(
                    Color.parseColor("#000000"),
                    Color.parseColor("#2196F3"),
                    MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                    1
            );

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.stay_still, R.anim.slide_out_right);
            ft.hide(frag);
            ft.commit();

            if (toClose) {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    private void animateToolbar(int colorFrom, int colorTo, final MaterialMenuDrawable.AnimationState state, final int offset) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                toolbar.setBackgroundColor((Integer) animator.getAnimatedValue());
                materialMenu.setTransformationOffset(
                        state,
                        animator.getAnimatedFraction() + offset
                );
            }
        });

        colorAnimation.setDuration(350);
        colorAnimation.setStartDelay(0);
        colorAnimation.start();
    }

    private void openFullScreenFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(FULLSCREEN_FRAGMENT_TAG);
        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.stay_still);
            fragment = new FullscreenPhotoFragment();
            ft.add(R.id.main_frame, fragment, FULLSCREEN_FRAGMENT_TAG);
            ft.addToBackStack(FULLSCREEN_FRAGMENT_TAG);
            ft.commit();
            getSupportFragmentManager().executePendingTransactions();
            animateToolbar(
                    R.color.primary,
                    Color.parseColor("#000000"),
                    MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                    0
            );
        }
    }

    @Override
    public void onButtonClick() {
        openFullScreenFragment();
    }

}
