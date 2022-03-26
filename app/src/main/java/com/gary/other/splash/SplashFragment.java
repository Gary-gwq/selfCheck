package com.gary.other.splash;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;


public class SplashFragment extends DialogFragment {

    private Activity mContext;
    private View image;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.e("selfCheck", "......................onCreateView");
        int layoutID = getResources().getIdentifier("huouniongame_splash_activity", "layout", mContext.getPackageName());
        View view = inflater.inflate(layoutID, container);// 需要用android.R.id.content这个view
        appendAnimation(view);
        setCancelable(false);
        return view;
    }

    private void appendAnimation(View view) {
        AlphaAnimation ani = new AlphaAnimation(0.0f, 1.0f);
        ani.setRepeatMode(Animation.REVERSE);
        ani.setRepeatCount(0);
        ani.setDuration(3000);
        image = view.findViewById(getResources().getIdentifier("huouniongame_splash_activity_img", "id", mContext.getPackageName()));
        if (image == null) {
            Log.e("SplashActivity", "image = null");
            int defaultID = getResources().getIdentifier("huouniongame_splash_activity_layout", "id", mContext.getPackageName());
            RelativeLayout layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(defaultID, null);
            image = layout.getChildAt(0);
        }
        ani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e("selfCheck", "......................onAnimationEnd");
                animation.cancel();
                if (image != null) {
                    image.clearAnimation();
                    image = null;
                }
                onDestroyView();
            }
        });
        image.setAnimation(ani);
        ani.startNow();
    }


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;
        Log.e("selfCheck", String.valueOf(widthPixels));
        if (null != getDialog() && null != getDialog().getWindow()) {
            getDialog().getWindow().setLayout(dm.widthPixels,dm.heightPixels);
        }
    }
}
