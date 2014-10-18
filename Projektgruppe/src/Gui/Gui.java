package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import tdti.Vertice;

public class Gui extends JPanel{

	private static final long serialVersionUID = 1L;
	private static Gui gui;

	private JPanel buttonBar = new JPanel();

	/** Buttons */
	private JButton testButton = new JButton("Berechnung starten");

	/**  */

	private List<VerticeGui> allVertices = new ArrayList<VerticeGui>();
	private VerticeGui rootVertice = null;
	private VerticeGui currentVertice = null;

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

				if(insertChild && !pointExists(e.getX(), e.getY())){
					System.out.println("insert Child");
					VerticeGui newVertice = new VerticeGui(e.getX(), e.getY(), new Vertice("test", currentVertice.getVertice()));
					allVertices.add(newVertice);
				}

				boolean onVerticeClick = false;
				for(VerticeGui verticeGui : allVertices){
					System.out.println("check points");
					if(verticeGui.isSamePoint(e.getX(), e.getY())){
						System.out.println("is same point");
						currentVertice = verticeGui;
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
					System.out.println("insert root");
					rootVertice = new VerticeGui(e.getX(), e.getY(), new Vertice("test", null));
					allVertices.add(rootVertice);
				}

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

		testButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if(rootVertice instanceof VerticeGui){
					rootVertice.getVertice().reset();
					rootVertice.getVertice().init();
					rootVertice.getVertice().logSubtree();

					repaint();
				} else {
					System.out.println('No root available');
				}

			}
		});
		buttonBar.add(testButton);
		this.add(buttonBar, "South");


	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		super.paintComponent(g);

		//mark current point
		g.setColor(Color.blue);
		if(currentVertice != null){
			g.drawRect(currentVertice.getX(), currentVertice.getY(), currentVertice.getWidth(), currentVertice.getHeight());
		}
		//draw all vertices
		for(VerticeGui verticeGui : allVertices){
			verticeGui.drawVertice(g);
		}

	}

	private boolean pointExists(int x, int y){
		for(VerticeGui verticeGui : allVertices){
			if(verticeGui.isSamePoint(x, y)){
				return true;
			}
		}
		return false;
	}

	public VerticeGui getRoot(){
		return rootVertice;
	}
}
