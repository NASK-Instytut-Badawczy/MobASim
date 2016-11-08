package pl.edu.asim.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.tree.DefaultMutableTreeNode;

import pl.edu.asim.gui.actions.ModelerAction;
import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;

public class GuiNode extends DefaultMutableTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String nameLabel;
	private boolean nameBlocked = false;
	private String type;
	private Map<String, Attribute> propertyMap;
	private Map<String, GuiNode> submodelMap;
	private Map<String, ModelerAction> actionMap;
	private org.w3c.dom.Element svgElement;

	private ASimDO sourceElementData;
	private JPanel panel;
	private Modeler modeler;

	NameAttribute nameAttribute;
	IdAttribute idAttribute;
	TypeAttribute typeAttribute;

	public Modeler getModeler() {
		return modeler;
	}

	public void setModeler(Modeler modeler) {
		this.modeler = modeler;
	}

	public Map<String, GuiNode> getSubmodelMap() {
		return submodelMap;
	}

	public void setSubmodelMap(Map<String, GuiNode> submodelMap) {
		this.submodelMap = submodelMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.setUserObject(name);
		this.name = name;
	}

	public Map<String, Attribute> getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map<String, Attribute> propertyMap) {
		this.propertyMap = propertyMap;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public ASimDO getSourceElementData() {
		return sourceElementData;
	}

	public void setSourceElementData(ASimDO sourceElementData) {
		this.sourceElementData = sourceElementData;
	}

	public void loadData(ASimDO sourceElementData, EntityManager em) {
		setSourceElementData(sourceElementData);
		this.name = sourceElementData.getName();
		this.type = sourceElementData.getType();
		this.setUserObject(name);
		for (Iterator<ASimPO> it = sourceElementData.getProperties().iterator(); it
				.hasNext();) {
			ASimPO ap = it.next();
			Attribute a = this.propertyMap.get(ap.getCode());
			if (a != null) {
				a.setModeler(modeler);
				a.setFather(this);
				a.setSourcePropertyData(ap);
			} else {

			}
		}
	}

	public void saveData(EntityManager em) {
		if (sourceElementData != null) {
			em.getTransaction().begin();
			em.persist(sourceElementData);
			em.getTransaction().commit();
		}
	}

	public void removeData(EntityManager em) {
		try {
			if (sourceElementData != null) {
				em.getTransaction().begin();
				em.remove(sourceElementData);
				em.getTransaction().commit();
				if (sourceElementData.getFather() != null)
					sourceElementData.getFather().getChildren()
							.remove(sourceElementData);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public JPanel getPanel() {
		showPanel();
		return panel;
	}

	public void showPanel() {
		// panel = new JTabbedPane();

		panel = new JPanel();
		panel.setLayout(null);

		int y = 10;

		Attribute an = new Attribute();
		an.setModeler(modeler);
		nameAttribute = (NameAttribute) modeler.getContext().getBean("name");
		nameAttribute.setFather(this);
		nameAttribute.setAttribute(an);
		JPanel namePanel = nameAttribute.show();
		nameAttribute.getField().getComponent().setEnabled(!nameBlocked);
		namePanel.setBounds(5, y, namePanel.getPreferredSize().width,
				namePanel.getPreferredSize().height);
		panel.add(namePanel);
		y = y + namePanel.getPreferredSize().height + 10;
		JSeparator separator = new JSeparator();
		separator.setBounds(5, y - 10,
		// att.getWidth().intValue() +
				namePanel.getPreferredSize().width, 2);
		separator.setForeground(Color.LIGHT_GRAY);
		panel.add(separator);

		Attribute ai = new Attribute();
		ai.setModeler(modeler);
		idAttribute = (IdAttribute) modeler.getContext().getBean("id");
		idAttribute.setFather(this);
		idAttribute.setAttribute(ai);
		JPanel idPanel = idAttribute.show();
		idPanel.setBounds(5, y, idPanel.getPreferredSize().width,
				idPanel.getPreferredSize().height);
		panel.add(idPanel);
		y = y + idPanel.getPreferredSize().height + 10;
		separator = new JSeparator();
		separator.setBounds(5, y - 10,
		// att.getWidth().intValue() +
				namePanel.getPreferredSize().width, 2);
		separator.setForeground(Color.LIGHT_GRAY);
		panel.add(separator);

		Attribute at = new Attribute();
		at.setModeler(modeler);
		typeAttribute = (TypeAttribute) modeler.getContext().getBean("type");
		typeAttribute.setFather(this);
		typeAttribute.setAttribute(at);
		JPanel typePanel = typeAttribute.show();
		typePanel.setBounds(5, y, typePanel.getPreferredSize().width,
				typePanel.getPreferredSize().height);
		panel.add(typePanel);
		y = y + typePanel.getPreferredSize().height + 10;
		separator = new JSeparator();
		separator.setBounds(5, y - 10,
		// att.getWidth().intValue() +
				typePanel.getPreferredSize().width, 2);
		separator.setForeground(Color.LIGHT_GRAY);
		panel.add(separator);

		if (propertyMap != null)
			for (Iterator<Attribute> it = propertyMap.values().iterator(); it
					.hasNext();) {
				Attribute a = it.next();
				a.setModeler(modeler);
				JPanel attributePanel = a.show();
				attributePanel.setBounds(5, y,
						attributePanel.getPreferredSize().width,
						attributePanel.getPreferredSize().height);
				panel.add(attributePanel);
				y = y + attributePanel.getPreferredSize().height + 10;
				separator = new JSeparator();
				separator.setBounds(5, y - 10,
				// att.getWidth().intValue() +
						attributePanel.getPreferredSize().width, 2);
				separator.setForeground(Color.LIGHT_GRAY);
				panel.add(separator);
			}

		panel.setPreferredSize(new Dimension(
				namePanel.getPreferredSize().width, y));
	}

	public void hidePanel() {
		nameAttribute.hide();
		idAttribute.hide();
		if (propertyMap != null)
			for (Iterator<Attribute> it = propertyMap.values().iterator(); it
					.hasNext();) {
				Attribute a = it.next();
				a.hide();
			}
		if (panel != null)
			panel.removeAll();
		panel = null;
	}

	public void generateSourceElementData(EntityManager em) {
		sourceElementData = new ASimDO();
		if (this.getParent() != null) {
			GuiNode parent = (GuiNode) this.getParent();
			sourceElementData.setFather(parent.getSourceElementData());
			parent.getSourceElementData().getChildren().add(sourceElementData);
		}
		sourceElementData.setName(this.getName());
		sourceElementData.setType(this.getType());
		saveData(em);

		if (propertyMap != null)
			for (Iterator<Attribute> it = propertyMap.values().iterator(); it
					.hasNext();) {
				Attribute a = it.next();
				a.setModeler(modeler);
				a.setFather(this);
				ASimPO ap = new ASimPO();
				ap.setFather(sourceElementData);
				ap.setCode(a.getName());
				ap.setValue(a.getDefaultValue());
				sourceElementData.getProperties().add(ap);
				a.setSourcePropertyData(ap);
				em.getTransaction().begin();
				em.persist(ap);
				em.getTransaction().commit();
			}
	}

	public Map<String, ModelerAction> getActionMap() {
		return actionMap;
	}

	public void setActionMap(Map<String, ModelerAction> actionMap) {
		this.actionMap = actionMap;
	}

	public String getNameLabel() {
		return nameLabel;
	}

	public void setNameLabel(String nameLabel) {
		this.nameLabel = nameLabel;
	}

	public org.w3c.dom.Element getSvgElement() {
		return svgElement;
	}

	public void setSvgElement(org.w3c.dom.Element svgElement) {
		this.svgElement = svgElement;
	}

	public void setNameBlocked(boolean nameBlocked) {
		this.nameBlocked = nameBlocked;
	}

	public boolean isNameBlocked() {
		return nameBlocked;
	}

}
