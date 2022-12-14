package receipt;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Used to save a final version of an opened receipt, it can be saved either as a fully formatted PDF file or as a
 * standard txt file, which can be reread by the application properly.
 */
public class Save {
    static private final String directory = System.getProperty("user.home") + "/Documents/OpenBook/";

    /**
     * Given the current receipt model and its labels format their contents, so they can be saved to a file and be
     * easily read
     * @param receipt a list model containing the receipt contents to be saved
     * @param labels a hashmap containing the labels and their total
     *              costs to be used for the final breakdown
     * @return a list of strings containing the now prepared contents from the file
     */
    public static List<String> prepareContents(DefaultListModel<String> receipt, Map<String, Double> labels){
        List<String> toWrite = new ArrayList<>(); // Store all contents in list for easier writing

        // Receipt contents
        for (int i = 0; i < receipt.size(); i++) {
            toWrite.add(receipt.get(i));
        }

        // Label breakdown section
        if (!labels.isEmpty()) {
            toWrite.add("Breakdown of Labels"); // Heading
            // Adding labels, their lines and the cost of the label
            for (String label : labels.keySet().stream().toList()) {
                List<String> labelledLines = getLabelledLines(label, toWrite);
                toWrite.add(label);
                toWrite.addAll(labelledLines);
                // Label stats
                toWrite.add("  Amount: " + labelledLines.size());
                toWrite.add("  Cost: ??" + labels.get(label));
                toWrite.add(""); // Spacer
            }
        }

        return toWrite;
    }

    /**
     * Save the prepared contents as a text file
     * @param text the prepared and formatted text to be written to the text file
     * @param labels a hashmap containing the label details
     * @param name the user decided name of the final receipts
     */
    public static void asTXT(DefaultListModel<String> text, Map<String, Double> labels, String name){
        try {
            File file = new File(directory);

            // If users directory does not exist create new folder
            if (file.exists() | file.mkdir()) {
                FileWriter writer = new FileWriter(directory + name + ".txt");

                // Write receipt contents
                for (int i = 0; i < text.size(); i++) {
                    writer.write(text.get(i) + System.lineSeparator());
                }
                // Write labelMap contents
                if (!labels.isEmpty()) {
                    writer.write("Label Breakdown" + System.lineSeparator());
                    for (String label : labels.keySet().stream().toList()) {
                        writer.write(label.substring(1, label.length() - 2) + System.lineSeparator());
                        writer.write(labels.get(label) + System.lineSeparator());
                    }
                }
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Error opening file");
        }
    }

    /**
     * Save the prepared contents as a formatted PDF file
     * @param text the prepared and formatted text to be written to the text file
     * @param labels a hashmap containing the label details
     * @param name the user decided name of the final receipts
     */
    public static void asPDF(DefaultListModel<String> text, Map<String, Double> labels, String name) {
        List<String> toWrite = prepareContents(text, labels);
        File file = new File(directory);

        if (file.exists() | file.mkdir()) {
            System.out.println("Creating directory");
        }

        try (PDDocument doc = new PDDocument()) {
            // Equivalence of space taken by title
            int lines = 2;
            int pageNum = 1;
            int totalPages = toWrite.size() / 30; // Rough estimation, known to sometimes be incorrect
            int defaultXOffset = 120;

            // Label breakdown takes another page or if less than a page taken
            if (!labels.isEmpty() | totalPages == 0){
                totalPages += 1;
            }
            // Set font
            PDType1Font font = PDType1Font.HELVETICA;
            // First page
            PDPage page = new PDPage(PDRectangle.A5);
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            // Adding a title
            content.setFont(font, 14);
            content.beginText();
            content.newLineAtOffset(150, 550); // Page margin + heading width
            content.showText("Your Receipt");
            content.endText();
            // Underline title, not calculating width as waste of resources
            content.moveTo(155, 545);
            content.lineTo(228, 545);
            content.stroke();
            // Prepare for standard content
            content.setFont(font, 8);
            pageNum = addFooterDecorations(content, pageNum, totalPages, name);

            for (String line : toWrite) {
                // Default text offset
                int xOffset = defaultXOffset;
                float yOffset = 555 - 15f * lines;
                content.setFont(font, 8);

                // Found 35 lines to be the max for a page and want separate page for label breakdown
                if (lines == 35 | line.equals("Breakdown of Labels")){
                    content.close(); // Close stream
                    // Shared aspects of each scenario
                    PDPage newPage = new PDPage(PDRectangle.A5);
                    doc.addPage(newPage); // Retain now complete page
                    content = new PDPageContentStream(doc, newPage); // New stream with a blank page
                    pageNum = addFooterDecorations(content, pageNum, totalPages, name);
                    yOffset = 555;

                    // A continuation of receipt or label breakdown
                    if (lines == 35 && !line.equals("Breakdown of Labels")){
                        String pageType = "receipt"; // Default
                        lines = 0;

                        // If line ends with colon or starts with bullet point then it's a label line
                        if (line.contains(":") | line.contains("???")){
                            pageType = "Labels";
                        }
                        // Add correct header
                        addHeaderDecorations(content, pageType);
                    }
                    // First label page, requires a heading
                    else {
                        // Offset for titles
                        xOffset = 150;
                        lines = 1;
                        // Draw underline of heading. not calculating width due to waste of resources
                        content.moveTo(155, 550);
                        content.lineTo(275, 550);
                        content.stroke();
                        content.setFont(font, 14); // Heading font size
                    }
                }
                else {
                    if (labels.containsKey(line)){
                        float width = font.getStringWidth(line) / 1000 * 10;
                        content.moveTo(50, yOffset - 2); // Adjusting line y to be lower than the text y
                        content.lineTo(35 + width, yOffset -2);
                        content.stroke();

                        content.setFont(font, 10);
                        line = line.substring(1, line.length() - 2) + ":"; // Remove <> and add a colon
                        // Change default xOffset to be closer to page border
                        defaultXOffset = 50;
                        xOffset = 50;
                    }
                }
                // Write the current line
                content.beginText();
                content.newLineAtOffset(xOffset, yOffset);
                content.showText(line);
                content.endText();
                ++lines;
            }
            content.close();
            doc.save(directory + name + ".pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Given a content stream add header details to the page
     * @param content the current content stream to add the header decorations to
     * @param pageType either a receipt or label page, both require unique heading titles
     */
    public static void addHeaderDecorations(PDPageContentStream content, String pageType){
        try{
            content.beginText();
            content.newLineAtOffset(160, 580); // Write in top center
            content.showText(pageType + " Continued"); // Mini-heading
            content.endText();
            // Line separator
            content.moveTo(0, 570);
            content.lineTo(450, 570);
            content.stroke();
        } catch (IOException e){
            System.out.println("Error adding header decorations");
        }
    }

    /**
     * Given a content stream add footer details to the current page
     * @param content the current content stream to add the footer decorations to
     * @param pageNum the number of the current page
     * @param total the total number of pages in this document
     * @param name the name of the receipt file
     * @return the updated number of pages
     */
    public static int addFooterDecorations(PDPageContentStream content, int pageNum, int total, String name) {
        try {
            content.setFont(PDType1Font.HELVETICA, 8);
            content.beginText();
            content.newLineAtOffset(30, 15); // Write in bottom left
            content.showText("Page " + pageNum + " of " + total);
            content.newLineAtOffset(140, 0); // Write in bottom middle
            content.showText("Date: " + new SimpleDateFormat("dd/MM/yy").format(new Date()));
            content.newLineAtOffset(190, 0); // Write in bottom right
            content.showText(name);
            content.endText();

            content.moveTo(0, 30);
            content.lineTo(650, 30);
            content.stroke();

            pageNum++; // Update number of pages
        }
        catch (IOException e){
          System.out.println("Error generating footer");
        }

        return pageNum;
    }

    /**
     * Given a label and a receipt get all matching strings within the list
     * @param label the label to get the specific lines for
     * @param receipt the receipt as a list of strings to be searched through
     * @return a list of strings containing the relevant labelled lines
     */
    public static List<String> getLabelledLines(String label, List<String> receipt){
        List<String> labelledLines = new ArrayList<>();

        for (String line: receipt) {
            if (line.contains(label)) {
                labelledLines.add("      ??? " + line);
            }
        }
        labelledLines.replaceAll(s -> s.replaceAll("<.*> ", ""));  // Remove label prefix from lines
        return labelledLines;
    }
}