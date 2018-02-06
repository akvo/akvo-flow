/*
 *  Copyright (C) 2010-2012, 2018 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.flow.util;

import java.awt.Color;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import org.akvo.AkvoColours;


/**
 * wrapper class for interaction with the JFreechart library.
 * 
 * @author cfagiani
 */

public class JFreechartChartUtil {


    /**
     * generates a pie chart with the set of labels and values passed in (the labels and values
     * arrays must both be non-null and contain the same number of elements). The chart is returned
     * as a byte array representing the image.
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
            return ChartUtilities.encodeAsPNG(chart.createBufferedImage(width, height));
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }

    }
    
    /**
     * generates a bar chart with the set of labels and values passed in (the labels and values
     * arrays must both be non-null and contain the same number of elements). The chart is returned
     * as a byte array representing the image.
     * 
     * @param labels
     * @param values
     * @param title
     * @param width
     * @param height
     * @return - byte array containing the image, null if there is an error.
     */
    public static byte[] getBarChart(List<String> labels, List<String> values,
            String title, int width, int height) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < labels.size(); i++) {
            dataset.setValue(
                    Double.parseDouble(values.get(i)),
                    "", // All on one nameless row
                    labels.get(i) // Set the labels for each column
                    );
        }

        JFreeChart chart = ChartFactory.createBarChart(title,
                "", //Domain axis label
                "", //Range axis label; counts need no unit
                dataset,
                PlotOrientation.HORIZONTAL,
                false, //No legend
                false, //not interactive, so no tooltips
                false); //and no URLs

        // Change overall look
        chart.getTitle().setPaint(new Color(AkvoColours.darkPurple));
        chart.setBackgroundPaint(Color.white);
        chart.setBorderVisible(false); //in the 2010's we do everything flat
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinePaint(Color.lightGray);
        
        // Change bar look
        BarRenderer r = (BarRenderer) plot.getRenderer();
        r.setShadowVisible(false); //Flatter
        r.setSeriesPaint(0, new Color(AkvoColours.orange));
        r.setBarPainter(new StandardBarPainter()); //Flattest
        
        try {
            return ChartUtilities.encodeAsPNG(chart.createBufferedImage(width, height));
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }

    }
}
