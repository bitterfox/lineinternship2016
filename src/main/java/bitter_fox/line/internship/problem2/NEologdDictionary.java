/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author bitter_fox
 */
public class NEologdDictionary {
    private Set<String> dict;

    NEologdDictionary() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResource("neologd-words.20160613.txt").openStream()));
                Stream<String> lines = br.lines()
                ) {
            dict = lines.collect(Collectors.toSet());
        }
    }

    Set<String> copyAsSet() {
        return new HashSet<>(dict);
    }

    List<String> copyAsList() {
        return new ArrayList<>(dict);
    }
}
