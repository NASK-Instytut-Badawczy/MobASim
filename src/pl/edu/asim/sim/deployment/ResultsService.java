package pl.edu.asim.sim.deployment;

import java.io.BufferedWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import pl.edu.asim.interfaces.Service;
import pl.edu.asim.proc.TLProcess;
import pl.edu.asim.service.mobility.JTSUtils;
import pl.edu.asim.service.mobility.OperatingAreaPoint;
import pl.edu.asim.service.wpan.WirelessChannel;
import pl.edu.asim.sim.ASimSimulatorManager;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

public class ResultsService extends TLProcess implements Service {

	private ASimSimulatorManager manager;
	private HashMap<String, Vector3D> entities;
	private HashMap<String, Double> ranges;
	private HashMap<String, Double> sensingRanges;
	private Double pathLenght;
	private Double energy;
	private Double fullArea;
	private Polygon area;
	private String bsId;
	private String resultsDirectory;
	private String simulatorName;
	private String separator = "&";
	private String hs;
	private BufferedWriter out;
	private WirelessChannel<BroadcastMessage> wlChannel;
	private double averageND;
	private double scale = 1;

	private boolean save = false;
	private boolean saveSVG = false;

	private GeometryFactory geometryFactory;
	private JTSUtils jts;

	@Override
	public Integer service() throws Exception {
		long time = System.currentTimeMillis();

		results();
		save = false;
		if (saveSVG) {
			manager.getSvgManager().exportToImage(
					resultsDirectory + "/" + simulatorName + "/"
							+ getTimestamp() + ".png");

			saveSVG = false;
		}
		this.setCalculationTime(new BigInteger(""
				+ (System.currentTimeMillis() - time)));

		return new Integer("0");
	}

	public void setManager(ASimSimulatorManager manager) {
		this.manager = manager;
	}

	public ASimSimulatorManager getManager() {
		return manager;
	}

	public void results() {

		try {
			Integer izCount = 0;
			Geometry networkArea = getGeometryFactory().toGeometry(
					new Envelope());
			Geometry networkArea2 = getGeometryFactory().toGeometry(
					new Envelope());
			// HashMap<String, List<String>> connections = this
			// .calculateWirelessConnections(entities, ranges,
			// sensingRanges);

			manager.getSvgManager().updatePath(wlChannel.getWirelessPath(),
					getTimestamp(), this.getDt());

			for (String entity : entities.keySet()) {
				HashSet<String> askSet = new HashSet<String>();
				askSet.add(bsId);
				Vector3D e = entities.get(entity);

				// Geometry coverage = new OperatingAreaPoint(e).getAsJTSBuffer(
				// manager.getGeometryFactory(), (ranges.get(entity) / 2));
				Geometry coverage = new OperatingAreaPoint(e).getAsJTSBuffer(
						getGeometryFactory(), sensingRanges.get(entity));

				if (!entity.equals(bsId)
						&& !wlChannel.isConnected(entity, bsId, askSet))
					izCount++;
				else {
					networkArea2 = networkArea2.union(coverage);
				}

				networkArea = networkArea.union(coverage);
			}

			// if (wlChannel.getLinkCount() == 0)
			// return;

			networkArea = networkArea.intersection(area);
			// double percent = new BigDecimal(networkArea.getArea() / fullArea)
			// .setScale(2, RoundingMode.HALF_DOWN).doubleValue();

			networkArea2 = networkArea2.intersection(area);
			double percent2 = new BigDecimal(networkArea2.getArea() / fullArea)
					.setScale(2, RoundingMode.HALF_DOWN).doubleValue();

			pathLenght = new BigDecimal(pathLenght)
					.divide(new BigDecimal(scale), BigDecimal.ROUND_HALF_UP)
					.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			// double e = new BigDecimal(energy).setScale(2,
			// BigDecimal.ROUND_HALF_DOWN).doubleValue();

			double averageLL = 0;
			if (wlChannel.getLinkCount() > 0)
				averageLL = new BigDecimal(
						(wlChannel.getLinkwidth() / wlChannel.getLinkCount())
								/ scale)
						.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue();
			if (save) {

				out.write(" \t " + getTimestamp() + " \t " + separator + " "
						+ averageND + " \t " + separator + " " + izCount
						+ " \t " + separator + " " + percent2 + " \t "
						+ separator + " " + pathLenght + " \\\\");

				out.newLine();
				manager.getLogger().info(
						" \t " + getTimestamp() + " \t " + separator + " "
								+ averageND + " \t " + separator + " "
								+ izCount + " \t " + separator + " " + percent2
								+ " \t " + separator + " " + pathLenght
								+ " \\\\");
			}
			System.out.println("k=" + getTimestamp() + " area = " + percent2
					+ " iz=" + izCount + " path=" + pathLenght + " link ="
					+ averageLL + " dim=" + averageND);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setEntities(HashMap<String, Vector3D> entities) {
		this.entities = entities;
	}

	public HashMap<String, Vector3D> getEntities() {
		return entities;
	}

	public void setRanges(HashMap<String, Double> ranges) {
		this.ranges = ranges;
	}

	public HashMap<String, Double> getRanges() {
		return ranges;
	}

	public void setPathLenght(Double pathLenght) {
		this.pathLenght = pathLenght;
	}

	public Double getPathLenght() {
		return pathLenght;
	}

	public void setEnergy(Double energy) {
		this.energy = energy;
	}

	public Double getEnergy() {
		return energy;
	}

	public void setSave(boolean save) {
		this.save = save;
	}

	public boolean isSave() {
		return save;
	}

	public void setSaveSVG(boolean saveSVG) {
		this.saveSVG = saveSVG;
	}

	public boolean isSaveSVG() {
		return saveSVG;
	}

	public void setBsId(String bsId) {
		this.bsId = bsId;
	}

	public String getBsId() {
		return bsId;
	}

	public void setFullArea(Double fullArea) {
		this.fullArea = fullArea;
	}

	public Double getFullArea() {
		return fullArea;
	}

	public Polygon getArea() {
		return area;
	}

	public void setArea(Polygon area) {
		this.area = area;
	}

	public String getResultsDirectory() {
		return resultsDirectory;
	}

	public void setResultsDirectory(String resultsDirectory) {
		this.resultsDirectory = resultsDirectory;
	}

	public String getSimulatorName() {
		return simulatorName;
	}

	public void setSimulatorName(String simulatorName) {
		this.simulatorName = simulatorName;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public HashMap<String, Double> getSensingRanges() {
		return sensingRanges;
	}

	public void setSensingRanges(HashMap<String, Double> sensingRanges) {
		this.sensingRanges = sensingRanges;
	}

	public String getHs() {
		return hs;
	}

	public void setHs(String hs) {
		this.hs = hs;
	}

	public BufferedWriter getOut() {
		return out;
	}

	public void setOut(BufferedWriter out) {
		this.out = out;
	}

	public GeometryFactory getGeometryFactory() {
		return geometryFactory;
	}

	public void setGeometryFactory(GeometryFactory geometryFactory) {
		this.geometryFactory = geometryFactory;
	}

	public JTSUtils getJts() {
		return jts;
	}

	public void setJts(JTSUtils jts) {
		this.jts = jts;
	}

	public WirelessChannel<BroadcastMessage> getWlChannel() {
		return wlChannel;
	}

	public void setWlChannel(WirelessChannel<BroadcastMessage> wlChannel) {
		this.wlChannel = wlChannel;
	}

	public double getAverageND() {
		return averageND;
	}

	public void setAverageND(double averageND) {
		this.averageND = averageND;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

}
