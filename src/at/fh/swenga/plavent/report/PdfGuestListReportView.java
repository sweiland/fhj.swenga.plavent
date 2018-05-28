package at.fh.swenga.plavent.report;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.border.Border;

import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.itextpdf.text.Font.FontStyle;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import at.fh.swenga.plavent.model.Happening;
import at.fh.swenga.plavent.model.User;

public class PdfGuestListReportView extends AbstractPdfView {
	
	private final Color borderColor = Color.DARK_GRAY;
	private final Color plaventGreen = new Color(77,189,116);
	private final Color plaventGrey = new Color(240,243,245);
	private final String fontFamily = FontFactory.HELVETICA;
	
	public PdfGuestListReportView() {}
	

	public Color getBorderColor() {
		return borderColor;
	}



	public Color getPlaventGreen() {
		return plaventGreen;
	}



	public Color getPlaventGrey() {
		return plaventGrey;
	}



	public String getFontFamily() {
		return fontFamily;
	}

	
	private Font getSectionHeaderFont() {
		Font font = FontFactory.getFont(getFontFamily());
		font.setSize(13);
		font.setStyle(Font.BOLD);
		font.setColor(Color.WHITE);
		
		return font;
	}
	
	private Font getBoldContentFont() {
		Font font = FontFactory.getFont(getFontFamily());
		font.setSize(13);
		font.setStyle(Font.BOLD);
		
		return font;
	}
	
	private Font getContentFont() {
		Font font = FontFactory.getFont(fontFamily);
		font.setSize(12);
		return font;
	}


	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Read attributes given from Controller
		List<User> guestList = (List<User>) model.get("guestList");
		Happening happening = (Happening) model.get("happening");

		// Set Document and Response Header information
		String happeningTitle = happening.getHappeningName().replaceAll(" ", "-");
		response.setHeader("Content-Disposition", "attachment; filename=\"guestlist_" + happeningTitle + ".pdf\"");
		document.addHeader("Happening", happening.getHappeningName());
		document.addHeader("Guests", guestList.size() + "");
		document.addTitle("Guest List of " + happening.getHappeningName());

		// Create Header and Headline
		document.add(createHeader(getFontFamily()));
		document.add(createHeadLine("Guest List"));

		// Generate Happening section
		document.add(createHappeningParagraph(getFontFamily(), happening));
		createDesignforHappeningParagraph(document);

		// Generate Guestlist
		document.add(createGuestList(getFontFamily(), guestList));
	}
	
	private Paragraph createHeadLine(String text) {
		Font headLineFont = FontFactory.getFont(getFontFamily());
		headLineFont.setSize(25);
		headLineFont.setStyle(FontStyle.ITALIC.getValue());
		Paragraph headline = new Paragraph(text, headLineFont);
		headline.setAlignment(Element.ALIGN_CENTER);
		headline.setSpacingAfter(15);
		
		return headline;
	}

	/**
	 * Method to create Header of document
	 * 
	 * @param fontFamily
	 *            - Fontfamily to use
	 * @return Parahraph which contains header of document
	 */
	private Paragraph createHeader(String fontFamily) {
		Font font = FontFactory.getFont(fontFamily);
		Paragraph pHeader = new Paragraph();
		font.setSize(10);

		pHeader.add(new Paragraph("FERNBACH - HEIDER - HöDL - WEILAND", font));
		pHeader.add(new Paragraph("FH JOANNEUM Informationsmanagement 2016", font));
		pHeader.setSpacingAfter(15);
		pHeader.setAlignment(Element.ALIGN_CENTER);

		return pHeader;
	}

	/**
	 * Method to create Happening Paragraph
	 * 
	 * @param fontFamily
	 * @param happening
	 * @return
	 * @throws DocumentException
	 */
	private Paragraph createHappeningParagraph(String fontFamily, Happening happening) throws DocumentException {

		Paragraph pHappening = new Paragraph();
		
		//Add Happing name as section header
		pHappening.add(new Paragraph(happening.getHappeningName(),getSectionHeaderFont()));
		

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
		User host = happening.getHappeningHost();
		
		//Create Table with content
		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100.0f);
		table.setWidths(new float[] { 1.5f, 3.0f, 1.5f, 3.0f });
		table.setSpacingBefore(3);	

		table.addCell(createCell("Start:", getBoldContentFont(),true,null));
		table.addCell(createCell(dateFormat.format(happening.getStart().getTime()), getContentFont(),true,null));
		table.addCell(createCell("End:", getBoldContentFont(),true,null));
		table.addCell(createCell(dateFormat.format(happening.getEnd().getTime()), getContentFont(),true,null));

		table.addCell(createCell("Location:", getBoldContentFont(),true,null));
		table.addCell(createCell(happening.getLocation(), getContentFont(),true,null));
		table.addCell(createCell("Host:", getBoldContentFont(),true,null));

		String hostString = host.getLastname().toUpperCase() + " " + host.getFirstname().substring(0, 1) + ".";
		table.addCell(createCell(hostString, getContentFont(),true,null));

		pHappening.add(table);
		
		//Return paragraph
		pHappening.setSpacingAfter(35);		
		return pHappening;
	}

	
	private void createDesignforHappeningParagraph(Document document) throws DocumentException {
		
		//Create the green rectangle
		Rectangle greenRect = new Rectangle(35,685,560,710);
		greenRect.setBackgroundColor(plaventGreen);
		greenRect.setBorderWidth(1);
		greenRect.setBorderColor(borderColor);
		greenRect.setBorder(13);
		document.add(greenRect);
		
		//Create grey rectangle
		Rectangle greyRect = new Rectangle(35,590,560,685);
		greyRect.setBackgroundColor(plaventGrey);
		greyRect.setBorderWidth(1);
		greyRect.setBorderColor(borderColor);	
		greyRect.setBorder(14);
		document.add(greyRect);
	}
	
	
	private Paragraph createGuestList(String fontFamily, List<User> guestList) throws DocumentException {

		Paragraph pGuestList = new Paragraph();

		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100.0f);
		table.setWidths(new float[] { 2.0f, 3.0f, 3.0f });
		table.setSpacingBefore(10);

		// add table header
		table.addCell(createCell("First name", getSectionHeaderFont(),false,getPlaventGreen()));
		table.addCell(createCell("Last name", getSectionHeaderFont(),false,getPlaventGreen()));
		table.addCell(createCell("Contact E-Mail", getSectionHeaderFont(),false,getPlaventGreen()));

		// add cell contents
		for (User guest : guestList) {
			table.addCell(new Phrase(guest.getFirstname(), getContentFont()));
			table.addCell(new Phrase(guest.getLastname(), getContentFont()));
			table.addCell(new Phrase(guest.geteMail(), getContentFont()));
		}

		pGuestList.add(table);
		return pGuestList;
	}

	/**
	 * Helper method to append text to given table
	 * 
	 * @param text
	 * @param font
	 */
	private PdfPCell createCell(String text, Font font, boolean disableBorder,Color cellBackGroundColor) {
		PdfPCell cell = new PdfPCell();
		cell.addElement(new Phrase(text, font));
		if(disableBorder)
			cell.disableBorderSide(-1);
		
		if(cellBackGroundColor != null)
			cell.setBackgroundColor(cellBackGroundColor);
		
		return cell;
	}
}
