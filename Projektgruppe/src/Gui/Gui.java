package Gui;


import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import Tree.Vertice;

public abstract class Gui extends JPanel{

	protected static final long serialVersionUID = 1L;

	protected Vertice rootVertice = null;
	protected Vertice currentVertice = null;

	protected JPanel buttonBar = new JPanel();

	/** Buttons */
	//protected JButton buttonCalculate = new JButton("Berechnung starten");
	protected JButton buttonClear = new JButton("Restart");
	protected JToggleButton toggleAutoAlgo = new JToggleButton("AutoCalc");

	protected boolean autoAlgo = false;

	protected Gui(){

		createGui();

		addGuiMouseListener();
	}

	protected void createGui(){

		setLayout(new BorderLayout());

		buttonClear.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				reset();
			}
		});

		/*
		buttonCalculate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				calcAlgorithmus(true);
			}
		});
		*/

		/*
		toggleAutoAlgo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					autoAlgo = true;
					calcAlgorithmus(true);
				}else if(e.getStateChange() == ItemEvent.DESELECTED){
					autoAlgo = false;
				}
			}
		});
		*/

		//buttonBar.add(buttonCalculate);
		buttonBar.add(buttonClear);
		//buttonBar.add(toggleAutoAlgo);
		this.add(buttonBar, "South");
	}

	public void addFieldToBar(JTextField tf){
		buttonBar.add(tf);
	}

	public void addButtonToBar(JButton btn){
		buttonBar.add(btn);
	}

	public Vertice getRoot(){
		return rootVertice;
	}

	@Override
	public void paintComponent(Graphics g) {

		if(rootVertice != null && autoAlgo){
			calcAlgorithmus(false);
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setStroke(new BasicStroke(2));
		super.paintComponent(g);

		//mark current point
		g.setColor(Color.blue);
		if(currentVertice != null){
			g.drawRect(currentVertice.getX(), currentVertice.getY(), currentVertice.getWidth(), currentVertice.getHeight());
		}

		//draw all vertices recursively
		if(rootVertice != null){
			rootVertice.drawTree(g,10,10,getWidth()-20,getHeight()-50);
		}
	}

	protected abstract void reset();
	protected abstract void calcAlgorithmus(boolean repaintBool);
	protected abstract void addGuiMouseListener();

}

