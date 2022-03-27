import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class IndexingEngine {
    public static void main(String[] args) throws Exception {
        
        // // initialize vars
        // HashMap<Integer, String> pageIdToPathMap  = new HashMap<Integer, String>();
        // HashMap<String, String> majorConceptsDictionary = new HashMap<String, String>();
        // HashMap<String, String> minorConceptsDictionary = new HashMap<String, String>();
        // HashMap<String, String> wordsDictionary = new HashMap<String, String>();
        
        // initialize vars
        HashSet<Page> pages = new HashSet<Page>();
        int fileCount = 0;


        // get user inputs
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please enter the path to the folder containing all the webpages: "); // C:\Users\Karthik Prasad\Desktop\Projects\Science of Teaching
        String dirPath = br.readLine();
        System.out.println("Please enter the path to the folder where you want to store the output files: "); // C:\Users\Karthik Prasad\Desktop\Projects\Science of Teaching\search engine
        String outputPath = br.readLine();

        // traverse all html files in folder
        File folder = new File(dirPath);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().endsWith(".html")) {

                // read and process file
                String filePath = file.getAbsolutePath();
                String pageKey = getFieldFromFile(file, "pageKey").get(0);
                String pageName = getFieldFromFile(file, "pageName").get(0);
                ArrayList<String> minorConcepts = getFieldFromFile(file, "minorConcepts");
                ArrayList<String> majorConcepts = getFieldFromFile(file, "majorConcepts");
                ArrayList<String> words = getFieldFromFile(file, "words");

                if (filePath != null && pageKey != null && pageName != null && minorConcepts != null && majorConcepts != null && words != null) {
                    
                    Page page = new Page(filePath, pageKey, pageName, minorConcepts, majorConcepts, words);
                    pages.add(page);
                }
                else
                {
                    System.out.println("Error: Fields in file not found for file: + " + filePath);
                }


                // // add file to dictionary
                // int fileId = fileCount;
                // fileCount++;
                // pageIdToPathMap.put(fileId, file.getAbsolutePath());

                // // add concepts to dictionary
                // ArrayList<String> concepts = getFieldFromFile(file, "concepts");
                // for (String concept : concepts) {
                //     if (!majorConceptsDictionary.containsKey(concept)) {
                //         majorConceptsDictionary.put(concept, String.valueOf(fileId));
                //     } else {
                //         majorConceptsDictionary.put(concept, majorConceptsDictionary.get(concept) + "," + String.valueOf(fileId));
                //     }
                // }
                
                // // add words to dictionary
                // ArrayList<String> words = getFieldFromFile(file, "words");
                // for (String word : words) {
                //     if (!wordsDictionary.containsKey(word)) {
                //         wordsDictionary.put(word, String.valueOf(fileId));
                //     } else {
                //         wordsDictionary.put(word, wordsDictionary.get(word) + "," + String.valueOf(fileId));
                //     }
                // }
            }
        }

        // write to file
        File outputFile = new File(outputPath + "/output.json");
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        int count = 0;

        bufferedWriter.write("[\n");
        for (Page page : pages) {
            count++;
            bufferedWriter.write("\t{\n");
            bufferedWriter.write("\t\t\"filePath\": \"" + page.filePath.replace('\\', '/') + "\",\n");
            bufferedWriter.write("\t\t\"pageKey\": \"" + page.pageKey + "\",\n");
            bufferedWriter.write("\t\t\"pageName\": \"" + page.pageName + "\",\n");
            bufferedWriter.write("\t\t\"minorConcepts\": \"" + page.minorConcepts + "\",\n");
            bufferedWriter.write("\t\t\"majorConcepts\": \"" + page.majorConcepts + "\",\n");
            bufferedWriter.write("\t\t\"words\": \"" + page.words.toString() + "\"\n");
            bufferedWriter.write("\t}");
            if (count < pages.size()) bufferedWriter.write(",");
            bufferedWriter.newLine();
        }

        bufferedWriter.write("]");
        bufferedWriter.close();
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
    
    static class Page {

        public String filePath;
        public String pageKey;
        public String pageName;
        public ArrayList<String> minorConcepts;
        public ArrayList<String> majorConcepts;
        public ArrayList<String> words;
        
        public Page(String filePath, String pageKey, String pageName, ArrayList<String> minorConcepts, ArrayList<String> majorConcepts, ArrayList<String> words) {
            this.filePath = filePath;
            this.pageKey = pageKey;
            this.pageName = pageName;
            this.minorConcepts = minorConcepts;
            this.majorConcepts = majorConcepts;
            this.words = words;
        }
    }
}
