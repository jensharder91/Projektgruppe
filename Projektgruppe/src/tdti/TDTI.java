package tdti;

import java.applet.Applet;
import java.awt.*;
import javax.swing.*;

import Gui.Gui;

public class TDTI {
	private static final String TITLE = "Tree Decontamination with Temporary Immunity";
	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGHT = 600;

	public static int IMMUNITY_TIME = 0;

	public TDTI(){

		Gui panel = Gui.getGui();


		JApplet applet = new JApplet();
		applet.add(panel, "Center");

		JFrame frame = new JFrame();
		frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		frame.setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		frame.setTitle(TITLE);
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);




		frame.add(applet);
		frame.setVisible(true);


	}

	public static void main(String[] args){
		new TDTI();
		Applet applet = new Applet();
		applet.setVisible(true);
	}
}
