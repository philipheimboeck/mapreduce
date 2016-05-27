package at.phe.def.mapreduce.demo.storyteller;

import at.enfilo.def.prototype1.commons.DEFTypeConverter;
import at.phe.def.mapreduce.base.JavaBaseLibraryFunction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Author: Philip Heimböck
 * Date: 21.04.16.
 */
public class StoryTeller extends JavaBaseLibraryFunction {

    private final Random random = new Random();

    /**
     * Create a new random sentence
     *
     * @return
     */
    private String getRandomSentence() throws FileNotFoundException {
        File dir = new File("texts");

        File[] textFiles = dir.listFiles((dir1, filename) -> {
            return filename.endsWith(".txt");
        });
        File chosenTextFile = textFiles[random.nextInt(textFiles.length)];

        FileInputStream inputStream = new FileInputStream(chosenTextFile);
        Scanner scanner = new Scanner(inputStream, "UTF-8").useDelimiter("\\.");

        ArrayList<String> sentences = new ArrayList<>();
        while (scanner.hasNext()) {
            sentences.add(scanner.next() + ".");
        }

        return sentences.get(random.nextInt(sentences.size()));
    }

    @Override
    public void run(List<String> parameters) throws Exception {
        StringBuilder storyBuilder = new StringBuilder();

        int numberSentences = DEFTypeConverter.convert(inParameters.get(0), Integer.class);
        for (int i = 0; i < numberSentences; i++) {
            storyBuilder.append(getRandomSentence());
        }

        setResult(storyBuilder.toString());
    }
}
