package pl.edu.asim.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import pl.edu.asim.gui.actions.ModelerAction;

public class ModelerMenu extends JMenuBar {

	JMenu modelerMenu;
	HashMap<String, JMenu> menus;
	Modeler modeler;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModelerMenu(Modeler modeler) {
		menus = new HashMap<String, JMenu>();
		this.modeler = modeler;
	}

	public JMenu getModelerMenu() {
		return modelerMenu;
	}

	public void setModelerMenu(JMenu modelerMenu) {
		this.modelerMenu = modelerMenu;
	}

	public HashMap<String, JMenu> getMenus() {
		return menus;
	}

	public void setMenus(HashMap<String, JMenu> menus) {
		this.menus = menus;
	}

	public void addModelerMenu(String name, Map<String, ModelerAction> actions) {
		if (modelerMenu != null) {
			this.removeAll();
			this.add(modelerMenu);
			modeler.getFrame().setJMenuBar(this);
			return;
		}
		modelerMenu = new JMenu(name);
		modelerMenu.setName(name);
		for (Iterator<String> it = actions.keySet().iterator(); it.hasNext();) {
			String menuName = it.next();
			ModelerAction action = actions.get(menuName);

			if (action.getName().equals("separator")) {
				modelerMenu.add(new JSeparator());
			} else {
				action.setModeler(modeler);
				JMenuItem item = new JMenuItem(menuName);
				item.setName(menuName);
				item.setAction(action);
				// item.setIcon(action.getIcon());
				modelerMenu.add(item);
			}
		}
		this.add(modelerMenu);
	}

	public void addMenu(String name, Map<String, ModelerAction> actions) {

		this.removeAll();
		this.add(modelerMenu);

		if (actions == null)
			return;
		JMenu mMenu = menus.get(name);
		if (mMenu == null) {
			mMenu = new JMenu(name);
			mMenu.setName(name);
			for (Iterator<String> it = actions.keySet().iterator(); it
					.hasNext();) {
				String menuName = it.next();
				ModelerAction action = actions.get(menuName);

				if (action.getName().equals("separator")) {
					mMenu.add(new JSeparator());
				} else {
					action.setModeler(modeler);
					JMenuItem item = new JMenuItem(menuName);
					item.setName(menuName);
					item.setAction(action);
					mMenu.add(item);
				}
			}
			menus.put(name, mMenu);
		}
		this.add(mMenu);
		modeler.getFrame().setJMenuBar(this);
	}

}
