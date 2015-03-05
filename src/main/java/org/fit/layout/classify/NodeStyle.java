/**
 * NodeStyle.java
 *
 * Created on 27.9.2012, 15:02:43 by burgetr
 */
package org.fit.layout.classify;

import java.awt.Color;
import java.util.Vector;

import org.fit.layout.model.Area;
import org.fit.layout.model.AreaTopology;
import org.fit.layout.model.Box;

/**
 * This class represents the features of the node style that are important for node purpose
 * comparison.
 * @author burgetr
 */
public class NodeStyle
{
    private float fontSize;
    private float style;
    private float weight;
    private Color color;
    private int indent;
    
    /**
     * Computes the style of an area node.
     * @param area
     */
    public NodeStyle(Area area)
    {
        Vector<Box> boxes = area.getAllBoxes();
        
        fontSize = area.getFontSize();
        style = area.getFontStyle();
        weight = area.getFontWeight();
        if (!boxes.isEmpty())
            color = boxes.firstElement().getColor();
        else
            color = Color.BLACK;
        indent = (int) Math.round(computeIndentation(area));
    }
    
    /**
     * Computes the style of a logical node.
     * @param node
     */
    public NodeStyle(NodeStyle src)
    {
        this.fontSize = src.fontSize;
        this.style = src.style;
        this.weight = src.weight;
        this.color = new Color(src.color.getRed(), src.color.getGreen(), src.color.getGreen(), src.color.getAlpha());
        this.indent = src.indent;
    }
    
    public float getFontSize()
    {
        return fontSize;
    }

    public float getStyle()
    {
        return style;
    }

    public float getWeight()
    {
        return weight;
    }

    public Color getColor()
    {
        return color;
    }

    public int getIndent()
    {
        return indent;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((color == null) ? 0 : color.hashCode());
        result = prime * result + Float.floatToIntBits(fontSize);
        result = prime * result + indent;
        result = prime * result + Float.floatToIntBits(style);
        result = prime * result + Float.floatToIntBits(weight);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NodeStyle other = (NodeStyle) obj;
        if (color == null)
        {
            if (other.color != null) return false;
        }
        else if (!color.equals(other.color)) return false;
        if (Float.floatToIntBits(fontSize) != Float
                .floatToIntBits(other.fontSize)) return false;
        if (indent != other.indent) return false;
        if (Float.floatToIntBits(style) != Float.floatToIntBits(other.style))
            return false;
        if (Float.floatToIntBits(weight) != Float.floatToIntBits(other.weight))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        String ret = "[fs:" + fontSize + " w:" + weight + " s:" + style + " c:";
        ret += String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        ret += " i:" + indent;
        ret += "]";
        return ret;
    }
    
    public String toARFFString()
    {
        return fontSize + "," + weight + "," + style + "," 
                + (color.getRed() / 255.0) + "," + (color.getGreen() / 255.0) + "," + (color.getBlue() / 255.0)
                + "," + indent; 
    }
    
    private double computeIndentation(Area area)
    {
        final double max_levels = 3;
        final AreaTopology topo = area.getTopology();
        
        if (topo.getPreviousOnLine() != null)
            return computeIndentation(topo.getPreviousOnLine()); //use the indentation of the first one on the line
        else
        {
            double ind = max_levels;
            if (/*!node.isCentered() &&*/ area.getParentArea() != null)
                ind = ind - (topo.getPosition().getX1() - area.getParentArea().getTopology().getMinIndent());
            if (ind < 0) ind = 0;
            return ind / max_levels;
        }
    }

}
