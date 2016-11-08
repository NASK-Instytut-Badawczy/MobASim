package pl.edu.asim.gui;

import java.awt.Color;
import java.awt.Component;

import java.awt.Dimension;

import java.awt.Font;

import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

public class TreeRenderer extends DefaultTreeCellRenderer {

	Modeler modeler;

	public TreeRenderer(Modeler modeler) {
		this.modeler = modeler;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
				row, hasFocus);
		setToolTipText(value.toString());
		Font f = this.getFont();
		Font italicFont = new Font(f.getName(), Font.ITALIC, 11);
		// Font boldItalicFont = new Font(f.getName(), Font.BOLD+Font.PLAIN,
		// 12);

		this.setFont(italicFont);
		if (((GuiNode) value) == modeler.getSelectedNode()) {
			this.setForeground(Color.RED);
		} else
			this.setForeground(Color.BLACK);
		this.setPreferredSize(new Dimension(20 + (this.getFontMetrics(this
				.getFont()).stringWidth(value.toString())), 20));
		// System.out.println(value.getClass());
		// System.out.println(selectedNode.getClass());
		// this.setBorder(new LineBorder(Color.RED));
		return this;
	}

}
