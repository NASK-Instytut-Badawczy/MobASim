package pl.edu.asim.gui.fields.svg;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import pl.edu.asim.gui.Modeler;
import pl.edu.asim.gui.actions.FieldUpdateAction;
import pl.edu.asim.gui.fields.ModelerActionListener;

public class SVGElementPane extends JPanel implements ModelerActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	Map<String,SVGAttribute> attributes;
	Modeler modeler;
	DefaultSVGEditorField editor;
	public DefaultSVGEditorField getEditor() {
		return editor;
	}

	public void setEditor(DefaultSVGEditorField editor) {
		this.editor = editor;
	}

	org.w3c.dom.Node element;
	int index;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public org.w3c.dom.Node getElement() {
		return element;
	}

	public void setElement(org.w3c.dom.Node element) {
		this.element = element;
		NamedNodeMap amap = element.getAttributes();
		int size = amap.getLength();

		for (int i = 0; i < size; i++) {
			Node n = amap.item(i);
			String name = n.getNodeName();
			SVGAttribute a = attributes.get(name);
			if(a==null) continue;
			if(!a.isTextAttribute()) {
				a.setSourcePropertyData(n);
			} else {
				a.setSourcePropertyData(this.element);				
			}
		}
	}

	public SVGElementPane(String name){
		super(new FlowLayout());
		this.name = name;
		this.setPreferredSize(new Dimension(520, 200));
		this.setName(name);

		JLabel elementTypeLabel1 = new JLabel("SVG element: ", JLabel.RIGHT);
		JLabel elementTypeLabel2 = new JLabel(name, JLabel.CENTER);

		this.add(elementTypeLabel1);
		this.add(elementTypeLabel2);

		JSeparator s = new JSeparator(JSeparator.HORIZONTAL);
		s.setPreferredSize(new Dimension(540, 2));
		this.add(s);

	}
	
	public void showField(){
		for(Iterator<SVGAttribute> it = attributes.values().iterator(); it.hasNext();) {
			SVGAttribute t = it.next();
			t.getField().showField();
			JLabel label = t.getLabel();
			this.add(label);
			JPanel field = (JPanel) t.getField();
			field.setPreferredSize(new Dimension(450, 25));
			this.add(field);
			
			if(t.getSourcePropertyData()==null) {
				if(t.isTextAttribute()) {
					t.setSourcePropertyData(this.element);
				} else {
					org.w3c.dom.Attr t1 = editor.manager.getSvgDocument().createAttributeNS(
						SVGConstant.getNS(t.getName()), t.getName());
					((org.w3c.dom.Element) element).setAttributeNodeNS(t1);
					t.setSourcePropertyData(t1);
				}
			}
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String,SVGAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String,SVGAttribute> attributes) {
		this.attributes = attributes;
	}

	public Modeler getModeler() {
		return modeler;
	}

	public void setModeler(Modeler modeler) {
		this.modeler = modeler;
		for(Iterator<SVGAttribute> it = attributes.values().iterator();it.hasNext();){
			SVGAttribute a = it.next();
			a.setModeler(modeler);
			a.getField().addModelerActionListener(this);			
		}
	}

	public void modelerAction(FieldUpdateAction action) {
		if (action.getField().getComponent().getName().equals("id")) {
			name = action.getField().getText();
			editor.elementPane.setTitleAt(index, name);
		}
		this.repaint();
	}

}
