package com.everflow.parser;


import com.everflow.sentence.Phrase;
import com.everflow.sentence.Sentence;
import com.everflow.sentence.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ildar Gafarov on 13.05.16.
 */
@Service
public class Parser {

    @Autowired
    CommaSplitter commaSplitter;

    @Autowired
    PunctuationMarksRemover punctuationMarksRemover;

    public Sentence parseSentence(String text) {
        List<Word> words = commaSplitter.split(text);
        words = punctuationMarksRemover.find(words);
        List<Phrase> phrases = new LinkedList<>();
        for(Word word : words) {
            phrases.add(new Phrase(Collections.singletonList(word)));
        }
        Sentence sentence = new Sentence(phrases);
        return sentence;
    }

}
