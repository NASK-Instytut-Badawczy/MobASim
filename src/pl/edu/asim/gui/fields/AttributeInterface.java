package pl.edu.asim.gui.fields;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.text.Document;

import pl.edu.asim.gui.Attribute;

public interface AttributeInterface {

	public String getText();

	public void setText(String s);

	public Document getDocument();

	public void setAttribute(Attribute attribute);

	public Component getComponent();

	public void showField();

	public void hideField();

	public void setBounds(int x, int y, int width, int height);

	public void setPreferredSize(Dimension d);

	public Dimension getPreferredSize();

	public void addModelerActionListener(ModelerActionListener listener);

	public void setDescription(String description);

}