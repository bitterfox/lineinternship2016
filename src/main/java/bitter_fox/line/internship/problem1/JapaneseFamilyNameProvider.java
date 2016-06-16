/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem1;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

/**
 *
 * @author bitter_fox
 */
public interface JapaneseFamilyNameProvider {
    String next();

    static JapaneseFamilyNameProvider fromRanking() throws IOException { // ThreadSafeに！
        class JapaneseFamilyNameFromRanking implements JapaneseFamilyNameProvider {
            private AtomicInteger index = new AtomicInteger(1);
            
            private Elements elements;
            private String next;

            JapaneseFamilyNameFromRanking() throws IOException {
                this.elements = Jsoup.connect("http://www2s.biglobe.ne.jp/~suzakihp/ju0001.html").get()
                        .getElementsByTag("tr");
            }

            @Override
            public String next() {
                int i = index.getAndIncrement();
                String iStr = String.valueOf(i);
                return elements.stream()
                        .map(tr -> tr.getElementsByTag("td"))
                        .filter(tds -> tds.size() >= 2 && tds.get(0).text().equals(iStr))
                        .map(tds -> tds.get(1).text())
                        .findAny()
                        .orElse(null);
            }
        }

        return new JapaneseFamilyNameFromRanking();
    }
}
