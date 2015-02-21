package cima;

import java.applet.Applet;

import cima.MyApplet;

public class CIMA extends MyApplet{
	
	public CIMA(){
		super(CIMAGui.getGui(), "Capture of an Intruder by Mobile Agents");
	}

	public static void main(String[] args){
		new CIMA();
		Applet applet = new Applet();
		applet.setVisible(true);
	}
}
