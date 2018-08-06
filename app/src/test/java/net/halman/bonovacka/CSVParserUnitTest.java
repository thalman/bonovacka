package net.halman.bonovacka;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class CSVParserUnitTest {
    @Test
    public void ParseSimpleLine() {
        ArrayList<String> alist = CSVParser.parse("a,b,c", ',');
        assertEquals(3, alist.size());
        assertEquals("c", alist.get(2));
    }

    @Test
    public void ParseLineWithStrings() {
        ArrayList<String> alist = CSVParser.parse("a,\"b b\",\"c, c\"", ',');
        assertEquals(3, alist.size());
        assertEquals("b b", alist.get(1));
        assertEquals("c, c", alist.get(2));
    }

    @Test
    public void ParseLineWithDoubleQuote() {
        ArrayList<String> alist = CSVParser.parse("a,\"b,\"\",b,\"\"\",\"c\"\"\"", ',');
        assertEquals(3, alist.size());
        assertEquals("b,\",b,\"", alist.get(1));
        assertEquals("c\"", alist.get(2));
    }

}
