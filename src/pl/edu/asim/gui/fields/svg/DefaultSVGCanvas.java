package pl.edu.asim.gui.fields.svg;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.SVGAnimationEngine;
import org.apache.batik.bridge.UpdateManagerEvent;
import org.apache.batik.bridge.UpdateManagerListener;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.svg12.SVG12BridgeContext;
import org.apache.batik.dom.events.NodeEventTarget;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.JSVGScrollPane;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderListener;
import org.apache.batik.swing.svg.SVGUserAgent;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLConstants;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.svg.SVGDocument;

public class DefaultSVGCanvas extends JSVGCanvas implements ChangeListener,
		UpdateManagerListener, GVTTreeBuilderListener, MouseMotionListener { /**
	 * 
	 */
	private static final long serialVersionUID = -6220870304813131029L;
		// ,
	// GVTTreeRendererListener{

	float fromTime = 0;
	float actualTime = 0;
	JSlider progress;
	JLabel timeLabel;
	JLabel xyLabel;

	int start = 0;
	int end = 0;

	String state = "start";
	double z = 1;
	private MyJSVGScrollPane scroll;
	
	public DefaultSVGCanvas(final int e) {
		super(null, true, true);

		end = e;
		setProgress(new JSlider(JSlider.HORIZONTAL, start, end, start));

		int major = (end < 10) ? 2 : (end / 5);
		int minor = (end < 10) ? 1 : (end / 10);
		getProgress().setMajorTickSpacing(major);
		getProgress().setMinorTickSpacing(minor);
		getProgress().setPaintTicks(true);
		getProgress().setPaintLabels(true);
		getProgress().addChangeListener(this);
		setTimeLabel(new JLabel("T=" + start, JLabel.CENTER));
		setXYLabel(new JLabel("(0,0)", JLabel.CENTER));
		this.addUpdateManagerListener(this);
		this.addMouseMotionListener(this);
		this.addGVTTreeBuilderListener(this);
		// this.addGVTTreeRendererListener(this);

	}

	public DefaultSVGCanvas(SVGUserAgent ua, boolean eventsEnabled,
			boolean selectableText, final int e) {
		super(ua, eventsEnabled, selectableText);
		end = e;
		setProgress(new JSlider(JSlider.HORIZONTAL, start, end, start));

		int major = (end < 10) ? 2 : (end / 5);
		int minor = (end < 10) ? 1 : (end / 10);
		getProgress().setMajorTickSpacing(major);
		getProgress().setMinorTickSpacing(minor);
		getProgress().setPaintTicks(true);
		getProgress().setPaintLabels(true);
		getProgress().addChangeListener(this);
		setTimeLabel(new JLabel("T = " + start, JLabel.CENTER));
		setXYLabel(new JLabel("(0,0)", JLabel.CENTER));
		this.addUpdateManagerListener(this);
		this.addMouseMotionListener(this);
		this.addGVTTreeBuilderListener(this);
	}

	public void stateChanged(ChangeEvent event) {
		getProgress().setToolTipText("" + progress.getValue());
		getTimeLabel().setText("T = " + progress.getValue());
	}

	public int getActualTime() {
		return new Double(actualTime).intValue();
	}

	/*
	 * protected void installSVGDocument(SVGDocument doc) { if (bridgeContext !=
	 * null) { bridgeContext.dispose(); bridgeContext = null; }
	 * 
	 * releaseRenderingReferences();
	 * 
	 * if (doc == null) { isDynamicDocument = false; isInteractiveDocument =
	 * false; disableInteractions = true; initialTransform = new
	 * AffineTransform(); setRenderingTransform(initialTransform, false);
	 * Rectangle vRect = getRenderRect(); repaint(vRect.x, vRect.y, vRect.width,
	 * vRect.height); return; }
	 * 
	 * bridgeContext = createBridgeContext((SVGOMDocument)doc);
	 * 
	 * switch (documentState) { case ALWAYS_STATIC: isDynamicDocument = false;
	 * isInteractiveDocument = false; break; case ALWAYS_INTERACTIVE:
	 * isDynamicDocument = false; isInteractiveDocument = true; break; case
	 * ALWAYS_DYNAMIC: isDynamicDocument = true; isInteractiveDocument = true;
	 * break; case AUTODETECT: isDynamicDocument =
	 * bridgeContext.isDynamicDocument(doc); isInteractiveDocument =
	 * isDynamicDocument || bridgeContext.isInteractiveDocument(doc); }
	 * 
	 * if (isInteractiveDocument) { if (isDynamicDocument)
	 * bridgeContext.setDynamicState(BridgeContext.DYNAMIC); else
	 * bridgeContext.setDynamicState(BridgeContext.INTERACTIVE); }
	 * 
	 * setBridgeContextAnimationLimitingMode();
	 * 
	 * updateZoomAndPanEnable(doc);
	 * 
	 * nextGVTTreeBuilder = new GVTTreeBuilder(doc, bridgeContext);
	 * nextGVTTreeBuilder.setPriority(Thread.MIN_PRIORITY);
	 * 
	 * Iterator it = gvtTreeBuilderListeners.iterator(); while (it.hasNext()) {
	 * nextGVTTreeBuilder
	 * .addGVTTreeBuilderListener((GVTTreeBuilderListener)it.next()); }
	 * 
	 * initializeEventHandling(); if (gvtTreeBuilder == null && documentLoader
	 * == null && gvtTreeRenderer == null && svgLoadEventDispatcher == null &&
	 * updateManager == null) { startGVTTreeBuilder(); }
	 * 
	 * }
	 */

	public void setSVGDocument(SVGDocument doc) {
		if ((doc != null)
				&& !(doc.getImplementation() instanceof SVGDOMImplementation)) {
			DOMImplementation impl;
			impl = SVGDOMImplementation.getDOMImplementation();
			Document d = DOMUtilities.deepCloneDocument(doc, impl);
			doc = (SVGDocument) d;
		}

		final SVGDocument svgdoc = doc;
		documentState = ALWAYS_DYNAMIC;
		svgDocument = svgdoc;
		svgDocument.normalize();

		String zS = svgDocument.getDocumentElement().getAttribute("zoom");
		if (zS != null && !zS.equals("")) {
			this.z = new Double(zS);
		}

		stopThenRun(new Runnable() {
			public void run() {
				installSVGDocument(svgDocument);
			}
		});

		NodeEventTarget root;
		root = (NodeEventTarget) doc.getRootElement();
		// On mouseover, it sets the tooltip to the given value
		root.addEventListenerNS(XMLConstants.XML_EVENTS_NAMESPACE_URI,
				SVGConstants.SVG_EVENT_MOUSEOVER, toolTipListener, true, null);
		// On mouseout, it removes the tooltip
		root.addEventListenerNS(XMLConstants.XML_EVENTS_NAMESPACE_URI,
				SVGConstants.SVG_EVENT_MOUSEOUT, toolTipListener, true, null);

	}

	public void startAnimation() {
		// if (state.equals("stop")) {
		// state = "start";
		// documentState = ALWAYS_DYNAMIC;
		// this.setFromTime(getProgress().getValue());
		//
		// stopThenRun(new Runnable() {
		// public void run() {
		// flushImageCache();
		// installSVGDocument(svgDocument);
		// }
		// });
		// } else
		if (state.equals("pause") && bridgeContext != null) {
			if (myDocumentStartTime > 0) {
				// float ft =
				// myDocumentStartTime - ((long)(getProgress().getValue() *
				// 1000f));
				// this.setFromTime(ft);
				bridgeContext.getAnimationEngine().setCurrentTime(
						getProgress().getValue());
			}
			bridgeContext.getAnimationEngine().unpause();
		}
		state = "start";
	}

	public void pauseAnimation() {
		if (state.equals("start") && bridgeContext != null) {
			bridgeContext.getAnimationEngine().pause();
		}
		state = "pause";
	}

	public void reloadAnimation() {

		state = "start";
		this.getUpdateManager().dispatchSVGUnLoadEvent();
		flushImageCache();
		setSVGDocument(svgDocument);
		// if (!state.equals("stop")) {
		// documentState = ALWAYS_INTERACTIVE;

		// bridgeContext.getAnimationEngine().dispose();

		// stopThenRun(new Runnable() {
		// public void run() {
		// flushImageCache();
		// installSVGDocument(svgDocument);
		// }
		// });
		getProgress().setValue(start);
		actualTime = start;
		// }
		// state = "stop";
	}

	protected BridgeContext createBridgeContext(SVGOMDocument doc) {
		if (loader == null) {
			loader = new DocumentLoader(userAgent);
		}
		BridgeContext result;
		if (doc.isSVG12()) {
			result = new SVG12BridgeContext(userAgent, loader);
		} else {
			result = new MyBridgeContext(userAgent, loader);
		}
		return result;
	}

	public float getFromTime() {
		return fromTime;
	}

	public void setFromTime(float fromTime) {
		this.fromTime = fromTime;
	}

	public JSlider getProgress() {
		return progress;
	}

	public void setProgress(JSlider progress) {
		this.progress = progress;
	}

	public JLabel getTimeLabel() {
		return timeLabel;
	}

	public void setTimeLabel(JLabel timeLabel) {
		this.timeLabel = timeLabel;

	}

	public void zoom(double zoom) {
		try {
			AffineTransform Tx = AffineTransform.getScaleInstance(zoom, zoom);
			AffineTransform at = super.getInitialTransform();
			AffineTransform mt = AffineTransform.getTranslateInstance(0, 0);
			AffineTransform newRT = new AffineTransform(at);
			newRT.preConcatenate(Tx);
			newRT.preConcatenate(mt);
			super.setRenderingTransform(newRT);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected UserAgent createUserAgent() {
		UserAgent ua = new MyUserAgent();
		return ua;
	}

	public void updateFailed(UpdateManagerEvent e) {
	}

	public void managerStopped(UpdateManagerEvent e) {
	}

	public void managerStarted(UpdateManagerEvent e) {
		zoom(z);
	}

	public void updateStarted(UpdateManagerEvent e) {
	}

	public void updateCompleted(UpdateManagerEvent e) {
		// System.out.println(this.lastTarget+" "+this.locationListener.getLastX()+" "+this.locationListener.getLastY());
		// System.out.println(this.userAgent+" "+this.toolTipListener);
	}

	public void managerResumed(UpdateManagerEvent e) {
	}

	public void managerSuspended(UpdateManagerEvent e) {
	}

	public void gvtBuildCancelled(GVTTreeBuilderEvent e) {
	}

	public void gvtBuildCompleted(GVTTreeBuilderEvent e) {
		// System.out.println(e.getGVTRoot());
		// RootGraphicsNode g = (RootGraphicsNode) e.getGVTRoot();
		// for(Iterator it = g.getChildren().iterator(); it.hasNext();){
		// CompositeGraphicsNode gn = (CompositeGraphicsNode) it.next();
		// System.out.println(gn.getOutline());
		// for(Iterator it2 = gn.getChildren().iterator(); it2.hasNext();){
		// CompositeGraphicsNode gn2 = (CompositeGraphicsNode) it2.next();
		// Shape s = gn2.getOutline();
		// ExtendedGeneralPath egp = new ExtendedGeneralPath(s);
		// ExtendedPathIterator epi = egp.getExtendedPathIterator();
		// while(!epi.isDone()) {
		// double[] cor = new double[6];
		// int i = epi.currentSegment(cor);
		// System.out.println(i+" "+cor[0]+" "+cor[1]+" "+cor[2]+" "+cor[3]+" "+cor[4]+" "+cor[5]);
		// epi.next();
		// }
		// }
		// }
	}

	public void gvtBuildFailed(GVTTreeBuilderEvent e) {
	}

	public void gvtBuildStarted(GVTTreeBuilderEvent e) {
	}

	public JLabel getXYLabel() {
		return xyLabel;
	}

	public void setXYLabel(JLabel xyLabel) {
		this.xyLabel = xyLabel;
	}

	public class MyBridgeContext extends BridgeContext {

		public MyBridgeContext(UserAgent userAgent, DocumentLoader loader) {
			super(userAgent, loader);
		}

		public SVGAnimationEngine getAnimationEngine() {
			if (animationEngine == null) {
				animationEngine = new MySVGAnimationEngine(document, this);
				setAnimationLimitingMode();
			}
			return animationEngine;
		}
	}

	long myDocumentStartTime = 0;

	public class MySVGAnimationEngine extends SVGAnimationEngine {
		public MySVGAnimationEngine(Document doc, BridgeContext ctx) {
			super(doc, ctx);
			setAnimationLimitingFPS(2);
			this.setAnimationLimitingCPU(75);
		}

		public void start(long documentStartTime) {
			myDocumentStartTime = documentStartTime;
			// super.start(documentStartTime - ((long)(getFromTime() * 1000f)));
			super.start(documentStartTime);
		}

		public float tick(float time, boolean hyperlinking) {
			actualTime = time;
			getProgress().setValue(new Double(time).intValue());
			return super.tick(time, hyperlinking);
		}
	}

	public class MyUserAgent extends CanvasUserAgent {
		public MyUserAgent() {
			super();
		}
		/*
		 * public void handleElement(Element elt, Object data) { //
		 * System.out.println("he "+elt+" "+data); super.handleElement(elt,
		 * data); }
		 * 
		 * public Element getPeerWithTag(Element parent, String nameSpaceURI,
		 * String localName) { // System.out.println("gpwt "+parent); return
		 * super.getPeerWithTag(parent, nameSpaceURI, localName); }
		 * 
		 * public boolean hasPeerWithTag(Element elt, String nameSpaceURI,
		 * String localName) { // System.out.println("hpwt "+elt); return
		 * super.hasPeerWithTag(elt, nameSpaceURI, localName); }
		 * 
		 * public void setToolTip(Element elt, String toolTip){ //
		 * System.out.println("stt "+elt+" "+toolTip); if (toolTipMap == null) {
		 * toolTipMap = new WeakHashMap(); } if (toolTipDocs == null) {
		 * toolTipDocs = new WeakHashMap(); } SVGDocument doc =
		 * (SVGDocument)elt.getOwnerDocument(); if (toolTipDocs.put(doc,
		 * MAP_TOKEN) == null) { NodeEventTarget root; root =
		 * (NodeEventTarget)doc.getRootElement(); // On mouseover, it sets the
		 * tooltip to the given value
		 * root.addEventListenerNS(XMLConstants.XML_EVENTS_NAMESPACE_URI,
		 * SVGConstants.SVG_EVENT_MOUSEOVER, toolTipListener, true, null); // On
		 * mouseout, it removes the tooltip
		 * root.addEventListenerNS(XMLConstants.XML_EVENTS_NAMESPACE_URI,
		 * SVGConstants.SVG_EVENT_MOUSEOUT, toolTipListener, true, null); }
		 * 
		 * toolTipMap.put(elt, toolTip);
		 * 
		 * if (elt == lastTarget) EventQueue.invokeLater(new
		 * ToolTipRunnable(toolTip)); }
		 * 
		 * public void removeToolTip(Element elt) { //
		 * System.out.println("rtt "+elt); super.removeToolTip(elt); }
		 */
	}

	protected EventListener toolTipListener = new MyToolTipModifier();

	protected class MyToolTipModifier extends ToolTipModifier {
		public MyToolTipModifier() {
			super();
		}

		public void handleEvent(Event evt) {

			if (matchLastToolTipEvent(evt.getTimeStamp(), evt.getTarget())) {
				return;
			}
			setLastToolTipEvent(evt.getTimeStamp(), evt.getTarget());
			EventTarget prevLastTarget = lastTarget;
			if (SVGConstants.SVG_EVENT_MOUSEOVER.equals(evt.getType())) {
				lastTarget = evt.getTarget();
			} else if (SVGConstants.SVG_EVENT_MOUSEOUT.equals(evt.getType())) {
				org.w3c.dom.events.MouseEvent mouseEvt;
				mouseEvt = ((org.w3c.dom.events.MouseEvent) evt);
				lastTarget = mouseEvt.getRelatedTarget();
			}

			Element e = (Element) lastTarget;

			if (prevLastTarget != lastTarget) {
				if (e != null) {
					String id = e.getAttribute("id");
					if (id != null && !id.equals(""))
						setToolTipText(e.getAttribute("id"));
					else {
						setToolTipText(((Element) e.getParentNode())
								.getAttribute("id"));
					}

				} else
					setToolTipText(null);
			}
		}
	}

	public void mouseMoved(MouseEvent me) {
		getXYLabel()
				.setText(
						"("
								+ new Double(((double) me.getX()+scroll.getHorizontalValue()) / z)
										.intValue()
								+ ","
								+ new Double(((double) me.getY()+scroll.getVerticalValue()) / z)
										.intValue() + ")");
	}

	public void mouseDragged(MouseEvent me) {
	}

	public MyJSVGScrollPane getScroll() {
		return scroll;
	}

	public void setScroll(MyJSVGScrollPane scroll) {
		this.scroll = scroll;
	}
	
	
}
