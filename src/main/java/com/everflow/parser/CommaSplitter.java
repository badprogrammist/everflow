package com.everflow.parser;

import com.everflow.sentence.Word;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Ildar Gafarov
 */
@Service
public class CommaSplitter {

    public List<Word> split(String text) {
        List<Word> words = new LinkedList<>();
        String[] splitted = text.split(" ");
        for(String part : splitted) {
            words.add(new Word(part));
        }
        return words;
    }

}
