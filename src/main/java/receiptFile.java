import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.*;
import java.util.List;

public class receiptFile {
    public static List<String> readReceipt(String imgName) {
        Tesseract ocr = new Tesseract();
        ocr.setTessVariable("user_defined_dpi", "300");
        String receipt = "";
        try {
            ocr.setDatapath("C:/Program Files/Tesseract-OCR/tessdata");
            // Use ocr to get contents of receipt
            receipt = ocr.doOCR(new File("src/img/" + imgName));
        } catch (TesseractException e) {
            System.out.println("File not found");
        }

        return List.of(receipt.split("\\R"));
    }

    static void writeToFile(List<String> arr, String fileName) throws IOException {
        FileWriter writer = new FileWriter("Receipts/" + fileName + ".txt");
        for(String str: arr) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();
        }
    }
