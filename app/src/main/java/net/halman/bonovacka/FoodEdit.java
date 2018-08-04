package net.halman.bonovacka;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FoodEdit extends AppCompatActivity {
    private BonovackaApp _model = new BonovackaApp ();
    private TabLayout groupTabs = null;
    private int selectedGroup = 1;
    private Button addFoodButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_edit);
        _model = (BonovackaApp) getIntent().getSerializableExtra("food");

        addFoodButton = (Button) findViewById(R.id.feAddButton);
        addFoodButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foodAddDialog ();
                    }
                }
        );
        groupTabs = (TabLayout) findViewById(R.id.feTabs);
        for (int i = 1; i < _model.groups(); i++) {
            groupTabs.addTab(groupTabs.newTab().setText(_model.group(i)));
        }

        groupTabs.addOnTabSelectedListener(
            new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    onGroupChange(tab);
                }
                @Override
                public void onTabReselected(TabLayout.Tab tab) { }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) { }
            }
        );

        createFoodList(_model.group(selectedGroup));
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        _model.menuSort();
        Intent intent = new Intent();
        intent.putExtra("food", _model);
        setResult(RESULT_OK, intent);

        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onGroupChange (TabLayout.Tab tab){
        selectedGroup = tab.getPosition () + 1;
        createFoodList ();
    }

    private void onFoodClick (View v) {
        FoodItemView i = (FoodItemView) v;
        foodEditDialog(i.getFood());
    }

    private void onDialogOK (Food f, String name, int price) {
        if (f == null) {
            if (_model.menuItem(name) != null) {
                showMessage("Chyba", "Jídlo s tímto jménem už existuje");
            } else {
                _model.addToMenu(name, price, _model.group(selectedGroup));
            }
        } else {
            if (! f.name().equals(name)) {
                if (_model.menuItem(name) != null) {
                    showMessage("Chyba", "Jídlo s tímto jménem už existuje");
                } else {
                    f.name(name);
                    f.price(price);
                }
            } else {
                f.price(price);
            }
        }
        createFoodList();
    }

    private void onDialogDelete (Food f) {
        _model.removeFromMenu (f);
        createFoodList();
    }

    private void createFoodList () {
        createFoodList(_model.group(selectedGroup));
    }

    private void createFoodList (String group) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout foodLayout = (LinearLayout) findViewById(R.id.feFoodList);
        foodLayout.removeAllViews();

        View.OnClickListener foodClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFoodClick(v);
            }
        };

        LinearLayout a = new LinearLayout(this);
        a.setOrientation(LinearLayout.VERTICAL);
        for (int i=0; i < _model.menuSize(); i++) {
            Food f = _model.menuItem (i);
            if (f.group().equals(group)) {
                FoodItemView item = new FoodItemView(this);

                item.setFood(f);
                item.setId(1000 + i);
                item.setLayoutParams(params);
                item.setOnClickListener(foodClickListener);
                a.addView(item);
            }
        }
        foodLayout.addView(a);
    }

    public void foodEditDialog (final Food f) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set up the input
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogContent = inflater.inflate (R.layout.food_dialog, null);
        builder.setView (dialogContent);

        final EditText foodName = dialogContent.findViewById(R.id.dialogFoodName);
        final EditText foodPrice = dialogContent.findViewById(R.id.dialogFoodPrice);

        builder.setTitle("Opravit název a cenu");
        foodName.setText(f.name());
        foodPrice.setText("" + f.price()/100);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int price = 0;
                try {
                    price = Integer.parseInt(foodPrice.getText().toString()) * 100;
                } catch (Exception e) {};
                onDialogOK (f, foodName.getText().toString(), price);
            }
        });
        builder.setNegativeButton("Neukádat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNeutralButton("Smazat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDialogDelete(f);
            }
        });
        builder.show();
    }

    public void foodAddDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set up the input
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogContent = inflater.inflate (R.layout.food_dialog, null);
        builder.setView (dialogContent);

        final EditText foodName = dialogContent.findViewById(R.id.dialogFoodName);
        final EditText foodPrice = dialogContent.findViewById(R.id.dialogFoodPrice);

        builder.setTitle("Nové jídlo a jeho cena");

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int price = 0;
                try {
                    price = Integer.parseInt(foodPrice.getText().toString()) * 100;
                } catch (Exception e) {};
                onDialogOK (null, foodName.getText().toString(), price);
            }
        });
        builder.setNegativeButton("Neukádat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void showMessage(String title, String text) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(text);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
