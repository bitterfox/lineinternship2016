/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem1;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 *
 * @author bitter_fox
 */
public class Main {
    public static void main(String[] args) throws IOException {
//        System.err.println(Arrays.toString(new SpyseeSearcher(SearchEngine.poweredByYahoo(), "吉田").newWordCandidates()));
        JapaneseAnalyzer ja = new JapaneseAnalyzer();
        TokenStream ts = ja.tokenStream("", "安倍晋三");
        CharTermAttribute cta = ts.addAttribute(CharTermAttribute.class);
        ts.reset();
        while (ts.incrementToken()) {
            String token = cta.toString();
            System.out.println(token);
        }
        ts.close();
    }
}
