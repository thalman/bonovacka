package net.halman.bonovacka;

import java.io.Serializable;

public class OrderItem implements Serializable {
    int _count;
    Food _food;

    public OrderItem(Food f) {
        _count = 1;
        _food = f;
    }

    public Food food() {
        return _food;
    }
    public int count() {
        return _count;
    }
    public void inc() {
        _count++;
    }
    public void dec() {
        if (_count > 0) _count--;
    }
    public int price () {
        return _count * _food.price();
    }
}
