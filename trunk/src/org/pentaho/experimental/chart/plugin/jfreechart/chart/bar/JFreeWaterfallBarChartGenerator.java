/*
 * Copyright 2007 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
 *
 * @created May 21, 2008 
 * @author wseyler
 */

package org.pentaho.experimental.chart.plugin.jfreechart.chart.bar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.WaterfallBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.pentaho.experimental.chart.ChartDocumentContext;
import org.pentaho.experimental.chart.core.ChartDocument;
import org.pentaho.experimental.chart.core.ChartElement;
import org.pentaho.experimental.chart.css.keys.ChartStyleKeys;
import org.pentaho.experimental.chart.data.ChartTableModel;
import org.pentaho.experimental.chart.plugin.jfreechart.utils.JFreeChartUtils;
import org.pentaho.experimental.chart.plugin.jfreechart.utils.StrokeFactory;
import org.pentaho.reporting.libraries.css.keys.border.BorderStyleKeys;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * @author wseyler
 *
 */
public class JFreeWaterfallBarChartGenerator extends JFreeBarChartGenerator {

  /* (non-Javadoc)
   * @see org.pentaho.experimental.chart.plugin.jfreechart.chart.IJFreeChartGenerator#createChart(org.pentaho.experimental.chart.ChartDocumentContext, org.pentaho.experimental.chart.data.ChartTableModel)
   */
  public JFreeChart createChart(final ChartDocumentContext chartDocContext, final ChartTableModel data) {
    final ChartDocument chartDocument = chartDocContext.getChartDocument();
    final String title = getTitle(chartDocument);
    final String valueCategoryLabel = getValueCategoryLabel(chartDocument);
    final String valueAxisLabel = getValueAxisLabel(chartDocument);
    final PlotOrientation orientation = getPlotOrientation(chartDocument);
    final boolean legend = getShowLegend(chartDocument);
    final boolean toolTips = getShowToolTips(chartDocument);

    final DefaultCategoryDataset categoryDataset = datasetGeneratorFactory.createDefaultCategoryDataset(
        chartDocContext, data);

    final JFreeChart chart = ChartFactory.createWaterfallChart(title, valueCategoryLabel, valueAxisLabel,
        categoryDataset, orientation, legend, toolTips, toolTips);

    final CategoryPlot categoryPlot = chart.getCategoryPlot();
    final ChartElement plotElement = chartDocument.getRootElement().findChildrenByName(ChartElement.TAG_NAME_PLOT)[0];
    setPlotAttributes(categoryPlot, plotElement);
    final ChartElement[] seriesElements = chartDocument.getRootElement().findChildrenByName(
        ChartElement.TAG_NAME_SERIES);
    if (categoryPlot != null && seriesElements != null) {
      setSeriesAttributes(seriesElements, data, categoryPlot);
    }

    return chart;
  }

  private void setPlotAttributes(CategoryPlot categoryPlot, ChartElement plotElement) {
    WaterfallBarRenderer render = (WaterfallBarRenderer) categoryPlot.getRenderer();
    Paint firstColor = (Paint) plotElement.getLayoutStyle().getValue(ChartStyleKeys.FIRST_BAR_COLOR);
    Paint lastColor = (Paint) plotElement.getLayoutStyle().getValue(ChartStyleKeys.LAST_BAR_COLOR);
    Paint positiveColor = (Paint) plotElement.getLayoutStyle().getValue(ChartStyleKeys.POSITIVE_BAR_COLOR);
    Paint negativeColor = (Paint) plotElement.getLayoutStyle().getValue(ChartStyleKeys.NEGATIVE_BAR_COLOR);
    render.setFirstBarPaint(firstColor);
    render.setLastBarPaint(lastColor);
    render.setPositiveBarPaint(positiveColor);
    render.setNegativeBarPaint(negativeColor);
  }

  /**
   * Sets the series specific attributes for the current chart object.
   * </p>
   * @param seriesElements -- Series elements from the current chart definition.
   * @param data           -- Actual data.
   * @param categoryPlot   -- The category plot from the current chart object. Category plot will be updated.
   */
  public void setSeriesAttributes(final ChartElement[] seriesElements, final ChartTableModel data,
      final CategoryPlot categoryPlot) {
    setSeriesItemLabel(categoryPlot, seriesElements, data);
    setSeriesPaint(categoryPlot, seriesElements, data);
    setSeriesBarOutline(categoryPlot, seriesElements, data);

  }
 
  /**
   * Paints the series border/outline.
   *
   * @param categoryPlot   The plot that has the renderer object
   * @param seriesElements The series elements from the chart document
   */
  private static void setSeriesBarOutline(final CategoryPlot categoryPlot, final ChartElement[] seriesElements, ChartTableModel data) {
    final int length = seriesElements.length;
    final StrokeFactory strokeFacObj = StrokeFactory.getInstance();
    for (int i = 0; i < length; i++) {
      final ChartElement currElement = seriesElements[i];
      final int column = JFreeChartUtils.getSeriesColumn(currElement, data, i);
      if (categoryPlot.getRenderer() instanceof BarRenderer) {
        final BarRenderer barRender = (BarRenderer) categoryPlot.getRenderer();
        final BasicStroke borderStyleStroke = strokeFacObj.getBorderStroke(currElement);
        if (borderStyleStroke != null) {
          final CSSValue borderColorValue = currElement.getLayoutStyle().getValue(BorderStyleKeys.BORDER_TOP_COLOR);
          final Color borderColor = JFreeChartUtils.getColorFromCSSValue(borderColorValue);
          if (borderColor != null) {
            barRender.setSeriesOutlinePaint(column, borderColor, true);
          }
          barRender.setSeriesOutlineStroke(column, borderStyleStroke, true);
          barRender.setDrawBarOutline(true);
        }
      }
    }
  }
        
//  /**
//   * @param currElement
//   * @return
//   */
//  public static boolean isMarkerVisible(ChartElement currElement) {
//    final String visibleStr = currElement.getLayoutStyle().getValue(ChartStyleKeys.MARKER_VISIBLE).getCSSText();
//    return ChartMarkerVisibleType.YES.getCSSText().equalsIgnoreCase(visibleStr);
//  }
}
