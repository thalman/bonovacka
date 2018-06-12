package net.halman.bonovacka;

import android.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class Bon implements Serializable {
    private int _index = 0;
    private ArrayList <OrderItem> _order = new ArrayList <OrderItem> ();

    public Bon (int index) {
        _index = index;
    }

    public int index () { return  _index; }
    public void set_index (int index) { _index = index; }
    public int size () { return _order.size(); }
    public OrderItem get(int index) {
        return _order.get(index);
    }
    public Food food (int index) {
        return _order.get(index).food();
    }
    public String foodName (int i) {
        return _order.get(i).food().name();
    }
    public int foodCount (int i) {
        return _order.get(i).count();
    }
    public int foodPrice (int i) {
        return _order.get(i).food().price();
    }
    public void addFood (Food f) {
        for (int i = 0; i < _order.size(); i++) {
            if (_order.get(i).food().equals(f)) {
                _order.get(i).inc();
                return;
            }
        }
        _order.add(new OrderItem(f));
    }

    public void removeFood (Food f) {
        for (int i = 0; i < _order.size(); i++) {
            if (_order.get(i).equals(f)) {
                _order.get(i).dec();
                if (_order.get(i).count() == 0) {
                    _order.remove(i);
                }
                return;
            }
        }
    }

    public void removeFood (int i) {
        if (i >= 0 && i < _order.size()) {
            _order.remove(i);
        }
    }

    public void deleteFood (Food f) {
        for (int i = 0; i < _order.size(); i++) {
            if (_order.get(i).equals(f)) {
                _order.remove(i);
                return;
            }
        }
    }

    public int price () {
        int sum = 0;
        for (int i = 0; i < _order.size(); i++) {
            sum += _order.get(i).price();
        }
        return sum;
    }

    void clear () {
        _order.clear();
    }
}

