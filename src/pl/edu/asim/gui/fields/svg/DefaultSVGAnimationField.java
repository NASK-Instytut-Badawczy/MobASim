package pl.edu.asim.gui.fields.svg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.batik.bridge.UpdateManagerEvent;
import org.apache.batik.bridge.UpdateManagerListener;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.DOMGroupManager;
import org.apache.batik.svggen.DOMTreeManager;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;
import org.w3c.dom.DOMImplementation;

import pl.edu.asim.gui.Modeler;

public class DefaultSVGAnimationField extends JFrame implements ActionListener,
		WindowListener, UpdateManagerListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4758750192913086852L;
	org.w3c.dom.Document document;
	DefaultSVGCanvas canvas;
	MyJSVGScrollPane svgPane;
	String svgNS;
	DOMImplementation impl;
	SVGGraphics2D svgGenerator;

	String parser;
	SAXSVGDocumentFactory factory;
	DOMTreeManager domTreeManager;
	DOMGroupManager domGroupManager;

	JButton reload;
	JButton pause;
	JButton stop;
	JButton save;
	JPanel service;
	JPanel buttons;
	int start = 0;
	int end = 0;

	String path = "";

	static int MIN_WIDTH = 500;
	static int MIN_HEIGHT = 200;

	double w = 301;
	double h = 301;
	double z = 1;

	int frameWidth;
	int frameHeight;

	Modeler modeler;

	public DefaultSVGAnimationField(String title, final int e, String p,
			Modeler modeler) {
		super(title);
		this.modeler = modeler;
		path = p;
		try {
			svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
			impl = SVGDOMImplementation.getDOMImplementation();
			end = e;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// SVGUserAgentAdapter userAgent = new SVGUserAgentAdapter();
		canvas = new DefaultSVGCanvas(end);
		// canvas.addMouseListener(this);
		// canvas.addMouseMotionListener(this);
		// canvas.addKeyListener(this);
		// canvas.addUpdateManagerListener(this);
		// canvas.setDisableInteractions(false);

		svgPane = new MyJSVGScrollPane(canvas);
		canvas.setScroll(svgPane);
		reload = (JButton) modeler.getContext().getBean("PLAY_button");
		;
		reload.setActionCommand("START");
		reload.addActionListener(this);
		pause = (JButton) modeler.getContext().getBean("PAUSE_button");
		pause.setActionCommand("PAUSE");
		pause.addActionListener(this);
		stop = (JButton) modeler.getContext().getBean("REFRESH_button");
		stop.setActionCommand("STOP");
		stop.addActionListener(this);
		save = (JButton) modeler.getContext().getBean("SAVE_button");
		;
		save.setActionCommand("V");
		save.addActionListener(this);
		buttons = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		// c.anchor = GridBagConstraints.LINE_START;
		buttons.add(reload, c);
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0;
		buttons.add(pause, c);
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0;
		buttons.add(stop, c);
		c.gridx = 3;
		c.gridy = 0;
		c.weightx = 0;
		buttons.add(save, c);
		// c.gridx = 4;
		// c.gridy = 0;
		// c.weightx = 0;
		// c.weightx = 0.1;
		// buttons.add(new JLabel("(x,y)="),c);
		// c.gridwidth = 2;
		c.gridx = 4;
		c.gridy = 0;
		c.weightx = 0;
		c.gridwidth = 2;
		// c.insets = new Insets(0,3,0,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		canvas.getXYLabel().setPreferredSize(new Dimension(80, 30));
		canvas.getXYLabel().setBorder(
				BorderFactory.createLineBorder(Color.GRAY));
		canvas.getXYLabel().setBackground(Color.white);
		buttons.add(canvas.getXYLabel(), c);
		c.gridx = 6;
		c.gridy = 0;
		c.weightx = 0;
		c.gridwidth = 2;
		// c.insets = new Insets(0,3,0,3);
		canvas.getTimeLabel().setPreferredSize(new Dimension(60, 30));
		canvas.getTimeLabel().setBorder(
				BorderFactory.createLineBorder(Color.GRAY));
		canvas.getTimeLabel().setBackground(Color.white);
		buttons.add(canvas.getTimeLabel(), c);
		c.gridwidth = 6;
		c.gridx = 8;
		c.gridy = 0;
		c.weightx = 1;
		c.insets = new Insets(0, 8, 0, 8);
		c.fill = GridBagConstraints.HORIZONTAL;
		buttons.add(canvas.getProgress(), c);

		this.getContentPane().removeAll();
		this.getContentPane().add(svgPane, BorderLayout.CENTER);
		this.getContentPane().add(buttons, BorderLayout.SOUTH);
		this.setMaximumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

		this.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		this.addWindowListener(this);
		this.setAlwaysOnTop(true);
		this.setLocation(600, 40);
		this.pack();
	}

	public void showAnimation(org.w3c.dom.Document doc) {

		this.document = doc;
		if (this.document == null)
			return;

		try {

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

			frameWidth = (((int) (w * z)) + 30);
			frameHeight = (((int) (h * z)) + 100);
			frameWidth = (frameWidth < MIN_WIDTH) ? MIN_WIDTH : frameWidth;
			frameHeight = (frameHeight < MIN_HEIGHT) ? MIN_HEIGHT : frameHeight;

			Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
			frameWidth = (frameWidth>bounds.width) ? bounds.width : frameWidth;
			frameHeight = (frameHeight>bounds.height) ? bounds.height-20 : frameHeight;

			this.setSize(new Dimension(frameWidth, frameHeight));

			canvas.setDocument(document);
			// ((SVGDocument)document).getRootElement().
			// svgGenerator = new SVGGraphics2D(document);
			// domTreeManager = svgGenerator.getDOMTreeManager();
			// domGroupManager =
			// new DOMGroupManager(new GraphicContext(), domTreeManager);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean running = false;

	int state = JSVGCanvas.ALWAYS_STATIC;

	@Override
	public synchronized void actionPerformed(ActionEvent e) {

		synchronized (this) {

			if (e.getActionCommand().equals("START")) {
				canvas.startAnimation();
			}

			if (e.getActionCommand().equals("STOP")) {
				canvas.reloadAnimation();
			}

			if (e.getActionCommand().equals("PAUSE")) {
				canvas.pauseAnimation();
			}

			if (e.getActionCommand().equals("V")) {
				saveComponentAsPNG(canvas,
						path + "/anim_" + canvas.getActualTime() + ".png");
			}
		}
	}

	@Override
	public void updateFailed(UpdateManagerEvent e) {
	}

	@Override
	public void managerStopped(UpdateManagerEvent e) {
	}

	@Override
	public void managerStarted(UpdateManagerEvent e) {
	}

	@Override
	public void updateStarted(UpdateManagerEvent e) {
	}

	@Override
	public void updateCompleted(UpdateManagerEvent e) {
	}

	@Override
	public void managerResumed(UpdateManagerEvent e) {
	}

	@Override
	public void managerSuspended(UpdateManagerEvent e) {
	}

	public void saveComponentAsPNG(Component myComponent, String filename) {
		Dimension size = myComponent.getSize();
		BufferedImage myImage = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = myImage.createGraphics();
		myComponent.paint(g2);
		try {
			ImageIO.write(myImage, "png", new File(filename));
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void windowClosing(WindowEvent e) {
		if (canvas != null) {
			canvas.stopProcessing();
		}
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
	public void mouseMoved(MouseEvent me) {
		// canvas.setToolTipText((me.getX() / z) + "," + (me.getY() / z));
	}

	@Override
	public void mouseDragged(MouseEvent me) {
	}

}
