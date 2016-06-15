/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.base;

import java.util.List;
import java.util.stream.Stream;
import org.jsoup.nodes.Document;

/**
 *
 * @author bitter_fox
 */
public interface SearchEngine {
    ResultSet search(String keyWord);
    Stream<Result> searchAll(String keyWord);

    public class Result {
        private String href;
        private String title;
        private String description;

        public Result(String href, String title, String description) {
            this.href = href;
            this.title = title;
            this.description = description;
        }

        public String href() {
            return href;
        }
        public String title() {
            return title;
        }
        public String description() {
            return description;
        }
    }

    public interface ResultSet {
        int startNumber();
        List<Result> results();
        ResultSet nextSet();
    }

    static SearchEngine poweredByYahoo() {
        return new YahooSearchEngine();
    }
}
