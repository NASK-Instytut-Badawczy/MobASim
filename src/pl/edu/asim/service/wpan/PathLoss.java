package pl.edu.asim.service.wpan;

import pl.edu.asim.util.Matrix;

public class PathLoss {

	Matrix n_matrix;

	Matrix s_matrix;

	Double defaultN = 2.0;

	Double defaultS = 1.0;

	public void setNMatrix(String m) {
		if (m != null && !m.equals(""))
			try {
				n_matrix = new Matrix(m);
			} catch (Exception e) {
				n_matrix = new Matrix();
			}
		else
			n_matrix = new Matrix();
	}

	public void setSMatrix(String s) {
		if (s != null && !s.equals(""))
			try {
				s_matrix = new Matrix(s);
			} catch (Exception e) {
				s_matrix = new Matrix();
			}
		else
			s_matrix = new Matrix();
	}

	public Matrix getNMatrix() {
		return n_matrix;
	}

	public Matrix getSMatrix() {
		return s_matrix;
	}

	public synchronized Double getN(int column, int row) {
		try {
			if (column >= n_matrix.getColumnCount()
					|| row >= n_matrix.getRowCount())
				return defaultN;
			Double result = n_matrix.getAsDouble(row, column);
			return (result == null || result.isNaN()) ? defaultN : result;
		} catch (Exception s) {
			s.printStackTrace();
		}
		return defaultN;
	}

	public synchronized Double getS(int column, int row) {
		try {
			if (column >= s_matrix.getColumnCount()
					|| row >= s_matrix.getRowCount())
				return defaultS;
			Double result = s_matrix.getAsDouble(row, column);
			return (result == null || result.isNaN()) ? defaultS : result;
		} catch (Exception s) {
			s.printStackTrace();
		}
		return defaultS;
	}

	public Double getDefaultN() {
		return defaultN;
	}

	public void setDefaultN(Double defaultN) {
		this.defaultN = defaultN;
	}

	public Double getDefaultS() {
		return defaultS;
	}

	public void setDefaultS(Double defaultS) {
		this.defaultS = defaultS;
	}

}
