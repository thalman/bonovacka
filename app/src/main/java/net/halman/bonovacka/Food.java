package net.halman.bonovacka;

import java.io.Serializable;

public class Food implements Serializable {
    private int _price = 0;
    private String _name = "";
    private String _group = "";

    public Food (String aName, int aPrice, String group) {
        _name = aName;
        _price = aPrice;
        _group = group;
    }

    // TODO: delete this constructor
    public Food (String aName, int aPrice) {
        _name = aName;
        _price = aPrice;
        _group = "Hotovky";
    }

    public Food (Food another) {
        this._name = another._name;
        this._price = another._price;
        this._group = another._group;
    }

    public int price () { return _price; }
    public void price (int p) { _price = p; }

    public String name () { return _name; }
    public void name (String aName) { _name = aName; }

    public String group () { return _group; }
    public void group (String group) { _group = group; }

    public boolean equals (Food other) {
        return  this._name.equals(other._name) && (this._price == other._price);
    }
}
