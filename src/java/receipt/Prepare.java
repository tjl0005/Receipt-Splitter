package receipt;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.*;


/**
 * Provide functions to easily get a receipt file from a given directory and of PDF, Txt, or picture file type
 */
public class Prepare {
    /**
     * Get a receipt from a directory and return it as a list of strings containing each line
     * @param fileDirectory the file directory of the receipt to be retrieved
     * @return A list of strings containing each line of the given receipt
     */
    public static List<String> getReceipt(String fileDirectory) {
        String fileType = fileDirectory.substring(fileDirectory.length() - 3); // Get file extension

        if (Objects.equals(fileType, "pdf")) {
            List<String> receipt = new ArrayList<>(getFromPDF(fileDirectory));
            // If the receipt is one that has been edited by OpenBook the decorations need to be removed
            receipt.removeIf(s -> s.contains("Page") | s.contains("Your Receipt"));

            // Label breakdown is removed as it will be replaced
            for (int i = 0; i < receipt.size(); i++){
                if (receipt.get(i).equals("Breakdown of Labels")){
                    return receipt.subList(0, i);
                }
            }
            return receipt;
        } else if (fileType.equals("txt")) {
            return Prepare.getFromText(fileDirectory);
        } else { // PNG or JPG
            return Prepare.getFromPicture(fileDirectory);
        }
    }

    /**
     * @return The names of all the users saved receipts, they must be in the open book directory to be noticed
     */
    public static List<String> getNames(){
        // Only txt or PDF files
        FilenameFilter textFilter = (dir, name) -> name.toLowerCase().endsWith(".txt") | name.toLowerCase().endsWith(".pdf");
        File[] files = new File(System.getProperty("user.home") + "/Documents/OpenBook/").listFiles(textFilter);

        if (Arrays.toString(files).equals("[]") | files == null){
            return null; // No files found
        }

        List<String> results = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }
        return results;
    }

    /**
     * Given a file directory containing a text file retrieve the contents of the file
     * @param fileDirectory the file directory of the receipt to be opened
     * @return a list of strings containing each line from the text file
     */
    public static List<String> getFromText(String fileDirectory) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader inputStream = new BufferedReader(new FileReader(fileDirectory))) {
            String line;
            while ((line = inputStream.readLine()) != null) {
                lines.add(line);
                }
            } catch (IOException e) {
                System.out.println("Error reading text file");
            }

        return lines;
    }

    /**
     * Get a receipt as a list of strings from a picture file using Tesseract OCR to identify the words
     * @param fileDirectory the directory of the image representing the receipt
     * @return a list of strings containing the lines of the receipt
     */
    private static List<String> getFromPicture(String fileDirectory) {
        Tesseract ocr = new Tesseract();
        String receipt = "";
        try {
            ocr.setDatapath("tessdata");
            receipt = ocr.doOCR(new File(fileDirectory));
        } catch (TesseractException e) {
            System.out.println("Image file not found");
        }

        return List.of(receipt.split("\\R"));
    }

    /**
     * Translate contents of a PDF line by line
     * @param fileDirectory the directory of the PDF receipt
     * @return a list of strings containing each line of the pdf
     */
    private static List<String> getFromPDF(String fileDirectory) {
        String receipt = "";
        try {
            PDDocument document = PDDocument.load(new File(fileDirectory));
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                receipt = stripper.getText(document);
            }
            document.close();
        } catch (IOException e) {
            System.out.println("PDF file not found");
        }
        return (List.of(receipt.split("\\R")));
    }

    /**
     * Produce the full labelMap from an edited PDF receipt
     * @param fileDirectory The file to get the label details from
     * @return the labelMap containing the labels of the receipt and their total costs
     */
    public static Map<String, Double> labelMapFromPDF(String fileDirectory){
        List<String> receipt = getFromPDF(fileDirectory);
        if (receipt.isEmpty()){
            return null;
        }
        //split by detecting newline
        Map<String, Double> labelMap = new LinkedHashMap<>();
        List<Double> costs = new ArrayList<>();

        for(String line: receipt) {
            if(line.contains("<") && line.contains(">")){ // Label found
                labelMap.put(line.substring(0, line.indexOf(">") + 1), 0.00); // Add label to the map with default cost
            }
            // Cost can not be added directly to the map
            else if (line.contains("Cost:")){
                costs.add(Double.valueOf(line.substring(9))); // Substring to only get the double value
            }
        }
        // Now add the costs to the labelMap
        int index= 0;
        for (String label : labelMap.keySet()){
            labelMap.put(label, costs.get(index));
            index++;
        }
        return labelMap;
    }
}