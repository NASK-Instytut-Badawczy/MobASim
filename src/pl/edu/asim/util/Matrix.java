package pl.edu.asim.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.StringTokenizer;

public class Matrix {

	private HashMap<String, String> values;
	private int rowCount = 0;
	private int columnCount = 0;

	public Matrix(String s) {
		values = new HashMap<String, String>();
		read(s);
	}

	public Matrix() {
		values = new HashMap<String, String>();
	}

	public HashMap<String, String> getValues() {
		return values;
	}

	public void setValues(HashMap<String, String> values) {
		this.values = values;
	}

	public void read(String s) {

		BufferedReader br = new BufferedReader(new StringReader(s));
		String line = null;
		int row = 0;
		try {
			while ((line = br.readLine()) != null) {
				String separator = ",";
				int column = 0;
				StringTokenizer st = new StringTokenizer(line, separator);
				while (st.hasMoreTokens()) {
					String word = st.nextToken().trim();
					values.put(row + "," + column, word);
					if (row >= rowCount)
						rowCount = row + 1;
					if (column >= columnCount)
						columnCount = column + 1;
					column++;
				}
				row++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getColumnCount() {
		return columnCount;
	}

	public void setColumnCount(int columnCount) {
		this.columnCount = columnCount;
	}

	public String exportToCSV() {
		String result = "";
		for (int i = 0; i < rowCount; i++) {
			if (i > 0)
				result = result + "\n";
			for (int ii = 0; ii < columnCount; ii++) {
				if (ii == 0)
					result = result + values.get(i + "," + ii);
				else
					result = result + "," + values.get(i + "," + ii);
			}
		}
		return result;
	}

	public void setAsString(String value, int row, int column) {
		values.put(row + "," + column, value);
		if (row >= rowCount)
			rowCount = row + 1;
		if (column >= columnCount)
			columnCount = column + 1;
	}

	public String getAsString(int row, int column) {
		return values.get(row + "," + column);
	}

	public Double getAsDouble(int row, int column) {
		String v = values.get(row + "," + column);
		if (v != null)
			return new Double(v);
		else
			return null;
	}

	public Integer getAsInteger(int row, int column) {
		String v = values.get(row + "," + column);
		if (v != null)
			return new Integer(v);
		else
			return null;
	}

	public static Matrix zeros(int r, int c) {
		Matrix m = new Matrix();
		for (int i = 0; i < r; i++)
			for (int ii = 0; ii < c; ii++)
				m.setAsString("0", i, ii);
		return m;
	}
}
