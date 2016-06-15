/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem1;

import bitter_fox.line.internship.base.SearchEngine;
import java.util.Iterator;
import java.util.Random;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author bitter_fox
 */
public class SpyseeSearcher implements NewWordSearcher {
    private SearchEngine engine;
    private String key;

    public SpyseeSearcher(SearchEngine engine, String key) {
        this.engine = engine;
        this.key = key;
    }

    @Override
    public String[] newWordCandidates() {
        return engine.searchAll(key)
                .map(SearchEngine.Result::title)
                .filter(s -> s.contains("プロフィール"))
                .map(s -> s.split(" ")[0])
                .peek(System.out::println)
                .toArray(String[]::new);
    }
}
