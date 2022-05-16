import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

public class IndexingEngine {
    public static void main(String[] args) throws Exception {

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

                String pageKey = "";
                ArrayList<String> pageKeyList = getFieldFromFile(file, "pageKey");
                if (pageKeyList.size() == 1)
                {
                    pageKey = pageKeyList.get(0);
                }
                else
                {
                    System.out.println("page key not found for page with path: " + filePath);
                }

                String pageName = "";
                ArrayList<String> pageNameList = getFieldFromFile(file, "pageName");
                if (pageNameList.size() == 1)
                {
                    pageName = pageNameList.get(0);
                }
                else
                {
                    System.out.println("page name not found for page with path: " + filePath);
                }

                ArrayList<String> minorConcepts = getFieldFromFile(file, "minorConcepts");
                ArrayList<String> majorConcepts = getFieldFromFile(file, "majorConcepts");
                ArrayList<String> words = getFieldFromFile(file, "words");

                if (filePath != null && pageKey != "" && pageName != "" && minorConcepts != null && majorConcepts != null && words != null) {
                    
                    Page page = new Page(filePath, pageKey, pageName, minorConcepts, majorConcepts, words);
                    pages.add(page);
                }
                else
                {
                    System.out.println("Error: Fields in file not found for file: + " + filePath);
                }
            }
        }

        // write to files
        WriteFieldToJson(pages, "minorConcepts", outputPath);
        WriteFieldToJson(pages, "majorConcepts", outputPath);
        WriteFieldToJson(pages, "words", outputPath);
    }

    // get field from html file
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

    // Method to write field in all pages to json file
    private static void WriteFieldToJson(HashSet<Page> pages, String field, String outputPath) throws IOException {
        File outputFile = new File(outputPath + "/" + field + ".json");
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        int count = 0;

        bufferedWriter.write("[\n");
        for (Page page : pages) {
            count++;
            bufferedWriter.write("\t{\n");
            bufferedWriter.write("\t\t\"pageKey\": \"" + page.pageKey + "\",\n");
            bufferedWriter.write("\t\t\"pageName\": \"" + page.pageName + "\",\n");

            if (field.equals("minorConcepts")) {
                bufferedWriter.write("\t\t\"minorConcepts\": \"" + page.minorConcepts.toString().replace("[", "").replace("]", "") + "\"\n");
            } else if (field.equals("majorConcepts")) {
                bufferedWriter.write("\t\t\"majorConcepts\": \"" + page.majorConcepts.toString().replace("[", "").replace("]", "") + "\"\n");
            } else if (field.equals("words")) {
                bufferedWriter.write("\t\t\"words\": \"" + page.words.toString().replace("[", "").replace("]", "") + "\"\n");
            } else {
                System.out.println("Error: Field not found: " + field);
            }

            bufferedWriter.write("\t}");
            if (count < pages.size()) bufferedWriter.write(",");
            bufferedWriter.newLine();
        }

        bufferedWriter.write("]");
        bufferedWriter.close();
    }
    
    // Class containing all the fields from html files
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
