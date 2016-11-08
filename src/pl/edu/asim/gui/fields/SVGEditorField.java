package pl.edu.asim.gui.fields;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.text.Document;

import org.apache.batik.util.gui.xmleditor.XMLTextEditor;

import pl.edu.asim.gui.Attribute;
import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;

public class SVGEditorField extends JPanel implements ActionListener,
		AttributeInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String value = "";
	String firstValue = "";
	XMLTextEditor valueField;

	JButton undoButton;
	JButton pictureButton;
	JButton fullPictureButton;
	JPanel buttonPanel;
	private Attribute attribute;
	ArrayList<ModelerActionListener> modelerActionListenerList;
	boolean cascade = false;
	org.w3c.dom.Document doc;
	SVGEditorFrame svgFrame;
	private String extensions = "";
	private String defaultExportDirectory = "./";

	public SVGEditorField() {
		super(new BorderLayout());
		valueField = new XMLTextEditor();
		modelerActionListenerList = new ArrayList<ModelerActionListener>();
	}

	public String getSVGString(ASimDO data, boolean cascade) {
		String result = "";
		String width = "0";
		String height = "0";
		String x = "0";
		String y = "0";
		String visibility = "visible";
		String s = "";
		List<ASimPO> attributes = data.getProperties();
		for (ASimPO a : attributes) {
			if (a.getCode().equals("svg_height")) {
				height = a.getValue();
			} else if (a.getCode().equals("svg_width")) {
				width = a.getValue();
			} else if (a.getCode().equals("svg_visibility")) {
				visibility = a.getValue();
			} else if (a.getCode().equals("svg_x")) {
				x = a.getValue();
			} else if (a.getCode().equals("svg_y")) {
				y = a.getValue();
			} else if (a.getCode().equals("svg_editor")) {
				s = a.getValue();
			}
		}

		result = "<svg xmlns=\"" + attribute.getModeler().getSvg().getSvgNS()
				+ "\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"" + " x=\""
				+ x + "\" y=\"" + y + "\" width=\"" + width + "\" height=\""
				+ height + "\" visibility=\"" + visibility + "\" id=\""
				+ data.getId() + "\">" + s;

		if (cascade)
			for (ASimDO ado : data.getChildren()) {
				result = result + getSVGString(ado, cascade);
			}

		result = result + "</svg>";

		return result;
	}

	public void updateSVGDocument(ASimDO data, boolean cascade) {
		try {
			try {

				String newS = getSVGString(data, cascade);
				doc = attribute
						.getModeler()
						.getSvg()
						.getFactory()
						.createDocument(
								attribute.getModeler().getSvg().getSvgNS(),
								"svg", null, new StringReader(newS));
				if (svgFrame == null)
					svgFrame = new SVGEditorFrame(this, attribute.getModeler());
			} catch (org.apache.batik.dom.util.SAXIOException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void showField() {
		undoButton = (JButton) attribute.getModeler().getContext()
				.getBean("UNDO_button");
		undoButton.addActionListener(this);
		pictureButton = (JButton) attribute.getModeler().getContext()
				.getBean("REFRESH_button");
		pictureButton.addActionListener(this);
		fullPictureButton = (JButton) attribute.getModeler().getContext()
				.getBean("PLAY_button");
		fullPictureButton.addActionListener(this);
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(undoButton);
		buttonPanel.add(pictureButton);
		buttonPanel.add(fullPictureButton);

		this.removeAll();
		this.add(new JScrollPane(valueField), BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.EAST);
	}

	@Override
	public void hideField() {
		undoButton = null;
		buttonPanel = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == undoButton) {
			setText(firstValue);
		} else if (e.getSource() == pictureButton) {
			cascade = false;
			refreshFrame();
		} else if (e.getSource() == fullPictureButton) {
			cascade = true;
			refreshFrame();
		}
	}

	@Override
	public String getText() {
		return valueField.getText();
	}

	@Override
	public void setText(String s) {
		value = s;
		firstValue = s;
		valueField.setText(s);
	}

	@Override
	public Document getDocument() {
		return valueField.getDocument();
	}

	@Override
	public void setBorder(Border b) {
		if (valueField != null)
			valueField.setBorder(b);
		else
			super.setBorder(b);
	}

	@Override
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public void addActionListener(ActionListener l) {
		// valueField.addActionListener(l);
	}

	public void setActionCommand(String s) {
		// valueField.setActionCommand(s);
	}

	@Override
	public String getToolTipText() {
		return getText();
	}

	@Override
	public Component getComponent() {
		return valueField;
	}

	@Override
	public void addModelerActionListener(ModelerActionListener listener) {
		modelerActionListenerList.add(listener);
	}

	public void refreshFrame() {
		updateSVGDocument(attribute.getSourcePropertyData().getFather(),
				cascade);
		if (svgFrame == null) {
			svgFrame = new SVGEditorFrame(this, attribute.getModeler());
		} else {
			svgFrame.setVisible(false);
		}
		svgFrame.setDocument(doc);
		svgFrame.setVisible(true);
	}

	public String getExtensions() {
		return extensions;
	}

	public void setExtensions(String extensions) {
		this.extensions = extensions;
	}

	public String getDefaultExportDirectory() {
		return defaultExportDirectory;
	}

	public void setDefaultExportDirectory(String defaultExportDirectory) {
		this.defaultExportDirectory = defaultExportDirectory;
	}

	public String getCellsize() {
		ASimDO data = attribute.getFather().getSourceElementData();
		for (ASimPO p : data.getProperties()) {
			if (p.getCode().equals("cell_size")) {
				return p.getValue();
			}
		}
		return null;
	}

	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub

	}
}
