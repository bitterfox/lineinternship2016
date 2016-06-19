/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem2;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author bitter_fox
 */
class WordRelatedResultWriter implements AutoCloseable {
    private Path outputPath;

    private PrintWriter relatedWordsWriter;

    WordRelatedResultWriter(Path outputPath) throws IOException {
        Files.createDirectories(outputPath);
        this.outputPath = outputPath;
        this.relatedWordsWriter = new PrintWriter(Files.newBufferedWriter(outputPath.resolve("relatedWords.tsv"), StandardOpenOption.APPEND));
    }

    void writeResult(WordRelatedDocuments wrd, LDA learnedLda) throws IOException {
        Path path = outputPath.resolve(wrd.originalWord());
        Files.createDirectories(path);

        double phi[][] = learnedLda.getPhi();
        double theta[][] = learnedLda.getTheta();

        try (PrintWriter pwWordTopic = new PrintWriter(Files.newBufferedWriter(path.resolve("wordTopic.txt")));
                PrintWriter pwDocTopic = new PrintWriter(Files.newBufferedWriter(path.resolve("docTopic.txt")))
                ) {
            outputWordTopicProb(phi, wrd, pwWordTopic);
            outputDocTopicProb(theta, wrd, pwDocTopic);
        }

        int K = phi.length;
        int W = phi[0].length;
        int D = theta.length;

        double zeta[] = new double[wrd.wordCount()];
        for (int d = 0; d < D; d++) {
            for (int w = 0; w < W; w++) {
                for (int k = 0; k < K; k++) {
                    zeta[w] += phi[k][w] * theta[d][k];
                }
            }
        }

        PComp ps[] = new PComp[W];
        for (int w = 0; w < W; w++) {
            PComp pc = new PComp();
            pc.id = w;
            pc.prob = zeta[w];
            ps[w] = pc;
        }
        Arrays.sort(ps, Comparator.reverseOrder());

        synchronized (this) {
            try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path.resolve("relatedWords.txt")))) {
                String line = Stream.concat(Stream.of(wrd.originalWord()),
                        Arrays.stream(ps).limit(100)
                                .peek(p -> out.println(wrd.word(p.id) + " " + p.prob))
                                .flatMap(p -> Stream.of(wrd.word(p.id), ""+p.prob)))
                                .collect(Collectors.joining("\t"));
                relatedWordsWriter.println(line);
            }
        }
    }

    @Override
    public void close() {
        relatedWordsWriter.close();
    }

    public void flush() {
        relatedWordsWriter.flush();
    }

    static class PComp implements Comparable<PComp> {
        int id;
        double prob;

        @Override
        public int compareTo(PComp o) {
            return Double.compare(prob, o.prob);
        }
    }

    private static void outputWordTopicProb(double phi[][], WordRelatedDocuments wrd, PrintWriter out) {
        int K = phi.length;
        int W = phi[0].length;
        for (int k = 0; k < K; ++k) {
            out.println("topic : " + k);
            PComp ps[] = new PComp[W];
            for (int w = 0; w < W; ++w) {
                PComp pc = new PComp();
                pc.id = w;
                pc.prob = phi[k][w];
                ps[w] = pc;
            }
            Arrays.sort(ps);
            for (int i = 0; i < Integer.min(10, ps.length); ++i) {
                // output related word
                PComp p = ps[W - 1 - i];
                out.println(wrd.word(p.id) + " " + p.prob);
            }
        }
    }
    private static void outputDocTopicProb(double theta[][], WordRelatedDocuments wrd, PrintWriter out) {
        int D = theta.length;
        int K = theta[0].length;
        for (int d = 0; d < D; ++d) {
            out.println("doc : " + d);
            PComp ps[] = new PComp[K];
            for (int k = 0; k < K; ++k) {
                PComp pc = new PComp();
                pc.id = k;
                pc.prob = theta[d][k];
                ps[k] = pc;
            }
            Arrays.sort(ps);
            for (int i = 0; i < Integer.min(10, ps.length); ++i) {
                // output related word
                PComp p = ps[K - 1 - i];
                out.println(p.id + " " + p.prob);
            }
        }
    }
}
