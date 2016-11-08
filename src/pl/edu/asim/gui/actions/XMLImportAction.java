package pl.edu.asim.gui.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.persistence.EntityManager;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.CopyGroup;

import pl.edu.asim.gui.GuiNode;
import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;
import pl.edu.asim.model.DataManager;

public class XMLImportAction extends ModelerAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XMLImportAction(String name) {
		super(name);
	}

	public XMLImportAction(String name, ImageIcon icon) {
		super(name, icon);
	}

	@Override
	public int operation(ActionEvent e) {
		try {

			JFileChooser dialog = new JFileChooser(modeler.getWorkspace());
			GuiNode node = getModeler().getSelectedNode();

			File file = null;
			int returnVal = dialog.showOpenDialog(getModeler().getFrame());

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = dialog.getSelectedFile();
				javax.xml.bind.Unmarshaller unmarshaller = DataManager
						.getInstance().getJaxbContext().createUnmarshaller();
				ASimDO cD = (ASimDO) unmarshaller.unmarshal(new File(file
						.getAbsolutePath()));

				getModeler().saveNode(node);
				getModeler().selectNode(node);

				EntityManager em = getModeler().getElementManager()
						.getEntityManager();
				em.getTransaction().begin();

				CopyGroup cg = new CopyGroup();
				cg.addAttribute("children");
				cg.addAttribute("name");
				cg.addAttribute("type");
				cg.addAttribute("inTreeOrder");
				cg.addAttribute("properties");

				cg.cascadeAllParts();
				cg.setShouldResetPrimaryKey(true);

				ASimDO empCopy = (ASimDO) ((JpaEntityManager) em).copy(cD, cg);
				empCopy.setId(null);
				empCopy.setFather(node.getSourceElementData());
				node.getSourceElementData().getChildren().add(empCopy);
				updateFather(empCopy, em);
				em.persist(empCopy);
				em.getTransaction().commit();
				node.saveData(em);

				em.getTransaction().begin();
				getModeler().loadNode(node, empCopy);
				em.getTransaction().commit();

			} else {
				;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return getDec();
	}

	public void updateFather(ASimDO data, EntityManager em) {
		em.persist(data);
		for (ASimPO p : data.getProperties()) {
			p.setFather(data);
		}
		for (ASimDO d : data.getChildren()) {
			d.setFather(data);
			updateFather(d, em);
		}
	}

}
