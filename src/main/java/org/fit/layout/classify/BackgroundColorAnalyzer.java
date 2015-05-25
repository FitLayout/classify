/**
 * BackgroundColorAnalyzer.java
 *
 * Created on 29.10.2012, 13:19:19 by burgetr
 */
package org.fit.layout.classify;

import java.awt.Color;
import java.util.HashMap;

import org.fit.layout.model.Area;


/**
 * Background color analyzer. It gathers the statistics about the background color usage in areas.
 * 
 * @author burgetr
 */
public class BackgroundColorAnalyzer
{
    /** Maps the color representation to the total area of that color in the document */
    private HashMap<Integer, Integer> colors;
    private Area root;
    private int totalArea;
    
    /**
     * Constructs a color analyzer.
     * @param root
     */
    public BackgroundColorAnalyzer(Area root)
    {
        colors = new HashMap<Integer, Integer>();
        this.root = root;
        computeRootStatistics(this.root);
        totalArea = root.getBounds().getArea();
        System.err.println("We have " + colors.size() + " different background colors, " + totalArea + " total area");
    }
    
    /**
     * Obtains the percentage of the area that has the given color.
     * @param color the color to be tested.
     * @return the percentage (0..1)
     */
    public double getColorPercentage(Color color)
    {
        if (color == null)
            return 0;
        else
        {
            Integer num = colors.get(colorKey(color));
            if (num == null) num = 0;
            if (totalArea == 0)
                return 0;
            else
                return (double) num / totalArea;
        }
    }

    /**
     * Obtains the average percentage of the total area that has the same background color as the given area.
     * @param area the area whose background should be compared.
     * @return the percentage (0..1) for background-separated areas or a negative value for non-separated nodes.
     */
    public double getColorPercentage(Area area)
    {
        if (area.isBackgroundSeparated())
            return getColorPercentage(getEffectiveBackgroundColor(area));
        else
            return -1.0;
    }

    //==================================================================================================
    
    /**
     * Recursively computes the statistics of the individual colors in a subtree.
     * @param root the root of the subtree
     */
    private void computeRootStatistics(Area root)
    {
        if (root.isBackgroundSeparated())
        {
            Color color = root.getBackgroundColor();
    
            if (color != null)
            {
                int key = colorKey(color);
                Integer val = colors.get(key);
                if (val == null) val = 0;
                val += root.getBounds().getArea();
                colors.put(key, val);
            }
        }
        
        for (int i = 0; i < root.getChildCount(); i++)
            computeRootStatistics(root.getChildArea(i));
    }
    
    /**
     * Converts a color to a number, 4 bits per color channel so that similar colors have similar numbers.
     */
    private int colorKey(Color color)
    {
        return (color.getRed() / 16) * 256 + (color.getGreen() / 16) * 16 + (color.getBlue() / 16);
    }

    /**
     * Computes the effective (visible) background color of an area considering
     * transparency and parent areas.
     * @param area The area
     * @return the visible background color
     */
    public static Color getEffectiveBackgroundColor(Area area)
    {
        if (area.getBackgroundColor() != null)
            return area.getBackgroundColor();
        else
        {
            if (area.getParentArea() != null)
                return getEffectiveBackgroundColor(area.getParentArea());
            else
                return Color.WHITE; //use white as the default root color
        }
    }
    
}
