package com.gallatinsystems.common.util;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 * wrapper class for interaction with the JFreechart library.
 * 
 * @author cfagiani
 * 
 */
public class JFreechartChartUtil {

	/**
	 * generates a pie chart with the set of labels and values passed in (the
	 * labels and values arrays must both be non-null and contain the same
	 * number of elements). The chart is returned as a byte array representing
	 * the image.
	 * 
	 * @param labels
	 * @param values
	 * @param title
	 * @param width
	 * @param height
	 * @return - byte array containing the image, null if there is an error.
	 */
	public static byte[] getPieChart(List<String> labels, List<String> values,
			String title, int width, int height) {
		DefaultPieDataset pieDataset = new DefaultPieDataset();

		for (int i = 0; i < labels.size(); i++) {
			pieDataset.setValue(labels.get(i) + " (" + values.get(i) + ")",
					Double.parseDouble(values.get(i)));
		}
		JFreeChart chart = ChartFactory.createPieChart(title, pieDataset,
				false, false, false);
		try {
			return ChartUtilities.encodeAsPNG(chart.createBufferedImage(width,
					height));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}

	}
}
