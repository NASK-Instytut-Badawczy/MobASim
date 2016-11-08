package pl.edu.asim.gui.actions;

import java.awt.event.ActionEvent;
import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.swing.ImageIcon;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;
import pl.edu.asim.model.DataManager;

public class UpdateAction extends ModelerAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UpdateAction(String name) {
		super(name);
	}

	public UpdateAction(String name, ImageIcon icon) {
		super(name, icon);
	}

	@Override
	public int operation(ActionEvent e) {
		try {

			String a_name = this.getModeler().getSelectedNode()
					.getPropertyMap().get("field_name").getField().getText();
			String a_value = this.getModeler().getSelectedNode()
					.getPropertyMap().get("field_value").getField().getText();

			ASimDO data = getModeler().getSelectedNode().getSourceElementData();
			EntityManager em = DataManager.getInstance().getEntityManager();
			for (ASimDO node : data.getChildren()) {
				em.getTransaction().begin();
				update(node, a_name, a_value);
				// for (ASimPO param : node.getProperties()) {
				// if (param.getCode().equals(a_name)) {
				// if (a_value.startsWith("$-$")) {
				// param.setValue(new BigDecimal(param.getValue())
				// .subtract(
				// new BigDecimal(a_value.substring(4)))
				// .toString());
				// } else if (a_value.startsWith("$+$")) {
				// param.setValue(new BigDecimal(param.getValue())
				// .add(new BigDecimal(a_value.substring(4)))
				// .toString());
				// } else
				// param.setValue(a_value);
				// }
				// }
				em.getTransaction().commit();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return getDec();
	}

	private void update(ASimDO node, String a_name, String a_value) {

		for (ASimPO param : node.getProperties()) {
			if (param.getCode().equals(a_name)) {
				if (a_value.startsWith("$-$")) {
					param.setValue(new BigDecimal(param.getValue()).subtract(
							new BigDecimal(a_value.substring(4))).toString());
				} else if (a_value.startsWith("$+$")) {
					param.setValue(new BigDecimal(param.getValue()).add(
							new BigDecimal(a_value.substring(4))).toString());
				} else
					param.setValue(a_value);
			}
		}
		for (ASimDO subnode : node.getChildren()) {
			update(subnode, a_name, a_value);
		}
	}

}
