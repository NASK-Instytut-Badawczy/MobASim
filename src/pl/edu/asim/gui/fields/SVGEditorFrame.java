package pl.edu.asim.gui.fields;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.svg.SVGDocument;

import pl.edu.asim.gui.Modeler;
import pl.edu.asim.gui.fields.svg.DefaultSVGCanvas;
import pl.edu.asim.gui.fields.svg.MyJSVGScrollPane;

public class SVGEditorFrame extends JFrame implements MouseListener,
		MouseMotionListener, KeyListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static int MIN_WIDTH = 500;
	static int MIN_HEIGHT = 200;

	double w = 301;
	double h = 301;
	double z = 1;

	int frameWidth;
	int frameHeight;

	int X;
	int Y;

	org.w3c.dom.Document doc;
	DefaultSVGCanvas canvas;
	MyJSVGScrollPane svgPane;

	SVGEditorField field;
	JButton reloadButton;
	JButton saveButton;

	boolean free = false;

	public SVGEditorFrame(SVGEditorField field, Modeler modeler) {
		super("ASimModeler: SVG Editor frame");
		this.field = field;
		this.doc = field.doc;

		String wS = doc.getDocumentElement().getAttribute("width");
		if (wS != null && !wS.equals("")) {
			this.w = new Double(wS);
		}
		String hS = doc.getDocumentElement().getAttribute("height");
		if (hS != null && !hS.equals("")) {
			this.h = new Double(hS);
		}
		String zS = doc.getDocumentElement().getAttribute("zoom");
		if (zS != null && !zS.equals("")) {
			this.z = new Double(zS);
		}

		canvas = new DefaultSVGCanvas(1);
		canvas.setDocumentState(JSVGCanvas.ALWAYS_INTERACTIVE);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
		canvas.setDisableInteractions(false);

		svgPane = new MyJSVGScrollPane(canvas);
		canvas.setScroll(svgPane);

		saveButton = (JButton) field.getAttribute().getModeler().getContext()
				.getBean("SAVE_button");
		saveButton.addActionListener(this);
		reloadButton = (JButton) field.getAttribute().getModeler().getContext()
				.getBean("REFRESH_button");
		reloadButton.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(saveButton);
		buttonPanel.add(reloadButton);
		buttonPanel.add(new JLabel("  Point: ", JLabel.RIGHT));
		canvas.getXYLabel().setPreferredSize(new Dimension(80, 30));
		buttonPanel.add(canvas.getXYLabel());

		this.getContentPane().add(buttonPanel, BorderLayout.NORTH);
		this.getContentPane().add(svgPane, BorderLayout.CENTER);
		this.setAlwaysOnTop(true);
		this.pack();
		this.setLocation(600, 40);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == reloadButton) {
			field.refreshFrame();
		} else if (e.getSource() == saveButton) {

			File file = getSaveFile();
			if (file != null) {
				field.getAttribute().getModeler().getSvg()
						.exportToImage(doc, file);

			}
		}
	}

	public File getSaveFile() {
		File file = null;
		JFileChooser jf = new JFileChooser(field.getDefaultExportDirectory());
		jf.setAcceptAllFileFilterUsed(false);

		jf.setFileSelectionMode(JFileChooser.FILES_ONLY);
		jf.addChoosableFileFilter(new ImageFilter());
		int returnVal = jf.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = jf.getSelectedFile();
		}
		return file;

	}

	public void setDocument(org.w3c.dom.Document doc) {

		System.gc();

		free = false;
		this.doc = doc;

		String wS = doc.getDocumentElement().getAttribute("width");
		if (wS != null && !wS.equals("")) {
			this.w = new Double(wS);
		}
		String hS = doc.getDocumentElement().getAttribute("height");
		if (hS != null && !hS.equals("")) {
			this.h = new Double(hS);
		}
		String zS = doc.getDocumentElement().getAttribute("zoom");
		if (zS != null && !zS.equals("")) {
			this.z = new Double(zS);
		}

		frameWidth = (((int) (w * z)) + 20);
		frameHeight = (((int) (h * z)) + 90);
		frameWidth = (frameWidth < MIN_WIDTH) ? MIN_WIDTH : frameWidth;
		frameHeight = (frameHeight < MIN_HEIGHT) ? MIN_HEIGHT : frameHeight;

		Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds();
		frameWidth = (frameWidth > bounds.width) ? bounds.width : frameWidth;
		frameHeight = (frameHeight > bounds.height) ? bounds.height
				: frameHeight;

		this.setSize(new Dimension(frameWidth, frameHeight));
		String sc = field.getCellsize();
		if (sc != null)
			updateGridString(doc, new Integer(sc), this.w, this.h);
		canvas.setSVGDocument((SVGDocument) doc);

		// wModel.setValue(w);
		// hModel.setValue(h);
	}

	@Override
	public void mouseDragged(MouseEvent me) {
		// if (editorPane.elementPane.getTabCount() == 0) // || mouseDragged)
		// return;
		// try {
		// SVGElement c =
		// editorPane.getElementAt(editorPane.elementPane.getSelectedIndex());
		// if (c == null)
		// return;
		// if (!me.isAltDown() && !me.isShiftDown() && !me.isControlDown())
		// return;
		// if (Y == -1 && X == -1 &&
		// (me.isAltDown() || me.isShiftDown() || me.isControlDown())) {
		// X = me.getX();
		// Y = me.getY();
		// }
		// if (me.isAltDown()) { // move
		// c.moveElement(X - me.getX(), Y - me.getY());
		// X = me.getX();
		// Y = me.getY();
		// }
		// } catch (Exception e) {
		// e.printStackTrace(); // mouseDragged = false;
		// }
	}

	@Override
	public void mouseExited(MouseEvent me) {
	}

	@Override
	public void mouseEntered(MouseEvent me) {
	}

	@Override
	public void mouseReleased(MouseEvent me) {
	}

	@Override
	public void mousePressed(MouseEvent me) {
	}

	@Override
	public void mouseClicked(MouseEvent me) {
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		// canvas.setToolTipText((me.getX() / z) + "," + (me.getY() / z));
		X = -1;
		Y = -1;
	}

	public void setW(Double w) {
		this.w = w.intValue();
	}

	public void setH(Double h) {
		this.h = h.intValue();
	}

	public void setZ(Double z) {
		this.z = z;
	}

	boolean mouseDragged = false;

	@Override
	public void keyReleased(KeyEvent me) {
		if (me.isAltDown()) {
			canvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			canvas.setCursor(Cursor.getDefaultCursor());
		}
	}

	@Override
	public void keyPressed(KeyEvent me) {
		// if (editorPane.elementPane.getTabCount() == 0)
		// return;
		// SVGElement c =
		// editorPane.getElementAt(editorPane.elementPane.getSelectedIndex());
		// if (c == null)
		// return;
		//
		// if (me.getKeyCode() == 17) {
		// canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		// } else if (me.getKeyCode() == 18) {
		// canvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		// } else if (me.getKeyCode() == 16) {
		// canvas.setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
		// } else if (me.getKeyCode() == 38 && me.isAltDown()) { //up
		// c.moveElement(0, 1);
		// } else if (me.getKeyCode() == 37 && me.isAltDown()) { //left
		// c.moveElement(1, 0);
		// } else if (me.getKeyCode() == 39 && me.isAltDown()) { //right
		// c.moveElement(-1, 0);
		// } else if (me.getKeyCode() == 40 && me.isAltDown()) { //down
		// c.moveElement(0, -1);
		// } else if (me.getKeyCode() == 38 && me.isShiftDown()) { //up
		// c.scaleElement(0, 1);
		// } else if (me.getKeyCode() == 37 && me.isShiftDown()) { //left
		// c.scaleElement(1, 0);
		// } else if (me.getKeyCode() == 39 && me.isShiftDown()) { //right
		// c.scaleElement(-1, 0);
		// } else if (me.getKeyCode() == 40 && me.isShiftDown()) { //down
		// c.scaleElement(0, -1);
		// } else if (me.getKeyCode() == 38 && me.isControlDown()) { //up
		// canvas.setCursor(Cursor.getDefaultCursor());
		// } else if (me.getKeyCode() == 37 && me.isControlDown()) { //left
		// c.rotate.keyPressed(-1);
		// } else if (me.getKeyCode() == 39 && me.isControlDown()) { //right
		// c.rotate.keyPressed(1);
		// } else if (me.getKeyCode() == 40 && me.isControlDown()) { //down
		// canvas.setCursor(Cursor.getDefaultCursor());
		// }
	}

	@Override
	public void keyTyped(KeyEvent me) {
	}

	// String getGridPath() {
	// String path = "";
	// double newStep = new Double("" + step.getValue());
	// for (int ii = 0; (ii * newStep) < w; ii++) {
	// path = path + "M " + (ii * newStep) + ",0 v" + h + " ";
	// }
	// for (int ii = 0; (ii * newStep) < h; ii++) {
	// path = path + "M 0," + (ii * newStep) + " h" + w + " ";
	// }
	// return path;
	// }

	// class MySVGCanvas extends JSVGCanvas {
	//
	// public MySVGCanvas() {
	// super(null, true, true);
	// }
	//
	// @Override
	// public void setSVGDocument(SVGDocument doc) {
	// super.setSVGDocument(doc);
	// System.gc();
	// }
	//
	// public void zoom(double zoom) {
	// try {
	// AffineTransform Tx = AffineTransform.getScaleInstance(zoom,
	// zoom);
	// AffineTransform at = super.getInitialTransform();
	// AffineTransform mt = AffineTransform.getTranslateInstance(0, 0);
	// AffineTransform newRT = new AffineTransform(at);
	// newRT.preConcatenate(Tx);
	// newRT.preConcatenate(mt);
	// super.setRenderingTransform(newRT);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// }

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
		for (int i = 0; i <= y; i++) {
			result = result + " M0," + (i * cellSize) + " L" + maxWidth + ","
					+ (i * cellSize);
		}

		ent.setAttributeNS(null, "d", result);
	}

	public class ImageFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}

			String extension = field.getAttribute().getModeler()
					.getExtension(f);
			if (extension != null) {
				if (field.getExtensions().contains(extension)) {
					return true;
				} else {
					return false;
				}
			}

			return false;
		}

		@Override
		public String getDescription() {
			return field.getExtensions();
		}
	}
}
