package cima;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class InfoDisplayClass {
	
	private static InfoDisplayClass infoClass;
	private Gui gui;
	private static boolean disableInfo = false;
	
	private InfoDisplayClass(){
		gui = CIMAGui.getGui();
	}
	
	public static InfoDisplayClass getInfoDisplayClass(){
		if(infoClass == null){
			infoClass = new InfoDisplayClass();
		}
		
		return infoClass;
	}
	
	/**
	 *  
	 * @param g
	 * @param displayedText
	 * @param row
	 * @param textColor
	 * @param backgroundColor
	 */
	public void displayInUpperLeftCorner(Graphics2D g, String displayedText, int row, Color textColor, Color backgroundColor){

		if(row < 1) row = 1;
	
		Font defaulFont = g.getFont();
		g.setFont(CIMAConstants.getTextFont());
		int stringHeight = (int) Math.floor(g.getFontMetrics().getStringBounds("stringheight",g).getHeight());
		int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(displayedText,g).getWidth());
		int xCoordText = 3;
		int yCoordText = (int) (5 + gui.getButtonBarNorth().getHeight() + (1.2*row * stringHeight));
		g.setFont(defaulFont);	
		
		drawString(g, displayedText, xCoordText, yCoordText, stringWidth, stringHeight, textColor, backgroundColor);
	}
	public void displayInUpperLeftCorner(Graphics2D g, Color textColor, Color backgroundColor, String... displayedText){

		if(displayedText == null || displayedText.length == 0){
			return;
		}
		
		for(int i = 0; i < displayedText.length; i++){
			displayInUpperLeftCorner(g, displayedText[i], i+1, textColor, backgroundColor);
		}
	}
	
	public void displayInUpperRightCorner(Graphics2D g, String displayedText, int row, Color textColor, Color backgroundColor){
		
		if(row < 1) row = 1;
	
		Font defaulFont = g.getFont();
		g.setFont(CIMAConstants.getTextFont());
		int stringHeight = (int) Math.floor(g.getFontMetrics().getStringBounds("stringheight",g).getHeight());
		int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(displayedText,g).getWidth());
		int xCoordText = CIMAGui.getGui().getWidth() - 5 - stringWidth;
		int yCoordText = (int) (5 + gui.getButtonBarNorth().getHeight() + (1.2*row * stringHeight));
		g.setFont(defaulFont);	
		
		drawString(g, displayedText, xCoordText, yCoordText, stringWidth, stringHeight, textColor, backgroundColor);
	}
	public void displayInUpperRightCorner(Graphics2D g, Color textColor, Color backgroundColor, String... displayedText){

		if(displayedText == null || displayedText.length == 0){
			return;
		}
		
		for(int i = 0; i < displayedText.length; i++){
			displayInUpperRightCorner(g, displayedText[i], i+1, textColor, backgroundColor);
		}
	}
	
	public void displayInLowerLeftCorner(Graphics2D g, String displayedText, int row, Color textColor, Color backgroundColor){

		if(row < 1) row = 1;
	
		Font defaulFont = g.getFont();
		g.setFont(CIMAConstants.getTextFont());
		int stringHeight = (int) Math.floor(g.getFontMetrics().getStringBounds("stringheight",g).getHeight());
		int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(displayedText,g).getWidth());
		int xCoordText = 3;
		int yCoordText = (int) (gui.getHeight() - 5 - gui.getButtonBarSouth().getHeight() - (1.2*row * stringHeight));
		g.setFont(defaulFont);	
		
		drawString(g, displayedText, xCoordText, yCoordText, stringWidth, stringHeight, textColor, backgroundColor);
	}
	public void displayInLowerLeftCorner(Graphics2D g, Color textColor, Color backgroundColor, String... displayedText){

		if(displayedText == null || displayedText.length == 0){
			return;
		}
		
		for(int i = displayedText.length-1; i >= 0; i--){
			displayInLowerLeftCorner(g, displayedText[i], i+1, textColor, backgroundColor);
		}
	}
	
	public void displayInLowerRightCorner(Graphics2D g, String displayedText, int row, Color textColor, Color backgroundColor){
		
		if(row < 1) row = 1;
	
		Font defaulFont = g.getFont();
		g.setFont(CIMAConstants.getTextFont());
		int stringHeight = (int) Math.floor(g.getFontMetrics().getStringBounds("stringheight",g).getHeight());
		int stringWidth = (int) Math.floor(g.getFontMetrics().getStringBounds(displayedText,g).getWidth());
		int xCoordText = CIMAGui.getGui().getWidth() - 5 - stringWidth;
		int yCoordText = (int) (gui.getHeight() - 5 - gui.getButtonBarSouth().getHeight() - (1.2*row * stringHeight));
		g.setFont(defaulFont);	
		
		drawString(g, displayedText, xCoordText, yCoordText, stringWidth, stringHeight, textColor, backgroundColor);
	}
	public void displayInLowerRightCorner(Graphics2D g, Color textColor, Color backgroundColor, String... displayedText){

		if(displayedText == null || displayedText.length == 0){
			return;
		}
		
		for(int i = displayedText.length-1; i >= 0; i--){
			displayInLowerRightCorner(g, displayedText[i], displayedText.length - 
					i+1, textColor, backgroundColor);
		}
	}
	

	
	private void drawString(Graphics2D g, String displayedText, int xCoordText, int yCoordText, int stringWidth, int stringHeight, Color textColor, Color backgroundColor){
		
		if(disableInfo){
			return;
		}
		
		Font defaulFont = g.getFont();
		g.setFont(CIMAConstants.getTextFont());
	
		if(backgroundColor != null){
			g.setColor(backgroundColor);
			g.fillRect(xCoordText, yCoordText - stringHeight + 3, stringWidth, stringHeight);
		}
		g.setColor(textColor);
		g.drawString(displayedText, xCoordText, yCoordText);
		
		g.setFont(defaulFont);	
	}
	
	public static void setDisableInfo(boolean disableInfo){
		InfoDisplayClass.disableInfo = disableInfo;
	}

}