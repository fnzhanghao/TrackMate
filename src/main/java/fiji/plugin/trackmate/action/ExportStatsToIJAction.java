package fiji.plugin.trackmate.action;


import ij.measure.ResultsTable;

import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import org.jgrapht.graph.DefaultWeightedEdge;

import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.TrackMateModel;
import fiji.plugin.trackmate.TrackMate_;
import fiji.plugin.trackmate.features.FeatureModel;
import fiji.plugin.trackmate.gui.TrackMateWizard;

public class ExportStatsToIJAction<T extends RealType<T> & NativeType<T>> extends AbstractTMAction<T> {

	public static final ImageIcon ICON = new ImageIcon(TrackMateWizard.class.getResource("images/calculator.png"));
	public static final String NAME = "Export statistics to tables";
	public static final String INFO_TEXT = "<html>" +
				"Compute and export all statistics to 3 ImageJ results table." +
				"Statistisc are separated in features computed for:" +
				"<ol>" +
				"	<li> spots in filtered tracks;" +
				"	<li> links between those spots;" +
				"	<li> filtered tracks." +
				"</ol>" +
				"For tracks and links, they are recalculated prior to exporting. Note " +
				"that spots and links that are not in a filtered tracks are not part" +
				"of this export." +
				"</html>";

	public ExportStatsToIJAction() {
		this.icon = ICON;
	}
	
	@Override
	public void execute(final TrackMate_<T> plugin) {
		logger.log("Exporting statistics.\n");
		
		// Compute links features Links
		logger.log("  - Calculating statistics on links...");
		plugin.computeEdgeFeatures();
		logger.log(" Done.\n");
		
		// Compute track features
		logger.log("  - Calculating statistics on tracks...");
		plugin.computeTrackFeatures();

		// Model
		final TrackMateModel<T> model = plugin.getModel();
		final FeatureModel<T> fm = model.getFeatureModel();
		
		// Export spots
		logger.log("  - Exporting spot statistics...");
		Set<Integer> trackIndices = model.getVisibleTrackIndices();
		List<String> spotFeatures = fm.getSpotFeatures();

		// Create table
		ResultsTable spotTable = new ResultsTable();
		
		// Parse spots to insert values as objects
		for (Integer trackIndex : trackIndices) {
			Set<Spot> track = model.getTrackSpots(trackIndex);
			for (Spot spot : track) {
				spotTable.incrementCounter();
				spotTable.addLabel(spot.getName());
				spotTable.addValue("ID", spot.ID());
				spotTable.addValue("TRACK", trackIndex);
				for (String feature : spotFeatures) {
					spotTable.addValue(feature, spot.getFeature(feature));
				}
			}
		}
		logger.log(" Done.\n");
		
		
		// Export edges
		logger.log("  - Exporting links statistics...");
		// Yield available edge feature
		List<String> edgeFeatures = fm.getEdgeFeatures();
		
		// Create table
		ResultsTable edgeTable = new ResultsTable();
		
		// Sort by track
		for (Integer trackIndex : trackIndices) {
			
			Set<DefaultWeightedEdge> track = model.getTrackEdges(trackIndex);
			for (DefaultWeightedEdge edge : track) {
				edgeTable.incrementCounter();
				edgeTable.addLabel(edge.toString());
				for(String feature : edgeFeatures) {
					Object o = fm.getEdgeFeature(edge, feature);
					if (o instanceof String) {
						continue;
					}
					Number d = (Number) o;
					edgeTable.addValue(feature, d.doubleValue());
				}
				
			}
		}
		logger.log(" Done.\n");
		
		// Export tracks
		logger.log("  - Exporting tracks statistics...");
		// Yield available edge feature
		List<String> trackFeatures = fm.getTrackFeatures();

		// Create table
		ResultsTable trackTable = new ResultsTable();

		// Sort by track
		for (Integer trackIndex : trackIndices) {
			trackTable.incrementCounter();
			trackTable.addLabel("TRACK_" + trackIndex);
			for (String feature : trackFeatures) {
				Double val = fm.getTrackFeature(trackIndex, feature);
				trackTable.addValue(feature, val);
			}
		}
		logger.log(" Done.\n");

		// Show tables
		spotTable.show("Spots in tracks statistics");
		edgeTable.show("Links in tracks statistics");
		trackTable.show("Track statistics");
	}

	@Override
	public String getInfoText() {
		return INFO_TEXT;
	}
	
	@Override
	public String toString() {
		return NAME;
	}

}