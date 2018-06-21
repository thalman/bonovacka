package net.halman.bonovacka;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * TODO: document your custom view class.
 */
public class FoodItemView extends LinearLayout {
    private TextView name = null;
    private TextView price = null;
    private Food food = null;
    private FrameLayout frame = null;

    public FoodItemView(Context context) {
        super(context);
        initializeViews(context);
    }

    public FoodItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public FoodItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.food_item_view, this);
        name = (TextView) this.findViewById(R.id.fiName);
        price = (TextView) this.findViewById(R.id.fiPrice);
        frame = (FrameLayout) this.findViewById(R.id.fiFrame);
    }

    public void setFood(Food food) {
        this.food = food;
        name.setText(this.food.name());
        price.setText("" + this.food.price()/100);
    }
    public Food getFood() { return food; }

    public void setColor (int c) {
        frame.setBackground(new ColorDrawable(c));
    }
}
