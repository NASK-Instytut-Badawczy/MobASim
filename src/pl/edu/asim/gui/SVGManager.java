package pl.edu.asim.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.net.URL;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.DOMImplementation;

public class SVGManager {

	File svgFile;
	Modeler modeler;
	// DefaultSVGEditorField editor;
	String path;
	private org.w3c.dom.Document svgDocument;

	String svgNS;
	DOMImplementation impl;
	// SVGGraphics2D svgGenerator;
	String parser;
	SAXSVGDocumentFactory factory;

	// DOMTreeManager domTreeManager;
	// DOMGroupManager domGroupManager;

	public SVGManager(Modeler modeler) {
		this.modeler = modeler;
		try {
			svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
			impl = SVGDOMImplementation.getDOMImplementation();
			parser = XMLResourceDescriptor.getXMLParserClassName();
			factory = new SAXSVGDocumentFactory(parser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// load();
		// svgGenerator = new SVGGraphics2D(svgDocument);
		// domTreeManager = svgGenerator.getDOMTreeManager();
		// domGroupManager = new DOMGroupManager(new GraphicContext(),
		// domTreeManager);
		// setSvgElements(modeler.root, svgDocument.getDocumentElement());
	}

	// public void load() {
	// editor = new DefaultSVGEditorField(this);
	// editor.setPreferredSize((Dimension) modeler.getContext().getBean(
	// "svgFieldDimension"));
	// updateFile();
	// try {
	// svgDocument = factory.createDocument(svgFile.toURI().toString());
	// } catch (IOException ex) {
	// ex.printStackTrace();
	// }
	// }

	// GuiNode node;

	// public void showModel(GuiNode node) {
	// this.node = node;
	// org.w3c.dom.Element root = node.getSvgElement();
	// editor.setElement(root);
	// }

	// public void reloadNode() {
	// if (node == null)
	// return;
	// org.w3c.dom.Node n1 = svgDocument.importNode(editor.getElement(), true);
	// editor.setElement((org.w3c.dom.Element)n1);
	// }
	//
	// public void hideModel(GuiNode node) {
	// if (this.node != null && this.node == node) {
	// save();
	// }
	// this.node = null;
	// }

	// public org.w3c.dom.Element setSvgElements(GuiNode node,
	// org.w3c.dom.Element parentElement) {
	// String id = "" + node.getSourceElementData().getId();
	// org.w3c.dom.Element element = svgDocument.getElementById(id);
	// if (element == null) {
	// if (node == modeler.root)
	// element = svgDocument.createElementNS(null, "svg");
	// else
	// element = svgDocument.createElementNS(svgNS, "svg");
	//
	// element.setAttributeNS(null, "id", node.getSourceElementData()
	// .getId() + "");
	// element.setAttributeNS(null, "x", "0");
	// element.setAttributeNS(null, "y", "0");
	// element.setAttributeNS(null, "width", svgDocument
	// .getDocumentElement().getAttribute("width"));
	// element.setAttributeNS(null, "height", svgDocument
	// .getDocumentElement().getAttribute("height"));
	// parentElement.appendChild(element);
	// }
	// node.setSvgElement(element);
	//
	// for (Enumeration e = node.children(); e.hasMoreElements();) {
	// GuiNode n = (GuiNode) e.nextElement();
	// org.w3c.dom.Element el = setSvgElements(n, element);
	// }
	// return element;
	// }
	//
	// public JPanel getEditor() {
	// return editor;
	// }

	public void updateFile() {
		if (svgFile != null && svgFile.exists())
			save();

		path = modeler.getSVGFilePath();
		svgFile = new File(path);

		if (!svgFile.exists()) {
			setNewDocument();
		}
	}

	private void save() {
		// reloadNode();
		try {
			FileWriter sw = new FileWriter(svgFile);
			DOMUtilities.writeDocument(svgDocument, sw);
			sw.flush();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		save();
		// editor.hideField();
	}

	private void setNewDocument() {
		try {
			URL url = modeler.getBundleContext().getBundle()
					.getResource("META-INF/EMPTY.svg");

			svgDocument = factory.createDocument(url.toURI().toString());
			save();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public org.w3c.dom.Document getSvgDocument() {
		return svgDocument;
	}

	public void setSvgDocument(org.w3c.dom.Document svgDocument) {
		this.svgDocument = svgDocument;
	}

	public String getSvgNS() {
		return svgNS;
	}

	public void setSvgNS(String svgNS) {
		this.svgNS = svgNS;
	}

	public DOMImplementation getImpl() {
		return impl;
	}

	public void setImpl(DOMImplementation impl) {
		this.impl = impl;
	}

	// public SVGGraphics2D getSvgGenerator() {
	// return svgGenerator;
	// }
	//
	// public void setSvgGenerator(SVGGraphics2D svgGenerator) {
	// this.svgGenerator = svgGenerator;
	// }

	public String getParser() {
		return parser;
	}

	public void setParser(String parser) {
		this.parser = parser;
	}

	public SAXSVGDocumentFactory getFactory() {
		return factory;
	}

	public void setFactory(SAXSVGDocumentFactory factory) {
		this.factory = factory;
	}

	// public DOMTreeManager getDomTreeManager() {
	// return domTreeManager;
	// }
	//
	// public void setDomTreeManager(DOMTreeManager domTreeManager) {
	// this.domTreeManager = domTreeManager;
	// }
	//
	// public DOMGroupManager getDomGroupManager() {
	// return domGroupManager;
	// }
	//
	// public void setDomGroupManager(DOMGroupManager domGroupManager) {
	// this.domGroupManager = domGroupManager;
	// }

	public org.w3c.dom.Document getNewDocument() {
		org.w3c.dom.Document document = null;
		try {
			document = impl.createDocument(svgNS, "svg", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}

	public Modeler getModeler() {
		return modeler;
	}

	public void exportToImage(org.w3c.dom.Document d, File newFile) {

		ImageTranscoder imt = null;
		TranscoderInput input = null;

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

}
