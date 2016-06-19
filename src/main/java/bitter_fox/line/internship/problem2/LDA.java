/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem2;

import java.util.Random;

/**
 *
 * @author bitter_fox
 */
public class LDA {

    int D; // number of document
    int K; // number of topic
    int W; // number of unique word
    int wordCount[][];
    int docCount[][];
    int topicCount[];
    // hyper parameter
    double alpha, beta;

    WordRelatedDocuments docs;
    LDAToken tokens[];
    double P[];
    // topic assignment
    int z[];
    Random rand;

    public LDA(WordRelatedDocuments docs, int topicNum, Random rand) {
        this(docs, topicNum, 50.0 / topicNum, 0.1, rand);
    }

    public LDA(WordRelatedDocuments docs, int topicNum, double alpha, double beta, Random rand) {
        int wordNum = docs.wordCount();
        int documentNum = docs.documentCount();
        wordCount = new int[wordNum][topicNum];
        topicCount = new int[topicNum];
        docCount = new int[documentNum][topicNum];
        D = documentNum;
        K = topicNum;
        W = wordNum;
        tokens = docs.tokens().toArray(new LDAToken[0]);
        z = new int[tokens.length];
        this.alpha = alpha;
        this.beta = beta;
        P = new double[K];
        this.rand = rand;
        init();
    }

    private void init() {
        for (int i = 0; i < z.length; ++i) {
            LDAToken t = tokens[i];
            int assign = rand.nextInt(K);
            wordCount[t.wordId][assign]++;
            docCount[t.docId][assign]++;
            topicCount[assign]++;
            z[i] = assign;
        }
    }

    private int selectNextTopic(LDAToken t) {
        for (int k = 0; k < P.length; ++k) {
            P[k] = (wordCount[t.wordId][k] + beta) * (docCount[t.docId][k] + alpha)
                    / (topicCount[k] + W * beta);
            if (k != 0) {
                P[k] += P[k - 1];
            }
        }
        double u = rand.nextDouble() * P[K - 1];
        for (int k = 0; k < P.length; ++k) {
            if (u < P[k]) {
                return k;
            }
        }
        return K - 1;
    }

    private void resample(int tokenId) {
        LDAToken t = tokens[tokenId];
        int assign = z[tokenId];
        // remove from current topic
        wordCount[t.wordId][assign]--;
        docCount[t.docId][assign]--;
        topicCount[assign]--;
        assign = selectNextTopic(t);
        wordCount[t.wordId][assign]++;
        docCount[t.docId][assign]++;
        topicCount[assign]++;
        z[tokenId] = assign;
    }

    public void update() {
        for (int i = 0; i < z.length; ++i) {
            resample(i);
        }
    }

    public double[][] getTheta() {
        double theta[][] = new double[D][K];
        for (int i = 0; i < D; ++i) {
            double sum = 0.0;
            for (int j = 0; j < K; ++j) {
                theta[i][j] = alpha + docCount[i][j];
                sum += theta[i][j];
            }
            // normalize
            double sinv = 1.0 / sum;
            for (int j = 0; j < K; ++j) {
                theta[i][j] *= sinv;
            }
        }
        return theta;
    }

    public double[][] getPhi() {
        double phi[][] = new double[K][W];
        for (int i = 0; i < K; ++i) {
            double sum = 0.0;
            for (int j = 0; j < W; ++j) {
                phi[i][j] = beta + wordCount[j][i];
                sum += phi[i][j];
            }
            // normalize
            double sinv = 1.0 / sum;
            for (int j = 0; j < W; ++j) {
                phi[i][j] *= sinv;
            }
        }
        return phi;
    }
}
