/**
 * ColorAnalyzer.java
 *
 * Created on 12.5.2011, 16:25:58 by burgetr
 */
package org.fit.layout.classify;

import java.awt.Color;
import java.util.HashMap;

import org.fit.layout.model.Area;
import org.fit.layout.model.AreaNode;
import org.fit.layout.model.BoxNode;

/**
 * Foreground color analyzer. It gathers the statistics about the color usage in text (non-space characters).
 * 
 * @author burgetr
 */
public class ColorAnalyzer
{
    /** Maps the color representation to the number of letters of that color in the document */
    private HashMap<Integer, Integer> colors;
    private AreaNode root;
    private int totalLength;
    
    /**
     * Constructs a color analyzer.
     * @param root
     */
    public ColorAnalyzer(AreaNode root)
    {
        colors = new HashMap<Integer, Integer>();
        this.root = root;
        computeRootStatistics(this.root);
        totalLength = letterLength(this.root.getText());
        System.err.println("We have " + colors.size() + " different colors, " + totalLength + " total length");
    }
    
    /**
     * Obtains the percentage of the text that has the given color.
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
            if (totalLength == 0)
                return 0;
            else
                return (double) num / totalLength;
        }
    }

    /**
     * Obtains the average percentage of all the text that has the given color.
     * @param color the color to be tested.
     * @return the percentage (0..1)
     */
    public double getColorPercentage(AreaNode node)
    {
        int tlen = 0;
        double sum = 0;
        
    	Area area = node.getArea();
    	for (BoxNode box : area.getBoxes())
    	{
    		int len = letterLength(box.getText());
    		if (len > 0)
    		{
    			sum += getColorPercentage(box.getBox().getVisualContext().getColor()) * len;
    			tlen += len;
    		}
    	}
        
        for (int i = 0; i < node.getChildCount(); i++)
        {
            AreaNode child = node.getChildArea(i);
            int nlen = letterLength(child.getText());
            tlen += nlen;
            sum += getColorPercentage(child) * nlen;
        }
        if (tlen == 0)
            return 0;
        else
            return sum / tlen;
    }

    //==================================================================================================
    
    /**
     * Recursively computes the statistics of the individual colors in a subtree.
     * @param root the root of the subtree
     */
    private void computeRootStatistics(AreaNode root)
    {
    	Area area = root.getArea();
    	for (BoxNode box : area.getBoxes())
    	{
    		int len = letterLength(box.getText());
    		if (len > 0)
    		{
    			int key = colorKey(box.getBox().getVisualContext().getColor());
    			Integer val = colors.get(key);
    			if (val == null) val = 0;
    			val += len;
    			colors.put(key, val);
    		}
    	}
        for (int i = 0; i < root.getChildCount(); i++)
            computeRootStatistics(root.getChildArea(i));
    }
    
    private int letterLength(String s)
    {
        int len = 0;
        for (int i = 0; i < s.length(); i++)
            if (Character.getType(s.charAt(i)) != Character.SPACE_SEPARATOR)
                len++;
        return len;
    }

    /**
     * Converts a color to a number, 4 bits per color channel so that similar colors have similar numbers.
     */
    private int colorKey(Color color)
    {
        return (color.getRed() / 16) * 256 + (color.getGreen() / 16) * 16 + (color.getBlue() / 16);
    }

}
