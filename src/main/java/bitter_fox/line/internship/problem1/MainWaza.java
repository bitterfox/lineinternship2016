/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem1;

import bitter_fox.line.internship.base.JsoupConnector;
import com.atilika.kuromoji.ipadic.Tokenizer;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

/**
 *
 * @author bitter_fox
 */
public class MainWaza {
    private static final String PATH_TO_PROCESSED_WAZA_URL = "processedWazaUrl.txt";

    public static void main(String[] args) throws IOException, InterruptedException {
//        System.err.println(Arrays.toString());
        Tokenizer tizer = new Tokenizer.Builder().build();

        NewWordFilter isNewWord = new NewWordFilter();

        try (NewWordRecorder nwr = new NewWordRecorder(tizer, "newWordsFromWazaJiten.tsv");
                PrintWriter namesWriter = new PrintWriter(new FileWriter(PATH_TO_PROCESSED_WAZA_URL, true))) {
            List<String> processedUrls = Files.readAllLines(Paths.get(PATH_TO_PROCESSED_WAZA_URL));

            Random r = new Random();

            int firstCount = nwr.recordedCount();

            WazaSearcher wazaSearcher = new WazaSearcher(new JsoupConnector());
            wazaSearcher.filterUrl(url -> !processedUrls.contains(url));
            wazaSearcher.onNextUrl(url -> {
                System.out.println(nwr.recordedCount());
                System.out.println(url);
            });
            wazaSearcher.onEndOfPage(url -> {
                System.out.println(url + " has been done.");
                nwr.flush();
                namesWriter.println(url);
                namesWriter.flush();
            });

            wazaSearcher.newWordCandidates()
                    .filter(isNewWord)
                    .peek(System.out::println)
                    .forEach(w -> {
                        if (!nwr.record(w)) {
                            System.out.println(w + " is already recorded");
                        }
                    });
            System.out.println((nwr.recordedCount() - firstCount) + " new words are added");
        }
    }

}
