package pl.edu.asim.gui.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.persistence.EntityManager;
import javax.swing.ImageIcon;

import pl.edu.asim.gui.GuiNode;
import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.DataManager;

public class SortAction extends ModelerAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SortAction(String name) {
		super(name);
	}

	public SortAction(String name, ImageIcon icon) {
		super(name, icon);
	}

	@Override
	public int operation(ActionEvent e) {
		try {

			ASimDO data = getModeler().getSelectedNode().getSourceElementData();
			EntityManager em = DataManager.getInstance().getEntityManager();
			ArrayList<ASimDO> copyList = new ArrayList<ASimDO>();
			copyList.addAll(data.getChildren());
			Collections.sort(copyList, new ComparatorASimDO());

			int i = 1;
			for (ASimDO node : copyList) {
				em.getTransaction().begin();
				node.setOrder(i);
				em.getTransaction().commit();
				GuiNode g_node = getModeler().getNodeMap().get(node.getId());
				if (g_node == null)
					continue;
				GuiNode parent = (GuiNode) g_node.getParent();
				getModeler().getTreeModel().removeNodeFromParent(g_node);
				getModeler().getTreeModel().insertNodeInto(g_node, parent,
						i - 1);
				i++;
			}

			// for (@SuppressWarnings("unchecked")
			// Enumeration<DefaultMutableTreeNode> en =
			// getModeler().getSelectedNode().children(); en
			// .hasMoreElements();) {
			// GuiNode node = (GuiNode) en.nextElement();
			// GuiNode parent = (GuiNode) node.getParent();
			// getModeler().getTreeModel().removeNodeFromParent(node);
			// getModeler().getTreeModel().insertNodeInto(node, parent,
			// node.getSourceElementData().getOrder()-1);
			// }

			// System.out.println("order " + i + " to " + node.getName()
			// + " old order " + node.getOrder());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return getDec();
	}

	class ComparatorASimDO implements Comparator<ASimDO> {
		@Override
		public int compare(ASimDO o1, ASimDO o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}

}
