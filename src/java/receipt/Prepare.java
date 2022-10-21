package receipt;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.*;


public class Prepare {
    public static List<String> get(String fileName) {
        String fileType = fileName.substring(fileName.length() - 3); // Get file extension

        if (Objects.equals(fileType, "pdf")) {
            return new ArrayList<>(Prepare.getFromPDF(fileName));
        } else if (fileType.equals("txt")) {
            return new ArrayList<>(Prepare.getFromText(fileName));
        } else { // PNG or JPG
            return new ArrayList<>(Prepare.getFromPicture(fileName));
        }
    }

    public static List<String> getNames(){
        List<String> results = new ArrayList<>();

        // Only txt or PDF files
        FilenameFilter textFilter = (dir, name) -> name.toLowerCase().endsWith(".txt") | name.toLowerCase().endsWith(".pdf");
        File[] files = new File("Receipts/OpenBook/").listFiles(textFilter);

        if (Arrays.toString(files).equals("[]") | files == null){
            return null; // No files found
        }

        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }

        return results;
    }

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

    private static List<String> getFromPicture(String fileName) {
        Tesseract ocr = new Tesseract();
        String receipt = "";
        try {
            ocr.setDatapath("tessdata");
            receipt = ocr.doOCR(new File(fileName));

            return List.of(receipt.split("\\R"));
        } catch (TesseractException e) {
            System.out.println("Image file not found");
        }

        return List.of(receipt.split("\\R"));
    }

    private static List<String> getFromPDF(String file) {
        String receipt = "";
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
