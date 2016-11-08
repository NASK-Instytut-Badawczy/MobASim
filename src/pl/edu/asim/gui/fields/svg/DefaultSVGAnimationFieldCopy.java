package pl.edu.asim.gui.fields.svg;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import java.math.BigDecimal;

import javax.imageio.ImageIO;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.batik.anim.timing.Interval;
import org.apache.batik.anim.timing.TimedElement;
import org.apache.batik.anim.timing.TimegraphListener;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.SVGAnimationEngine;
import org.apache.batik.bridge.UpdateManager;
import org.apache.batik.bridge.UpdateManagerEvent;
import org.apache.batik.bridge.UpdateManagerListener;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.script.InterpreterPool;
import org.apache.batik.svggen.DOMGroupManager;
import org.apache.batik.svggen.DOMTreeManager;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.svg.SVGDocument;

import pl.edu.asim.gui.Modeler;

public class DefaultSVGAnimationFieldCopy extends JFrame implements ActionListener,
                                                                    TimegraphListener,
                                                                    ChangeListener,
                                                                    WindowListener,
                                                                    UpdateManagerListener,
                                                                    MouseMotionListener {

    org.w3c.dom.Document document;
    MySVGCanvas inSvgCanvas;
    String svgNS;
    DOMImplementation impl;
    SVGGraphics2D svgGenerator;
    String parser;
    SAXSVGDocumentFactory factory;
    DOMTreeManager domTreeManager;
    DOMGroupManager domGroupManager;
    JScrollPane inScroll;
    JButton reload;
    JButton pause;
    JButton stop;
    JButton save;
    JPanel service;
    JPanel buttons;
    MyJSlider progress;
    JLabel timeLabel;
    String value = "";
    int start = 0;
    int end = 0;
    Timer timer;
    boolean timerSource = false;
    String path = "";
    Modeler modeler;

    public DefaultSVGAnimationFieldCopy(String title, final int e, String p, Modeler modeler) {
        super(title);
        path = p;
        this.modeler = modeler;
        try {
            svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
            impl = SVGDOMImplementation.getDOMImplementation();
            end = e;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        this.addWindowListener(this);
    }

    public void showAnimation(org.w3c.dom.Document doc) {

        document = null;
        inSvgCanvas = null;
        svgGenerator = null;
        parser = null;
        factory = null;
        domTreeManager = null;
        domGroupManager = null;
        inScroll = null;
        reload = null;
        pause = null;
        stop = null;
        save = null;
        buttons = null;
        service = null;
        progress = null;
        timeLabel = null;

        try {
            if (doc != null)
                refreshDocument(doc);
            else
                setDocument(value);

            try {
                inSvgCanvas = new MySVGCanvas();
            } catch (Exception e) {
                e.printStackTrace();
            }

            timer = null;
            timer = new Timer(1000, this);
            timer.setActionCommand("T");
            timer.setRepeats(true);

            String width = document.getDocumentElement().getAttribute("width");
            String height =
                document.getDocumentElement().getAttribute("height");
            inSvgCanvas.setPreferredSize(new Dimension(new BigDecimal(width).intValue(),
                                                       new BigDecimal(height).intValue()));
            inSvgCanvas.setSVGDocument((SVGDocument)document);
            inSvgCanvas.setDisableInteractions(false);

            svgGenerator = new SVGGraphics2D(document);
            domTreeManager = svgGenerator.getDOMTreeManager();
            domGroupManager =
                    new DOMGroupManager(new GraphicContext(), domTreeManager);


            inScroll =
                    new JScrollPane(inSvgCanvas, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            inScroll.setPreferredSize(new Dimension(700, 600));
            inSvgCanvas.reloadCanvas();
            inSvgCanvas.addUpdateManagerListener(this);
            inSvgCanvas.addMouseMotionListener(this);


            reload = (JButton) modeler.getContext().getBean("PLAY_button");
            reload.setActionCommand("R");
            reload.addActionListener(this);
            pause = (JButton) modeler.getContext().getBean("PAUSE_button");
            pause.setActionCommand("P");
            pause.addActionListener(this);
            stop = (JButton) modeler.getContext().getBean("STOP_button");
            stop.setActionCommand("S");
            stop.addActionListener(this);
            save = (JButton) modeler.getContext().getBean("SAVE_button");
            save.setActionCommand("V");
            save.addActionListener(this);
            timeLabel = new JLabel("T = " + start, JLabel.CENTER);
            buttons = new JPanel(new GridLayout(1, 5));
            buttons.add(reload);
            buttons.add(pause);
            buttons.add(stop);
            buttons.add(save);
            buttons.add(timeLabel);

            progress = new MyJSlider(JSlider.HORIZONTAL, start, end, start);

            int major = ((end / 5) / 100) * 100;
            int minor = ((end / 50) / 10) * 10;
            progress.setMajorTickSpacing(major);
            progress.setMinorTickSpacing(minor);
            progress.setPaintTicks(true);
            progress.setPaintLabels(true);
            service = new JPanel(new GridLayout(1, 3));
            service.add(buttons);
            service.add(progress);

            this.getContentPane().removeAll();
            this.getContentPane().add(inScroll, BorderLayout.CENTER);
            this.getContentPane().add(service, BorderLayout.SOUTH);

        } catch (Exception e) {
            e.printStackTrace();
        }
        this.pack();
        this.setVisible(true);
    }

    public void stateChanged(ChangeEvent e) {
    }

    public org.w3c.dom.Document getNewDocument() {
        org.w3c.dom.Document document = null;
        try {
            document = impl.createDocument(svgNS, "svg", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    int progressTime = 0;
    int animTime = 0;
    int timerTime = 0;

    public void actionPerformed(ActionEvent e) {


        if (inSvgCanvas.getUpdateManager() == null &&
            !e.getActionCommand().equals("V"))
            return;

        synchronized (this) {
            if (e.getActionCommand().equals("R")) {
                if (((SVGDocument)document).getRootElement().getCurrentTime() ==
                    0) {
                    timer.stop();
                    timer = null;
                    showAnimation(null);
                    progressTime = 0;
                    timerTime = 0;
                } else
                    inSvgCanvas.getUpdateManager().getBridgeContext().getAnimationEngine().unpause();
            } else if (e.getActionCommand().equals("P")) {
                inSvgCanvas.getUpdateManager().getBridgeContext().getAnimationEngine().pause();
            } else if (e.getActionCommand().equals("V")) {

                saveComponentAsJPEG(inSvgCanvas,
                                    path + "/anim_" + timerTime + ".png");

            } else if (e.getActionCommand().equals("S")) {
                inSvgCanvas.getUpdateManager().getBridgeContext().getAnimationEngine().pause();
                //while (inSvgCanvas.getUpdateManager().isRunning()) {
                inSvgCanvas.getUpdateManager().suspend();
                //    try {
                //        this.wait(10);
                //    } catch (Exception ex) {
                //        ex.printStackTrace();
                //    }
                //}
                ((SVGDocument)document).getRootElement().setCurrentTime(0);
                animTime = 0;
            } else if (e.getActionCommand().equals("T") &&
                       inSvgCanvas.getUpdateManager() != null) {

                synchronized (e.getSource()) {

                    if (animTime >= end) {
                        timer.stop();
                        //while (inSvgCanvas.getUpdateManager().isRunning()) {
                        inSvgCanvas.getUpdateManager().suspend();
                        //    try {
                        //        this.wait(10);
                        //    } catch (Exception ex) {
                        //        ex.printStackTrace();
                        //    }
                        //}
                        ((SVGDocument)document).getRootElement().setCurrentTime(0);
                        animTime = 0;
                        timerTime = 0;
                        return;
                    }

                    progress.setEnabled(false);
                    if (progressTime != progress.getValue() &&
                        inSvgCanvas.getUpdateManager() != null) {
                        int newProgressTime = progress.getValue();
                        try {
                            //while (inSvgCanvas.getUpdateManager().isRunning()) {
                            inSvgCanvas.getUpdateManager().suspend();
                            //    this.wait(100);
                            //}
                            ((SVGDocument)document).getRootElement().setCurrentTime(newProgressTime);
                            animTime = newProgressTime;
                            timerTime = newProgressTime;
                            //while (!inSvgCanvas.getUpdateManager().isRunning()) {
                            //    if (!inSvgCanvas.getUpdateManager().isRunning())
                            inSvgCanvas.getUpdateManager().resume();
                            //    this.wait(100);
                            //}
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    } else {
                        if (!((SVGDocument)document).getRootElement().animationsPaused()) {
                            timerTime++;
                        }
                        if (!((SVGDocument)document).getRootElement().animationsPaused() &&
                            timerTime > animTime) {
                            animTime = timerTime;
                        }
                        progress.setEnabled(true);
                        progress.setValue(animTime);
                    }
                    timeLabel.setText("T = " + animTime);
                    progressTime = progress.getValue();
                    progress.setEnabled(true);
                }

            }
        }
    }

    public void setDocument(String s) {
        try {
            parser = XMLResourceDescriptor.getXMLParserClassName();
            factory = new SAXSVGDocumentFactory(parser);
            document = null;
            document = factory.createDocument(svgNS, new StringReader(s));
            value = s;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void refreshDocument(org.w3c.dom.Document doc) {
        try {
            StringWriter sw = new StringWriter(4096);
            DOMUtilities.writeDocument(doc, sw);
            sw.flush();
            String result = sw.toString();
            result = result.replaceAll(">", ">\n");
            sw.close();
            setDocument(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void elementActivated(TimedElement e, float t) {
    }

    public void elementAdded(TimedElement e) {
    }

    public void elementDeactivated(TimedElement e, float t) {
    }

    public void elementFilled(TimedElement e, float t) {
    }

    public void elementInstanceTimesChanged(TimedElement e, float isBegin) {
    }

    public void elementRemoved(TimedElement e) {
    }

    public void elementRepeated(TimedElement e, int i, float t) {
    }

    public void intervalBegan(TimedElement e, Interval i) {
    }

    public void intervalChanged(TimedElement e, Interval i) {
    }

    public void intervalCreated(TimedElement e, Interval i) {
    }

    public void intervalRemoved(TimedElement e, Interval i) {
    }

    class MySVGCanvas extends JSVGCanvas {

        public MyBridgeContext bridgeContext;
        public MyUpdateManager updateManager;

        public MySVGCanvas() {
            super(null, true, true);
        }

        public void initCanvasRenderer() {
            renderGVTTree();
        }

        public void reloadCanvas() {
            if (gvtTreeRenderer != null) {
                needRender = true;
                gvtTreeRenderer.halt();
            } else
                renderGVTTree();
        }
    }

    class MyUpdateManager extends UpdateManager {

        public MyBridgeContext bridgeContext;

        public MyUpdateManager(MyBridgeContext ctx, GraphicsNode gn,
                               org.w3c.dom.Document doc) {
            super(ctx, gn, doc);
        }
    }

    class MyBridgeContext extends BridgeContext {

        public MySVGAnimationEngine animationEngine;

        public MyBridgeContext() {
            super();
        }

        public MyBridgeContext(UserAgent userAgent) {
            super(userAgent);
        }

        public MyBridgeContext(UserAgent userAgent, DocumentLoader loader) {
            super(userAgent, loader);
        }

        public MyBridgeContext(UserAgent userAgent,
                               InterpreterPool interpreterPool,
                               DocumentLoader documentLoader) {
            super(userAgent, interpreterPool, documentLoader);
        }

    }

    class MySVGAnimationEngine extends SVGAnimationEngine {

        public MySVGAnimationEngine(org.w3c.dom.Document doc,
                                    MyBridgeContext ctx) {
            super(doc, ctx);
        }

        protected float tick(float time, boolean hyperlinking) {
            return super.tick(time, hyperlinking);
        }
    }

    class MyJSlider extends JSlider {
        public MyJSlider(int orientation, int min, int max, int value) {
            super(orientation, min, max, value);
        }

        public String getToolTipText() {
            return "" + this.getValue();
        }

        public String getToolTipText(MouseEvent event) {
            return "" + this.getValue();
        }
    }

    public void windowClosing(WindowEvent e) {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        if (inSvgCanvas != null) {
            if (inSvgCanvas.getUpdateManager() != null) {
                inSvgCanvas.getUpdateManager().interrupt();
            }
            inSvgCanvas = null;
        }
    }

    public void windowClosed(WindowEvent e) {
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }

    public void updateFailed(UpdateManagerEvent e) {
    }

    public void mouseMoved(MouseEvent me) {
        //       System.out.println(me.getX()+","+me.getY());
        inSvgCanvas.setToolTipText(me.getX() + "," + me.getY());
    }

    public void mouseDragged(MouseEvent me) {
    }

    public void updateCompleted(UpdateManagerEvent e) {
        progress.setValue(animTime);
        if (timer == null)
            return;
        timer.stop();
        animTime =
                Math.round(inSvgCanvas.getUpdateManager().getBridgeContext().getAnimationEngine().getCurrentTime());
        timerTime = animTime;
        timer.start();
        timeLabel.setText("T = " + animTime);
    }

    public void updateStarted(UpdateManagerEvent e) {
    }

    public void managerStopped(UpdateManagerEvent e) {
        managerStarted = false;
    }

    public void managerResumed(UpdateManagerEvent e) {
    }

    public void managerSuspended(UpdateManagerEvent e) {
    }

    public void managerStarted(UpdateManagerEvent e) {
        timer.start();
    }

    boolean managerStarted = false;

    public void saveComponentAsJPEG(Component myComponent, String filename) {
        Dimension size = myComponent.getSize();
        BufferedImage myImage =
            new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = myImage.createGraphics();
        myComponent.paint(g2);
        try {
            ImageIO.write(myImage, "png", new File(filename));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void exportToImage(String fileName) {

        File newFile = new File(fileName);
        ImageTranscoder imt = null;
        TranscoderInput input = null;

        org.w3c.dom.Document d = getNewDocument();

        d.replaceChild(d.importNode(document.getDocumentElement(), true),
                       d.getDocumentElement());
        try {
            input = new TranscoderInput(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (newFile.toURI().toString().endsWith("jpg") ||
                newFile.toURI().toString().endsWith("jpeg")) {
                imt = new JPEGTranscoder();
                imt.addTranscodingHint(JPEGTranscoder.KEY_QUALITY,
                                       new Float(1.0));
                OutputStream ostream = new FileOutputStream(newFile);
                TranscoderOutput output = new TranscoderOutput(ostream);
                imt.transcode(input, output);
                ostream.flush();
                ostream.close();
            } else if (newFile.toURI().toString().endsWith("tif") ||
                       newFile.toURI().toString().endsWith("tiff")) {
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
