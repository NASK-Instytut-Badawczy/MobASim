package pl.edu.asim.gui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import pl.edu.asim.model.DataManager;

public class XMLExportAction extends ModelerAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XMLExportAction(String name) {
		super(name);
	}

	public XMLExportAction(String name, ImageIcon icon) {
		super(name, icon);
	}

	@Override
	public int operation(ActionEvent e) {
		try {

			JFileChooser dialog = new JFileChooser(modeler.getWorkspace());

			File file = null;
			int returnVal = dialog.showSaveDialog(getModeler().getFrame());

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = dialog.getSelectedFile();
				javax.xml.bind.Marshaller marshaller = DataManager
						.getInstance().getJaxbContext().createMarshaller();
				marshaller.setProperty(
						javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, true);
				marshaller.marshal(getModeler().getSelectedNode()
						.getSourceElementData(),
						new FileOutputStream(file.getAbsolutePath()));
			} else {
				;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return getDec();
	}

}
