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
 * Created 3/26/2008 
 * @author David Kincade 
 */
package org.pentaho.experimental.chart.core;

import junit.framework.TestCase;
import org.jfree.resourceloader.ResourceException;
import org.pentaho.experimental.chart.ChartDocumentContext;
import org.pentaho.experimental.chart.ChartFactory;

import java.util.List;

/**
 * Tests for the ChartDocument class
 *
 * @author David Kincade
 */
public class ChartDocumentTest extends TestCase {
  /**
   * Tests for the <code>booleanAttributeValue()</code> method
   */
  public void testBooleanAttributeValue() {
    // Test the method where the default should be used
    final ChartElement blankElement = new ChartElement();
    assertEquals("The default value is not being used when the attribute does not exist", false, ChartDocument.booleanAttributeValue(blankElement, "test", false));
    assertEquals("The default value is not being used when the attribute does not exist", true, ChartDocument.booleanAttributeValue(blankElement, "test", true));

    // Test the method when the value is null
    final ChartElement nullValueElement = new ChartElement();
    nullValueElement.setAttribute("test", null);
    assertEquals("The default value is not being used when the attribute's value is null", false, ChartDocument.booleanAttributeValue(blankElement, "test", false));
    assertEquals("The default value is not being used when the attribute's value is null", true, ChartDocument.booleanAttributeValue(blankElement, "test", true));

    // Test the method when the attributes have valid values
    final ChartElement validValueElement = new ChartElement();
    validValueElement.setAttribute("true1", "true");
    validValueElement.setAttribute("true2", "tRUE");
    validValueElement.setAttribute("true3", "Yes");
    validValueElement.setAttribute("true4", "oN");
    validValueElement.setAttribute("false1", "false");
    validValueElement.setAttribute("false2", "OFF");
    validValueElement.setAttribute("false3", "nO");
    validValueElement.setAttribute("false4", "");
    validValueElement.setAttribute("junk", "junk");

    assertEquals("The default value is not being used when the attribute's value is neither true or false", false, ChartDocument.booleanAttributeValue(blankElement, "junk", false));
    assertEquals("The default value is not being used when the attribute's value is neither true or false", true, ChartDocument.booleanAttributeValue(blankElement, "junk", true));

    assertEquals("The method is not interpreting the value correctly", true, ChartDocument.booleanAttributeValue(validValueElement, "true1", false));
    assertEquals("The method is not interpreting the value correctly", true, ChartDocument.booleanAttributeValue(validValueElement, "true2", false));
    assertEquals("The method is not interpreting the value correctly", true, ChartDocument.booleanAttributeValue(validValueElement, "true3", false));
    assertEquals("The method is not interpreting the value correctly", true, ChartDocument.booleanAttributeValue(validValueElement, "true4", false));

    assertEquals("The method is not interpreting the value correctly", false, ChartDocument.booleanAttributeValue(validValueElement, "false1", true));
    assertEquals("The method is not interpreting the value correctly", false, ChartDocument.booleanAttributeValue(validValueElement, "false2", true));
    assertEquals("The method is not interpreting the value correctly", false, ChartDocument.booleanAttributeValue(validValueElement, "false3", true));
    assertEquals("The method is not interpreting the value correctly", false, ChartDocument.booleanAttributeValue(validValueElement, "false4", true));
  }

  /**
   * Tests for the <code>isCategorical()</code> method
   */
  public void testIsCategorical() throws ResourceException {
    // Load a chart where the chart element is not the root, but categorical is true
    ChartDocumentContext cdc = null;
    cdc = ChartFactory.generateChart(this.getClass().getResource("ChartDocumentTest1.xml"));
    assertEquals(false, cdc.getChartDocument().isCategorical());

    // Load a chart where categorical is specified correctly in the root element
    cdc = ChartFactory.generateChart(this.getClass().getResource("ChartDocumentTest2.xml"));
    assertEquals(true, cdc.getChartDocument().isCategorical());

    // Load a chart where the chart tag is the top-most tag, but categorical is not set at all
    cdc = ChartFactory.generateChart(this.getClass().getResource("ChartDocumentTest3.xml"));
    assertEquals(false, cdc.getChartDocument().isCategorical());
  }

  /**
   * Tests for the <code>isByRow()</code> method
   */
  public void testIsByRow() throws ResourceException {
    // Load a chart where the chart element is not the root, but byrow is true
    ChartDocumentContext cdc = null;
    cdc = ChartFactory.generateChart(this.getClass().getResource("ChartDocumentTest1.xml"));
    assertEquals(false, cdc.getChartDocument().isCategorical());

    // Load a chart where byrow is specified correctly in the root element
    cdc = ChartFactory.generateChart(this.getClass().getResource("ChartDocumentTest2.xml"));
    assertEquals(true, cdc.getChartDocument().isCategorical());

    // Load a chart where the chart tag is the top-most tag, but byrow is not set at all
    cdc = ChartFactory.generateChart(this.getClass().getResource("ChartDocumentTest3.xml"));
    assertEquals(false, cdc.getChartDocument().isCategorical());
  }

  /**
   * Tests for the <code>getSeriesTags()</code> helper methods
   */
  public void testGetSeriesTags() {
    ChartElement rootElement = new ChartElement();
    rootElement.setTagName(ChartElement.TAG_NAME_CHART);
    ChartElement series1 = new ChartElement(); series1.setTagName(ChartElement.TAG_NAME_SERIES);
    ChartElement series2 = new ChartElement(); series2.setTagName(ChartElement.TAG_NAME_SERIES);
    ChartElement series3 = new ChartElement(); series3.setTagName(ChartElement.TAG_NAME_SERIES);
    ChartElement series4 = new ChartElement(); series4.setTagName(ChartElement.TAG_NAME_SERIES);
    ChartDocument doc = new ChartDocument(rootElement);
    rootElement.addChildElement(new ChartElement());
    rootElement.addChildElement(series1);
    rootElement.addChildElement(new ChartElement());
    rootElement.addChildElement(series2);
    series2.addChildElement(series3);
    rootElement.addChildElement(series4);

    List elements = doc.getSeriesChartElements();
    assertNotNull(elements);
    assertEquals(3, elements.size());
    assertEquals(series1, elements.get(0));
    assertEquals(series2, elements.get(1));
    assertEquals(series4, elements.get(2));
  }

  /**
   * Tests for the <code>getSeriesTags()</code> helper methods
   */
  public void testGetGroupTags() {
    ChartElement rootElement = new ChartElement();
    rootElement.setTagName(ChartElement.TAG_NAME_CHART);
    ChartElement series1 = new ChartElement(); series1.setTagName(ChartElement.TAG_NAME_GROUP);
    ChartElement series2 = new ChartElement(); series2.setTagName(ChartElement.TAG_NAME_GROUP);
    ChartElement series3 = new ChartElement(); series3.setTagName(ChartElement.TAG_NAME_GROUP);
    ChartElement series4 = new ChartElement(); series4.setTagName(ChartElement.TAG_NAME_GROUP);
    ChartDocument doc = new ChartDocument(rootElement);
    rootElement.addChildElement(series1);
    rootElement.addChildElement(new ChartElement());
    rootElement.addChildElement(series2);
    series2.addChildElement(series3);
    rootElement.addChildElement(series4);
    rootElement.addChildElement(new ChartElement());

    List elements = doc.getGroupChartElements();
    assertNotNull(elements);
    assertEquals(3, elements.size());
    assertEquals(series1, elements.get(0));
    assertEquals(series2, elements.get(1));
    assertEquals(series4, elements.get(2));
  }

}
