package com.guang.sun.game;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.game2048.R;

public class Card extends FrameLayout {


    public static int width;

    private TextView label;

    public Card(Context context) {
        super(context);

        LayoutParams lp = null;

        View background = new View(getContext());
        lp = new LayoutParams(-1, -1);
        lp.setMargins(40, 40, 0, 0);

//        background.setBackgroundColor(getResources().getColor(R.color.sekuai));
        background.setBackground(getResources().getDrawable(R.drawable.bg_edittext_focused));
        addView(background, lp);

        label = new TextView(getContext());
        label.setTextSize(20);
        label.setTextColor(getResources().getColor(R.color.alpha_black));
        label.setGravity(Gravity.CENTER);

        lp = new LayoutParams(-1, -1);
        lp.setMargins(40, 40, 0, 0);
        addView(label, lp);

        setNum(0);
    }


    private int num = 0;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;

        if (num <= 0) {
            label.setText("");
        } else {
            label.setText(String.valueOf(num));

        }

        switch (num) {
            case 0:
                label.setBackgroundColor(0x00000000);
                break;
            case 2:
                label.setBackground(getResources().getDrawable(R.drawable.bg_2));
                break;
            case 4:
                label.setBackground(getResources().getDrawable(R.drawable.bg_4));
                break;
            case 8:
                label.setBackground(getResources().getDrawable(R.drawable.bg_8));
                break;
            case 16:
                label.setBackground(getResources().getDrawable(R.drawable.bg_16));
                break;
            case 32:
                label.setBackground(getResources().getDrawable(R.drawable.bg_32));
                break;
            case 64:
                label.setBackground(getResources().getDrawable(R.drawable.bg_64));
                break;
            case 128:
                label.setBackground(getResources().getDrawable(R.drawable.bg_128));
                break;
            case 256:
                label.setBackground(getResources().getDrawable(R.drawable.bg_256));
                break;
            case 512:
                label.setBackground(getResources().getDrawable(R.drawable.bg_512));
                break;
            case 1024:
                label.setBackground(getResources().getDrawable(R.drawable.bg_1024));
                break;
            case 2048:
                label.setBackground(getResources().getDrawable(R.drawable.bg_2048));
                break;
            default:
                label.setBackground(getResources().getDrawable(R.drawable.bg_else));
                break;
        }
    }

    public boolean equals(Card another) {
        return getNum() == another.getNum();
    }

    public TextView getLabel() {
        return label;
    }

    public void addScaleAnimation() {
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1, 0.1f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(500);
        setAnimation(null);
        getLabel().startAnimation(sa);
    }

}
