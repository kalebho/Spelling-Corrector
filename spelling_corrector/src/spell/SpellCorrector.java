package spell;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.TreeSet;

public class SpellCorrector implements ISpellCorrector {

    private ArrayList<String> realWordsList = new ArrayList<>();        //List of all the real words

    private ArrayList<String> editDistanceWords1 = new ArrayList<>();   //lists for the edit distance one words

    private ArrayList<String> editDistanceWords2 = new ArrayList<>();   //lists for the edit distance two words
    private Trie dictionary = new Trie();       //make new trie to add the words to; THIS WILL BE DICTIONARY

    @Override
    public void useDictionary(String dictionaryFileName) throws IOException {

        File myFile = new File(dictionaryFileName);
        //make scanner object to parse words
        Scanner scan = new Scanner(myFile);
        dictionary = new Trie();        //to clear old dictionary
        while (scan.hasNext()) {
            String word = scan.next();  //get next word in txt file
            dictionary.add(word);       //add word to trie dictionary
        }

    }

    @Override
    public String suggestSimilarWord(String inputWord) {

        realWordsList.clear();
        editDistanceWords1.clear();
        editDistanceWords2.clear();

        inputWord = inputWord.toLowerCase(Locale.ROOT);
        if (dictionary.find(inputWord) != null){
            return inputWord;
        }
        //Take word written and use functions to see if
        // it can be changed into a word in our dictionary
        insertion(inputWord,editDistanceWords1);
        deletion(inputWord, editDistanceWords1);
        transposition(inputWord, editDistanceWords1);
        alteration(inputWord, editDistanceWords1);

        //check if there are any reals words after editing once
        if (realWordsList.size() >= 1) {
            //Declare int for checking greatest value
            int greatestValue = 0;
            TreeSet<String> wordsWithSameValue = new TreeSet<>();   //create list of words with same values in trie
            // Compare real words to see which one to suggest
            for (int i = 0; i < realWordsList.size(); i++) {
                INode node = dictionary.find(realWordsList.get(i));     //Get the node from dictionary
                if (greatestValue < node.getValue()) {      //use node to get value of word and compare with greatest value
                    wordsWithSameValue = new TreeSet<>();      //wipe list
                    greatestValue = node.getValue();        //update the value
                    wordsWithSameValue.add(realWordsList.get(i));    //add the word to wordsWithSameValue list
                }
                else if (greatestValue == node.getValue()) {        //if greatest value and and node value the same then just add the word to wordsWithSameValue
                    wordsWithSameValue.add(realWordsList.get(i));
                }
            }
            return wordsWithSameValue.first();      //Whether or not there is more than 1 word, return the first
                                                    // because the TreeSet is alphabetical
        }
        else {
            //repeat functions on editDistanceWords1; Loop through all the words and do it on them
            //this will create edit distance 2 words list want to look through this list to be able to find a word in our dictionary
            for (int j = 0; j < editDistanceWords1.size(); j++) {
                insertion(editDistanceWords1.get(j),editDistanceWords2);
                deletion(editDistanceWords1.get(j), editDistanceWords2);
                transposition(editDistanceWords1.get(j), editDistanceWords2);
                alteration(editDistanceWords1.get(j), editDistanceWords2);
            }

            // Check to see if there are any real words in realWordsList
            if (realWordsList.size() >= 1) {
                //Declare int for checking greatest value
                int greatestValue = 0;
                TreeSet<String> wordsWithSameValue = new TreeSet<>();   //create list of words with same values in trie
                // Compare real words to see which one to suggest
                for (int i = 0; i < realWordsList.size(); i++) {
                    INode node = dictionary.find(realWordsList.get(i));     //Get the node from dictionary
                    if (greatestValue < node.getValue()) {      //use node to get value of word and compare with greatest value
                        wordsWithSameValue = new TreeSet<>();      //wipe list
                        greatestValue = node.getValue();        //update the value
                        wordsWithSameValue.add(realWordsList.get(i));    //add the word to wordsWithSameValue list
                    } else if (greatestValue == node.getValue()) {        //if greatest value and and node value the same then just add the word to wordsWithSameValue
                        wordsWithSameValue.add(realWordsList.get(i));
                    }
                }
                return wordsWithSameValue.first();
            }
            else {      //if no words in realWordsList after second iteration then no suggested words
                return null;
            }
        }
    }


    private void insertion(String inputWord, ArrayList<String> listOfEditDistanceWords) {
        //make list for all the different words with insertions
        // make new words by adding letters to all the positions in the input word
        //first make string builder

        for (int i = 0; i <= inputWord.length(); i++) {      //loop through input word characters
            for (char letter = 'a'; letter <= 'z'; letter++) {     //loop through and add all the different letters
                StringBuilder sb = new StringBuilder(inputWord);
                sb.insert(i, letter);      //create new word with added letter to certain spot
                listOfEditDistanceWords.add(sb.toString());     //add word to editDistance1 regardless
                if (dictionary.find(sb.toString()) != null) {       //add word to realWordsList if found in dictionary
                    realWordsList.add(sb.toString());
                }
            }
        }
    }

    private void deletion(String inputWord, ArrayList<String> listOfEditWords) {
        //make new words by deleting each letter in word

        for (int i = 0; i < inputWord.length(); i++) {      //loop through inputWord
            StringBuilder sb = new StringBuilder(inputWord);    //string builder
            sb.deleteCharAt(i);         //delete characters at each index
            listOfEditWords.add(sb.toString());     // add word to editDistance1 regardless
            if (dictionary.find(sb.toString()) != null) {       //add word to realWordsList if found in dictionary
                realWordsList.add(sb.toString());
            }
        }

    }

    private void transposition(String inputWord, ArrayList<String> listOfEditWords) {
        //make new words by transposing each letter in word

        for (int i = 0; i < inputWord.length() - 1; i++) {      //loop through input word
            StringBuilder sb = new StringBuilder(inputWord);        //string builder
            char temp_letter = sb.charAt(i + 1);       //save letter that is being deleted
            sb.deleteCharAt(i + 1);        //Delete the letter next the one being transposed
            sb.insert(i, temp_letter);      //insert the letter you deleted to swap their positions
            listOfEditWords.add(sb.toString());     // add word to editDistance1 regardless
            if (dictionary.find(sb.toString()) != null) {       //add word to realWordsList if found in dictionary
                realWordsList.add(sb.toString());
            }
        }

    }

    private void alteration(String inputWord, ArrayList<String> listOfEditWords) {
        //make new words by transposing each letter in word

        for (int i = 0; i < inputWord.length(); i++) {      //loop through input word
            for (char letter = 'a'; letter <= 'z'; letter++) {      //loop through alphabet
                StringBuilder sb = new StringBuilder(inputWord);    //string builder
                sb.deleteCharAt(i);     //Deleter letter at i index
                sb.insert(i, letter);       //insert alphabet letter at i index
                listOfEditWords.add(sb.toString());     // add word to editDistance1 regardless
                if (dictionary.find(sb.toString()) != null) {       //add word to realWordsList if found in dictionary
                    realWordsList.add(sb.toString());
                }
            }
        }
    }
}

