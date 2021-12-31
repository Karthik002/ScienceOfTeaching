import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class IndexingEngine {
    public static void main(String[] args) throws Exception {
        
        // initialize vars
        HashMap<Integer, String> pageIdToPathMap  = new HashMap<Integer, String>();
        HashMap<String, String> conceptsDictionary = new HashMap<String, String>();
        HashMap<String, String> wordsDictionary = new HashMap<String, String>();
        int fileCount = 0;

        // get user inputs
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter the path to the folder containing all the webpages: ");
        String dirPath = br.readLine();
        System.out.println("Please enter the path to the folder where you want to store the output files: ");
        String outputPath = br.readLine();

        // traverse all html files in folder
        File folder = new File(dirPath);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".html")) {

                // add file to dictionary
                int fileId = fileCount;
                fileCount++;
                pageIdToPathMap.put(fileId, file.getAbsolutePath());

                // add concepts to dictionary
                ArrayList<String> concepts = getFieldFromFile(file, "concepts");
                for (String concept : concepts) {
                    if (!conceptsDictionary.containsKey(concept)) {
                        conceptsDictionary.put(concept, String.valueOf(fileId));
                    } else {
                        conceptsDictionary.put(concept, conceptsDictionary.get(concept) + "," + String.valueOf(fileId));
                    }
                }
                
                // add words to dictionary
                ArrayList<String> words = getFieldFromFile(file, "words");
                for (String word : words) {
                    if (!wordsDictionary.containsKey(word)) {
                        wordsDictionary.put(word, String.valueOf(fileId));
                    } else {
                        wordsDictionary.put(word, wordsDictionary.get(word) + "," + String.valueOf(fileId));
                    }
                }
            }
        }

        // write to files
        writePageIdToPathMap(pageIdToPathMap, outputPath + "/pageIdToPathMap.txt");
        writeDictionaryToFile(conceptsDictionary, outputPath + "/conceptsDictionary.txt");
        writeDictionaryToFile(wordsDictionary, outputPath + "/wordsDictionary.txt");
    }

    private static ArrayList<String> getFieldFromFile(File file, String field) {
        ArrayList<String> fields = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = "";
            Boolean inField = false;
            while ((line = br.readLine()) != null) {
                if (line.replaceAll("\\s","").equals("<" + field + ">")) {
                    inField = true;
                } else if (line.replaceAll("\\s","").equals("</" + field + ">")) {
                    inField = false;
                    break;
                }
                else {
                    if (inField) {
                        String[] fieldArray = line.trim().split(", ");
                        for (String term : fieldArray) {
                            fields.add(term);
                        }
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fields;
    }

    private static void writeDictionaryToFile(HashMap<String, String> output, String outputPath) {
        try {
            File outputFile = new File(outputPath);
            FileWriter fileWriter = new FileWriter(outputFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (String key : output.keySet()) {
                bufferedWriter.write(key + ":");
                for (String value : output.get(key).split(",")) {
                    bufferedWriter.write(value + " ");
                }
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writePageIdToPathMap(HashMap<Integer, String> output, String outputPath) {
        try {
            File outputFile = new File(outputPath);
            FileWriter fileWriter = new FileWriter(outputFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (Integer key : output.keySet()) {
                bufferedWriter.write(key + ":" + output.get(key));
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
