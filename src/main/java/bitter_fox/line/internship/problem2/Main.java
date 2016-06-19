/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem2;

import bitter_fox.line.internship.base.JsoupConnector;
import bitter_fox.line.internship.base.SearchEngine;
import com.atilika.kuromoji.ipadic.Tokenizer;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jsoup.nodes.Document;

/**
 *
 * @author bitter_fox
 */
public class Main {
    private static final int TARGET_COUNT_OF_META_INFOS = 10_000;

    public static void main(String[] args) throws IOException, InterruptedException {
        Tokenizer tokenizer = new Tokenizer();
        JsoupConnector jc = new JsoupConnector();
        SearchEngine se = SearchEngine.poweredByYahoo();

        NEologdDictionary ned = new NEologdDictionary();
        List<String> words = ned.copyAsList();
        Collections.shuffle(words);

        LinkedBlockingQueue<String> wordQueue = new LinkedBlockingQueue(words);

        int coreNumber = Runtime.getRuntime().availableProcessors()*2;
        ExecutorService es = Executors.newFixedThreadPool(coreNumber);

        Path outputPath = Paths.get("output");

        try (WordRelatedResultWriter w = new WordRelatedResultWriter(outputPath)) {
            AtomicInteger index = new AtomicInteger(0);
            class Task implements  Callable<Void> {
                long threadId = -1;

                private void println(String s) {
                    System.out.println("[" + threadId + "]: " + s);
                }

                @Override
                public Void call() throws Exception {
                    Random rand = ThreadLocalRandom.current();
                    threadId = Thread.currentThread().getId();

                    while (index.getAndIncrement() < TARGET_COUNT_OF_META_INFOS && !wordQueue.isEmpty()) {
                        String word = wordQueue.poll();
                        if (word == null) break;

                        println("Finding meta data of " + word);

                        if (Files.exists(outputPath.resolve(word))) {
                            println(word + " is already processed. Skipping...");
                            continue;
                        }

                        long t = System.currentTimeMillis();

                        List<String> docs = se.searchAll(word)
                                .map(r -> jc.connectNoWait(r.href(), 0))
                                .flatMap(opt -> opt.map(Stream::of).orElseGet(Stream::empty))
                                .limit(50)
                                .map(Document::text)
                                .collect(Collectors.toList());
                        println("[" + (System.currentTimeMillis() - t) + "]: " + "Downloaded documents");
                        if(docs.isEmpty()) {
                            println("[" + (System.currentTimeMillis() - t) + "]: " + "No dada of " + word + " is found. Skipping LDA");
                            continue;
                        }

                        WordRelatedDocuments wrd = new WordRelatedDocuments(tokenizer, docs, word);
                        println("[" + (System.currentTimeMillis() - t) + "]: " + "Tokenized");

                        int K = 10;
                        LDA lda = new LDA(wrd, K, rand);
                        for (int i = 0; i < 200; i++) {
                            lda.update();
                        }
                        println("[" + (System.currentTimeMillis() - t) + "]: " + "Done LDA");

                        w.writeResult(wrd, lda);
                        w.flush();
                        println("[" + (System.currentTimeMillis() - t) + "]: " + "Wrote");
                    }

                    return null;
                }
            }

            List<Future<Void>> tasks = es.invokeAll(Stream.generate(Task::new).limit(coreNumber).collect(Collectors.toList()));
            tasks.forEach(f -> {
                try {
                    f.get();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }
}
