package cima;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import Gui.Gui;
import Tree.Vertice;

public class CIMAGui extends Gui{

	private static final long serialVersionUID = 1L;
	private static CIMAGui gui;


	private CIMAGui(){
		super();
	}

	public static CIMAGui getGui(){
		if(gui == null){
			gui = new CIMAGui();
		}
		return gui;
	}
	


	@Override
	protected void calcAlgorithmus(boolean repaintBool) {
		
		if(rootVertice instanceof CIMAVertice){
			((CIMAVertice) rootVertice).algorithmus();
		}
		
		if(repaintBool){
			repaint();
		}
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
						rootVertice = new CIMAVertice("root", null);
					}else if(rootVertice.pointExists(e.getX(), e.getY()) != null){
						// add a child to this point
						CIMAVertice parent = null;
						parent = (CIMAVertice) rootVertice.pointExists(e.getX(), e.getY());
						if(parent != null){
							System.out.println("Add new Child to "+parent);
						}
						if(parent instanceof CIMAVertice){
							new CIMAVertice("test", parent);
						}
					}
				}

				repaint();
			}
		});
	}

}
