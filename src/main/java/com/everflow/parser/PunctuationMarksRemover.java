package com.everflow.parser;

import com.everflow.sentence.Word;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Ildar Gafarov on 14.05.16.
 */
@Service
public class PunctuationMarksRemover {

    public List<Word> find(List<Word> words) {
        List<Word> result = new LinkedList<>();
        for(Word word: words) {
            result.add(new Word(word.getSource().replaceAll("[^a-zA-Z']", "")));
        }
        return result;
    }

}
