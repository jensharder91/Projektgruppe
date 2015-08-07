package cima;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics;

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
	public void displayInUpperLeftCorner(Graphics g, String displayedText, int row, Color textColor, Color backgroundColor){

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
	
	public void displayInUpperRightCorner(Graphics g, String displayedText, int row, Color textColor, Color backgroundColor){
		
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
	
	private void drawString(Graphics g, String displayedText, int xCoordText, int yCoordText, int stringWidth, int stringHeight, Color textColor, Color backgroundColor){
		
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