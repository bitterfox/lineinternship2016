/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.base;

import java.util.Random;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author bitter_fox
 */
public class JsoupConnector {
    private Random r = new Random();

    public Document connect(String url) {
        return connect(url, 2000 + r.nextInt(2000));
    }

    public Document connectNoWait(String url) {
        return connect(url, 0);
    }

    private Document connect(String url, int firstWait) {
        while (true) {
            int sleepMillis = firstWait;
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
}
