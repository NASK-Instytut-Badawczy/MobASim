package pl.edu.asim.interfaces;

import java.io.IOException;

import pl.edu.asim.model.ASimDO;

public interface ASimSimulatorInterface {

	public void test();

	public void simulation(ASimDO simulator, String resultsDirectory);

	public void optimization(ASimDO network, String resultsDirecory,
			String taskType);

	// public void setResultsDirectory(String resultsDirectory);

	//public void convertFromObjectToXML(Object object, String filepath)
	//		throws IOException;

	//public Object convertFromXMLToObject(String xmlfile) throws IOException;

	public void econetTask(String bean, String taskType);

}
