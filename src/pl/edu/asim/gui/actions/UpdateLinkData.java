package pl.edu.asim.gui.actions;

import java.awt.event.ActionEvent;

import javax.persistence.EntityManager;
import javax.swing.ImageIcon;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.DataManager;
import pl.edu.econet.topology.Link;

public class UpdateLinkData extends ModelerAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UpdateLinkData(String name) {
		super(name);
	}

	public UpdateLinkData(String name, ImageIcon icon) {
		super(name, icon);
	}

	@Override
	public int operation(ActionEvent e) {
		try {
			ASimDO data = getModeler().getSelectedNode().getSourceElementData();
			EntityManager em = DataManager.getInstance().getEntityManager();
			for (ASimDO link : data.getChildren()) {
				em.getTransaction().begin();
				Link l = new Link();
				l.updateData(link);
				em.getTransaction().commit();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return getDec();
	}

}
