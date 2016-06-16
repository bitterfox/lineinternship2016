/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.base;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author bitter_fox
 */
class YahooSearchEngine implements SearchEngine {
    private Random r = new Random();

    private class YahooResultSet implements ResultSet {
        private String keyWord;
        private int startNumber;
        private List<Result> results;

        public YahooResultSet(String keyWord, int startNumber, List<Result> results) {
            this.keyWord = keyWord;
            this.startNumber = startNumber;
            this.results = results;
        }

        @Override
        public int startNumber() {
            return startNumber;
        }

        @Override
        public List<Result> results() {
            return results;
        }

        @Override
        public ResultSet nextSet() {
            return search(keyWord, startNumber + results.size());
        }
    }

    @Override
    public ResultSet search(String keyWord) {
        return search(keyWord, 1);
    }

    private ResultSet search(String keyWord, int start) {
        Document d = connect(url(keyWord, start));
        if (d.getElementById("noRes") != null) {
            return null;
        }

        /*
         * <div id="res"><div id="web">
         * <li><a href=url>title</a><div>desc</div></li>
         * </div></div>
         */
        List<Result> results = d.getElementById("res").select("li").stream()
                .map(li -> new Result(li.select("a").attr("href"), li.select("a").text(), li.select("div").text()))
                .collect(Collectors.toList());
        return new YahooResultSet(keyWord, start, results);
    }

    private String url(String keyWord, int start) {
        return "http://search.yahoo.co.jp/search?p=site%3Aspysee.jp+"+
                                keyWord+"&search.x=1&tid=top_ga1_sa&ei=UTF-8&dups=1&fr=top_ga1_sa&b="+start;
    }

    private Document connect(String url) {
        while (true) {
            int sleepMillis = 2000 + r.nextInt(2000);
            try {
                Thread.sleep(sleepMillis); // To avoid 高頻度アクセス
                return Jsoup.connect(url)
//                                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/" + r.nextInt() + " Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get();
            } catch (Exception ex) {
                ex.printStackTrace();
                sleepMillis += r.nextInt(3000);
                System.out.println("Sleeping while " + sleepMillis + "[ms]");
            }
        }
    }

    @Override
    public Stream<Result> searchAll(String keyWord) {
        Iterator<Result> resultIterator = new Iterator<Result>() {
            private ResultSet currentResult = search(keyWord);
            private Iterator<Result> currentResultIterator =
                    currentResult != null ? currentResult.results().iterator() : null;

            @Override
            public boolean hasNext() {
                if (currentResultIterator == null) {
                    return false;
                }

                if (currentResultIterator.hasNext()) {
                    return true;
                }

                currentResult = currentResult.nextSet();
                currentResultIterator = currentResult != null ? currentResult.results().iterator() : null;

                return currentResultIterator != null && currentResultIterator.hasNext();
            }

            @Override
            public Result next() {
                return currentResultIterator.next();
            }
        };

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(resultIterator, 0), false);
    }

}
