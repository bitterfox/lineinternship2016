/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem1;

import bitter_fox.line.internship.base.JsoupConnector;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 *
 * @author bitter_fox
 */
class WazaSearcher implements NewWordSearcher {
    private static final String URL = "http://senki.kusakage.com/";

    private List<Predicate<String>> urlFilterList = new ArrayList<>();
    private List<Consumer<String>> onNextUrlList = new ArrayList<>();
    private List<Consumer<String>> onEndOfPageList = new ArrayList<>();

    private JsoupConnector connector;

    public WazaSearcher(JsoupConnector connector) {
        this.connector = connector;
    }

    void filterUrl(Predicate<String> filter) {
        urlFilterList.add(filter);
    }
    void onNextUrl(Consumer<String> onNext) {
        onNextUrlList.add(onNext);
    }
    void onEndOfPage(Consumer<String> onEnd) {
        onEndOfPageList.add(onEnd);
    }

    @Override
    public Stream<String> newWordCandidates() {
        Iterator<String> pages = new Iterator<String>() { // hack for onNextUrl, onEndOfPage
            Iterator<String> pages = pages().iterator();
            String page;

            @Override
            public boolean hasNext() {
                if (!pages.hasNext() && page != null) {
                    onEndOfPageList.forEach(c -> c.accept(page)); // 最後の時だけnextではやらない
                    page = null;
                }
                return pages.hasNext();
            }

            @Override
            public String next() {
                if (page != null) {
                    onEndOfPageList.forEach(c -> c.accept(page));
                }

                page = pages.next();
                onNextUrlList.forEach(c -> c.accept(page));

                return page;
            }
        };

        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(pages, 0), false)
                .flatMap(this::loadPage)
                .filter(s -> !s.isEmpty());
    }

    private Stream<String> pages() {
        try {
            Stream<String> pages = Jsoup.connect(URL + "index.htm").get()
                    .getElementById("click1")
                    .getElementsByTag("a").stream()
                    .map(e -> URL + e.attr("href"));


            return pages.filter(urlFilterList.stream()
                    .reduce(s -> true, Predicate::and));
        } catch (IOException ex) {
            Logger.getLogger(WazaSearcher.class.getName()).log(Level.SEVERE, null, ex);
            throw new UncheckedIOException(ex);
        }
    }

    private Stream<String> loadPage(String url) {
        Element gameTable = connector.connect(url).getElementsByTag("table").get(1);
        List<Element> gameRecords = gameTable.getElementsByTag("tr");

        return gameRecords.stream()
                .flatMap(r -> r.getElementsByTag("td").stream())
                .flatMap(e -> e.getElementsByTag("a").stream())
                .map(e -> e.text())
                .map(e -> toWrodableString(e));
    }

    private String toWrodableString(String s) {
        return String.join("", s.replaceAll("\\s", "")
                .replaceAll("　", "")
                .split("(\\(.+?\\))|(\\[.+?\\])|(\\（.+?\\）)"));
    }
}
