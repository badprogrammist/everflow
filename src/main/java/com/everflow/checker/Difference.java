package com.everflow.checker;


import com.everflow.sentence.Phrase;

/**
 * @author Ildar Gafarov
 */
public class Difference {

    private Phrase right;
    private Phrase entered;

    private DifferenceType type;

    public Difference(Phrase right, Phrase entered, DifferenceType type) {
        this.right = right;
        this.entered = entered;
        this.type = type;
    }

    public Phrase getRight() {
        return right;
    }

    public Phrase getEntered() {
        return entered;
    }

    public DifferenceType getType() {
        return type;
    }
}
