package com.gallatinsystems.common.util;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class JFreechartChartUtil {

	public static byte[] getPieChart(List<String> labels, List<String> values,
			String title, int width, int height) {
		DefaultPieDataset pieDataset = new DefaultPieDataset();

		for (int i = 0; i < labels.size(); i++) {
			pieDataset.setValue(labels.get(i)+" ("+values.get(i)+")",
					Double.parseDouble(values.get(i)));
		}
		JFreeChart chart = ChartFactory.createPieChart(title, pieDataset, false,
				false, false);
		try {
			return ChartUtilities.encodeAsPNG(chart.createBufferedImage(width,
					height));
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}

	}
}
