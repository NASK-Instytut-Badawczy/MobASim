package pl.edu.asim.gui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import javax.swing.ImageIcon;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import pl.edu.asim.gui.Attribute;
import pl.edu.asim.gui.fields.svg.DefaultSVGAnimationField;
import pl.edu.asim.interfaces.ASimSimulatorInterface;

public class ShowAnimationAction extends ModelerAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3110536757042855218L;
	ASimSimulatorInterface manager;

	public ShowAnimationAction(String name, ImageIcon icon) {
		super(name, icon);
	}

	@Override
	public int operation(ActionEvent e) {

		Attribute a = this.getModeler().getSelectedNode().getPropertyMap()
				.get("time");
		if (a == null)
			return 0;
		int time = new BigDecimal(a.getField().getText()).intValue();

		DefaultSVGAnimationField anim = new DefaultSVGAnimationField(this
				.getModeler().getSelectedNode().getSourceElementData()
				.getName(), time, this.getModeler().getWorkspace(),
				this.getModeler());

		File file = new File(this.getModeler().getWorkspace().toString()
				+ "/svg_model/"
				+ this.getModeler().getSelectedNode().getSourceElementData()
						.getName() + "_anim.svg");

		if (file.exists()) {

			try {
				String parser = XMLResourceDescriptor.getXMLParserClassName();
				SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
				String uri = file.toURI().toString();
				SVGDocument doc = (SVGDocument) f.createDocument(uri);
				anim.showAnimation(doc);
				anim.setVisible(true);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return 0;
	}
}