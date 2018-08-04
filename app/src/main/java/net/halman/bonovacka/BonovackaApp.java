package net.halman.bonovacka;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class BonovackaApp implements Serializable {
    private ArrayList<Food> _foods = new ArrayList<Food>();
    private ArrayList<String> _groups = new ArrayList <String> ();
    private BonBook _book = new BonBook ();

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
        for (int i = 0; i < _groups.size(); i++) {
            if (group.equals(group(i))) return i;
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
}
