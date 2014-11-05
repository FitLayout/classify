/**
 * FeatureAnalyzer.java
 *
 * Created on 6.5.2011, 14:48:51 by burgetr
 */
package org.fit.layout.classify;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.fit.layout.model.Area;
import org.fit.layout.model.Box;
import org.fit.layout.model.Rectangular;


/**
 * This class provides the methods for obtaining the values of various vertical features from the area tree.
 * 
 * @author burgetr
 */
public class FeatureAnalyzer
{
    /** Minimal difference in the markedness that should be interpreted as a difference between the meaning of the areas. */
    public static final double MIN_MARKEDNESS_DIFFERENCE = 0.5; //0.5 is the difference between the whole area in italics and not in italics
    
    public static final double[] DEFAULT_WEIGHTS = {1000.0, 2.0, 0.5, 5.0, 0.0, 1.0, 0.5, 100.0}; 
    
    //weights
    private static final int WFSZ = 0; 
    private static final int WFWT = 1;
    private static final int WFST = 2; 
    private static final int WIND = 3;
    private static final int WCON = 4;
    private static final int WCEN = 5;
    private static final int WCP = 6;
    private static final int WBCP = 7;
    
    private double[] weights;
    
    private Area root;
    private float avgfont;
    private ColorAnalyzer ca;
    private BackgroundColorAnalyzer bca;
    
    public FeatureAnalyzer(Area root)
    {
        weights = DEFAULT_WEIGHTS;
        setTree(root);
    }
    
    public void setTree(Area rootNode)
    {
        root = rootNode;
        avgfont = root.getFontSize();
        ca = new ColorAnalyzer(root);
        bca = new BackgroundColorAnalyzer(root);
    }
    
    public void setWeights(double[] weights)
    {
        this.weights = weights;
    }
    
    public double[] getWeights()
    {
        return weights;
    }
    
    public FeatureVector getFeatureVector(Area node)
    {
        FeatureVector ret = new FeatureVector();
        String text = node.getText();
        int plen = text.length();
        if (plen == 0) plen = 1; //kvuli deleni nulou
        
        ret.setFontSize(node.getFontSize() / avgfont);
        ret.setWeight(node.getFontWeight());
        ret.setStyle(node.getFontStyle());
        ret.setReplaced(node.isReplaced());
        ret.setAabove(countAreasAbove(node));
        ret.setAbelow(countAreasBelow(node));
        ret.setAleft(countAreasLeft(node));
        ret.setAright(countAreasRight(node));
        ret.setNlines(getLineCount(node));
        ret.setDepth(node.getDepth() + 1); //+2: annotator counts the boxes and their areas as well
        ret.setTlength(text.length());
        ret.setPdigits(countChars(text, Character.DECIMAL_DIGIT_NUMBER) / (double) plen);
        ret.setPlower(countChars(text, Character.LOWERCASE_LETTER) / (double) plen);
        ret.setPupper(countChars(text, Character.UPPERCASE_LETTER) / (double) plen);
        ret.setPspaces(countChars(text, Character.SPACE_SEPARATOR) / (double) plen);
        ret.setPpunct(countCharsPunct(text) / (double) plen);
        ret.setRelx(getRelX(node));
        ret.setRely(getRelY(node));
        ret.setTlum(getAverageTextLuminosity(node));
        ret.setBglum(getBackgroundLuminosity(node));
        ret.setContrast(getContrast(node));
        ret.setCperc(ca.getColorPercentage(node));
        ret.setBcperc(bca.getColorPercentage(node));
        ret.setMarkedness(getMarkedness(node));
        ret.setTagLevel(node.getTagLevel());
        
        //TODO ostatni vlastnosti obdobne
        return ret;
    }
    
    /**
     * Computes the indentation metric.
     * @return the indentation metric (0..1) where 1 is for the non-indented areas, 0 for the most indented areas.
     */
    public double getIndentation(Area node)
    {
        final double max_levels = 3;
        
        if (node.getPreviousOnLine() != null)
        	return getIndentation(node.getPreviousOnLine()); //use the indentation of the first one on the line
        else
        {
	        double ind = max_levels;
	        if (!node.isCentered() && node.getParentArea() != null)
	            ind = ind - (node.getGridX() - node.getParentArea().getGrid().getMinIndent());
	        if (ind < 0) ind = 0;
	        return ind / max_levels;
        }
    }
    
    /**
     * Computes the markedness of the area. The markedness generally describes the visual importance of the area based on different criteria.
     * @return the computed expressiveness
     */
    public double getMarkedness(Area node)
    {
        double fsz = node.getFontSize() / avgfont; //use relative font size, 0 is the normal font
        double fwt = node.getFontWeight();
        double fst = node.getFontStyle();
        double ind = getIndentation(node);
        double cen = node.isCentered() ? 1.0 : 0.0;
        double contrast = getContrast(node);
        double cp = 1.0 - ca.getColorPercentage(node);
        double bcp = bca.getColorPercentage(node);
        bcp = (bcp < 0.0) ? 0.0 : (1.0 - bcp);
        
        //weighting
        double exp = weights[WFSZ] * fsz 
                      + weights[WFWT] * fwt 
                      + weights[WFST] * fst 
                      + weights[WIND] * ind
                      + weights[WCON] * contrast
                      + weights[WCEN] * cen
                      + weights[WCP] * cp
                      + weights[WBCP] * bcp;
        
        return exp;
    }
    
    //========================================================================================================
    
    /**
     * Counts the number of sub-areas in the specified region of the area
     * @param a the area to be examined
     * @param r the grid region of the area to be examined
     * @return the number of visual areas in the specified area of the grid
     */
    private int countAreas(Area a, Rectangular r)
    {
        int ret = 0;
        
        for (int i = 0; i < a.getChildCount(); i++)
        {
            Area n = a.getChildArea(i);
            if (n.getGridPosition().intersects(r))
                ret++;
        }
        return ret;
    }
    
    private int countAreasAbove(Area a)
    {
        Rectangular gp = a.getGridPosition();
        Area parent = a.getParentArea();
        if (parent != null)
        {
            Rectangular r = new Rectangular(gp.getX1(), 0, gp.getX2(), gp.getY1() - 1);
            return countAreas(parent, r);
        }
        else
            return 0;
    }

    private int countAreasBelow(Area a)
    {
        Rectangular gp = a.getGridPosition();
        Area parent = a.getParentArea();
        if (parent != null)
        {
            Rectangular r = new Rectangular(gp.getX1(), gp.getY2()+1, gp.getX2(), Integer.MAX_VALUE);
            return countAreas(parent, r);
        }
        else
            return 0;
    }

    private int countAreasLeft(Area a)
    {
        Rectangular gp = a.getGridPosition();
        Area parent = a.getParentArea();
        if (parent != null)
        {
            Rectangular r = new Rectangular(0, gp.getY1(), gp.getX1() - 1, gp.getY2());
            return countAreas(parent, r);
        }
        else
            return 0;
    }

    private int countAreasRight(Area a)
    {
        Rectangular gp = a.getGridPosition();
        Area parent = a.getParentArea();
        if (parent != null)
        {
            Rectangular r = new Rectangular(gp.getX2()+1, gp.getY1(), Integer.MAX_VALUE, gp.getY2());
            return countAreas(parent, r);
        }
        else
            return 0;
    }

    private int countChars(String s, int type)
    {
        int ret = 0;
        for (int i = 0; i < s.length(); i++)
            if (Character.getType(s.charAt(i)) == type)
                    ret++;
        return ret;
    }

    private int countCharsPunct(String s)
    {
        int ret = 0;
        for (int i = 0; i < s.length(); i++)
        {
            char ch = s.charAt(i);
            if (ch == ',' || ch == '.' || ch == ';' || ch == ':')
                    ret++;
        }
        return ret;
    }
    
    private double getAverageTextLuminosity(Area a)
    {
        double sum = 0;
        int cnt = 0;
        
        if (!a.getBoxes().isEmpty()) //has some content
        {
            int l = a.getText().length();
            sum += a.getColorLuminosity() * l;
            cnt += l;
        }
        
        for (int i = 0; i < a.getChildCount(); i++)
        {
            int l = a.getChildArea(i).getText().length();
            sum += getAverageTextLuminosity(a.getChildArea(i)) * l;
            cnt += l;
        }
        
        if (cnt > 0)
            return sum / cnt;
        else
            return 0;
    }
    
    private double getBackgroundLuminosity(Area a)
    {
        Color bg = a.getEffectiveBackgroundColor();
        if (bg != null)
            return FeatureAnalyzer.colorLuminosity(bg);
        else
            return 0;
    }
    
    private double getContrast(Area a)
    {
        double bb = getBackgroundLuminosity(a);
        double tb = getAverageTextLuminosity(a);
        double lum;
        if (bb > tb)
            lum = (bb + 0.05) / (tb + 0.05);
        else
            lum = (tb + 0.05) / (bb + 0.05);
        return lum;
    }
    
    public static double colorLuminosity(Color c)
    {
        double lr, lg, lb;
        if (c == null)
        {
            lr = lg = lb = 255;
        }
        else
        {
            lr = Math.pow(c.getRed() / 255.0, 2.2);
            lg = Math.pow(c.getGreen() / 255.0, 2.2);
            lb = Math.pow(c.getBlue() / 255.0, 2.2);
        }
        return lr * 0.2126 +  lg * 0.7152 + lb * 0.0722;
    }

    private double getRelX(Area a)
    {
        int objx1 = a.getX1();
        if (objx1 < 0) objx1 = 0;
        int objx2 = a.getX2();
        if (objx2 < 0) objx2 = 0;
        
        int topx1 = root.getX1();
        if (topx1 < 0) topx1 = 0;
        int topx2 = root.getX2();
        if (topx2 < 0) topx2 = 0;
        
        double midw = (objx2 - objx1) / 2.0;
        double topx = topx1 + midw; //zacatek oblasti, kde lze objektem posunovat
        double midx = (objx1 + objx2) / 2.0 - topx; //stred objektu v ramci teto oblasti
        double topw = (topx2 - topx1) - (objx2 - objx1); //sirka, kam lze stredem posunovat
        return midx / topw;
    }

    public double getRelY(Area a)
    {
        int objy1 = a.getY1();
        if (objy1 < 0) objy1 = 0;
        int objy2 = a.getY2();
        if (objy2 < 0) objy2 = 0;
        
        int topy1 = root.getY1();
        if (topy1 < 0) topy1 = 0;
        int topy2 = root.getY2();
        if (topy2 < 0) topy2 = 0;
        
        double midh = (objy2 - objy1) / 2.0;
        double topy = topy1 + midh; //zacatek oblasti, kde lze objektem posunovat
        double midy = (objy1 + objy2) / 2.0 - topy; //stred objektu v ramci teto oblasti
        double toph = (topy2 - topy1) - (objy2 - objy1); //sirka, kam lze stredem posunovat
        return midy / toph;
    }
    
    public int getLineCount(Area a)
    {
        final int LINE_THRESHOLD = 5; //minimal distance between lines in pixels
        
        List<Box> leaves = a.getAllBoxes();
        Collections.sort(leaves, new AbsoluteYPositionComparator());
        int lines = 0;
        int lastpos = -10;
        for (Box leaf : leaves)
        {
            int pos = leaf.getBounds().getY1();
            if (pos - lastpos > LINE_THRESHOLD)
            {
                lines++;
                lastpos = pos;
            }
        }
        return lines;
    }
    
    //========================================================================================================
    
    /**
     * Updates the weights according to the used style of presentation based on statistical tag analysis.
     * @param root
     */
    /*public void updateWeights(Area root, SearchTree stree)
    {
    }*/
    
    
    //============================================================================================
    
    class AbsoluteXPositionComparator implements Comparator<Box>
    {
        @Override
        public int compare(Box o1, Box o2)
        {
            return o1.getBounds().getX1() - o2.getBounds().getX1(); 
        }
    }
    
    class AbsoluteYPositionComparator implements Comparator<Box>
    {
        @Override
        public int compare(Box o1, Box o2)
        {
            return o1.getBounds().getY1() - o2.getBounds().getY1(); 
        }
    }
    
}
