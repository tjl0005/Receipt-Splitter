package classes;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Receipt {
    public static List<String> get(String fileName) {
        String fileType = fileName.substring(fileName.length() - 3);

        if (Objects.equals(fileType, "pdf")) {
            return new ArrayList<>(Receipt.readPDF(fileName));
        } else {
            return new ArrayList<>(Receipt.readPicture(fileName));
        }
    }

    public static void writeToFile(List<String> textToWrite, String userID, String fileName) {
        try {
            String directory = "Receipts/" + userID + "/";
            File file = new File(directory);

            // If user directory does not exist create new folder
            if (file.exists() | file.mkdir()) {
                FileWriter writer = new FileWriter(directory + fileName + ".txt");
                for (String str : textToWrite) {
                    writer.write(str + System.lineSeparator());
                }
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Error opening file");
        }
    }

    public static List<String> getReceiptNames(String userID){
        List<String> results = new ArrayList<>();

        // Only get txt files
        FilenameFilter textFilter = (dir, name) -> name.toLowerCase().endsWith(".txt");
        File[] files = new File("Receipts/" + userID + "/").listFiles(textFilter);

        if (Arrays.toString(files).equals("[]") | files == null){
            System.out.println("No files found");
            return null;
        }

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }

        return results;
    }

    public static List<String> getReceiptFile(String fileDirectory) {
        List<String> contents = new ArrayList<>();

        try (BufferedReader inputStream = new BufferedReader(
                new FileReader(fileDirectory))) {
                String line;

            while ((line = inputStream.readLine()) != null) {
                contents.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        return contents;
    }

    private static List<String> readPicture(String fileName) {
        Tesseract ocr = new Tesseract();
        ocr.setTessVariable("user_defined_dpi", "300");
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

    private static List<String> readPDF(String fileName) {
        String receipt = "";
        try {
            PDDocument document = PDDocument.load(new File("Receipts/Original/" + fileName));
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
