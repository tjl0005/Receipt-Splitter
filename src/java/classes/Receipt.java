package classes;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.*;
import java.io.*;
import java.util.*;


public class Receipt {
    public static List<String> get(String fileName, boolean newFile) {
        String fileType = fileName.substring(fileName.length() - 3); // Get file extension

        if (Objects.equals(fileType, "pdf")) {
            return new ArrayList<>(Receipt.readPDF(fileName, newFile));
        } else if (fileType.equals("txt")) {
            return new ArrayList<>(Receipt.getReceiptTxtFile(fileName));
        } else {
            return new ArrayList<>(Receipt.translatePicture(fileName));
        }
    }

    public static void toTxt(DefaultListModel<String> text, Map<String, Double> labels, String dir, String name){
        try {
            String directory = "Receipts/" + dir + "/";
            File file = new File(directory);

            // If users directory does not exist create new folder
            if (file.exists() | file.mkdir()) {
                FileWriter writer = new FileWriter(directory + name + ".txt");

                // Write receipt contents
                for (int i = 0; i < text.size(); i++) {
                    writer.write(text.get(i) + System.lineSeparator());
                }
                // Write labelMap contents
                writer.write("Label Breakdown" + System.lineSeparator());
                for (String label: labels.keySet().stream().toList()){
                    writer.write(label.substring(1, label.length() - 2) + System.lineSeparator());
                    writer.write(labels.get(label) + System.lineSeparator());
                }
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Error opening file");
        }
    }

    public static List<String> getReceiptNames(String userID){
        List<String> results = new ArrayList<>();

        // Only get txt files -> Will update for pdfs as well, so saved receipts can be viewed/edited further
        FilenameFilter textFilter = (dir, name) -> name.toLowerCase().endsWith(".txt") | name.toLowerCase().endsWith(".pdf");
        File[] files = new File("Receipts/" + userID + "/").listFiles(textFilter);

        if (Arrays.toString(files).equals("[]") | files == null){
            System.out.println("No files found");
            return null;
        }

        for (File file : files) {
            if (file.isFile()) {
                System.out.println(file);
                results.add(file.getName());
            }
        }

        return results;
    }

    public static List<String> getReceiptTxtFile(String fileDirectory) {
        List<String> contents = new ArrayList<>();

        try (BufferedReader inputStream = new BufferedReader(new FileReader(fileDirectory))) {
            String line;
            while ((line = inputStream.readLine()) != null) {
                contents.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        return contents;
    }

    private static List<String> translatePicture(String fileName) {
        Tesseract ocr = new Tesseract();
        ocr.setVariable("user_defined_dpi", "300");
        String receipt = "";
        try {
            ocr.setDatapath("tessdata");
            receipt = ocr.doOCR(new File("Receipts/Original/" + fileName));

            return List.of(receipt.split("\\R"));
        } catch (TesseractException e) {
            System.out.println("Image file not found");
        }

        return List.of(receipt.split("\\R"));
    }

    private static List<String> readPDF(String file, boolean newFile) {
        String receipt = "";

        // This will be deprecated
        if (newFile){
            file = "Receipts/Original/" + file;
        }

        try {
            PDDocument document = PDDocument.load(new File(file));
            if (!document.isEncrypted()) {
                PDFTextStripper stripper = new PDFTextStripper();
                receipt = stripper.getText(document);
            }
            document.close();

        } catch (IOException e) {
            System.out.println("PDF file not found");
        }
        return List.of(receipt.split("\\R"));
    }
}
