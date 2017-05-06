package com.aliv3.rickshawaladriver2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ImageView imageSplash = (ImageView) findViewById(R.id.imageViewSplash);
        final Animation animSplash1 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.anim_scale);
        final Animation animSplash2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_out);

        imageSplash.startAnimation(animSplash1);
        animSplash1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageSplash.startAnimation(animSplash2);
                finish();
                Intent i = new Intent(getBaseContext(),RegisterActivity.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }
}
