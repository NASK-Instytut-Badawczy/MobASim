package pl.edu.asim.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.osgi.framework.BundleContext;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;

import pl.edu.asim.gui.actions.FindAction;
import pl.edu.asim.gui.actions.ModelerExitAction;
import pl.edu.asim.interfaces.ASimModelerInterface;
import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimElementManager;
import pl.edu.asim.model.ASimPO;

//import org.eclipse.persistence.config.PersistenceUnitProperties;

public class Modeler implements Callable<String>, TreeSelectionListener,
		TreeModelListener, WindowStateListener, WindowListener,
		WindowFocusListener, ASimModelerInterface, ChangeListener {

	// private static final String MODELER_NAME = "MobASim Modeler";
	private Settings settings;
	private JFrame panel;
	private OsgiBundleXmlApplicationContext context;
	private org.apache.log4j.Logger logger;
	ASimElementManager elementManager;
	BundleContext bundleContext;
	double HEIGHT = 100;
	double WIDTH = 200;
	double WIDTH_LEFT = 225;
	double WIDTH_RIGHT = 75;
	double STATUS_HEIGHT = 40;

	JTree tree;
	DefaultTreeModel treeModel;

	JSplitPane splitPane;
	JScrollPane scrollPane;
	private GuiNode root;
	GuiNode selectedNode;
	TreePath path;
	JTextField findField;
	ModelerExitAction ea;
	ModelerMenu menu;

	// JAXBContext jaxbContext;
	Map<Long, GuiNode> nodeMap;

	static Modeler instance;
	boolean call = false;
	SVGManager svg;

	JScrollPane nodeScrollPane;

	Modeler() {
	}

	public static Modeler getInstance() {
		if (instance == null)
			instance = new Modeler();
		return instance;
	}

	@Override
	public String call() {

		if (call)
			return "ASimModelerMain";

		java.io.File logDir = new java.io.File("log/Modeler");

		if (!logDir.exists()) {
			logDir.mkdirs();
		}

		try {
			logger = org.apache.log4j.Logger.getLogger("pl.edu.asim");
			logger.info("MobASim Modeler: Start");

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			elementManager = ASimElementManager.getInstance();

			context = new OsgiBundleXmlApplicationContext();
			context.setBundleContext(this.getBundleContext());
			// context.setPublishContextAsService(true);
			context.normalRefresh();
			settings = (Settings) context.getBean("settings");
			// ServiceReference[] ref =
			// bundleContext.getAllServiceReferences(null, null);
			// for(int i =0; i<ref.length; i++) {
			// System.out.println(ref[i]);
			// }

			// jaxbContext = JAXBContext.newInstance(ASimPO.class,
			// ASimDO.class);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Modeler", e);
		}

		nodeMap = new HashMap<Long, GuiNode>();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		panel = new JFrame(settings.getDefaultModelerName());

		menu = new ModelerMenu(this);
		loadModeler();
		svg = new SVGManager(this);

		panel.setJMenuBar(menu);

		this.selectedNode = root;
		path = new TreePath(root);
		tree = new JTree(treeModel);
		tree.setCellRenderer(new TreeRenderer(this));
		tree.addTreeSelectionListener(this);
		tree.setExpandsSelectedPaths(true);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setEditable(false);
		tree.setRootVisible(true);
		tree.add(new TreePopup(this));

		ToolTipManager.sharedInstance().registerComponent(tree);

		this.scrollPane = new JScrollPane(tree);
		scrollPane
				.setMinimumSize(new Dimension((int) WIDTH_LEFT, (int) HEIGHT));

		JPanel left = new JPanel(new BorderLayout());
		left.setSize(new Dimension((int) WIDTH_LEFT, (int) HEIGHT));
		left.add(scrollPane, BorderLayout.CENTER);

		FindAction find = (FindAction) context.getBean("findElementAction");
		find.setModeler(this);
		left.add(find.getFind(), BorderLayout.PAGE_END);

		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, null);
		this.splitPane.setContinuousLayout(true);
		this.splitPane.setOneTouchExpandable(true);
		panel.getContentPane().add(splitPane, BorderLayout.CENTER);
		panel.addWindowStateListener(this);
		panel.addWindowFocusListener(this);
		panel.addWindowListener(this);

		panel.setSize(new Dimension(screenSize.width, screenSize.height - 30));
		panel.setVisible(true);
		panel.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		nodeScrollPane = new JScrollPane();
		// nodeTabbedPane.addChangeListener(this);

		showPanel(root);

		// this.splitPane.setDividerLocation(0.25);
		call = true;
		return "ASimModelerMain";

		// Bundle[] b = pl.edu.asim.gui.ModelerActivator.getBundles();
		// for(int i=0;i<b.length; i++) {
		// Bundle bn = b[i];
		// System.out.println(bn.getBundleId()+" "+bn.getSymbolicName());
		// }
	}

	public SVGManager getSvg() {
		return svg;
	}

	public void setSvg(SVGManager svg) {
		this.svg = svg;
	}

	// public JAXBContext getJaxbContext() {
	// return jaxbContext;
	// }
	//
	// public void setJaxbContext(JAXBContext jaxbContext) {
	// this.jaxbContext = jaxbContext;
	// }

	public void loadModeler() {

		ASimDO modelerElement = null;

		List<ASimDO> modelerList = elementManager.getElementListByTypeAndName(
				settings.getDefaultModelerType(),
				settings.getDefaultModelerName());

		if (modelerList.isEmpty()) {
			createModeler();
		} else {
			modelerElement = modelerList.get(0);
			loadNode(null, modelerElement);
		}

		menu.addModelerMenu(root.getName(), root.getActionMap());

		ea = (ModelerExitAction) root.getActionMap().get("Exit");
		ea.setModeler(this);

	}

	public static void main(String[] args) {
		Modeler modeler = new Modeler();
		modeler.call();
	}

	@Override
	public void windowStateChanged(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		ea.actionPerformed(null);
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowGainedFocus(WindowEvent e) {
	}

	@Override
	public void windowLostFocus(WindowEvent e) {
	}

	@Override
	public void treeNodesChanged(TreeModelEvent e) {
	}

	@Override
	public void treeNodesInserted(TreeModelEvent e) {
	}

	@Override
	public void treeNodesRemoved(TreeModelEvent e) {
	}

	@Override
	public void treeStructureChanged(TreeModelEvent e) {
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		Object n = tree.getLastSelectedPathComponent();

		if (n instanceof GuiNode && n != selectedNode) {
			selectNode((GuiNode) n);
		}
		panel.setCursor(Cursor.getDefaultCursor());
	}

	public void selectNode(GuiNode node) {
		selectedNode.saveData(elementManager.getEntityManager());
		selectedNode.hidePanel();
		// svg.hideModel(selectedNode);
		selectedNode = node;

		path = new TreePath(selectedNode.getPath());
		tree.expandPath(path);
		tree.setSelectionPath(path);
		tree.scrollPathToVisible(path);
		showPanel(selectedNode);

	}

	public void showPanel(GuiNode g) {
		try {
			int location = splitPane.getDividerLocation();

			nodeScrollPane.removeAll();
			nodeScrollPane = new JScrollPane(g.getPanel());
			// nodeTabbedPane.insertTab(g.getName(), null, g.getPanel(),
			// g.getName(), 0);
			// nodeTabbedPane.addTab("SVG", svg.getEditor());

			splitPane.setRightComponent(nodeScrollPane);
			if (g != root)
				menu.addMenu(g.getName(), g.getActionMap());
			else
				menu.addModelerMenu(g.getName(), g.getActionMap());

			splitPane.repaint();
			splitPane.setDividerLocation(location);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createModeler() {
		GuiNode node = (GuiNode) context.getBean(settings
				.getDefaultModelerType());
		node.setModeler(this);
		root = node;
		root.generateSourceElementData(elementManager.getEntityManager());
		treeModel = new DefaultTreeModel(root);
		treeModel.addTreeModelListener(this);
		root.saveData(elementManager.getEntityManager());
		nodeMap.put(root.getSourceElementData().getId(), root);

		if (root.getSubmodelMap() != null)
			for (Iterator<GuiNode> it = root.getSubmodelMap().values()
					.iterator(); it.hasNext();) {
				GuiNode submodel = it.next();
				addNewNode(root, submodel);
			}
	}

	public void addNewNode(GuiNode parent, String nodeType) {
		try {
			GuiNode node = (GuiNode) context.getBean(nodeType);
			node.setModeler(this);
			addNewNode(parent, node);
			selectNode(node);
		} catch (Exception e) {
			logger.error(nodeType, e);
		}
	}

	public void addNewNode(GuiNode parent, GuiNode child) {
		child.setModeler(this);
		int index = parent.getChildCount();
		if(child.getType().compareTo("SIMULATORS") == 0 && index == 4){

		} else {
			treeModel.insertNodeInto(child, parent, index);
		}
		child.generateSourceElementData(elementManager.getEntityManager());
		nodeMap.put(child.getSourceElementData().getId(), child);

		if (child.getSubmodelMap() != null)
			for (Iterator<GuiNode> it = child.getSubmodelMap().values()
					.iterator(); it.hasNext();) {
				GuiNode submodel = it.next();
				addNewNode(child, submodel);
			}
	}

	public void importNode(GuiNode parent, ASimDO element) {
		String type = element.getType();
		if (!context.containsBean(type))
			return;
		GuiNode node = (GuiNode) context.getBean(type);
		importNode(node, parent, element);
	}

	public void importNode(GuiNode node, GuiNode parent, ASimDO element) {

		node.setModeler(this);
		node.loadData(element, elementManager.getEntityManager());
		node.getSourceElementData().setFather(parent.getSourceElementData());
		if (!parent.getSourceElementData().getChildren()
				.contains(node.getSourceElementData())) {
			parent.getSourceElementData().getChildren()
					.add(node.getSourceElementData());
		}
		saveNode(node);
		saveNode(parent);
		nodeMap.put(node.getSourceElementData().getId(), node);

		int index = 0;
		for (@SuppressWarnings("unchecked")
		Enumeration<DefaultMutableTreeNode> e = parent.children(); e
				.hasMoreElements();) {
			GuiNode gn = (GuiNode) e.nextElement();
			if (gn.getSourceElementData().getOrder() > element.getOrder())
				break;
			index++;
		}
		treeModel.insertNodeInto(node, parent, index);

		for (Iterator<ASimDO> it = element.getChildren().iterator(); it
				.hasNext();) {
			ASimDO e = it.next();
			importNode(node, e);
		}
	}

	public void loadNode(GuiNode parent, ASimDO element) {
		String type = element.getType();
		if (type == null || type.equals("") || !context.containsBean(type))
			return;
		GuiNode node = (GuiNode) context.getBean(type);
		loadNode(node, parent, element);
	}

	public void loadNode(GuiNode node, GuiNode parent, ASimDO element) {

		node.setModeler(this);
		node.loadData(element, elementManager.getEntityManager());
		nodeMap.put(node.getSourceElementData().getId(), node);
		if (parent != null) {
			int index = 0;
			for (@SuppressWarnings("unchecked")
			Enumeration<DefaultMutableTreeNode> e = parent.children(); e
					.hasMoreElements();) {
				GuiNode gn = (GuiNode) e.nextElement();
				if (gn.getSourceElementData().getOrder() > element.getOrder())
					break;
				index++;
			}
			try {
				treeModel.insertNodeInto(node, parent, index);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		} else {
			root = node;
			treeModel = new DefaultTreeModel(root);
			treeModel.addTreeModelListener(this);
		}

		if (node.getPropertyMap() != null)
			for (Iterator<Attribute> it = node.getPropertyMap().values()
					.iterator(); it.hasNext();) {
				Attribute a = it.next();
				a.setModeler(this);
				boolean hit = false;
				for (Iterator<ASimPO> itp = node.getSourceElementData()
						.getProperties().iterator(); itp.hasNext();) {
					ASimPO ap = itp.next();
					if (ap.getCode().equals(a.getName())) {
						a.setSourcePropertyData(ap);
						hit = true;
						break;
					}
				}
				if (!hit) {
					ASimPO ap = new ASimPO();
					ap.setFather(node.getSourceElementData());
					ap.setCode(a.getName());
					ap.setValue(a.getDefaultValue());
					node.getSourceElementData().getProperties().add(ap);
					a.setSourcePropertyData(ap);
					elementManager.getEntityManager().getTransaction().begin();
					elementManager.getEntityManager().persist(ap);
					elementManager.getEntityManager().getTransaction().commit();
				}
			}

		Set<String> set = new HashSet<String>();
		for (Iterator<ASimDO> it = element.getChildren().iterator(); it
				.hasNext();) {
			ASimDO ae = it.next();

			if (node.getSubmodelMap() != null
					&& node.getSubmodelMap().get(ae.getType()) != null) {
				GuiNode submodel = node.getSubmodelMap().get(ae.getType());
				loadNode(submodel, node, ae);
				set.add(ae.getName());
			} else {
				loadNode(node, ae);
			}
		}

		if (node.getSubmodelMap() != null)
			for (Iterator<GuiNode> it = node.getSubmodelMap().values()
					.iterator(); it.hasNext();) {
				GuiNode subModel = it.next();
				if (!set.contains(subModel.getName())) {
					addNewNode(node, subModel);
				}
			}
	}

	public void deleteNode(GuiNode node) {
		if (node == selectedNode) {
			node.hidePanel();
			selectedNode = (GuiNode) node.getParent();
			path = new TreePath(selectedNode);
			tree.expandPath(path);
			tree.setSelectionPath(path);
			tree.scrollPathToVisible(path);
			showPanel(selectedNode);
		}
		node.removeData(elementManager.getEntityManager());
		treeModel.removeNodeFromParent(node);
	}

	public void saveNode(GuiNode node) {
		node.saveData(elementManager.getEntityManager());
	}

	public JFrame getFrame() {
		return panel;
	}

	public GuiNode getSelectedNode() {
		return selectedNode;
	}

	public ApplicationContext getContext() {
		return context;
	}

	public Map<Long, GuiNode> getNodeMap() {
		return nodeMap;
	}

	public void setNodeMap(Map<Long, GuiNode> nodeMap) {
		this.nodeMap = nodeMap;
	}

	@Override
	public void close() {
		if (call) {
			saveNode(getSelectedNode());
			// svg.close();
			getFrame().dispose();
			call = false;
		}
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}

	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	// public void prepareSVGEditor() {
	// svg = new DefaultSVGEditorField();
	// svg.setModeler(this);
	// svg.setPreferredSize((Dimension)context.getBean("svgFieldDimension"));
	// Attribute a = root.getPropertyMap().get("svg_model");
	// String svgPath = a.getSourcePropertyData().getPropertyValue();
	//
	// File file = new File(svgPath);
	// svg.setDocument(file);
	// svg.showField();
	// }

	public String getSVGFilePath() {
		Attribute a = root.getPropertyMap().get("svg_model");
		String svgPath = a.getSourcePropertyData().getValue();
		return svgPath;
	}

	public String getWorkspace() {
		Attribute a = root.getPropertyMap().get("workspace");
		String svgPath = a.getSourcePropertyData().getValue();
		return svgPath;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// if (e.getSource().equals(nodeTabbedPane)) {
		// if (nodeTabbedPane.getSelectedIndex() == 0) {
		//
		// } // else if(nodeTabbedPane.getSelectedIndex()==1) {
		// // svg.showModel(this.getSelectedNode());
		// // }
		// }
	}

	public String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	public GuiNode getRoot() {
		return root;
	}

	public void setRoot(GuiNode root) {
		this.root = root;
	}

	public ASimElementManager getElementManager() {
		return elementManager;
	}

	public void setElementManager(ASimElementManager elementManager) {
		this.elementManager = elementManager;
	}

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(DefaultTreeModel treeModel) {
		this.treeModel = treeModel;
	}

}
