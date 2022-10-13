import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.*;
import java.util.*;

public class receipt {
    public static List<String> get(String fileName) {
        String fileType = fileName.substring(fileName.length() - 3);

        if (Objects.equals(fileType, "pdf")){
            return new ArrayList<>(receipt.readPDF(fileName));
        }
        else if (Objects.equals(fileType, "png") | Objects.equals(fileType, "jpg")){
            return new ArrayList<>(receipt.readPicture(fileName));
        }

        System.out.println("An error has occurred, please try again");
        return get(fileName);
    }

    static void writeToFile(List<String> arr, String fileName) {
        try {
            FileWriter writer = new FileWriter("Receipts/Edited/" + fileName + ".txt");
            for (String str : arr) {
                writer.write(str + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving file");
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
            System.out.println("File not found");
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
            System.out.println("File not found");
        }
        return List.of(receipt.split("\\R"));
    }
}
