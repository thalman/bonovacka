package net.halman.bonovacka;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class BonovackaApp implements Serializable {
    private ArrayList<Food> _foods = new ArrayList<Food>();
    private ArrayList<String> _groups = new ArrayList <String> ();
    private BonBook _book = new BonBook ();
    private String _csvurl = new String();

    public BonovackaApp() {
        _groups.add("-");
        _groups.add("Hotovky");
        _groups.add("Sobotky");
        _groups.add("Minutky");

        addToMenu("řízek", 9900);
        addToMenu("smažák", 9000);
        addToMenu("špagety", 8500);
        addToMenu("znojemská", 8600);
        addToMenu("koprovka", 8600, _groups.get(1));
        addToMenu("steak z lososa", 8600, _groups.get(2));
        addToMenu("candát na grilu", 8600, _groups.get(2));
    }

    public void addToMenu(Food f) {
        _foods.add(new Food(f));
    }

    public void addToMenu(String name, int price) {
        _foods.add(new Food(name, price, _groups.get(1)));
    }

    public void addToMenu(String name, int price, String group) {
        _foods.add(new Food(name, price, group));
    }

    public void addToOrder(Food f) {
        _book.addFood(f);
    }

    public void addToOrder(String name) {
        for (int i = 0; i < _foods.size(); i++) {
            Food f = _foods.get(i);
            if (f.name().equals(name)) {
                addToOrder(f);
            }
        }
    }

    public void removeFromMenu (Food f) {
        _book.removeFromBook (f);
        _foods.remove (f);
    }

    public int menuSize() {
        return _foods.size();
    }

    public Food menuItem(int index) {
        return _foods.get(index);
    }

    public Food menuItem (String name) {
        for (int i = 0; i < _foods.size(); i++) {
            if (_foods.get (i).name().equals(name)) return _foods.get(i);
        }
        return null;
    }

    public void menuSort() {
        Collections.sort(_foods, new Comparator<Food>() {
            @Override
            public int compare(Food lhs, Food rhs) {
            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
            int g1 = groupIndex(lhs.group());
            int g2 = groupIndex(rhs.group());
            if (g1 < g2) return -1;
            if (g1 > g2) return +1;
            return 0;
            }
        });
    }

    public int orderSize() {
        return _book.get().size();
    }

    public OrderItem orderItem(int index) {
        return _book.get().get (index);
    }

    public int groups() {
        return _groups.size();
    }

    public String group(int index) {
        return _groups.get(index);
    }

    public int groupIndex (String group) {
        return groupIndex(group, _groups);
    }

    private int groupIndex (String group, ArrayList<String> groups) {
        for (int i = 0; i < groups.size(); i++) {
            if (group.equals(groups.get(i))) return i;
        }
        return 0;
    }

    public int price() {
        return _book.price ();
    }

    public int bookSize() {
        return _book.size ();
    }

    public int bookStartingIndex () {
        return _book.startingIndex();
    }

    public void bookStartingIndex (int index) {
        _book.startingIndex(index);
    }

    public void bookClear () {
        _book.clear ();
    }

    public int bookLastIndex () {
        return _book.lastIndex();
    }

    public void clearOrder () {
        _book.get().clear();
    }

    public Bon next () { return _book.next(); }
    public Bon prev () { return _book.prev(); }
    public Bon get () { return _book.get(); }
    public Bon first () { return _book.first(); }
    public Bon last () { return _book.last(); }

    public void upgrade () {
        if (! _groups.get(0).equals("-")) {
            _groups.add(0, "-");
        }
    }

    public String csvurl() {
        if (_csvurl.isEmpty()) return "https://halman.net/nextcloud/index.php/s/??????/download?path=%2F&files=menu.csv";
        return _csvurl;
    }

    public void cvsurl (String url) {
        _csvurl = url;
    }

    public void getCsv () throws Exception {
        getCsv(_csvurl);
    }

    public void getCsv (String link) throws Exception {
        URL url;
        url = new URL(link);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream is = urlConnection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        String group = "", food = "";
        int price;

        ArrayList<Food> newfoods = new ArrayList<Food>();
        ArrayList<String> newgroups = new ArrayList <String> ();
        newgroups.add("-");
        while ((line = reader.readLine()) != null) {
            try {
                ArrayList<String> RowData = CSVParser.parse(line, ',');
                if (RowData.size() >= 3) {
                    if (RowData.get(0).length() > 0 && !group.equals(RowData.get(0))) {
                        group = RowData.get(0);
                    }
                    food = RowData.get(1);
                    try {
                        price = Integer.parseInt(RowData.get(2)) * 100;
                    } catch (Exception e) {
                        price = 0;
                    }
                    if (group.length() > 0 && food.length() > 0 && price > 0) {
                        // we have some date
                        // group
                        if (groupIndex(group, newgroups) == 0) {
                            newgroups.add(group);
                        }
                        // food
                        newfoods.add(new Food(food, price, group));
                    }
                }
            } catch (Exception e) { }
        }
        urlConnection.disconnect();
        _groups = newgroups;
        _foods = newfoods;
        _book = new BonBook();
        _csvurl = link;
        menuSort();
    }
}
