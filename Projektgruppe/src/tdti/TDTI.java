package tdti;

import java.applet.Applet;

import Gui.MyApplet;

public class TDTI extends MyApplet{

	public TDTI(){
		super(TDTIGui.getGui(), "Tree Decontamination with Temporary Immunity");
	}

	public static void main(String[] args){
		new TDTI();
		Applet applet = new Applet();
		applet.setVisible(true);
	}
}
