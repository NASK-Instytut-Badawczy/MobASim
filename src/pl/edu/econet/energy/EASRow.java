package pl.edu.econet.energy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EASRow {

	List<EAS> easList;
	HashMap<String, Double> variables;
	HashMap<String, Integer> indexes;

	public EASRow() {
		easList = new ArrayList<EAS>();
		variables = new HashMap<String, Double>();
		indexes = new HashMap<String, Integer>();
	}

	public double getMin() {
		double result = 1;
		for (Double d : variables.values()) {
			if (d.doubleValue() < result) {
				result = d.doubleValue();
			}
		}
		return result;
	}

	public int getMinIndex() {
		double result = 1;
		int index = 0;
		int i = 0;
		for (EAS eas : easList) {
			Double d = variables.get(eas.getId());
			if (d.doubleValue() < result) {
				result = d.doubleValue();
				index = i;
			}
			i++;
		}
		return index;
	}

	public double getMax() {
		double result = 0;
		for (Double d : variables.values()) {
			if (d.doubleValue() > result) {
				result = d.doubleValue();
			}
		}
		return result;
	}

	public int getMaxIndex() {
		double result = 0;
		int index = 0;
		int i = 0;
		for (EAS eas : easList) {
			Double d = variables.get(eas.getId());
			if (d.doubleValue() > result) {
				result = d.doubleValue();
				index = i;
			}
			i++;
		}
		return index;
	}

	public double getFirstNonInteger() {
		int result = -1;
		for (EAS eas : easList) {
			Double d = variables.get(eas.getId());
			if (d.doubleValue() > 1.0e-015 && d.doubleValue() < 1 - 1.0e-015) {
				return d.doubleValue();
			}
		}
		return result;
	}

	public int getFirstNonIntegerIndex() {
		int i = 1;
		for (EAS eas : easList) {
			Double d = variables.get(eas.getId());
			if (d.doubleValue() > 1.0e-015 && d.doubleValue() < 1 - 1.0e-015) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public int getLastNonIntegerIndex() {
		int i = 1;
		int result = -1;
		for (EAS eas : easList) {
			Double d = variables.get(eas.getId());
			if (d.doubleValue() > 1.0e-015 && d.doubleValue() < 1 - 1.0e-015) {
				result = i;
			} else if(result>-1) {
				return result;
			}
			i++;
		}
		return result;
	}

	public String getFirstNonIntegerId() {

		for (EAS eas : easList) {
			Double d = variables.get(eas.getId());
			if (d.doubleValue() > 1.0e-015 && d.doubleValue() < 1 - 1.0e-015) {
				return eas.getId();
			}
		}
		return "";
	}

	public String getLastNonIntegerId() {

		String prev="";
		for (EAS eas : easList) {
			Double d = variables.get(eas.getId());
			if (d.doubleValue() > 1.0e-015 && d.doubleValue() < 1 - 1.0e-015) {
				prev = eas.getId();
			} else if (!prev.equals("")){
				return prev;
			}
		}
		return prev;
	}

	public double getUnallocation() {
		double result = 0;
		for (EAS eas : easList) {
			Double d = variables.get(eas.getId());
			if (d.doubleValue() > 1.0e-015 && d.doubleValue() < 1 - 1.0e-015) {
				result = result + (1 - d.doubleValue());
			}
		}
		return result;
	}

	public List<EAS> getEasList() {
		return easList;
	}

	public void setEasList(List<EAS> easList) {
		this.easList = easList;
	}

	public HashMap<String, Double> getVariables() {
		return variables;
	}

	public void setVariables(HashMap<String, Double> variables) {
		this.variables = variables;
	}

	public HashMap<String, Integer> getIndexes() {
		return indexes;
	}

	public void setIndexes(HashMap<String, Integer> indexes) {
		this.indexes = indexes;
	}

}
