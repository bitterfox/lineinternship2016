/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem1;

import bitter_fox.line.internship.base.Util;
import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author bitter_fox
 */
public class NewWordRecorder implements AutoCloseable {
    private Tokenizer tokenizer;
    private PrintWriter writer;
    private Set<String> set = new HashSet<>();

    public NewWordRecorder(Tokenizer tokenizer, String path) throws IOException {
        this.tokenizer = tokenizer;
        this.set = Util.computeAndClose(Files.lines(Paths.get(path)), s -> s
                .map(l -> l.split(",")[0])
                .collect(Collectors.toSet()));
        this.writer = new PrintWriter(new FileWriter(path, true));
    }

    public boolean record(String newWord) {
        if (set.contains(newWord)) {
            return false;
        }

        set.add(newWord);

        String[] yomiganas = yomiganas(newWord);

        String row = Stream.concat(Stream.of(newWord), Arrays.stream(yomiganas))
                .collect(Collectors.joining(", "));
        writer.println(row);

        return true;
    }

    private String[] yomiganas(String newWord) {
        List<Token> tokens = tokenizer.tokenize(newWord);
        return new String[] {
            tokens.stream().map(Token::getPronunciation).collect(Collectors.joining()),
            tokens.stream().map(Token::getReading).collect(Collectors.joining())
        };
    }

    int recordedCount() {
        return set.size();
    }

    void flush() {
        writer.flush();
    }

    @Override
    public void close() {
        writer.close();
    }
}
