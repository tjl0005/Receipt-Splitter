# Receipt Splitter

### Breakdown and Future Aspirations
This application can take images or PDFs of receipts (and other documents) and allow the user to create a new copy of 
the receipt as a PDF with the ability to edit the contents of the receipt. The main purpose is to enable editing 
receipts and add labels to the receipt which can be used be to get statistics and groupings of aspects of the receipt.

The final receipt can be saved as a formatted PDF with a label breakdown or as a basic text file without formatting. If 
a receipt is saved as a PDF it can be reopened and the labels will be recognised. These receipts will be stored in the 
documents directory under the folder "OpenBook" (Automatically generated).

For images of receipts the Tesseract OCR is used and this will always provide the best possible experience and for PDFs 
PDFBox is used.

#### Usage
* Viewing - The current receipt and any saved receipts (within the OpenBook directory) can be viewed through this tab.
* Editing - The current receipt can have lines added, edited, deleted and centered.
* Labelling - Given the current receipt the user can specify lines and add a custom label which will be used for
  tracking and generating basic statistics, these labels can also be removed if required.
* Costs - This tab shows the breakdown of the user added labels and provides a view of the labelled aspects of the 
  receipt
* Undo/Redo - Upto five changes will be recorded so that they can either be undone/redone, this includes the 
  addition/deletion of labels
* Saving - A receipt either edited or just generated can be saved as either a formatted PDF or Text file and will be 
  viewable from then on from the viewing tab.

#### Requirements and Limitations
* The application is designed for receipt documents, it can handle larger documents but the experience will not be 
  optimal
* The generated copy of receipts from images and PDFs will not be able to retain centering and fonts
* Once a receipt has been edited it can be opened and further edited/viewed. For further editing the receipt must be 
  opened directly through the application, not via the receipts tab which will only provide a basic view and be PDF.

### Jar File
If using the jar file of the application the "tessdata" folder must be in the same directory as the jar file for image
receipts to be used, if only PDF support is required then this can be ignored.