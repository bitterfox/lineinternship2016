/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem1;

import bitter_fox.line.internship.base.SearchEngine;
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
public class MainSpysee {
    private static final int TARGET_COUNT_OF_NEW_WORDS = 100_000;

    private static final String PATH_TO_PROCESSED_NAME = "processedNames.txt";

    public static void main(String[] args) throws IOException, InterruptedException {
//        System.err.println(Arrays.toString());
        Tokenizer tizer = new Tokenizer.Builder().build();

        NewWordFilter isNewWord = new NewWordFilter();


        try (NewWordRecorder nwr = new NewWordRecorder(tizer, "newWordsFromSpysee.tsv");
                PrintWriter namesWriter = new PrintWriter(new FileWriter(PATH_TO_PROCESSED_NAME, true))) {
            List<String> processedNamed = Files.readAllLines(Paths.get(PATH_TO_PROCESSED_NAME));
            Random r = new Random();

            JapaneseFamilyNameProvider jfnp = JapaneseFamilyNameProvider.fromRanking();

            SearchEngine se = SearchEngine.poweredByYahoo();

            int currentCount = nwr.recordedCount();
            while (currentCount < TARGET_COUNT_OF_NEW_WORDS) {
                String firstName = jfnp.next();
                System.out.println(firstName);

                if (processedNamed.contains(firstName)) {
                    System.out.println(firstName + " is already processed. Skipping...");
                    continue;
                }

                if (firstName == null) break;

                new SpyseeSearcher(se, firstName).newWordCandidates()
                        .filter(isNewWord)
//                                .peek(System.out::println)
                        .forEach(w -> {
                            if (!nwr.record(w)) {
                                System.out.println(w + " is already recorded");
                            }
                        });
                nwr.flush();

                currentCount = nwr.recordedCount();
                System.out.println(currentCount);

                namesWriter.println(firstName);
                namesWriter.flush();

                Thread.sleep(10000 + r.nextInt(30000));
            }
            System.out.println(nwr.recordedCount() + " new words are added");
        }
    }
}
