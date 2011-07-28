package fiji.plugin.trackmate.tests;

import java.io.File;
import java.io.IOException;

import org.jdom.JDOMException;

import fiji.plugin.trackmate.Logger;
import fiji.plugin.trackmate.TrackMateModel;
import fiji.plugin.trackmate.TrackMate_;
import fiji.plugin.trackmate.io.TmXmlReader;
import fiji.plugin.trackmate.segmentation.SegmenterSettings;
import fiji.plugin.trackmate.segmentation.SegmenterType;

public class MultiThread_TestDrive {

	public static void main(String[] args) throws JDOMException, IOException {

		int REPEAT = 15;
		
//		File file = new File("/Users/tinevez/Projects/DMontaras/20052011_8_20.xml");
		File file = new File("/Users/tinevez/Desktop/Data/FakeTracks.xml");
		TmXmlReader reader = new TmXmlReader(file);
		reader.parse();
		TrackMateModel model = reader.getModel();
		
		model.getSettings().segmenterType = SegmenterType.LOG_SEGMENTER;
		SegmenterSettings old = model.getSettings().segmenterSettings;
		model.getSettings().segmenterSettings = model.getSettings().segmenterType.createSettings();
		model.getSettings().segmenterSettings.expectedRadius = old.expectedRadius;
		model.getSettings().segmenterSettings.spaceUnits = old.spaceUnits;
		model.getSettings().segmenterSettings.threshold = old.threshold;
		model.getSettings().segmenterSettings.useMedianFilter = old.useMedianFilter;

		System.out.println(model.getSettings());
		System.out.println(model.getSettings().segmenterSettings);
		System.out.println(model.getSettings().trackerSettings);
		
		TrackMate_ plugin = new TrackMate_(model);
		long start = System.currentTimeMillis();
		
		for (int i = 0; i < REPEAT; i++) {

//			plugin.computeSpotFeatures();
			plugin.execSegmentation();
		
		}
		
		long end  = System.currentTimeMillis();
		model.getLogger().log(String.format("Computing done in %.1f s per repetition.\n", (end-start)/1e3f/REPEAT), Logger.BLUE_COLOR);
		
		
		
		
	}
	
}