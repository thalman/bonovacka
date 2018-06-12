package net.halman.bonovacka;

import java.io.Serializable;
import java.util.ArrayList;

public class BonBook implements Serializable {
    private ArrayList<Bon> _book = new ArrayList<Bon>();
    private int _cursor = 0;
    private int _start = 1;


    public void startingIndex (int index) {
        _start = index;
        for (int i = 0; i < _book.size(); i++) {
            _book.get(i).set_index (_start + i);
        }
    }

    int startingIndex () {
        return _start;
    }

    int lastIndex () {
        return _start + _book.size() - 1;
    }

    int size () { return _book.size(); }

    void append () {
        Bon b = new Bon(_start + size());
        _book.add(b);
    }

    int price () {
        int result = 0;
        for (int i = 0; i < _book.size(); i++) {
            result += _book.get(i).price();
        }
        return result;
    }

    void addFood (Food f) {
        while (_cursor >= _book.size() ) {
            append();
        }
        _book.get(_cursor).addFood(f);
    }

    Bon get() {
        while (_cursor >= _book.size() ) {
            append();
        }
        return _book.get(_cursor);
    }

    Bon get(int i) {
        _cursor = i;
        return get();
    }

    void removeZeros () {
        Bon current = get();
        for (int i = current.size() - 1; i >= 0; i--) {
            if (current.foodCount(i) == 0) {
                current.removeFood(i);
            }
        }
    }

    Bon next() {
        removeZeros();
        _cursor++;
        return get();
    }

    Bon prev() {
        if (_cursor > 0) {
            removeZeros();
            _cursor--;
        }
        shrink();
        return get();
    }

    void removeFromBook (Food f) {
        for (int i = 0; i < _book.size(); i++) {
            _book.get(i).deleteFood(f);
        }
    }

    void shrink () {
        int i = _book.size() - 1;
        while (i > _cursor) {
            if (_book.get(i).price() != 0) {
                return;
            } else {
                _book.remove(i);
            }
            i--;
        }
    }

    void clear() {
        _book.clear();
        _cursor = 0;
    }
}
