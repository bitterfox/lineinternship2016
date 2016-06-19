/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bitter_fox.line.internship.problem2;

/**
 *
 * @author bitter_fox
 */
class LDAToken {
    public int docId;
    public int wordId;

    public LDAToken(int did, int wid){
        docId = did;
        wordId = wid;
    }
}
