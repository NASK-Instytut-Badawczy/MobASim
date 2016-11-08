package pl.edu.asim.gui.fields.svg;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;

import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;

import pl.edu.asim.gui.SVGManager;
import pl.edu.asim.gui.fields.SVGEditorFrame;
import pl.edu.asim.gui.fields.FileField;

public class DefaultSVGEditorField extends JPanel implements ActionListener,
		ChangeListener {

	/**
	 * 
	 */

	private org.w3c.dom.Element element;

	private static final long serialVersionUID = 1L;
	JTabbedPane editorPane;
	JTextArea svgPanel;
	JTabbedPane elementPane;
	JScrollPane outScroll;

	JButton plusB;
	JButton minusB;
	JButton upB;
	JButton downB;
	JButton refreshB;
	JButton duplicate;

	// HashMap<Integer, SVGElement> elementMap;
	// HashMap<Integer, Integer> indexMap;

	String name;
	boolean isMousePressed = false;
	int X = -1;
	int Y = -1;

	SVGEditorFrame svgFrame;
	SVGManager manager;

	public DefaultSVGEditorField(SVGManager manager) {
		super(null);
		this.manager = manager;
		this.setName("SVG_FIELD");
		try {
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			svgPanel = new JTextArea();

			elementPane = new JTabbedPane(JTabbedPane.TOP);
			elementPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
			editorPane = new JTabbedPane();
			editorPane.addTab("Element", elementPane);
			editorPane.addTab("SVG", new JScrollPane(svgPanel));
			editorPane.setBounds(35, 5, 590, 500);
			editorPane.addChangeListener(this);

			plusB = (JButton) manager.getModeler().getContext()
					.getBean("ADD_button");
			plusB.addActionListener(this);
			plusB.setActionCommand("ADD");
			minusB = (JButton) manager.getModeler().getContext()
					.getBean("DEL_button");
			minusB.addActionListener(this);
			minusB.setActionCommand("DEL");
			upB = (JButton) manager.getModeler().getContext()
					.getBean("NEXT_button");
			upB.addActionListener(this);
			upB.setActionCommand("DOWN");
			upB.setToolTipText("to front");
			downB = (JButton) manager.getModeler().getContext()
					.getBean("PREVIOUS_button");
			downB.addActionListener(this);
			downB.setActionCommand("UP");
			downB.setToolTipText("to back");
			refreshB = (JButton) manager.getModeler().getContext()
					.getBean("REFRESH_button");
			refreshB.addActionListener(this);
			refreshB.setActionCommand("REFRESH");
			refreshB.setToolTipText("refresh");
			duplicate = (JButton) manager.getModeler().getContext()
					.getBean("DUPLICATE_button");
			duplicate.addActionListener(this);
			duplicate.setActionCommand("DUPLICATE");
			duplicate.setToolTipText("duplicate");

			JPanel buttonPanel = new JPanel();
			buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			buttonPanel.setBounds(5, 5, 25, 590);
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
			buttonPanel.add(refreshB);
			buttonPanel.add(duplicate);
			buttonPanel.add(plusB);
			buttonPanel.add(minusB);
			buttonPanel.add(upB);
			buttonPanel.add(downB);

			JPanel p1 = new JPanel(null);
			p1.setPreferredSize(new Dimension(595, 570));
			p1.add(editorPane);
			p1.add(buttonPanel);
			p1.setAlignmentX(Component.LEFT_ALIGNMENT);
			this.add(p1);
			init();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public org.w3c.dom.Element getElement() {
		return element;
	}

	public void setElement(org.w3c.dom.Element element) {
		init();
		this.element = element;
		loadElements();
	}

	private void init() {
		// setElementMap(new HashMap<Integer, SVGElement>());
		// setIndexMap(new HashMap<Integer, Integer>());
		editorPane.setSelectedIndex(0);
	}

	public void loadElements() {

		elementPane.removeAll();
		org.w3c.dom.NodeList nl = element.getChildNodes();
		int childCount = nl.getLength();
		int index = 0;
		for (int i = 0; i < childCount; i++) {
			if (nl.item(i) instanceof org.w3c.dom.Element) {

				org.w3c.dom.Node child = nl.item(i);
				String nodeName = child.getNodeName();

				if (manager.getModeler().getContext().containsBean(nodeName)) {
					SVGElementPane elementP = (SVGElementPane) manager
							.getModeler().getContext().getBean(nodeName);
					elementP.setModeler(manager.getModeler());
					elementP.setIndex(index);
					elementP.setEditor(this);
					elementP.setElement(child);
					elementP.showField();
					elementPane.addTab(
							((org.w3c.dom.Element) child).getAttribute("id"),
							elementP);
					index++;
				}
			}
		}
	}

	public void setDocument(String s) {
		try {
			try {
				String newS = "<svg xmlns=\"" + manager.getSvgNS() + "\" >" + s
						+ "</svg>";

				org.w3c.dom.Document doc = manager.getFactory()
						.createDocument(manager.getSvgNS(), "svg", null,
								new StringReader(newS));
				element = (org.w3c.dom.Element) doc.getDocumentElement()
						.getFirstChild();
				// if (svgFrame == null)
				// svgFrame = new DefaultSVGFrame(this, manager.getModeler());
			} catch (org.apache.batik.dom.util.SAXIOException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void reloadSVGString() {
		try {
			StringWriter sw = new StringWriter(4096);
			DOMUtilities.writeNode(element, sw);
			sw.flush();
			String result = sw.toString();
			result = result.replaceAll(">", ">\n");
			svgPanel.setText(result);
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {

		org.w3c.dom.Element root = null;

		synchronized (this) {
			root = element;
			if (root == null)
				return;
			if (e.getSource().equals(editorPane)) {
				int selected = editorPane.getSelectedIndex();
				if (selected == 0) {
					String newS = svgPanel.getText();

					newS = newS.replaceAll(">\n", ">");
					if (newS != null && !newS.equals(""))
						setDocument(newS);
//					manager.reloadNode();
					svgPanel.setText("");
				} else if (selected == 1) {
					this.reloadSVGString();
					if (svgFrame != null)
						svgFrame.setVisible(false);
				}
			}
		}

	}

	public int getElementsCount() {

		org.w3c.dom.NodeList nl = element.getChildNodes();
		int childCount = nl.getLength();
		int index = 0;
		for (int i = 0; i < childCount; i++) {
			if (nl.item(i) instanceof org.w3c.dom.Element) {
				org.w3c.dom.Element child = (org.w3c.dom.Element) nl.item(i);
				if (child != null && !child.getNodeName().equals("symbol")
						&& !child.getNodeName().equals("paint")
						&& !child.getNodeName().equals("defs")) {
					index++;
				}
			}
		}
		return index;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ADD")) {
			int index = elementPane.getTabCount();
			int elementIndex = element.getChildNodes().getLength();
			SVGElement element = new SVGElement(elementIndex, "E_"
					+ (index + 1), manager.getSvgNS(), manager.getImpl(), this);
			element.showField();
			JScrollPane sc = new JScrollPane(element,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			elementPane.add(sc, index);
			elementPane.setSelectedIndex(index);
			elementPane.setTitleAt(index, "E_" + (index + 1));
			element.select(SVGConstant.TYPE_0);

			this.element.appendChild(element.getElement());
			// getElementMap().put(element.index, element);
		} else if (e.getActionCommand().equals("DOWN")) {
			int index = elementPane.getSelectedIndex();
			if (index < elementPane.getTabCount() - 1) {
				JScrollPane sc = (JScrollPane) elementPane
						.getSelectedComponent();
				SVGElement c = (SVGElement) ((JViewport) sc.getComponent(0))
						.getComponent(0);
				String title = elementPane.getTitleAt(index);

				JScrollPane sc2 = (JScrollPane) elementPane
						.getComponentAt(index + 1);
				SVGElement c2 = (SVGElement) ((JViewport) sc2.getComponent(0))
						.getComponent(0);

				elementPane.removeTabAt(index);

				element.removeChild(element.getChildNodes().item(
						c.index.intValue()));
				elementPane.add(sc, index + 1);
				elementPane.setTitleAt(index + 1, title);
				elementPane.setSelectedIndex(index + 1);
				element.insertBefore(c.getElement(), element.getChildNodes()
						.item(c2.index.intValue()));
			}
		} else if (e.getActionCommand().equals("UP")) {
			int index = elementPane.getSelectedIndex();
			if (index > 0) {
				JScrollPane sc = (JScrollPane) elementPane
						.getSelectedComponent();
				SVGElement c = (SVGElement) ((JViewport) sc.getComponent(0))
						.getComponent(0);
				String title = elementPane.getTitleAt(index);

				JScrollPane sc2 = (JScrollPane) elementPane
						.getComponentAt(index - 1);
				SVGElement c2 = (SVGElement) ((JViewport) sc2.getComponent(0))
						.getComponent(0);

				elementPane.removeTabAt(index);

				element.removeChild(element.getChildNodes().item(
						c.index.intValue()));
				elementPane.add(sc, index - 1);
				elementPane.setTitleAt(index - 1, title);
				elementPane.setSelectedIndex(index - 1);
				element.insertBefore(c.getElement(), element.getChildNodes()
						.item(c2.index.intValue()));
			}
		} else if (e.getActionCommand().equals("DEL")) {
			int index = elementPane.getSelectedIndex();
			SVGElement c = getElementAt(index);
			// getElementMap().remove(c.index);
			if (index >= 0) {
				elementPane.removeTabAt(index);
				elementPane.setSelectedIndex(index - 1);
				element.removeChild(element.getChildNodes().item(
						c.index.intValue()));
			}
		} else if (e.getActionCommand().equals("REFRESH")) {
			refreshFrame();
		} else if (e.getActionCommand().equals("DUPLICATE")) {
			int index2 = elementPane.getSelectedIndex();
			SVGElement c = getElementAt(index2);
			org.w3c.dom.Node element2 = c.getElement();
			element.appendChild(manager.getSvgDocument().importNode(element2,
					true));
			editorPane.setSelectedIndex(1);
			editorPane.setSelectedIndex(0);

		}
	}

	public Component getComponent() {
		return svgPanel;
	}

	public SVGElement getElementAt(int index) {
		synchronized (DefaultSVGEditorField.class) {
			JScrollPane sc = null;
			if (elementPane == null || index < 0
					|| elementPane.getComponentCount() < index)
				return null;
			sc = (JScrollPane) elementPane.getComponentAt(index);
			return (SVGElement) ((JViewport) sc.getComponent(0))
					.getComponent(0);
		}
	}

	public Document getDocument() {
		return svgPanel.getDocument();
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public void exportToImage() {

		FileField fc = new FileField();
		fc.setText("../results");
		// fc.setModeler(manager.getModeler());

		fc.showField();
		File newFile = fc.getSaveFile();
		if (newFile == null)
			return;
		ImageTranscoder imt = null;
		TranscoderInput input = null;

		org.w3c.dom.Document d = manager.getNewDocument();

		d.replaceChild(d.importNode(element, true), d.getDocumentElement());
		try {
			input = new TranscoderInput(d);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (newFile.toURI().toString().endsWith("jpg")
					|| newFile.toURI().toString().endsWith("jpeg")) {
				imt = new JPEGTranscoder();
				imt.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(
						1.0));
				OutputStream ostream = new FileOutputStream(newFile);
				TranscoderOutput output = new TranscoderOutput(ostream);
				imt.transcode(input, output);
				ostream.flush();
				ostream.close();
			} else if (newFile.toURI().toString().endsWith("tif")
					|| newFile.toURI().toString().endsWith("tiff")) {
				imt = new TIFFTranscoder();
				OutputStream ostream = new FileOutputStream(newFile);
				TranscoderOutput output = new TranscoderOutput(ostream);
				imt.transcode(input, output);
				ostream.flush();
				ostream.close();
			} else if (newFile.toURI().toString().endsWith("png")) {
				imt = new PNGTranscoder();
				OutputStream ostream = new FileOutputStream(newFile);
				TranscoderOutput output = new TranscoderOutput(ostream);
				imt.transcode(input, output);
				ostream.flush();
				ostream.close();
			} else if (newFile.toURI().toString().endsWith("svg")) {
				try {
					FileWriter sw = new FileWriter(newFile);
					DOMUtilities.writeDocument(d, sw);
					sw.flush();
					sw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void exportToImage(String fileName) {

		File newFile = new File(fileName);
		ImageTranscoder imt = null;
		TranscoderInput input = null;

		org.w3c.dom.Document d = manager.getNewDocument();

		d.replaceChild(d.importNode(element, true), d.getDocumentElement());
		try {
			input = new TranscoderInput(d);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			if (newFile.toURI().toString().endsWith("jpg")
					|| newFile.toURI().toString().endsWith("jpeg")) {
				imt = new JPEGTranscoder();
				imt.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(
						1.0));
				OutputStream ostream = new FileOutputStream(newFile);
				TranscoderOutput output = new TranscoderOutput(ostream);
				imt.transcode(input, output);
				ostream.flush();
				ostream.close();
			} else if (newFile.toURI().toString().endsWith("tif")
					|| newFile.toURI().toString().endsWith("tiff")) {
				imt = new TIFFTranscoder();
				OutputStream ostream = new FileOutputStream(newFile);
				TranscoderOutput output = new TranscoderOutput(ostream);
				imt.transcode(input, output);
				ostream.flush();
				ostream.close();
			} else if (newFile.toURI().toString().endsWith("png")) {
				imt = new PNGTranscoder();
				OutputStream ostream = new FileOutputStream(newFile);
				TranscoderOutput output = new TranscoderOutput(ostream);
				imt.transcode(input, output);
				ostream.flush();
				ostream.close();
			} else if (newFile.toURI().toString().endsWith("svg")) {
				try {
					FileWriter sw = new FileWriter(newFile);
					DOMUtilities.writeDocument(d, sw);
					sw.flush();
					sw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// public HashMap<Integer, SVGElement> getElementMap() {
	// return elementMap;
	// }
	//
	// public void setElementMap(HashMap<Integer, SVGElement> elementMap) {
	// this.elementMap = elementMap;
	// }
	//
	// public HashMap<Integer, Integer> getIndexMap() {
	// return indexMap;
	// }
	//
	// public void setIndexMap(HashMap<Integer, Integer> indexMap) {
	// this.indexMap = indexMap;
	// }

	public JTabbedPane getElementPane() {
		return elementPane;
	}

	public org.w3c.dom.Document getEncapsulatedDoc() {
		org.w3c.dom.Document doc = manager.getNewDocument();
		org.w3c.dom.Element newElement = (org.w3c.dom.Element) doc.importNode(
				element, true);
		newElement.setAttributeNS(null, "visibility", "visible");
		doc.replaceChild(newElement, doc.getDocumentElement());
		return doc;
	}

	public void refreshFrame() {
		// if (svgFrame == null) {
		// svgFrame = new DefaultSVGFrame(this, manager.getModeler());
		// } else {
		// svgFrame.setVisible(false);
		// org.w3c.dom.Document doc = getEncapsulatedDoc();
		// svgFrame.setDocument(doc);
		// }
		// svgFrame.setVisible(true);
	}
}
