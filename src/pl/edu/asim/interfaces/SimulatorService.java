package pl.edu.asim.interfaces;

import pl.edu.asim.model.ASimDO;

public interface SimulatorService {

	public SimulatorService newInstance();

	public void simulation(ASimDO simulator, String resultsDirecory);

	public void setManager(ASimSimulatorInterface manager);
	
}
