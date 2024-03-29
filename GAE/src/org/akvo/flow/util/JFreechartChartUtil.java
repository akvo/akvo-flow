/*
 *  Copyright (C) 2010-2012, 2018, 2021 Stichting Akvo (Akvo Foundation)
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
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger log = Logger
            .getLogger(JFreechartChartUtil.class.getName());

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
        } catch (IOException e) {
            log.log(Level.SEVERE, "Failed to generate chart", e);
            return null;
        }

    }
}
