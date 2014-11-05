package tdti;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import Gui.Gui;
import Tree.Vertice;

public class TDTIGui extends Gui{

	private static final long serialVersionUID = 1L;
	private static TDTIGui gui;

	/**  */
	private Vertice rootVertice = null;
	private Vertice currentVertice = null;
	/** */

	private TDTIGui(){
		super();
	}

	public static TDTIGui getGui(){
		if(gui == null){
			gui = new TDTIGui();
		}
		return gui;
	}
	
	@Override
	public void paintComponent(Graphics g) {

		if(rootVertice != null && autoAlgo && rootVertice instanceof TDTIVertice){
			((TDTIVertice) rootVertice).algorithmus();
		}

		super.paintComponent(g);

		//mark current point
		g.setColor(Color.blue);
		if(currentVertice != null){
			g.drawRect(currentVertice.getX(), currentVertice.getY(), currentVertice.getWidth(), currentVertice.getHeight());
		}

		//draw all vertices recursively
		if(rootVertice instanceof TDTIVertice){
			rootVertice.drawTree(g,10,10,780);
		}
	}

	public Vertice getRoot(){
		return rootVertice;
	}

	@Override
	protected void calcAlgorithmus() {
		if(rootVertice instanceof TDTIVertice){
			((TDTIVertice) rootVertice).algorithmus();
			rootVertice.logSubtree();

			repaint();
		} else {
			System.out.println("No root available");
		}
	}

	@Override
	protected void clearGui() {
		rootVertice = null;
		currentVertice = null;
		//				allVertices = new ArrayList<Vertice>();
		repaint();
	}

	@Override
	protected void addGuiMouseListener() {
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

				if(SwingUtilities.isRightMouseButton(e)){
					if(rootVertice != null){
						Vertice selectedVertice = rootVertice.pointExists(e.getX(), e.getY());
						if(selectedVertice != null){
							if(selectedVertice == rootVertice){
								rootVertice = null;
							}else{
								selectedVertice.delete();
							}
						}
					}
				}else{

					if(rootVertice == null){
						rootVertice = new TDTIVertice("root", null);
					}else if(rootVertice.pointExists(e.getX(), e.getY()) != null){
						// add a child to this point
						TDTIVertice parent = null;
						parent = (TDTIVertice) rootVertice.pointExists(e.getX(), e.getY());
						if(parent != null){
							System.out.println("Add new Child to "+parent);
						}
						if(parent instanceof TDTIVertice){
							new TDTIVertice("test", parent);
						}
					}
				}

				repaint();
			}
		});
	}
}
