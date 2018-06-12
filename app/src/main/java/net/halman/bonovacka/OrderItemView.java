package net.halman.bonovacka;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OrderItemView extends LinearLayout {
    private TextView name = null;
    private TextView price = null;
    private TextView plus = null;
    private TextView minus = null;
    private OrderItem item = null;
    private FrameLayout frame = null;
    private Bonovacka app = null;

    private View.OnClickListener plusMinusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            plusMinusClicked(v);
        }
    };

    public OrderItemView(Context context) {
        super(context);
        initializeViews(context);
    }

    public OrderItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public OrderItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.order_item_view, this);

        name = (TextView) this.findViewById(R.id.orderName);
        price = (TextView) this.findViewById(R.id.orderPrice);
        plus = (TextView) this.findViewById(R.id.orderPlus);
        plus.setOnClickListener(plusMinusClickListener);
        minus = (TextView) this.findViewById(R.id.orderMinus);
        minus.setOnClickListener(plusMinusClickListener);
        frame = (FrameLayout) this.findViewById(R.id.orderFrame);
        app = (Bonovacka) context;
    }

    public void updateView () {
        name.setText(item.count() == 1 ? item.food().name() : "" + item.count() + "x " + item.food().name());
        price.setText("" + item.price()/100);
        if (item.count() == 0) {
            setVisibility(GONE);
        } else {
            setVisibility(VISIBLE);
        }
    }

    public void setOrderItem (OrderItem i) {
        item = i;
        updateView();
    }

    public void setColor (int c) {
        frame.setBackground(new ColorDrawable(c));
    }

    private void plusMinusClicked(View v) {
        if (v.getId() == R.id.orderPlus) {
            if (item != null) item.inc();
            updateView();
            if (app != null) app.updatePrize();
        }
        if (v.getId() == R.id.orderMinus) {
            if (item != null) item.dec();
            updateView();
            if (app != null) app.updatePrize();
        }
    }
}
