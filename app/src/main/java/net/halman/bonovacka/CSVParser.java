package net.halman.bonovacka;

import java.util.ArrayList;

public class CSVParser {
    public static ArrayList<String> parse(String line, char separator) {
        ArrayList<String> result = new ArrayList<String>();
        char [] letters = line.toCharArray();
        int idx = 0;
        while (idx < letters.length) {
            String word = "";
            if (letters[idx] == '"') {
                idx++;
                while (idx < letters.length) {
                    if (letters[idx] == '"' && idx + 1 == letters.length) {
                        idx++;
                        break;
                    } else if (letters[idx] == '"' && letters[idx + 1] == separator) {
                        idx += 2;
                        break;
                    } else if (letters[idx] == '"' && letters[idx + 1] == '"') {
                        word = word + '"';
                        idx += 2;
                    } else {
                        word = word + letters [idx];
                        idx++;
                    }
                }
            } else {
                while (idx < letters.length) {
                    if (letters [idx] == separator) {
                        idx++;
                        break;
                    } else {
                        word = word + letters [idx];
                        idx++;
                    }
                }
            }
            result.add(word);
        }
        return result;
    }
}
