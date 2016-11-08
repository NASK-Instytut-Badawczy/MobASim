package pl.edu.asim.gui.fields;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;

import pl.edu.asim.gui.Attribute;

public class FileField extends JPanel implements ActionListener,
		AttributeInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String value = "";
	String firstValue = "";
	JTextField valueField;

	JButton undoButton;
	JButton openButton;
	JPanel buttonPanel;
	private Attribute attribute;
	ChangeListener cListener;
	String extensions;
	ArrayList<ModelerActionListener> modelerActionListenerList;
	private String defaultDirectory = "./";
	private boolean directory = false;

	public String getExtensions() {
		return extensions;
	}

	public void setExtensions(String extensions) {
		this.extensions = extensions;
	}

	public FileField() {
		super(new BorderLayout());
		valueField = new JTextField("");
		modelerActionListenerList = new ArrayList<ModelerActionListener>();
	}

	@Override
	public void showField() {
		openButton = (JButton) attribute.getModeler().getContext()
				.getBean("OPEN_button");
		openButton.addActionListener(this);
		undoButton = (JButton) attribute.getModeler().getContext()
				.getBean("UNDO_button");
		undoButton.addActionListener(this);
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(openButton);
		buttonPanel.add(undoButton);

		this.removeAll();
		this.add(valueField, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.EAST);
	}

	@Override
	public void hideField() {
		openButton = null;
		undoButton = null;
		buttonPanel = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == undoButton) {
			setText(firstValue);
		} else if (e.getSource() == openButton) {
			Dialog dialog = new Dialog(value);
			if (directory)
				dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			dialog.setFileFilter(new ExtensionFilter());

			int returnVal = dialog.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = dialog.getSelectedFile();
				valueField.setText(file.getPath());
				if (cListener != null)
					cListener.stateChanged(null);
			} else {
				;
			}
		}
	}

	@Override
	public String getText() {
		return valueField.getText();
	}

	@Override
	public void setText(String s) {
		if (s != null) {
			File f = new File(s);
			value = f.getPath();
		}
		firstValue = value;
		valueField = new JTextField(value);
	}

	@Override
	public Document getDocument() {
		return valueField.getDocument();
	}

	@Override
	public void setBorder(Border b) {
		if (valueField != null)
			valueField.setBorder(b);
		else
			super.setBorder(b);
	}

	@Override
	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	public void addActionListener(ActionListener l) {
		valueField.addActionListener(l);
	}

	public void setChangeListener(ChangeListener cListener) {
		this.cListener = cListener;
	}

	public void setActionCommand(String s) {
		valueField.setActionCommand(s);
	}

	@Override
	public String getToolTipText() {
		return getText();
	}

	@Override
	public Component getComponent() {
		return valueField;
	}

	class Dialog extends JFileChooser {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		Dialog(String v) {
			super(v);
		}
	}

	public class ExtensionFilter extends FileFilter {

		public ExtensionFilter() {
			super();

		}

		public String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 && i < s.length() - 1) {
				ext = s.substring(i + 1).toLowerCase();
			}
			return ext;
		}

		@Override
		public boolean accept(File f) {

			if (extensions == null || extensions.equals(""))
				return true;
			if (f.isDirectory())
				return true;
			String ext = getExtension(f);
			if (ext != null && !ext.equals("")
					&& extensions.contains(getExtension(f))) {
				return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return extensions;
		}
	}

	public File getSaveFile() {
		File file = null;
		Dialog dialog = null;
		if (value.equals("")) {
			dialog = new Dialog(this.defaultDirectory);
		} else {
			File f = new File(value);
			String p = f.getParent();
			if (f.exists()) {
				dialog = new Dialog(value);
			} else if (!p.equals("")) {
				dialog = new Dialog(p);
			} else {
				dialog = new Dialog(defaultDirectory);
			}
		}
		if (directory)
			dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		ExtensionFilter ef = new ExtensionFilter();
		dialog.setFileFilter(ef);
		dialog.setAcceptAllFileFilterUsed(false);

		int returnVal = dialog.showSaveDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = dialog.getSelectedFile();
			valueField.setText(file.getPath());
		} else {
			;
		}
		return file;

	}

	@Override
	public void addModelerActionListener(ModelerActionListener listener) {
		modelerActionListenerList.add(listener);
	}

	public String getDefaultDirectory() {
		return defaultDirectory;
	}

	public void setDefaultDirectory(String defaultDirectory) {
		this.defaultDirectory = defaultDirectory;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub

	}

}
