package pl.edu.asim.gui.actions;

import java.awt.event.ActionEvent;

import javax.persistence.EntityManager;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import pl.edu.asim.gui.GuiNode;
import pl.edu.asim.model.ASimDO;
import pl.edu.econet.Network;

public class ImportEconetNetwork extends ModelerAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130111333562399056L;
	ImageIcon icon;

	public ImportEconetNetwork(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public ImportEconetNetwork(String name, ImageIcon icon) {
		super(name, icon);
	}

	@Override
	public int operation(ActionEvent e) {

		GuiNode root = getModeler().getRoot();
		try {
			ApplicationContext aContext = getApplicationContext();
			String[] networks = aContext.getBeanNamesForType(Network.class);
			String s = (String) JOptionPane.showInputDialog(getModeler()
					.getFrame(), "Network bean", "Import Econet network",
					JOptionPane.PLAIN_MESSAGE, icon, networks, "network");
			if (s == null || s.equals(""))
				return 0;
			Network n = (Network) aContext.getBean(s);
			ASimDO no = n.getAsData();
			no.setName(s);

			getModeler().saveNode(getModeler().getSelectedNode());
			getModeler().selectNode(root);

			EntityManager em = getModeler().getElementManager()
					.getEntityManager();
			em.getTransaction().begin();
			no.setFather(root.getSourceElementData());
			root.getSourceElementData().getChildren().add(no);
			em.persist(no);
			em.getTransaction().commit();

			// root.saveData(em);

			getModeler().loadNode(root, no);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return 0;
	}

	public static ApplicationContext getApplicationContext() {
		return new FileSystemXmlApplicationContext(
				new String[] { "econet/*.xml" });
	}

	class Dialog extends JFileChooser {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		Dialog(String v) {
			super(v);
		}
	}

}
