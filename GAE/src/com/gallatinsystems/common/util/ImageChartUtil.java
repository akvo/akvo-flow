/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.common.util;

import java.util.List;

/**
 * Utility to wrap the Google Image Charts API. This class can be used to invoke a call to the
 * charts api that will return a byte array representing the generated image data
 * 
 * @author Christopher Fagiani
 */
public class ImageChartUtil {
    private static final String CHART_API_URL = "http://chart.apis.google.com/chart";
    private static final String PIE_CHART_TYPE = "cht=p";
    private static final String DIM_PARAM = "&chs=";
    private static final String DATA_PARAM = "&chd=t:";
    private static final String LABEL_PARAM = "&chl=";
    private static final String LEGEND_PARAM = "&chdl=";
    private static final String TITLE_PARAM = "&chtt=";
    private static final String SCALE_PARAM = "&chds=";

    /**
     * Gets a Pie chart for the values passed in that includes the raw data value as the data labels
     * in addition to the legend. Due to the use of the labels, the chart usually needs to be wider
     * than tall to prevent clipping. The generated image chart is returned as a byte array
     * representing a PNG image.
     * 
     * @param labels - labels for the data in the Legend
     * @param values - double (or integer) values (number of values must match number of labels)
     * @param title - chart title
     * @param width
     * @param height
     * @return
     */
    public static byte[] getPieChart(List<String> labels, List<String> values,
            String title, int width, int height) {
        StringBuilder urlParameters = new StringBuilder();
        urlParameters.append(PIE_CHART_TYPE).append(DIM_PARAM)
                .append(height + "x" + width).append(DATA_PARAM);
        if (title != null) {
            urlParameters.append(TITLE_PARAM).append(title);
        }
        StringBuilder dataString = new StringBuilder();
        StringBuilder labelString = new StringBuilder();
        StringBuilder legendString = new StringBuilder();
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                dataString.append(",");
                labelString.append("|");
                legendString.append("|");
            }

            Double val = null;
            try {
                val = Double.parseDouble(values.get(i));
                int valInt = val.intValue();
                if (valInt > max) {
                    max = valInt;
                }
                if (valInt < min) {
                    min = valInt;
                }
                dataString.append(values.get(i));
                labelString.append(val);
                legendString.append(labels.get(i));
            } catch (Exception e) {
                System.err
                        .println("Could not parse value. Omitting from chart: "
                                + e);
            }
            urlParameters.append(DATA_PARAM).append(dataString.toString());
            urlParameters.append(LABEL_PARAM).append(labelString.toString());
            urlParameters.append(LEGEND_PARAM).append(legendString.toString());
            if (min == max) {
                // to ensure the chart renders properly if all values are the same
                min--;
            }
            urlParameters.append(SCALE_PARAM).append(min + "," + max);
        }
        return HttpUtil.doPost(CHART_API_URL, urlParameters.toString());
    }

}
