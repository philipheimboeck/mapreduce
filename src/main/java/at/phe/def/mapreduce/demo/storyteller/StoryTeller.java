package at.phe.def.mapreduce.demo.storyteller;

import at.enfilo.def.prototype1.commons.DEFTypeConverter;
import at.phe.def.mapreduce.demo.JavaBaseLibraryFunction;

import java.util.List;
import java.util.Random;

/**
 * Author: Philip Heimböck
 * Date: 21.04.16.
 */
public class StoryTeller extends JavaBaseLibraryFunction {

    private final Random random = new Random();

    private final int WORD_LENGTH_MIN = 1;
    private final int WORD_LENGTH_MAX = 10;
    private final int SENTENCE_LENGTH_MIN = 3;
    private final int SENTENCE_LENGTH_MAX = 40;

    /**
     * Create a new random sentence
     *
     * @param minSentenceLength
     * @param maxSentenceLength
     * @return
     */
    private String getRandomSentence(int minSentenceLength, int maxSentenceLength) {
        StringBuilder stringBuilder = new StringBuilder();

        int sentenceLength = random.nextInt(maxSentenceLength - minSentenceLength) + minSentenceLength;

        for (int i = 0; i < sentenceLength; i++) {
            int wordLength = random.nextInt(WORD_LENGTH_MAX - WORD_LENGTH_MIN) + WORD_LENGTH_MIN;

            char word[] = new char[wordLength];
            for (int j = 0; j < wordLength; j++) {
                word[j] = (char) (random.nextInt(26) + 'a');
            }

            stringBuilder.append(word);
            stringBuilder.append(' ');
        }

        return stringBuilder.toString();
    }

    @Override
    public void run(List<String> parameters) throws Exception {
        StringBuilder storyBuilder = new StringBuilder();

        int numberSentences = DEFTypeConverter.convert(inParameters.get(0), Integer.class);
        for(int i = 0; i < numberSentences; i++) {
            storyBuilder.append(getRandomSentence(SENTENCE_LENGTH_MIN, SENTENCE_LENGTH_MAX));
        }

        setResult(storyBuilder.toString());
    }
}
