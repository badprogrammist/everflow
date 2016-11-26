package com.everflow.sentence;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ildar Gafarov on 13.05.16.
 */
public class Phrase extends Composite<Word, Phrase> {

    private List<Phrase> equivalents = new ArrayList<>();

    public Phrase() {
    }

    public Phrase(List<Word> parts) {
        super(parts);
    }

    @Override
    public Phrase clone() {
        Phrase clone = new Phrase();
        for(Word word: getParts()) {
            clone.add(word.clone());
        }
        return clone;
    }

//    @Override
//    public boolean equals(Object o) {
//        if(!super.equals(o)) {
//            for(Phrase equivalent : equivalents) {
//                if(equivalent.equals(o)) {
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
}
