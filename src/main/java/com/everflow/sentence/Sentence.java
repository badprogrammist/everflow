package com.everflow.sentence;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ildar Gafarov on 13.05.16.
 */
public class Sentence extends Composite<Phrase, Sentence> {

    List<Equivalent> equivalents = new ArrayList<>();

    public Sentence() {
    }

    public Sentence(List<Phrase> phrases) {
        super(phrases);
    }

    @Override
    public Sentence clone() {
        Sentence clone = new Sentence();
        for(Phrase phrase: getParts()) {
            clone.add(phrase.clone());
        }
        return clone;
    }
}
