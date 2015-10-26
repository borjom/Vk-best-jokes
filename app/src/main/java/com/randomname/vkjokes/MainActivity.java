package com.randomname.vkjokes;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.randomname.vkjokes.Fragments.CommentsFragment;
import com.randomname.vkjokes.Fragments.FullscreenPhotoFragmentHost;
import com.randomname.vkjokes.Fragments.PublicListFragment;
import com.randomname.vkjokes.Fragments.VkLoginAlert;
import com.randomname.vkjokes.Interfaces.FragmentsCallbacks;
import com.randomname.vkjokes.Models.WallPostModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FragmentsCallbacks {

    private final static String FULLSCREEN_FRAGMENT_TAG = "full_screen_tag";
    private final static String COMMENTS_FRAGMENT_TAG = "comments_fragment_tag";
    private final static String MENU_ICON_STATE = "menu_icon_state";
    private final static String TOOLBAR_COLOR_STATE = "toolbar_color_state";
    private final static String STATUS_COLOR_STATE = "window_color_state";
    private final static String TOOLBAR_TITLE_STATE = "toolbar_title_state";
    private final static String TOOLBAR_OLD_TITLE_STATE = "toolbar_old_title_state";
    private final static String TOOLBAR_IS_SHOWN = "toolbar_is_shown";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.toolbar_title_switcher)
    TextSwitcher toolbarTitleSwitcher;

    private Drawer materialDrawer;
    private MaterialMenuDrawable materialMenu;
    private PublicListFragment publicListFragment;
    private String title;
    private String oldTitle;
    private boolean toolbarShown = true;

    private float transitionOffset = 2.0f;

    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        FragmentManager fm = getSupportFragmentManager();

        publicListFragment =(PublicListFragment) fm.findFragmentByTag("PublicListFragment");
        if (publicListFragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            publicListFragment = new PublicListFragment();
            ft.replace(R.id.main_frame, publicListFragment, "PublicListFragment");
            ft.commit();
        }

        if (savedInstanceState != null) {

            String stringIconState = savedInstanceState.getString(MENU_ICON_STATE);
            materialMenu.setIconState(MaterialMenuDrawable.IconState.valueOf(stringIconState));

            toolbar.setBackgroundColor(savedInstanceState.getInt(TOOLBAR_COLOR_STATE, Color.parseColor("#2196F3")));
            title = savedInstanceState.getString(TOOLBAR_TITLE_STATE);
            oldTitle = savedInstanceState.getString(TOOLBAR_OLD_TITLE_STATE);
            setNewToolbarTitle(title);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(savedInstanceState.getInt(STATUS_COLOR_STATE, Color.parseColor("#1E88E5")));
            }

            toolbarShown = savedInstanceState.getBoolean(TOOLBAR_IS_SHOWN);

            if (!toolbarShown) {
                final ViewTreeObserver observer= toolbar.getViewTreeObserver();
                observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        AnimatorSet set = new AnimatorSet();
                        toolbarShown = false;
                        set.playTogether(
                                ObjectAnimator.ofFloat(toolbar, "translationY", -toolbar.getHeight())
                        );
                        set.setDuration(0).start();
                        if (Build.VERSION.SDK_INT < 16) {
                            toolbar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            toolbar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int color = Color.TRANSPARENT;
        String iconStateString;

        if (transitionOffset == 2.0f) {

            MaterialMenuDrawable.IconState iconState = materialMenu.getIconState();
            iconStateString = iconState.name();

            Drawable background = toolbar.getBackground();
            if (background instanceof ColorDrawable) {
                color = ((ColorDrawable) background).getColor();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                outState.putInt(STATUS_COLOR_STATE, getWindow().getStatusBarColor());
            }
        } else {
            color = Color.argb(100, 0, 0, 0);
            iconStateString = MaterialMenuDrawable.IconState.ARROW.name();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                outState.putInt(STATUS_COLOR_STATE, Color.parseColor("#000000"));
            }
        }

        outState.putInt(TOOLBAR_COLOR_STATE, color);
        outState.putString(MENU_ICON_STATE, iconStateString);

        outState.putString(TOOLBAR_TITLE_STATE, getToolbarTitle());
        outState.putString(TOOLBAR_OLD_TITLE_STATE, oldTitle);
        outState.putBoolean(TOOLBAR_IS_SHOWN, toolbarShown);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if(materialDrawer.isDrawerOpen()) {
            materialDrawer.closeDrawer();
        } else if(transitionOffset == 2.0f) {
            closeFullscreen(false);
            super.onBackPressed();
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initToolbarSwitcher();
        final String[] publicNames = getResources().getStringArray(R.array.public_name);
        final String[] publicUrls = getResources().getStringArray(R.array.public_url);

        materialDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        materialDrawer.closeDrawer();
                        publicListFragment.changePublic(publicUrls[position]);
                        title = publicNames[position];
                        oldTitle = title;
                        setNewToolbarTitle(title);
                        return true;
                    }
                })
                .build();

        for (int i = 0; i < publicNames.length; i++) {
            materialDrawer.addItem(new PrimaryDrawerItem().withName(publicNames[i]));
        }

        materialMenu = new MaterialMenuDrawable(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (transitionOffset != 2.0f) {
                    return;
                }

                if (!closeFullscreen(true)) {
                    materialDrawer.openDrawer();
                }
            }
        });

        toolbar.setNavigationIcon(materialMenu);
        title = publicNames[0];
        oldTitle = title;
        setNewToolbarTitle(title);
    }

    private void initToolbarSwitcher() {
        toolbarTitleSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like clolr, size etc
                TextView myText = new TextView(MainActivity.this);
                myText.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL | Gravity.LEFT);
                myText.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.accent));
                return myText;
            }
        });

        Animation in = AnimationUtils.loadAnimation(this, R.anim.stay_still);
        Animation out = AnimationUtils.loadAnimation(this,R.anim.stay_still);

        toolbarTitleSwitcher.setInAnimation(in);
        toolbarTitleSwitcher.setOutAnimation(out);
    }

    public void setNewToolbarTitle(String title) {
        toolbarTitleSwitcher.setText(title);
    }

    private String getToolbarTitle() {
        TextView currentlyShownTextView = (TextView) toolbarTitleSwitcher.getCurrentView();
        String currentlyShownText = currentlyShownTextView.getText().toString();

        return currentlyShownText;
    }

    private boolean closeFullscreen(boolean toClose) {
        materialDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        Fragment frag = fragments.get(fragments.size() - 1);

        if (frag == null) {
            return false;
        }

        String tag = frag.getTag();

        switch (tag) {
            case FULLSCREEN_FRAGMENT_TAG:
                closePhotoFragment(toClose, frag);
                return true;
            case COMMENTS_FRAGMENT_TAG:
                closeCommentFragment(toClose, frag);
                return true;
            default:
                return false;
        }
    }

    private void closePhotoFragment(boolean toClose, Fragment frag) {
        if (frag != null) {

            int color = Color.TRANSPARENT;
            Drawable background = toolbar.getBackground();
            if (background instanceof ColorDrawable) {
                color = ((ColorDrawable) background).getColor();
            }

            animateToolbar(
                    color,
                    Color.parseColor("#2196F3"),
                    MaterialMenuDrawable.AnimationState.BURGER_ARROW,
                    1
            );
            animateStatusBar(Color.parseColor("#000000"),
                    Color.parseColor("#1E88E5")
            );

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.stay_still, R.anim.slide_out_right);
            ft.hide(frag);
            ft.commit();

            if (toClose) {
                getSupportFragmentManager().popBackStack();
            }

            setNewToolbarTitle(oldTitle);
        }
    }

    private void closeCommentFragment(boolean toClose, Fragment frag) {
        if (frag != null) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.stay_still, R.anim.slide_out_top);
            ft.hide(frag);
            ft.commit();

            if (toClose) {
                getSupportFragmentManager().popBackStack();
            }

            setNewToolbarTitle(oldTitle);
            materialMenu.animateIconState(MaterialMenuDrawable.IconState.BURGER, false);
        }
    }

    private void animateToolbar(int colorFrom, int colorTo, final MaterialMenuDrawable.AnimationState state, final int offset) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);

        colorAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                transitionOffset = 2.0f;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                toolbar.setBackgroundColor((Integer) animator.getAnimatedValue());
                if (transitionOffset == 2.0f) {
                    materialMenu.setTransformationOffset(
                            state,
                            animator.getAnimatedFraction() + offset
                    );
                } else if (animator.getAnimatedFraction() > (1 - transitionOffset)) {
                    materialMenu.setTransformationOffset(
                            state,
                            1 - animator.getAnimatedFraction()
                    );
                }

                int alphaValue = 255;

                if (offset == 0) {
                    alphaValue = Math.round(255 * (1.0f - animator.getAnimatedFraction()));
                } else {
                    alphaValue = Math.round(255 * animator.getAnimatedFraction());
                }

                if (transitionOffset != 2.0f && animator.getAnimatedFraction() > (1 - transitionOffset)) {
                    alphaValue = Math.round(255 * animator.getAnimatedFraction());
                } else if (transitionOffset != 2.0f) {
                    alphaValue = Math.round(255 * (1.0f - transitionOffset));
                }

                if (alphaValue > 100) {
                    toolbar.getBackground().setAlpha(alphaValue);
                } else {
                    toolbar.getBackground().setAlpha(100);
                }
            }
        });

        colorAnimation.setDuration(350);
        colorAnimation.setStartDelay(0);
        colorAnimation.start();
    }

    private void animateStatusBar(int colorFrom, int colorTo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);

            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    getWindow().setStatusBarColor((Integer) animator.getAnimatedValue());
                }
            });

            colorAnimation.setDuration(350);
            colorAnimation.setStartDelay(0);
            colorAnimation.start();
        }
    }

    private void openFullScreenFragment(ArrayList<String> wallPhotos, int position) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(FULLSCREEN_FRAGMENT_TAG);
        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.stay_still);

            Bundle data = new Bundle();
            data.putStringArrayList(FullscreenPhotoFragmentHost.PHOTOS_ARRAY_KEY, wallPhotos);
            data.putInt(FullscreenPhotoFragmentHost.POSITION_KEY, position);

            fragment = FullscreenPhotoFragmentHost.getInstance(data);
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
            animateStatusBar(
                    R.color.primary_dark,
                    Color.parseColor("#000000")
            );
        }
    }

    @Override
    public void onButtonClick(ArrayList<String> wallPhotos, int position) {
        openFullScreenFragment(wallPhotos, position);
        materialDrawer.getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onCommentsClick(WallPostModel wallPostModel) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(COMMENTS_FRAGMENT_TAG);
        if (fragment == null) {
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_bottom, R.anim.stay_still);

            Bundle data = new Bundle();
            data.putParcelable(CommentsFragment.WALL_POST_MODEL_KEY, wallPostModel);

            fragment = CommentsFragment.getInstance(data);
            ft.add(R.id.main_frame, fragment, COMMENTS_FRAGMENT_TAG);
            ft.addToBackStack(COMMENTS_FRAGMENT_TAG);
            ft.commit();
            getSupportFragmentManager().executePendingTransactions();

            materialMenu.animateIconState(MaterialMenuDrawable.IconState.CHECK, false);
        }
    }

    @Override
    public void showVkAlert() {
        VkLoginAlert alert = VkLoginAlert.newInstance();
        alert.show(getSupportFragmentManager(), "alertTag");
    }

    @Override
    public void onPhotoFragmentPageSlide(float offset) {
        transitionOffset = offset;
        ArgbEvaluator ev = new ArgbEvaluator();
        int startColor = Color.parseColor("#000000");
        int endColor;
        int resultColor;

        materialMenu.setTransformationOffset(MaterialMenuDrawable.AnimationState.BURGER_ARROW, 1 + (1 - offset));

        endColor = Color.parseColor("#2196F3");
        resultColor = (Integer)ev.evaluate(1 - offset, startColor, endColor);
        toolbar.setBackgroundColor(resultColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            endColor = Color.parseColor("#1E88E5");
            resultColor = (Integer)ev.evaluate(1 - offset, startColor, endColor);

            getWindow().setStatusBarColor(resultColor);

        }
        int alphaValue = Math.round(255 * (1.0f - offset));

        if (alphaValue > 100) {
            toolbar.getBackground().setAlpha(alphaValue);
        } else {
            toolbar.getBackground().setAlpha(100);
        }

        if (offset == 0.0f) {
            getSupportFragmentManager().popBackStack();
            transitionOffset = 2.0f;
            toolbarShown = true;
            setNewToolbarTitle(oldTitle);
        }
    }

    @Override
    public void onPhotoPageStop() {
        transitionOffset = 2.0f;
    }

    @Override
    public void onPhotoClick() {
        if (toolbarShown) {
            toolbarShown = false;
            hideToolbar();

        } else {
            toolbarShown = true;
            showToolbar();
        }
    }

    private void hideToolbar() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(toolbar, "translationY", -toolbar.getBottom())
        );
        set.setDuration(350).start();
    }

    private void showToolbar() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(toolbar, "translationY", 0)
        );
        set.setDuration(350).start();
    }

    @Override
    public void onPageScroll(int offset) {
        if (scrolledDistance > HIDE_THRESHOLD && toolbarShown) {
            hideToolbar();
            toolbarShown = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -HIDE_THRESHOLD && !toolbarShown) {
            showToolbar();
            toolbarShown = true;
            scrolledDistance = 0;
        }

        if((toolbarShown && offset > 0) || (!toolbarShown && offset < 0)) {
            scrolledDistance += offset;
        }
    }
}
