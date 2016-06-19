/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem2;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 *
 * @author bitter_fox
 */
class WordRelatedDocuments {
    private List<String> documents; // Raw data


    private String word;

    //  Word    Id
    private Map<String, Integer> words = new HashMap();

    private List<LDAToken> ldaTokens = new ArrayList<>();

    WordRelatedDocuments(Tokenizer tokenizer, List<String> documents, String word) {
        // TODO
        this.documents = documents;
        this.word = word;

        tokenize(tokenizer);
    }

    private void tokenize(Tokenizer tokenizer) {
        String[] docs = documents.toArray(new String[documents.size()]);

        for (int did = 0; did < docs.length; did++) {
            for (Token token : tokenizer.tokenize(docs[did])) {
                if (token.getSurface().equals(word)) {
                    continue;
                }

                String[] features = token.getAllFeaturesArray();
                if (!features[0].equals("名詞")) {
                    continue;
                }
                if (features[1].contains("数")) {
                    continue;
                }
                if (features[1].equals("接尾")) {
                    continue;
                }

                int size = words.size();
                int wid = words.computeIfAbsent(token.getSurface(), w -> size);
                ldaTokens.add(new LDAToken(did, wid));
            }
        }
    }

    int documentCount() {
        return documents.size();
    }

    int wordCount() {
        return words.size();
    }

    List<LDAToken> tokens() {
        return ldaTokens;
    }

    String word(int id) {
        return words.entrySet().stream()
                .filter(e -> e.getValue().equals(id))
                .map(e -> e.getKey())
                .findFirst().orElseThrow(() -> new NoSuchElementException(""+id));
    }

    String originalWord() {
        return word;
    }
}
