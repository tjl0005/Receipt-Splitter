package Classes;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

    public static void writeToFile(List<String> textToWrite, String fileName) {
        try {
            FileWriter writer = new FileWriter("Receipts/Edited/" + fileName + ".txt");
            for (String str : textToWrite) {
                writer.write(str + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not save file");
        }
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
