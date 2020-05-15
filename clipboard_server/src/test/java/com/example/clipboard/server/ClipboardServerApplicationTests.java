package com.example.clipboard.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.StreamSupport;

@SpringBootTest
class ClipboardServerApplicationTests {
    @Test
    void contextLoads() {

        SortedMap<Object, Object> map = new TreeMap<>();
        map.put(1, new Simple("a"));
        map.put(3, new Simple("c"));
        map.put(2, new Simple("b"));
        Simple d = new Simple("d");
        map.put(4, d);
        d.text = "e";
        for(Object simple : map.keySet()) {
            System.out.println(map.get(simple));
        };
    }

}

class Simple implements Comparable<Simple> {
    public String text;

    public Simple(String text) {
        this.text = text;
    }

    @Override
    public int compareTo(Simple o) {
        return text.compareTo(o.text);
    }

    @Override
    public String toString() {
        return text;
    }
}