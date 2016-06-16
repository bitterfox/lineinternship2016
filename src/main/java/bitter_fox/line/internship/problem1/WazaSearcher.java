/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem1;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.jsoup.Jsoup;

/**
 *
 * @author bitter_fox
 */
class WazaSearcher implements NewWordSearcher {
    private static final String URL = "http://senki.kusakage.com/";

    private List<Predicate<String>> urlFilterList = new ArrayList<>();
    private List<Consumer<String>> onNextUrlList = new ArrayList<>();
    private List<Consumer<String>> onEndOfPageList = new ArrayList<>();

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Stream<String> pages() {
        try {
            Stream<String> pages = Jsoup.connect(URL + "index.htm").get()
                    .getElementById("click1")
                    .getElementsByTag("a").stream()
                    .map(e -> e.attr("href"));

            urlFilterList.forEach(pages::filter);

            return pages;
        } catch (IOException ex) {
            Logger.getLogger(WazaSearcher.class.getName()).log(Level.SEVERE, null, ex);
            throw new UncheckedIOException(ex);
        }
    }
}
