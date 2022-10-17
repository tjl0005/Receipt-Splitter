package classes;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class Receipt {
    public static List<String> get(String fileName) {
        String fileType = fileName.substring(fileName.length() - 3);

        if (Objects.equals(fileType, "pdf")) {
            return new ArrayList<>(Receipt.readPDF(fileName));
        } else {
            return new ArrayList<>(Receipt.readPicture(fileName));
        }
    }

    public static void writeToPDF(DefaultListModel<String> listModel, Map<String, Double> labelMap) {
        String filename = "sample.pdf"; // Will be custom filename
        List<String> toWrite = new ArrayList<>(); // Store all contents in list for easier writing

        // Receipt contents
        for (int i = 0; i < listModel.size(); i++) {
            toWrite.add(listModel.get(i));
        }

        // Label breakdown section
        if (!labelMap.isEmpty()) {
            toWrite.add("Breakdown of Your Labels:");
            // Adding labels, their lines and the cost of the label
            for (String label : labelMap.keySet().stream().toList()) {
                List<String> labelledLines = getLabelledLines(label, toWrite);
                toWrite.add(label.substring(1, label.length() - 2) + ":");
                toWrite.addAll(labelledLines);
                toWrite.add("Amount: " + labelledLines.size());
                toWrite.add("Cost: £" + labelMap.get(label));
                toWrite.add(""); // Spacer
            }
        }

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);

            content.setFont(PDType1Font.TIMES_ROMAN, 20);
            content.beginText();
            content.newLineAtOffset(250, 700); // Perfectly centered
            content.showText("Your Receipt"); // Title
            content.endText();

            content.setFont(PDType1Font.TIMES_ROMAN, 12);
            // Equivalence of space taken by title
            int lines = 2;
            for (String line : toWrite) {
                // Found 30 lines to be the max for a page and want separate page for label breakdown
                if (lines / 30 == 1 | line.equals("Breakdown of Your Labels:")){
                    content.close();
                    PDPage secondPage = new PDPage();
                    doc.addPage(secondPage);
                    content = new PDPageContentStream(doc, secondPage);
                    content.setFont(PDType1Font.TIMES_ROMAN, 12);
                    lines = 0;

                    // Breakdown of etc. -> Font size 20 -> Centered
                    // (Insert label) -> Font size 16
                }
                content.beginText();
                content.newLineAtOffset(100, 700 - 20f * lines);
                content.showText(line);
                content.endText();

                ++lines;
            }

            content.close();
            doc.save(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveAsTxt(DefaultListModel<String> text, Map<String, Double> labels, String dir, String name){
        try {
            String directory = "Receipts/" + dir + "/";
            File file = new File(directory);

            // If user directory does not exist create new folder
            if (file.exists() | file.mkdir()) {
                FileWriter writer = new FileWriter(directory + name + ".txt");

                for (int i = 0; i < text.size(); i++) {
                    writer.write(text.get(i) + System.lineSeparator());
                }
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

    public static List<String> getLabelledLines(String label, List<String> receipt){
        List<String> labelledLines = new ArrayList<>();

        for (String line: receipt) {
            if (line.contains(label)) {
                labelledLines.add("    • " + line);
            }
        }
        labelledLines.replaceAll(s -> s.replaceAll("<.*> ", ""));  // Remove label prefix from lines
        return labelledLines;
    }
}
