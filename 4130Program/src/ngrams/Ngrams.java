package ngrams;
/*
    This program computes letter and word frequencies
    on a subset of documents in the Gutenberg corpus
    Author: V. Gudivada
    Date Created: 02 Jan 2021
    Date Last Modified: 09 Jan 2021
*/

// represents files and directory pathnames 
// in an abstract manner
import java.io.File;

// reads data from files as streams of characters
import java.io.FileReader;

// reads text efficiently from character-input
// stream buffers 
import java.io.BufferedReader;

// for writing data to files
import java.io.PrintWriter;

// signals that an input/output (I/O) exception 
// of some kind has occurred
import java.io.IOException;

// compiled representation of a regular expressions
import java.util.regex.Pattern;
import java.util.Set;
import java.util.TreeMap;
// matches a compiled regular expression with an input string
import java.util.regex.Matcher;


public class Ngrams {

    // no more than this many input files needs to be processed
    final static int MAX_NUMBER_OF_INPUT_FILES = 100;

    // an array to hold Gutenberg corpus file names
    static String[] inputFileNames = new String[MAX_NUMBER_OF_INPUT_FILES];

    static int fileCount = 0;


    // loads all files names in the directory subtree into an array
    // violates good programming practice by accessing a global variable (inputFileNames)
    public static void listFilesInPath(final File path) {
        for (final File fileEntry : path.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesInPath(fileEntry);
            } 
            else if (fileEntry.getName().endsWith((".txt")))  {
                inputFileNames[fileCount++] = fileEntry.getPath();
                // fileNameListWriter.println(fileEntry.getPath());
                // System.out.println(fileEntry.getName());
                // System.out.println(fileEntry.getAbsolutePath());
                // System.out.println(fileEntry.getCanonicalPath());
            }
        }
    }

    // returns index of a character in the alphabet 
    // uses zero-based indexing
    public static int getLetterValue(char letter) {
        return (int) Character.toUpperCase(letter) - 65;
    }
	
	public static void main(String[] args){

        // did the user provide correct number of command line arguments?
        // if not, print message and exit
        if (args.length != 5){
            System.err.println("Number of command line arguments must be 3");
            System.err.println("You have given " + args.length + " command line arguments");
            System.err.println("Incorrect usage. Program terminated");
            System.err.println("Correct usage: java Ngrams <path-to-input-files> <outfile-for-words> <outfile-for-char-counts");
            System.exit(1);
        }

        // extract input file name from command line arguments
        // this is the name of the file from the Gutenberg corpus
        String inputFileDirName = args[0];
        System.out.println("Input files directory path name is: " + inputFileDirName);

        // collects file names and write them to 
        listFilesInPath(new File (inputFileDirName));

        // System.out.println("Number of Gutenberg corpus files: " + fileCount);

        // br for efficiently reading characters from an input stream
        BufferedReader br = null;

        // wdWriter for writing extracted words to an output file
        PrintWriter wdWriter = null;

        // ccWriter for writing characters and their occurrence 
        // counts to an output file
        PrintWriter ccWriter = null;
        
        //biWriter for writing bigrams to an output file
        
        PrintWriter biWriter = null;
        //triWriter for writing trigrams to an output file
        
        PrintWriter triWriter = null;

        // wordPattern specifies pattern for words using a regular expression
        Pattern wordPattern = Pattern.compile("[a-zA-Z]+");

        // wordMatcher finds words by spotting word word patterns with input
        Matcher wordMatcher;

        // a line read from file
        String line;

        // an extracted word from a line
        String word;
        
        String word2;
        
        String word3;
        
        String bigram;
        
        String trigram;

        // letter characters
        String alphabet = "abcdefghijklmnopqrstuvwxyz";

        // open output file for writing words
        try {
            wdWriter = new PrintWriter(args[1], "UTF-8");
            System.out.println(args[1] + " successfully opened for writing words");
        }
        catch (IOException ex){
            System.err.println("Unable to open " + args[1] + " for writing words");
            System.err.println("Program terminated\n");
            System.exit(1);
        }

        // array to keep track of character occurrence counts
        int[] charCountArray = new int[26];

        // initialize character counts
        for (int index = 0; index < charCountArray.length; index++){
            charCountArray[index] = 0;
        }
        
        
        TreeMap<String,Integer> uni = new TreeMap<String,Integer>();
        TreeMap<String,Integer> bi = new TreeMap<String,Integer>();
        TreeMap<String,Integer> tri = new TreeMap<String,Integer>();

        // process one file at a time
        for (int index = 0; index < fileCount; index++){

            // open the input file, read one line at a time, extract words
            // in the line, extract characters in a word, write words and
            // character counts to disk files
            try {
                // get a BufferedReader object, which encapsulates
                // access to a (disk) file
                br = new BufferedReader(new FileReader(inputFileNames[index]));
                

                // as long as we have more lines to process, read a line
                // the following line is doing two things: makes an assignment
                // and serves as a boolean expression for while test
                while ((line = br.readLine()) != null) {
                    // process the line by extracting words using the wordPattern
                    wordMatcher = wordPattern.matcher(line);

                    // process one word at a time
                    while ( wordMatcher.find() ) {
                        // extract the word
                        word = line.substring(wordMatcher.start(), wordMatcher.end());
                        // System.out.println(word);

                        // // convert the word to lowercase, and write to word file
                        word = word.toLowerCase();
                        
                        if(uni.containsKey(word)) {
                        	uni.replace(word, uni.get(word), uni.get(word)+1);
                        }
                        else {
                        	uni.put(word, 1);
                        }
                        
                        

                        // process characters in a word
                        for (int i = 0; i < word.length(); i++){
                            // System.out.println("word.charAt(i) " + word.charAt(i));
                            // System.out.println("alphabet.indexOf(word.charAt(i)) " + alphabet.indexOf(word.charAt(i)));
                            
                            // if the character is a letter, increment the 
                            // corresponding count, otherwise discard the character
                            if (Character.isLetter(word.charAt(i))) {
                                charCountArray[alphabet.indexOf(word.charAt(i))]++;
                            }
                        } // for
                    } // while - wordMatcher
                } // while - line
              
            	   
               
                
            } // try
            catch (IOException ex) {
                System.err.println("File " + inputFileNames[index] + " not found. Program terminated.\n");
                System.exit(1);
            }
        } // for -- process one file at a time

        // write letters and their counts to file named args[2]
        // open output file 2 for writing characters and their counts
        try {
            ccWriter = new PrintWriter(args[2], "UTF-8");
            System.out.println(args[2] + " successfully opened for writing character counts");
        }
        catch (IOException ex){
            System.err.println("Unable to open " + args[2] + " for writing character counts");
            System.err.println("Program terminated\n");
            System.exit(1);
        }

        for (int index = 0; index < charCountArray.length; index++){
            ccWriter.println(alphabet.charAt(index) + "\t" + charCountArray[index]);
        }
        
        
        try {
        	biWriter = new PrintWriter(args[3], "UTF-8");
        	System.out.println(args[3] + " successfully opened for writing bigrams!");
        }
        catch (IOException ex) {
        	System.err.println("Unable to open " + args[3] + " for writing character counts");
            System.err.println("Program terminated\n");
            System.exit(1);
        }
        
        
        for (int index = 0; index < fileCount; index++){

            // open the input file, read one line at a time, extract words
            // in the line, extract characters in a word, write words and
            // character counts to disk files
            try {
                // get a BufferedReader object, which encapsulates
                // access to a (disk) file
                br = new BufferedReader(new FileReader(inputFileNames[index]));
                
                word = null;
                word2 = null;

                // as long as we have more lines to process, read a line
                // the following line is doing two things: makes an assignment
                // and serves as a boolean expression for while test
                while ((line = br.readLine()) != null) {
                    // process the line by extracting words using the wordPattern
                    wordMatcher = wordPattern.matcher(line);

                    // process one word at a time
                    while ( wordMatcher.find() ) {
                        // extract the word
                    	
                    	if(word == null) {
                    		word = line.substring(wordMatcher.start(), wordMatcher.end());
                    	}
                    	else if(word2 == null) {
                     		word2 = line.substring(wordMatcher.start(), wordMatcher.end());
                     	}
                        // System.out.println(word);

                        
                        if(word != null && word2 != null) {
                        bigram = word.toLowerCase() + " " + word2.toLowerCase();
                        
                        bigram.toLowerCase();
                        
                        if(bi.containsKey(bigram)) {
                        	bi.replace(bigram, bi.get(bigram), bi.get(bigram)+1);
                        }
                        else {
                        	bi.put(bigram, 1);
                        }
                        
                        word = word2;
                        word2 = null;
                        }
                    }
                }
            }
            catch (IOException ex) {
                System.err.println("File " + inputFileNames[index] + " not found. Program terminated.\n");
                System.exit(1);
            }
            
            try {
            	triWriter = new PrintWriter(args[4], "UTF-8");
            	System.out.println(args[4] + " successfully opened for writing trigrams!");
            }
            catch (IOException ex) {
            	System.err.println("Unable to open " + args[4] + " for writing character counts");
                System.err.println("Program terminated\n");
                System.exit(1);
            }
            
            for (index = 0; index < fileCount; index++){

                // open the input file, read one line at a time, extract words
                // in the line, extract characters in a word, write words and
                // character counts to disk files
            	 try {
                     // get a BufferedReader object, which encapsulates
                     // access to a (disk) file
                     br = new BufferedReader(new FileReader(inputFileNames[index]));
                     
                     word = null;
                     word2 = null;
                     word3 = null;

                     // as long as we have more lines to process, read a line
                     // the following line is doing two things: makes an assignment
                     // and serves as a boolean expression for while test
                     while ((line = br.readLine()) != null) {
                         // process the line by extracting words using the wordPattern
                         wordMatcher = wordPattern.matcher(line);

                         // process one word at a time
                         while ( wordMatcher.find() ) {
                             // extract the word
                         	
                         	if(word == null) {
                         		word = line.substring(wordMatcher.start(), wordMatcher.end());
                         		
                         	}
                             // System.out.println(word);
                         	else if(word2 == null) {
                         		word2 = line.substring(wordMatcher.start(), wordMatcher.end());
                         	}
                         	else if(word3 == null) {
                         		word3 = line.substring(wordMatcher.start(), wordMatcher.end());
                         	}
                            
                      
                            if(word !=null && word2!= null && word3 != null) {
                            trigram = word.toLowerCase() + " " + word2.toLowerCase() + " " + word3.toLowerCase();
                            
                            trigram.toLowerCase();
                            
                            if(tri.containsKey(trigram)) {
                            	tri.replace(trigram, tri.get(trigram), tri.get(trigram)+1);
                            }
                            else {
                            	tri.put(trigram, 1);
                            }
                            
                            word = word2;
                            word2 = word3;
                            word3 = null;
                         }
                        }
                    }
                }
                catch (IOException ex) {
                    System.err.println("File " + inputFileNames[index] + " not found. Program terminated.\n");
                    System.exit(1);
                }
       
            	 Set<String> uniSet = uni.keySet();
                 for(String key : uniSet) {
                 	wdWriter.println(key + "\t" + uni.get(key));
                 }
                 Set<String> biSet = bi.keySet();
                 for(String key : biSet) {
                 	biWriter.println(key + "\t" + bi.get(key));
                 }
                 Set<String> triSet = tri.keySet();
                 for(String key : triSet) {
                 	triWriter.println(key + "\t" + tri.get(key));
                 }
        

        // close buffered reader. gives error
        // needs a try ... catch block
        // br.close();

        // close output file 1
        wdWriter.close();

        // close output file 2
        ccWriter.close();
        
        // close output file 3
        biWriter.close();
        
        //close output file 4
        triWriter.close();

            } // main()
            
            
            
        }
       
	}
}// class