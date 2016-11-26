package com.everflow.checker;

import com.everflow.sentence.Phrase;
import com.everflow.sentence.Sentence;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Ildar Gafarov on 13.05.16.
 */
@Service
public class DifferenceFinder {


    public List<Difference> find(Sentence right, Sentence entered) {
        List<Difference> differences = new ArrayList<>();

        Phrase[] rightPhrases = right.getParts().toArray(new Phrase[right.getParts().size()]);
        Phrase[] enteredPhrases = entered.getParts().toArray(new Phrase[entered.getParts().size()]);

        LinkedList<Equality> equalitiesList = new LinkedList<>();
        int lastEnteredIndex = 0;//не ищем слова которые стоят раньше enteredSeek
        for (int rightSeek = 0; rightSeek < rightPhrases.length; rightSeek++) {
            for (int enteredSeek = 0; enteredSeek < enteredPhrases.length; enteredSeek++) {
                // не добавляем ранее добавленные слова
                boolean used = false;
                for (Equality equality : equalitiesList) {
                    if (equality.rightPhrasePosition == rightSeek || equality.enteredPhrasePosition == enteredSeek) {
                        used = true;
                        break;
                    }
                }
                if (rightPhrases[rightSeek].equals(enteredPhrases[enteredSeek]) && !used && lastEnteredIndex <= enteredSeek) {
                    Equality equality = new Equality();
                    equality.enteredPhrasePosition = enteredSeek;
                    equality.rightPhrasePosition = rightSeek;
                    lastEnteredIndex = enteredSeek;
                    equalitiesList.add(equality);
                    break;
                }
            }
        }

        Equality[] equalities = equalitiesList.toArray(new Equality[equalitiesList.size()]);
        for (int equalitySeek = 0; equalitySeek < equalities.length; equalitySeek++) {
            Equality equality = equalities[equalitySeek];
            if (equalitySeek == 0) {
                addDifference(calcDifference(
                        0, equality.rightPhrasePosition,
                        0, equality.enteredPhrasePosition,
                        rightPhrases, enteredPhrases), differences);
                Equality nextEquality = equalities[equalitySeek + 1];
                addDifference(calcDifference(
                        equality.rightPhrasePosition + 1, nextEquality.rightPhrasePosition,
                        equality.enteredPhrasePosition + 1, nextEquality.enteredPhrasePosition,
                        rightPhrases, enteredPhrases), differences);
            } else if (equalitySeek == equalities.length - 1) {
                addDifference(calcDifference(
                        equality.rightPhrasePosition + 1, rightPhrases.length,
                        equality.enteredPhrasePosition + 1, enteredPhrases.length,
                        rightPhrases, enteredPhrases), differences);
            } else {
                Equality nextEquality = equalities[equalitySeek + 1];
                addDifference(calcDifference(
                        equality.rightPhrasePosition + 1, nextEquality.rightPhrasePosition,
                        equality.enteredPhrasePosition + 1, nextEquality.enteredPhrasePosition,
                        rightPhrases, enteredPhrases), differences);
            }
        }

        return differences;
    }

    private Difference calcDifference(int rightStart, int rightEnd,
                                      int enteredStart, int enteredEnd,
                                      Phrase[] rightPhrases, Phrase[] enteredPhrases) {
        Phrase diffRightPhrase = new Phrase();
        Phrase diffEnteredPhrase = new Phrase();
        Difference difference = null;
        for (int rightSeek = rightStart; rightSeek < rightEnd; rightSeek++) {
            diffRightPhrase.add(rightPhrases[rightSeek]);
        }
        for (int enteredSeek = enteredStart; enteredSeek < enteredEnd; enteredSeek++) {
            diffEnteredPhrase.add(enteredPhrases[enteredSeek]);
        }
        if (!diffRightPhrase.isEmpty() || !diffEnteredPhrase.isEmpty()) {
            if (!diffRightPhrase.isEmpty() && diffEnteredPhrase.isEmpty()) {
                difference = new Difference(diffRightPhrase, diffEnteredPhrase, DifferenceType.MISSING);
            } else if (diffRightPhrase.isEmpty() && !diffEnteredPhrase.isEmpty()) {
                difference = new Difference(diffRightPhrase, diffEnteredPhrase, DifferenceType.EXTRA);
            } else {
                difference = new Difference(diffRightPhrase, diffEnteredPhrase, DifferenceType.SYNTAX);
            }
        }
        return difference;
    }

    private class Equality {
        int rightPhrasePosition;
        int enteredPhrasePosition;
    }

    private void addDifference(Difference difference, List<Difference> differences) {
        if (difference != null) {
            differences.add(difference);
        }
    }

}
