# Bill Splitter

### Breakdown and Future Aspirations
This application can take images or PDFS of receipts (or documents) and allow the user to create a new copy of the 
receipt as a PDF with the ability to edit the contents of the receipt. The main purpose is to edit receipts and add
labels to the receipt which can be used be to get statistics and groupings of aspects of the receipt.

Currently, the Tesseract OCR is used, and will provide the best possible experience for image receipts but a custom OCR
is also intended to be developed for testing.

The final receipt can be saved as a formatted PDF with a label breakdown or as a Text file without formatting. 
Currently, there is ability to edit a saved receipt fully, although this is intended to be added at a later date if the 
time comes.

#### Usage
* Viewing - The current receipt and any saved receipts (within the OpenBook directory) can be viewed through this tab.
* Editing - The current receipt can have lines added, edited, deleted and centered.
* Labelling - Given the current receipt the user can specify lines and add a custom label which will be used for
  tracking and generating basic statistics. These labels can also be removed if required.
* Costs - This tab shows the breakdown of the user added labels and provides a view of the labelled aspects of the receipt
* Undo/Redo - Upto five changes will be recorded so that they can either be undone/redone, this includes the 
  addition/deletion of labels
* Saving - A receipt either edited or just generated can be saved as either a formatted PDF or Text file and will be 
  viewable from then on from the viewing tab.

#### Requirements and Limitations
* The application is designed for receipt documents, it can handle larger documents but the experience will not be optimal
* The generated copy of receipts from images and PDFs will not be able to retain centering and fonts
* At this time an edited receipt can not be edited again with full functionality of labels, it is intended to support this
* The main purpose of this application was to practice writing in Java and providing a new challenge, and so the
  application is far from perfect but is intended as a proof of concept.


