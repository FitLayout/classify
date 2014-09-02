/**
 * FeatureVector.java
 *
 * Created on 6.5.2011, 14:54:37 by burgetr
 */
package org.fit.layout.classify;

/**
 * @author burgetr
 *
 */
public class FeatureVector
{
    /** Average font size */
    private double fontSize;
    /** Average font weight */
    private double weight; 
    /** Average font style */
    private double style;
    /** Replaced box? */
    private boolean replaced;
    /** Number of areas above the area */
    private int aabove; 
    /** Number of areas below the area */
    private int abelow; 
    /** Number of areas on the left of the area */
    private int aleft; 
    /** Number of areas on the right the area */
    private int aright; 
    /** Number of text lines inside */
    private int nlines;
    /** The depth of the tree rooted in this node */
    private int depth;
    /** Length of the text inside */
    private int tlength; 
    /** Number of digits inside */
    private double pdigits; 
    /** Number of lowercase letters inside */
    private double plower; 
    /** Number of uppercase letters inside */
    private double pupper; 
    /** Number of whitespace characters inside */
    private double pspaces; 
    /** Number of punctuation characters inside */
    private double ppunct; 
    /** Relative X position within the parent area */
    private double relx; 
    /** Relative Y position within the parent area */
    private double rely; 
    /** Text luminosity */
    private double tlum;
    /** Background luminosity */
    private double bglum;
    /** Color contrast */
    private double contrast;
    /** Overall markedness */
    private double markedness;
    /** Percentage of the content of the same color in the page */
    private double cperc;
    /** Percentage of the content if the same background color */
    private double bcperc;
    /** Tag level */
    private int tagLevel;
    
    /**
     * @return the fontsize
     */
    public double getFontSize()
    {
        return fontSize;
    }
    
    /**
     * @param fontsize the fontsize to set
     */
    public void setFontSize(double fontsize)
    {
        this.fontSize = fontsize;
    }
    
    /**
     * @return the weight
     */
    public double getWeight()
    {
        return weight;
    }
    
    /**
     * @param weight the weight to set
     */
    public void setWeight(double weight)
    {
        this.weight = weight;
    }
    
    /**
     * @return the style
     */
    public double getStyle()
    {
        return style;
    }
    
    /**
     * @param style the style to set
     */
    public void setStyle(double style)
    {
        this.style = style;
    }
    
    /**
     * @return the replaced
     */
    public boolean isReplaced()
    {
        return replaced;
    }
    
    /**
     * @param replaced the replaced to set
     */
    public void setReplaced(boolean replaced)
    {
        this.replaced = replaced;
    }
    
    /**
     * @return the aabove
     */
    public int getAabove()
    {
        return aabove;
    }
    
    /**
     * @param aabove the aabove to set
     */
    public void setAabove(int aabove)
    {
        this.aabove = aabove;
    }
    
    /**
     * @return the abelow
     */
    public int getAbelow()
    {
        return abelow;
    }
    
    /**
     * @param abelow the abelow to set
     */
    public void setAbelow(int abelow)
    {
        this.abelow = abelow;
    }
    
    /**
     * @return the aleft
     */
    public int getAleft()
    {
        return aleft;
    }
    
    /**
     * @param aleft the aleft to set
     */
    public void setAleft(int aleft)
    {
        this.aleft = aleft;
    }
    
    /**
     * @return the aright
     */
    public int getAright()
    {
        return aright;
    }
    
    /**
     * @param aright the aright to set
     */
    public void setAright(int aright)
    {
        this.aright = aright;
    }
    
    /**
     * @return the nlines
     */
    public int getNlines()
    {
        return nlines;
    }
    
    /**
     * @param nlines the nlines to set
     */
    public void setNlines(int nlines)
    {
        this.nlines = nlines;
    }
    
    public int getDepth()
    {
        return depth;
    }

    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    /**
     * @return the tlength
     */
    public int getTlength()
    {
        return tlength;
    }
    
    /**
     * @param tlength the tlength to set
     */
    public void setTlength(int tlength)
    {
        this.tlength = tlength;
    }
    
    /**
     * @return the pdigits
     */
    public double getPdigits()
    {
        return pdigits;
    }
    
    /**
     * @param pdigits the pdigits to set
     */
    public void setPdigits(double pdigits)
    {
        this.pdigits = pdigits;
    }
    
    /**
     * @return the plower
     */
    public double getPlower()
    {
        return plower;
    }
    
    /**
     * @param plower the plower to set
     */
    public void setPlower(double plower)
    {
        this.plower = plower;
    }
    
    /**
     * @return the pupper
     */
    public double getPupper()
    {
        return pupper;
    }
    
    /**
     * @param pupper the pupper to set
     */
    public void setPupper(double pupper)
    {
        this.pupper = pupper;
    }
    
    /**
     * @return the pspaces
     */
    public double getPspaces()
    {
        return pspaces;
    }
    
    /**
     * @param pspaces the pspaces to set
     */
    public void setPspaces(double pspaces)
    {
        this.pspaces = pspaces;
    }
    
    /**
     * @return the ppunct
     */
    public double getPpunct()
    {
        return ppunct;
    }
    
    /**
     * @param ppunct the ppunct to set
     */
    public void setPpunct(double ppunct)
    {
        this.ppunct = ppunct;
    }
    
    /**
     * @return the relx
     */
    public double getRelx()
    {
        return relx;
    }
    
    /**
     * @param relx the relx to set
     */
    public void setRelx(double relx)
    {
        this.relx = relx;
    }
    /**
     * @return the rely
     */
    public double getRely()
    {
        return rely;
    }
    /**
     * @param rely the rely to set
     */
    public void setRely(double rely)
    {
        this.rely = rely;
    }
    /**
     * @return the tlum
     */
    public double getTlum()
    {
        return tlum;
    }
    /**
     * @param tlum the tlum to set
     */
    public void setTlum(double tlum)
    {
        this.tlum = tlum;
    }
    /**
     * @return the bglum
     */
    public double getBglum()
    {
        return bglum;
    }
    /**
     * @param bglum the bglum to set
     */
    public void setBglum(double bglum)
    {
        this.bglum = bglum;
    }
    /**
     * @return the contrast
     */
    public double getContrast()
    {
        return contrast;
    }
    /**
     * @param contrast the contrast to set
     */
    public void setContrast(double contrast)
    {
        this.contrast = contrast;
    }
    /**
     * @return the markedness
     */
    public double getMarkedness()
    {
        return markedness;
    }
    /**
     * @param markedness the markedness to set
     */
    public void setMarkedness(double markedness)
    {
        this.markedness = markedness;
    }
    /**
     * @return the cperc
     */
    public double getCperc()
    {
        return cperc;
    }
    /**
     * @param cperc the cperc to set
     */
    public void setCperc(double cperc)
    {
        this.cperc = cperc;
    }

    public double getBcperc()
    {
        return bcperc;
    }

    public void setBcperc(double bcperc)
    {
        this.bcperc = bcperc;
    }

    public int getTagLevel()
    {
        return tagLevel;
    }

    public void setTagLevel(int tagLevel)
    {
        this.tagLevel = tagLevel;
    }

    
    
    
    
}
