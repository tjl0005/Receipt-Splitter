package classes;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReceiptPDF {
    public static void saveAsPDF(DefaultListModel<String> list, Map<String, Double> labels, String dir, String name) {
        List<String> toWrite = prepareContents(list, labels);
        String directory = "Receipts/" + dir + "/" + name + ".pdf";

        try (PDDocument doc = new PDDocument()) {
            // Equivalence of space taken by title
            int lines = 2;
            int pageNum = 1;
            int totalPages = toWrite.size() / 31; // Rough estimation, known to sometimes be incorrect

            // Label breakdown takes another page or if less than a page taken
            if (!labels.isEmpty() | totalPages == 0){
                totalPages += 1;
            }
            // Set font
            PDType1Font font = PDType1Font.HELVETICA;
            // First page
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            // Adding a title
            content.setFont(font, 20);
            content.beginText();
            content.newLineAtOffset(250, 700); // Page margin + heading width
            content.showText("Your Receipt");
            content.endText();
            // Underline title, not calculating width as waste of resources
            content.moveTo(250, 695);
            content.lineTo(370, 695);
            content.stroke();
            // Prepare for standard content
            content.setFont(font, 12);
            pageNum = addFooterDecorations(content, pageNum, totalPages, name);

            for (String line : toWrite) {
                // Default text offset
                int xOffset = 100;
                float yOffset = 700 - 20f * lines;
                content.setFont(font, 12);

                // Found 31 lines to be the max for a page and want separate page for label breakdown
                if (lines == 31 | line.equals("Breakdown of Labels")){
                    content.close(); // Close stream
                    // Shared aspects of each scenario
                    PDPage newPage = new PDPage();
                    doc.addPage(newPage); // Retain now complete page
                    content = new PDPageContentStream(doc, newPage); // New stream with a blank page
                    pageNum = addFooterDecorations(content, pageNum, totalPages, name);
                    yOffset = 700;

                    // A continuation of receipt or label breakdown
                    if (lines == 31 && !line.equals("Breakdown of Labels")){
                        String pageType = "Receipt"; // Default
                        lines = 0;

                        // If line ends with colon or starts with bullet point then it's a label line
                        if (line.contains(":") | line.contains("•")){
                            pageType = "Labels";
                        }
                        // Add correct header
                        addHeaderDecorations(content, pageType);
                    }
                    // First label page, requires a heading
                    else {
                        // Offset for titles
                        xOffset = 200;
                        lines = 1;
                        // Draw underline of heading. not calculating width due to waste of resources
                        content.moveTo(200, 695);
                        content.lineTo(373, 695);
                        content.stroke();
                        content.setFont(font, 18); // Heading font size
                    }
                }
                else {
                    if (labels.containsKey(line)){
                        float width = font.getStringWidth(line) / 1000 * 14;
                        content.moveTo(100, yOffset - 2); // Adjusting line y to be lower than the text y
                        content.lineTo(80 + width, yOffset -2);
                        content.stroke();

                        content.setFont(font, 14);
                        line = line.substring(1, line.length() - 2) + ":"; // Remove <> and add a colon
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
            doc.save(directory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> prepareContents(DefaultListModel<String> list, Map<String, Double> labels){
        List<String> toWrite = new ArrayList<>(); // Store all contents in list for easier writing

        // Receipt contents
        for (int i = 0; i < list.size(); i++) {
            toWrite.add(list.get(i));
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
                toWrite.add("Amount: " + labelledLines.size());
                toWrite.add("Cost: £" + labels.get(label));
                toWrite.add(""); // Spacer
            }
        }

        return toWrite;
    }

    public static int addFooterDecorations(PDPageContentStream content, int pageNum, int total, String name) {
        try {
            content.setFont(PDType1Font.HELVETICA, 12);
            content.beginText();
            content.newLineAtOffset(50, 30); // Write in bottom left
            content.showText("Page " + pageNum + " of " + total);
            content.newLineAtOffset(200, 0); // Write in bottom middle
            content.showText("Date: " + new SimpleDateFormat("dd/MM/yy").format(new Date()));
            content.newLineAtOffset(250, 0); // Write in bottom right
            content.showText(name);
            content.endText();

            content.moveTo(0, 60);
            content.lineTo(650, 60);
            content.stroke();

            pageNum++; // Update number of pages
        }
        catch (IOException e){
          System.out.println("Error generating footer");
        }

        return pageNum;
    }

    public static void addHeaderDecorations(PDPageContentStream content, String pageType){
        try{
            content.beginText();
            content.newLineAtOffset(250, 750); // Write in top center
            content.showText(pageType + " Continued"); // Mini-heading
            content.endText();
            // Line separator
            content.moveTo(0, 740);
            content.lineTo(650, 740);
            content.stroke();
        } catch (IOException e){
            System.out.println("Error adding header decorations");
        }
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
