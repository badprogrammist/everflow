package com.everflow.sentence;

/**
 * @author Ildar Gafarov
 */
public class Word extends Part<Word> {

    private String source;

    public Word(String source) {
        this.source = source;
    }

    @Override
    public Word clone() {
        return new Word(source);
    }

    public String getSource() {
        return source;
    }

}
