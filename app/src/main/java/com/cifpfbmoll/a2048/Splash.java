package com.cifpfbmoll.a2048;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    AnimatorSet animatorSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        TextView text2 = findViewById(R.id.number2);
        TextView text0 = findViewById(R.id.number0);
        TextView text4 = findViewById(R.id.number4);
        TextView text8 = findViewById(R.id.number8);
        text2.setText(Html.fromHtml("<font color=#D98EF5>2</font>"));
        text0.setText(Html.fromHtml("<font color=#AF23EB>0</font>"));
        text4.setText(Html.fromHtml("<font color=#F08B94>4</font>"));
        text8.setText(Html.fromHtml("<font color=#F54060>8</font>"));
        animatorSet = new AnimatorSet();

        ObjectAnimator down2 = ObjectAnimator.ofFloat(text2,"translationY",1500f).setDuration(0);
        ObjectAnimator up2 = ObjectAnimator.ofFloat(text2,"translationY",-150f).setDuration(1500);
        ObjectAnimator truePosition2 = ObjectAnimator.ofFloat(text2,"translationY",0f).setDuration(750);
        animatorSet.play(down2);
        animatorSet.play(up2).after(down2);
        animatorSet.play(truePosition2).after(up2);

        ObjectAnimator down0 = ObjectAnimator.ofFloat(text0,"translationY",1500f).setDuration(0);
        ObjectAnimator up0 = ObjectAnimator.ofFloat(text0,"translationY",-150f).setDuration(1500);
        ObjectAnimator truePosition0 = ObjectAnimator.ofFloat(text0,"translationY",0f).setDuration(750);
        animatorSet.play(down0);
        animatorSet.play(up0).after(down0).after(250);
        animatorSet.play(truePosition0).after(up0);

        ObjectAnimator down4 = ObjectAnimator.ofFloat(text4,"translationY",1500f).setDuration(0);
        ObjectAnimator up4 = ObjectAnimator.ofFloat(text4,"translationY",-150f).setDuration(1500);
        ObjectAnimator truePosition4 = ObjectAnimator.ofFloat(text4,"translationY",0f).setDuration(750);
        animatorSet.play(down4);
        animatorSet.play(up4).after(down4).after(500);
        animatorSet.play(truePosition4).after(up4);

        ObjectAnimator down8 = ObjectAnimator.ofFloat(text8,"translationY",1500f).setDuration(0);
        ObjectAnimator up8 = ObjectAnimator.ofFloat(text8,"translationY",-150f).setDuration(1500);
        ObjectAnimator truePosition8 = ObjectAnimator.ofFloat(text8,"translationY",0f).setDuration(750);
        animatorSet.play(down8);
        animatorSet.play(up8).after(down8).after(750);
        animatorSet.play(truePosition8).after(up8);

        TextView theGame = findViewById(R.id.theGameText);
        ObjectAnimator disappear = ObjectAnimator.ofFloat(theGame,"alpha",1,0).setDuration(0);
        ObjectAnimator appear = ObjectAnimator.ofFloat(theGame,"alpha",0,1).setDuration(750);
        animatorSet.play(disappear).with(down2);
        animatorSet.play(appear).with(truePosition8);

        View delay = findViewById(R.id.timerDelay);
        ObjectAnimator delaying = ObjectAnimator.ofFloat(delay,"alpha",0,1).setDuration(500);
        animatorSet.play(delaying).after(appear);

        animatorSet.start();

        delaying.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                startActivity(new Intent(Splash.this,
                        Menu.class));
                overridePendingTransition(R.anim.fade_in,R.anim.fade_in2);
                Splash.this.finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        animatorSet.cancel();
    }

    @Override
    public void onPause(){
        super.onPause();
        animatorSet.pause();
    }

    @Override
    public void onResume(){
        super.onResume();
        animatorSet.resume();
    }
}
