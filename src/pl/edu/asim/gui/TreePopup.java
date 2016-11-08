package pl.edu.asim.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.persistence.EntityManager;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.CopyGroup;

import pl.edu.asim.model.ASimDO;

public class TreePopup extends JPopupMenu implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Modeler father;
	JTree tree;
	GuiNode[] copyNode = null;
	int X;
	int Y;
	JMenuItem copyItem;

	public TreePopup(Modeler f) {
		super();
		father = f;
		tree = f.tree;

		JMenuItem menuItem = new JMenuItem("move up");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("U");
		menuItem.setMnemonic('u');
		this.add(menuItem);
		menuItem = new JMenuItem("move down");
		menuItem.setActionCommand("D");
		menuItem.setMnemonic('d');
		menuItem.addActionListener(this);
		this.add(menuItem);
		this.add(new JSeparator());
		menuItem = new JMenuItem("copy");
		menuItem.setActionCommand("C");
		menuItem.setMnemonic('c');
		menuItem.addActionListener(this);
		this.add(menuItem);
		copyItem = new JMenuItem("paste");
		copyItem.setActionCommand("P");
		copyItem.setMnemonic('p');
		if (copyNode == null && tree != null)
			copyItem.setEnabled(false);
		copyItem.addActionListener(this);
		this.add(copyItem);

		MouseListener popupListener = new PopupListener(this);
		if (tree != null)
			tree.addMouseListener(popupListener);
	}

	@Override
	public void actionPerformed(ActionEvent a) {

		if (tree != null) {

			TreePath path = tree.getPathForLocation(X, Y);
			if (path == null || path.getLastPathComponent() == null)
				return;
			GuiNode node = (GuiNode) path.getLastPathComponent();
			if (node.getParent() == null)
				return;

			if (a.getActionCommand().equals("U")) {
				int index = node.getParent().getIndex(node);
				if (index > 0) {
					index--;
					GuiNode parent = (GuiNode) node.getParent();
					Object o = father.treeModel.getChild(parent, index);
					father.treeModel.removeNodeFromParent(node);
					father.treeModel.insertNodeInto(node, parent, index);
					node.getSourceElementData().setOrder(index);
					if (o != null)
						((GuiNode) o).getSourceElementData()
								.setOrder(index + 1);
				}
			} else if (a.getActionCommand().equals("D")) {
				int index = node.getParent().getIndex(node);
				int size = node.getParent().getChildCount();
				if (index >= 0 && index < size - 1) {
					index++;
					GuiNode parent = (GuiNode) node.getParent();
					Object o = father.treeModel.getChild(parent, index);
					father.treeModel.removeNodeFromParent(node);
					father.treeModel.insertNodeInto(node, parent, index);
					node.getSourceElementData().setOrder(index);
					if (o != null)
						((GuiNode) o).getSourceElementData()
								.setOrder(index - 1);
				}
			} else if (a.getActionCommand().equals("C")) {

				TreePath[] p = tree.getSelectionPaths();
				if (p != null) {
					boolean set = false;
					for (int i = 0; i < p.length; i++) {
						TreePath pa = p[i];
						if (pa.equals(path)) {
							set = true;
							break;
						}
					}
					if (set) {
						copyNode = new GuiNode[p.length];
						for (int i = 0; i < p.length; i++) {
							TreePath pa = p[i];
							DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) pa
									.getLastPathComponent();
							if (node instanceof GuiNode) {
								copyNode[i] = (GuiNode) node2;
								father.saveNode(copyNode[i]);
							}
						}
					} else {
						copyNode = new GuiNode[1];
						copyNode[0] = node;
						father.saveNode(copyNode[0]);
					}
					copyItem.setEnabled(true);
				}
			} else if (a.getActionCommand().equals("P")) {

				try {
					father.saveNode(node);
					father.selectNode(node);

					ASimDO cD = copyNode[0].getSourceElementData();
					EntityManager em = father.elementManager.getEntityManager();
					em.getTransaction().begin();
					CopyGroup cg = new CopyGroup();
					cg.addAttribute("children");
					cg.addAttribute("name");
					cg.addAttribute("type");
					cg.addAttribute("inTreeOrder");
					cg.addAttribute("properties");

					cg.cascadeAllParts();
					cg.setShouldResetPrimaryKey(true);

					ASimDO empCopy = (ASimDO) em.unwrap(JpaEntityManager.class)
							.copy(cD, cg);
					empCopy.setId(null);
					empCopy.setFather(node.getSourceElementData());
					node.getSourceElementData().getChildren().add(empCopy);
					em.persist(empCopy);
					em.getTransaction().commit();

					node.saveData(em);

					em.getTransaction().begin();
					father.loadNode(node, empCopy);
					em.getTransaction().commit();

					// father.saveNode(node);
					// father.selectNode(node);
					// Marshaller marshaller = father.getJaxbContext()
					// .createMarshaller();
					// Unmarshaller unmarshaller = father.getJaxbContext()
					// .createUnmarshaller();
					// for (int i = 0; i < copyNode.length; i++) {
					// GuiNode copy = copyNode[i];
					// System.out.println("paste "
					// + copy.getSourceElementData() + " "
					// + copy.getSourceElementData().getFather());
					// StringWriter sw = new StringWriter(4096);
					// marshaller.marshal(copy.getSourceElementData(), sw);
					//
					// ASimDO element = (ASimDO) unmarshaller
					// .unmarshal(new StringReader(sw.toString()));
					// father.importNode(node, element);
					// sw.close();
					// }
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				// String oldStrictName =
				// father.getXmlDatabase().getStrictName(copyNode[i].getFatherID(),
				// "");
				// String type = copyNode[i].getType();
				//
				// if (element.isSetButtonPanel() &&
				// element.getButtonPanel().getButton().size() > 0) {
				// List<Button> bl = element.getButtonPanel().getButton();
				// //GUINode result = null;
				// for (Iterator<Button> it = bl.iterator(); it.hasNext();
				// ) {
				// Button b = it.next();
				// if (b.getType().value().equals("NEW") &&
				// b.getElement().value().equals(type)) {
				// HashMap<String, String> changeID =
				// new HashMap<String, String>();
				// GuiSE model =
				// father.getXmlDatabase().exportGUIElement(copyNode[i].getType(),
				// copyNode[i].getName(),
				// copyNode[i].getFatherID());
				//
				//
				// father.getXmlDatabase().importElement(pasteNode,
				// model,
				// changeID,
				// oldStrictName,
				// newStrictName);
				// break;
				// }
				// }
				// }
				// }
			}
		}
	}

	public class PopupListener extends MouseAdapter {

		TreePopup popup;

		public PopupListener(TreePopup p) {
			popup = p;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
				popup.X = e.getX();
				popup.Y = e.getY();
			}
		}
	}

}
