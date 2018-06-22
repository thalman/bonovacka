package net.halman.bonovacka;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Bonovacka extends AppCompatActivity {

    final String stateFile = "bonovacka.bin";
    private BonovackaApp _model = new BonovackaApp();
    private int _columns = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadState();

        setContentView(R.layout.activity_bonovacka);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        View.OnClickListener clearClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appClicked(v);
            }
        };

        // bind onClick actions
        TextView clear = (TextView) findViewById(R.id.clear);
        clear.setOnClickListener(clearClickListener);
        Button button = (Button) findViewById(R.id.back);
        button.setOnClickListener(clearClickListener);
        button = (Button) findViewById(R.id.forward);
        button.setOnClickListener(clearClickListener);

    }

    @Override
    public void onStart () {
        super.onStart();
        loadState();
        recreateFoodList();
        recreateOrder();
        updatePrize();
    }

    @Override
    public void onStop () {
        super.onStop();
        saveState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bonovacka, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionEditFood) {
            Intent intent = new Intent(this, FoodEdit.class);
            intent.putExtra("food", _model);
            startActivityForResult(intent, 1);
            return true;
        }
        else if (id == R.id.actionStartStockTaking) {
            startDialog();
            return true;
        }
        else if (id == R.id.actionFirstBon) {
            _model.first();
            recreateOrder();
            updatePrize();
            return true;
        }
        else if (id == R.id.actionLastBon) {
            _model.last();
            recreateOrder();
            updatePrize();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        _columns = 0;
        recreateFoodList();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                BonovackaApp temp = (BonovackaApp) data.getSerializableExtra("food");
                if (temp != null) {
                    _model = temp;
                    saveState();
                }
            }
        }
    }

    public int numberOfColumns () {
        if (_columns > 0) return _columns;
        int size = 0;
        /*
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int size = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        if (width > size) size = width;
        */
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);

        float density  = getResources().getDisplayMetrics().density;
        float dpWidth  = outMetrics.widthPixels / density;

        size = Math.round(dpWidth);
        _columns =  size / 180 - 1;
        if (_columns > 4) _columns = 4;
        if (_columns < 1) _columns = 1;
        return _columns;
    }

    private void recreateOrder () {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.boOrderContainer);
        linearLayout.removeAllViews();

        // create new list
        LinearLayout a = new LinearLayout(this);
        a.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < _model.orderSize(); i++) {
            OrderItemView item = new OrderItemView (this);

            item.setOrderItem(_model.orderItem(i));
            item.setId(2000 + i);
            item.setLayoutParams(params);
            item.setColor(foodColor(_model.orderItem(i).food()));
            a.addView(item);
        }
        linearLayout.addView(a);

        TextView boninfo = findViewById(R.id.bonInfo);
        if (boninfo != null) {
            boninfo.setText("" + _model.get().index() + "/(" + _model.bookStartingIndex() + "-" + _model.bookLastIndex() + ")");
        }
    }

    private LinearLayout findColumn (int idx) {
        switch (idx) {
            case 0:
                return (LinearLayout) findViewById(R.id.boFoodContainer1);
            case 1:
                return (LinearLayout) findViewById(R.id.boFoodContainer2);
            case 2:
                return (LinearLayout) findViewById(R.id.boFoodContainer3);
            case 3:
                return (LinearLayout) findViewById(R.id.boFoodContainer4);
            default:
                return null;
        }
    }

    private void setupWidth () {
        for (int i = 0; i < 4; i++) {
            LinearLayout l = findColumn(i);
            if (i < numberOfColumns()) {
                l.setVisibility(View.VISIBLE);
            } else {
                l.setVisibility(View.GONE);
            }
        }

        LinearLayout l = (LinearLayout) findViewById(R.id.boBonContainer);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, numberOfColumns()*10 - 5);
        //  p.weight = numberOfColumns();
        l.setLayoutParams(p);
    }

    private int foodColor (Food f) {
        switch (_model.groupIndex(f.group())) {
            case 0:
                return 0xffe0e0e0;
            case 1:
                return 0xffa3cfc6;
            case 2:
                return 0xffe7d3b6;
            default:
                return 0xfff0f0f0;
        }
    }

    void recreateFoodList () {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View.OnClickListener foodClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodClicked(v);
            }
        };

        int itemsPerColumn = _model.menuSize() / numberOfColumns() + (_model.menuSize() % numberOfColumns() == 0 ? 0 : 1);

        for (int column = 0; column < numberOfColumns(); column++) {
            LinearLayout a = new LinearLayout(this);
            a.setOrientation(LinearLayout.VERTICAL);
            for (int i = 0; i < itemsPerColumn; i++) {
                int idx = i + itemsPerColumn * column;
                if (idx < _model.menuSize()) {
                    FoodItemView item = new FoodItemView(this);

                    item.setFood(_model.menuItem(idx));
                    item.setId(1000 + idx);
                    item.setLayoutParams(params);
                    item.setOnClickListener(foodClickListener);
                    item.setColor(foodColor(_model.menuItem(idx)));
                    a.addView(item);
                }
            }
            LinearLayout foodContainer = findColumn(column);
            foodContainer.removeAllViews();
            foodContainer.addView(a);
        }

        setupWidth ();

        /*
        // working with just one column
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout foodContainer = (LinearLayout) findViewById(R.id.boFoodContainer);
        foodContainer.removeAllViews();
        View.OnClickListener foodClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodClicked(v);
            }
        };

        LinearLayout a = new LinearLayout(this);
        a.setOrientation(LinearLayout.VERTICAL);
        for (int i=0; i < _model.menuSize(); i++) {
            FoodItemView item = new FoodItemView(this);

            item.setFood(_model.menuItem(i));
            item.setId(1000 + i);
            item.setLayoutParams(params);
            item.setOnClickListener(foodClickListener);
            a.addView(item);
        }
        foodContainer.addView(a);
        */

        /*
        TableLayout a = new TableLayout(this);
        a.setLayoutParams(
                new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT)
        );
        int itemsPerColumn = _model.menuSize() / numberOfColumns() + 1;
        for (int line = 0; line < itemsPerColumn; line++) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            for (int col = 0; col < numberOfColumns(); col++) {
                int index = line + col*itemsPerColumn;
                if (index < _model.menuSize()) {
                    FoodItemView item = new FoodItemView(this);
                    item.setFood(_model.menuItem(index));
                    item.setId(1000 + index);
                    item.setLayoutParams(params);
                    item.setOnClickListener(foodClickListener);
                    row.addView(item);
                }
            }
            a.addView(row);
        }
        b.addView(a);
        foodContainer.addView(b);
        */
    }

    public BonovackaApp getModel() {
        return _model;
    }

    public void addFoodToOrder (Food f) {
        _model.addToOrder(f);
        recreateOrder();
        updatePrize();
    }

    public void updatePrize () {
        TextView price = findViewById(R.id.priceTotal);
        if (price != null) {
            price.setText(_model.get().price()/100 + "/" + _model.price()/100 + "Kč");
        }
    }

    private void foodClicked (View v) {
        FoodItemView i = (FoodItemView) v;
        addFoodToOrder(i.getFood());
    }

    private void appClicked (View v) {
        if (v.getId () == R.id.clear) {
            _model.clearOrder();
            recreateOrder();
            updatePrize();
        }
        else if (v.getId() == R.id.back) {
            _model.prev();
            recreateOrder();
            updatePrize();
        }
        else if (v.getId() == R.id.forward) {
            _model.next();
            recreateOrder();
            updatePrize();
        }
    }

    void saveState () {
        try {
            FileOutputStream file = openFileOutput(stateFile, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(file);
            oos.writeObject(_model);
            oos.flush();
            oos.close();
            file.close();
        } catch (Exception e) {
        }

    }

    void loadState () {
        try {
            FileInputStream file = openFileInput(stateFile);
            ObjectInputStream ois = new ObjectInputStream(file);
            _model = (BonovackaApp) ois.readObject();
            ois.close();
            file.close();
        } catch (Exception e) {
            _model = new BonovackaApp();
        }

    }

    public void showMessage(String text) {
        AlertDialog alertDialog = new AlertDialog.Builder(Bonovacka.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(text);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void foodDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Přidat novou položku");

        // Set up the input
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.food_dialog, null));

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //m_Text = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void startDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Začít znovu od bonu č.");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String startstr = input.getText().toString();
                int start;
                try {
                    start = Integer.parseInt(startstr);
                } catch (Exception e) {
                    start = 1;
                }
                _model.bookClear ();
                _model.bookStartingIndex(start);
                recreateOrder();
                updatePrize();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
