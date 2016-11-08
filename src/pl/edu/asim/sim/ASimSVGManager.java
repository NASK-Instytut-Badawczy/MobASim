package pl.edu.asim.sim;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.List;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.svggen.DOMGroupManager;
import org.apache.batik.svggen.DOMTreeManager;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.w3c.dom.DOMImplementation;

import pl.edu.asim.model.ASimDO;
import pl.edu.asim.model.ASimPO;
import pl.edu.asim.service.mobility.Entity;
import pl.edu.asim.service.mobility.OperatingAreaPoint;

public class ASimSVGManager {

	ASimSimulatorManager manager;
	String path;
	org.w3c.dom.Document svgDocument;
	org.w3c.dom.Document svgDocumentAnim;

	String svgNS;
	DOMImplementation impl;
	SVGGraphics2D svgGenerator;
	String parser;
	SAXSVGDocumentFactory factory;

	DOMTreeManager domTreeManager;
	DOMGroupManager domGroupManager;

	private int cellSize = 100;
	private int width = 0;
	private int height = 0;

	public ASimSVGManager(ASimSimulatorManager manager, String path,
			ASimDO simulator) {
		this.manager = manager;
		this.path = path;
		try {
			svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
			impl = SVGDOMImplementation.getDOMImplementation();
			parser = XMLResourceDescriptor.getXMLParserClassName();
			factory = new SAXSVGDocumentFactory(parser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		updateSVGDocument(simulator, true);
		svgGenerator = new SVGGraphics2D(svgDocument);
		domTreeManager = svgGenerator.getDOMTreeManager();
		domGroupManager = new DOMGroupManager(new GraphicContext(),
				domTreeManager);
	}

	public void updateSVGDocument(ASimDO data, boolean cascade) {
		try {
			try {

				String newS = getSVGString(data, cascade);
				svgDocument = getFactory().createDocument(getSvgNS(), "svg",
						null, new StringReader(newS));
				svgDocumentAnim = getFactory().createDocument(getSvgNS(),
						"svg", null, new StringReader(newS));

				updateGridString(svgDocument, cellSize, width, height);
				updateGridString(svgDocumentAnim, cellSize, width, height);

			} catch (org.apache.batik.dom.util.SAXIOException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public org.w3c.dom.Document getSVGDocument(ASimDO data, boolean cascade) {
		org.w3c.dom.Document svgDocument = null;
		try {
			try {

				String newS = getSVGString(data, cascade);
				svgDocument = getFactory().createDocument(getSvgNS(), "svg",
						null, new StringReader(newS));
			} catch (org.apache.batik.dom.util.SAXIOException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return svgDocument;
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
				if (data.getType().equals("SIMULATOR"))
					this.height = new Integer(height);
			} else if (a.getCode().equals("svg_width")) {
				width = a.getValue();
				if (data.getType().equals("SIMULATOR"))
					this.width = new Integer(width);
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

		result = "<svg xmlns=\"" + getSvgNS() + "\" x=\"" + x + "\" y=\"" + y
				+ "\" width=\"" + width + "\" height=\"" + height
				+ "\" visibility=\"" + visibility + "\" id=\"" + data.getId()
				+ "\">" + s;

		if (cascade)
			for (ASimDO ado : data.getChildren()) {
				result = result + getSVGString(ado, cascade);
			}

		result = result + "</svg>";

		return result;
	}

	public void move(String id, double x, double y, double startTime,
			double duration, String points, String oldPoints) {

		org.w3c.dom.Element ent = svgDocument.getElementById(id);
		String xOld = ent.getAttributeNS(null, "x");
		String yOld = ent.getAttributeNS(null, "y");
		ent.setAttributeNS(null, "x", "" + x);
		ent.setAttributeNS(null, "y", "" + y);

		double dx = x - new Double(xOld);
		double dy = y - new Double(yOld);

		org.w3c.dom.Element entA = svgDocumentAnim.getElementById(id);

		org.w3c.dom.Element anim = svgDocumentAnim.createElementNS(
				this.getSvgNS(), "animate");
		anim.setAttributeNS(null, "attributeName", "x");
		anim.setAttributeNS(null, "from", xOld);
		anim.setAttributeNS(null, "to", "" + x);
		anim.setAttributeNS(null, "begin", startTime + "s");
		anim.setAttributeNS(null, "dur", duration + "s");
		anim.setAttributeNS(null, "fill", "freeze");
		entA.appendChild(anim);

		org.w3c.dom.Element anim2 = svgDocumentAnim.createElementNS(
				this.getSvgNS(), "animate");
		anim2.setAttributeNS(null, "attributeName", "y");
		anim2.setAttributeNS(null, "from", yOld);
		anim2.setAttributeNS(null, "to", "" + y);
		anim2.setAttributeNS(null, "begin", startTime + "s");
		anim2.setAttributeNS(null, "dur", duration + "s");
		anim2.setAttributeNS(null, "fill", "freeze");
		entA.appendChild(anim2);

		org.w3c.dom.NodeList nodeList = entA.getElementsByTagName("polygon");
		if (nodeList != null && nodeList.getLength() > 0) {
			org.w3c.dom.Element anim3 = svgDocumentAnim.createElementNS(
					this.getSvgNS(), "animate");
			anim3.setAttributeNS(null, "attributeName", "points");
			anim3.setAttributeNS(null, "from", oldPoints);
			anim3.setAttributeNS(null, "to", "" + points);
			anim3.setAttributeNS(null, "begin", startTime + "s");
			anim3.setAttributeNS(null, "dur", duration + "s");
			anim3.setAttributeNS(null, "fill", "freeze");
			((org.w3c.dom.Element) nodeList.item(0)).appendChild(anim3);
		}

		org.w3c.dom.NodeList nodeList2 = ent.getElementsByTagName("polygon");
		if (nodeList2 != null && nodeList2.getLength() > 0) {
			((org.w3c.dom.Element) nodeList2.item(0)).setAttributeNS(null,
					"points", points);
		}

	}

	public String anime(long startTime, long duration,
			OperatingAreaPoint fromPoint, OperatingAreaPoint toPoint) {

		double dx = toPoint.getX() - fromPoint.getX();
		double dy = toPoint.getY() - fromPoint.getY();

		return " <animateMotion path=\"M 0 0 L " + dx + " " + dy + "\" "
				+ " begin=\"" + (startTime - duration) + "s\" dur=\""
				+ duration + "s\" fill=\"freeze\" additive=\"sum\" /> ";

	}

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

	// public void updateFile() {
	// if (svgFile != null && svgFile.exists())
	// save();
	//
	// svgFile = new File(path);
	//
	// if (!svgFile.exists()) {
	// setNewDocument();
	// }
	// }

	public void save(String svgFile) {
		// reloadNode();
		File f = new File(svgFile + ".svg");
		File f2 = new File(svgFile + "_anim.svg");
		try {
			FileWriter sw = new FileWriter(f);
			DOMUtilities.writeDocument(svgDocument, sw);
			sw.flush();
			sw.close();

			sw = new FileWriter(f2);
			DOMUtilities.writeDocument(svgDocumentAnim, sw);
			sw.flush();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		File svgFile = new File(path);
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
			URL url = manager.getBundle().getResource("META-INF/EMPTY.svg");

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

	public void exportToImage(String name) {
		File f = new File(name);
		exportToImage(svgDocument, f);
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

	private String getPath(List<Entity> entities) {
		String path = "";

		for (Entity e1 : entities) {
			Vector3D v1 = e1.getOrientationPoint().getAsVector3D()
					.add(e1.getSvgZeroPoint().getAsVector3D());
			e1.getConnections().clear();
			for (Entity e2 : entities) {
				Vector3D v2 = e2.getOrientationPoint().getAsVector3D()
						.add(e2.getSvgZeroPoint().getAsVector3D());
				if (v1.distance(v2) <= e1.getCard()
						.getOptimalWirelessDistance()) {
					path = path
							+ " M"
							+ new BigDecimal(v1.getX()).setScale(2,
									RoundingMode.HALF_DOWN)
							+ ","
							+ new BigDecimal(v1.getY()).setScale(2,
									RoundingMode.HALF_DOWN);
					path = path
							+ " L"
							+ new BigDecimal(v2.getX()).setScale(2,
									RoundingMode.HALF_DOWN)
							+ ","
							+ new BigDecimal(v2.getY()).setScale(2,
									RoundingMode.HALF_DOWN);

					e1.getConnections().put(e2.getName(), e2);
				}

			}
		}

		if (path.equals(" ") || path.equals(""))
			path = "M0,0";
		return path;
	}

	private String getPath(Entity entity) {
		String path = "";
		String pointString = "";
		String oldPointString = "";

		if (entity.getSvgZeroPointHistory().size() < 2)
			return "M0,0";
		for (OperatingAreaPoint p : entity.getSvgZeroPointHistory()) {
			Vector3D v1 = entity.getOrientationPoint().getAsVector3D()
					.add(p.getAsVector3D());

			pointString = new BigDecimal(v1.getX()).setScale(2,
					RoundingMode.HALF_DOWN)
					+ ","
					+ new BigDecimal(v1.getY()).setScale(2,
							RoundingMode.HALF_DOWN);

			if (oldPointString.equals("") || oldPointString.equals(pointString)) {
				oldPointString = pointString;
				continue;
			}
			path = path + " M" + oldPointString;
			path = path + " L" + pointString;

			oldPointString = pointString;
		}

		return path;
	}

	public void updatePath(String path, double startTime, double duration) {

		org.w3c.dom.Element ent = svgDocument.getElementById("lineAB");
		if (ent == null)
			return;
		String dOld = ent.getAttributeNS(null, "d");
		ent.setAttributeNS(null, "d", path);

		org.w3c.dom.Element entA = svgDocumentAnim.getElementById("lineAB");
		org.w3c.dom.Element anim = svgDocumentAnim.createElementNS(
				this.getSvgNS(), "set");
		anim.setAttributeNS(null, "attributeName", "d");
		anim.setAttributeNS(null, "from", dOld);
		anim.setAttributeNS(null, "to", "" + path);
		anim.setAttributeNS(null, "begin", (startTime) + "s");
		anim.setAttributeNS(null, "dur", duration + "s");
		anim.setAttributeNS(null, "fill", "freeze");
		entA.appendChild(anim);

	}

	public void updatePath(List<Entity> entities, double startTime,
			double duration) {

		org.w3c.dom.Element ent = svgDocument.getElementById("lineAB");
		if (ent == null)
			return;
		String path = getPath(entities);
		String dOld = ent.getAttributeNS(null, "d");
		ent.setAttributeNS(null, "d", path);

		org.w3c.dom.Element entA = svgDocumentAnim.getElementById("lineAB");
		org.w3c.dom.Element anim = svgDocumentAnim.createElementNS(
				this.getSvgNS(), "set");
		anim.setAttributeNS(null, "attributeName", "d");
		anim.setAttributeNS(null, "from", dOld);
		anim.setAttributeNS(null, "to", "" + path);
		anim.setAttributeNS(null, "begin", (startTime) + "s");
		anim.setAttributeNS(null, "dur", duration + "s");
		anim.setAttributeNS(null, "fill", "freeze");
		entA.appendChild(anim);
	}

	public void updatePathForEntity(Entity entity, double startTime,
			double duration) {

		org.w3c.dom.Element ent = svgDocument.getElementById("line_"
				+ entity.getName());
		if (ent == null)
			return;
		String path = getPath(entity);
		String dOld = ent.getAttributeNS(null, "d");
		ent.setAttributeNS(null, "d", path);

		org.w3c.dom.Element entA = svgDocumentAnim.getElementById("line_"
				+ entity.getName());
		org.w3c.dom.Element anim = svgDocumentAnim.createElementNS(
				this.getSvgNS(), "set");
		anim.setAttributeNS(null, "attributeName", "d");
		anim.setAttributeNS(null, "from", dOld);
		anim.setAttributeNS(null, "to", "" + path);
		anim.setAttributeNS(null, "begin", startTime + "s");
		anim.setAttributeNS(null, "dur", duration + "s");
		anim.setAttributeNS(null, "fill", "freeze");
		entA.appendChild(anim);

	}

	public void updateGridString(org.w3c.dom.Document doc, int cellSize,
			double maxWidth, double maxHeight) {

		org.w3c.dom.Element ent = doc.getElementById("grid");
		if (ent == null)
			return;

		String result = "";
		double x = maxWidth / cellSize;
		double y = maxHeight / cellSize;

		for (int i = 0; i <= x; i++) {
			result = result + " M" + (i * cellSize) + ",0" + " L"
					+ (i * cellSize) + "," + maxHeight;
		}
		for (int i = 0; i <= x; i++) {
			result = result + " M0," + (i * cellSize) + " L" + maxWidth + ","
					+ (i * cellSize);
		}

		ent.setAttributeNS(null, "d", result);
	}

	public int getCellSize() {
		return cellSize;
	}

	public void setCellSize(int cellSize) {
		this.cellSize = cellSize;
	}

}
