package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import tdti.Vertice;

public class Gui extends JPanel{

	private static final long serialVersionUID = 1L;
	private static Gui gui;

	private JPanel buttonBar = new JPanel();

	/** Buttons */
	private JButton buttonCalculate = new JButton("Berechnung starten");
	private JButton buttonClear = new JButton("Clear");
	private JToggleButton toggleAutoAlgo = new JToggleButton("AutoCalc");

	/**  */

	private List<Vertice> allVertices = new ArrayList<Vertice>();
	private Vertice rootVertice = null;
	private Vertice currentVertice = null;

	private boolean autoAlgo = false;
	private boolean insertChild = false;
	/** */

	private Gui(){

		createGui();


		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println(e.getX() +" / "+e.getY());

				if(pointExists(e.getX(), e.getY())){
					// add a child to this point
					Vertice parent = null;
					for(Vertice vertice : allVertices){
						if(vertice.isSamePoint(e.getX(), e.getY())){
							parent = vertice;
							System.out.println("Add new Child to "+parent);
							break;
						}
					}
					if(parent instanceof Vertice){
						Vertice newVertice = new Vertice("test", parent);
						allVertices.add(newVertice);
					}
				} else {
					if(allVertices.size() == 0){
						Vertice newVertice = new Vertice("root", null);
						allVertices.add(newVertice);
						rootVertice = newVertice;
					}
				}

				/*
				if(insertChild && !pointExists(e.getX(), e.getY())){
					System.out.println("insert Child");
					Vertice newVertice = new Vertice("test", currentVertice, e.getX(), e.getY());
					allVertices.add(newVertice);
				}

				boolean onVerticeClick = false;
				for(Vertice vertice : allVertices){
					System.out.println("check points");
					if(vertice.isSamePoint(e.getX(), e.getY())){
						System.out.println("is same point");
						currentVertice = vertice;
						onVerticeClick = true;
						if(SwingUtilities.isRightMouseButton(e)){
							// insert child
							insertChild = true;
						}else{
							insertChild = false;
						}
					}
				}
				if(!onVerticeClick && !insertChild){
					currentVertice = null;
				}

				if(allVertices.size() == 0 && SwingUtilities.isRightMouseButton(e)){
					System.out.println("insert root : coord("+e.getX()+" , "+e.getY()+")");
					rootVertice = new Vertice("test", null, e.getX(), e.getY());
					allVertices.add(rootVertice);
				}*/

				repaint();
			}
		});
	}

	public static Gui getGui(){
		if(gui == null){
			gui = new Gui();
		}
		return gui;
	}

	private void createGui(){

		setLayout(new BorderLayout());

		buttonClear.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				rootVertice = null;
				currentVertice = null;
				allVertices = new ArrayList<Vertice>();
				repaint();
			}
		});

		buttonCalculate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				calcAlgorithmus();

			}
		});

		toggleAutoAlgo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					autoAlgo = true;
					calcAlgorithmus();
				}else if(e.getStateChange() == ItemEvent.DESELECTED){
					autoAlgo = false;
				}
			}
		});

		buttonBar.add(buttonCalculate);
		buttonBar.add(buttonClear);
		buttonBar.add(toggleAutoAlgo);
		this.add(buttonBar, "South");


	}

	private void calcAlgorithmus(){
		if(rootVertice instanceof Vertice){
			rootVertice.algorithmus();
			rootVertice.logSubtree();

			repaint();
		} else {
			System.out.println("No root available");
		}
	}

	@Override
	public void paintComponent(Graphics g) {

		if(rootVertice != null && autoAlgo){
			rootVertice.algorithmus();
		}

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		super.paintComponent(g);

		//mark current point
		g.setColor(Color.blue);
		if(currentVertice != null){
			g.drawRect(currentVertice.getX(), currentVertice.getY(), currentVertice.getWidth(), currentVertice.getHeight());
		}

		//draw all vertices recursively
		if(rootVertice instanceof Vertice){
			rootVertice.drawTree(g,10,10,780);
		}
	}

	private boolean pointExists(int x, int y){
		for(Vertice vertice : allVertices){
			System.out.println("Comparing to "+vertice);
			if(vertice.isSamePoint(x, y)){
				return true;
			}
		}
		return false;
	}

	public Vertice getRoot(){
		return rootVertice;
	}
}
