/**
 * NumberTagger.java
 *
 * Created on 9. 2. 2016, 9:38:26 by burgetr
 */
package org.fit.layout.classify.taggers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fit.layout.classify.TagOccurrence;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

/**
 * 
 * @author burgetr
 */
public abstract class IntegerTagger extends BaseTagger
{
    private static final float YES = 0.9f;
    private static final float NO = 0.0f;
    
    private int min;
    private int max;
    private boolean allowsLeadingZero = false;

    private Pattern numexpr;
    
    
    public IntegerTagger(int min, int max)
    {
        super();
        this.min = min;
        this.max = max;
    }
    
    public int getMin()
    {
        return min;
    }

    public int getMax()
    {
        return max;
    }

    public boolean isAllowsLeadingZero()
    {
        return allowsLeadingZero;
    }

    public void setAllowsLeadingZero(boolean allowsLeadingZero)
    {
        this.allowsLeadingZero = allowsLeadingZero;
    }

    @Override
    public String getDescription()
    {
        return "Numbers from " + min + " to " + max;
    }
    
    @Override
    public float belongsTo(Area node)
    {
        if (node.isLeaf())
        {
            String text = node.getText();
            Matcher match = getNumExpr().matcher(text);
            while (match.find())
            {
                final int ms = match.start();
                final int me = match.end();
                if ((ms == 0 || !Character.isAlphabetic(text.codePointAt(ms))) && //require something non-alphabetic chars around
                    (me == text.length() || !Character.isAlphabetic(text.codePointAt(me))))
                {
                    int num = Integer.parseInt(match.group());
                    if (num >= getMin() && num <= getMax())
                        return YES;
                }
            }
        }
        return NO;
    }
    
    @Override
    public boolean allowsContinuation(Area node)
    {
        return false;
    }

    @Override
    public boolean allowsJoining()
    {
        return false;
    }
    
    public boolean mayCoexistWith(Tag other)
    {
        return true;
    }
    
    @Override
    public List<TagOccurrence> extract(String src)
    {
        List<TagOccurrence> ret = new ArrayList<>();
        
        Matcher match = getNumExpr().matcher(src);
        while (match.find())
        {
            ret.add(new TagOccurrence(match.group(), match.start(), YES));
        }
        
        return ret;
    }

    @Override
    public List<String> split(String src)
    {
        List<String> ret = new ArrayList<String>(2);
        int start = 0;
        while (start < src.length() && Character.isSpaceChar(src.charAt(start)))
            start++;
        int end = start;
        while (end < src.length() && Character.isDigit(src.charAt(end)))
            end++;
        if (end > start) //some number found
        {
            if (start > 0)
                ret.add(src.substring(0, start));
            ret.add(src.substring(start, end));
            if (end < src.length())
                ret.add(src.substring(end));
        }
        else //no number found
            ret.add(src); //return the whole string
        return ret;
    }

    protected Pattern getNumExpr()
    {
        if (numexpr == null)
        {
            final String lead = isAllowsLeadingZero() ? "[0-9]" : "[1-9]";
            final String re = lead + "[0-9]*";
            numexpr = Pattern.compile(re);
        }
        return numexpr;
    }
    
}
