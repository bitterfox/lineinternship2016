/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem1;

import com.atilika.kuromoji.ipadic.Tokenizer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author bitter_fox
 */
class NewWordFilter implements Predicate<String> {
    private Set<String> dict;

    NewWordFilter() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResource("mecab-user-dict-seed.20160613.csv").openStream()));
                Stream<String> lines = br.lines()
                ) {
            dict = lines.map(l -> l.split(",")[0])
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public boolean test(String t) {
        return !dict.contains(t);
    }
}
