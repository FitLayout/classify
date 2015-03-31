/**
 * DateTagger.java
 *
 * Created on 11.11.2011, 15:15:51 by burgetr
 */
package org.fit.layout.classify.taggers;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fit.layout.classify.TextTag;
import org.fit.layout.classify.Tagger;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

/**
 * @author burgetr
 *
 */
public class DateTagger implements Tagger
{
    protected Pattern[] dateexpr = {Pattern.compile("[1-2][0-9][0-9][0-9]\\-[0-9][0-9]\\-[0-9][0-9]")};
    
    private Set<String> dw;
    private int dfirst;
    private int dlast;

    public DateTagger()
    {
        dw = new HashSet<String>(24);
        dw.add("jan");
        dw.add("feb");
        dw.add("mar");
        dw.add("apr");
        dw.add("may");
        dw.add("jun");
        dw.add("jul");
        dw.add("aug");
        dw.add("sep");
        dw.add("oct");
        dw.add("nov");
        dw.add("dec");
        dw.add("january");
        dw.add("february");
        dw.add("march");
        dw.add("april");
        dw.add("june");
        dw.add("july");
        dw.add("august");
        dw.add("september");
        dw.add("october");
        dw.add("novebrer");
        dw.add("december");
        
        dw.add("januar");
        dw.add("februar");
        dw.add("märz");
        dw.add("april");
        dw.add("mai");
        dw.add("juni");
        dw.add("juli");
        dw.add("august");
        dw.add("september");
        dw.add("oktober");
        dw.add("november");
        dw.add("dezember");
        
        dw.add("janvier");
        dw.add("février");
        dw.add("mars");
        dw.add("avril");
        dw.add("mai");
        dw.add("juin");
        dw.add("juillet");
        dw.add("août");
        dw.add("septembre");
        dw.add("octobre");
        dw.add("novembre ");
        dw.add("décembre ");
        
        dw.add("gennaio");
        dw.add("febbraio");
        dw.add("marzo");
        dw.add("aprile");
        dw.add("maggio");
        dw.add("giugno");
        dw.add("luglio");
        dw.add("agosto");
        dw.add("settembre");
        dw.add("ottobre");
        dw.add("novembre");
        dw.add("dicembre");
    }
    
    public TextTag getTag()
    {
        return new TextTag("date", this);
    }

    public double getRelevance()
    {
        return 0.95;
    }
    
    public boolean belongsTo(Area node)
    {
        if (node.isLeaf())
        {
            String text = node.getText().toLowerCase();
            //try to find some standard formats
            String[] words = text.split("\\s+");
            for (String s : words)
            {
                for (Pattern p : dateexpr)
                {
                    if (p.matcher(s).lookingAt()) 
                        return true;
                }
            }
            //try to find a sequence of known words
            words = text.split("\\W+");
            return containsDate(words, 1);
        }
        return false;
    }
    
    public boolean allowsContinuation(Area node)
    {
    	return false;
    }

    public boolean allowsJoining()
    {
        return false;
    }

    public boolean mayCoexistWith(Tag other)
    {
        return true;
    }
    
    public Vector<String> extract(String src)
    {
        Vector<String> ret = new Vector<String>();
        
        //check for common formats
        String[] words = src.toLowerCase().split("[^0-9\\-]");
        for (String s : words)
        {
            for (Pattern p : dateexpr)
            {
                Matcher match = p.matcher(s);
                if (match.lookingAt())
                {
                    ret.add(match.group());
                }
            }
        }
        //try to compose the individual values
        if (ret.isEmpty())
        {
            words = src.toLowerCase().split("\\W+");
            if (findDate(words, 1))
            {
                String s = "";
                for (int i = dfirst; i <= dlast; i++)
                {
                    if (i != dfirst) s += " ";
                    s += words[i];
                }
                ret.add(s);
            }
        }
        return ret;
    }
    
    private List<Date> extractDates(String s)
    {
        Vector<Date> ret = new Vector<Date>();
        
        Vector<String> srcdates = extract(s);
        for (String sdate : srcdates)
        {
            String[] words = sdate.split("\\s+");
            //TODO extract
        }
        
        return ret;
    }
    
    //=================================================================================================
    
    /**
     * Searches for sequences like num,month or month,num or num,year in the string.
     * @param strs list of words to be examined
     * @param tolerance maximal distance of the terms
     * @return <code>true</code> if the words form a date of some kind
     */
    private boolean containsDate(String[] strs, int tolerance)
    {
        dfirst = -1;
        dlast = -1;
        int lastdw = -999;
        int lastnum = -999;
        int lastyear = -999;
        for (int i = 0; i < strs.length; i++)
        {
            if (dw.contains(strs[i]))
            {
                lastdw = i;
                if (lastdw - lastnum <= tolerance)
                    return true;
            }
            else if (isYear(strs[i]))
            {
                lastyear = i;
                if (lastyear - lastnum <= tolerance)
                    return true;
            }
            else if (isNum(strs[i]))
            {
                lastnum = i;
                if (lastnum - lastdw <= tolerance)
                    return true;
            }
        }
        return false;
    }
    
    /**
     * Searches for sequences like num,month or month,num or num,year in the string.
     * If the date is found, the dfirst and dlast properties are set to the indices of the first and last
     * index of the corresponding words. 
     * @param strs list of words to be examined
     * @param tolerance maximal distance of the terms
     * @return <code>true</code> if the words form a date of some kind
     */
    private boolean findDate(String[] strs, int tolerance)
    {
        dfirst = -1;
        dlast = -1;
        int curend = -1;
        int intpos = -1; //interesting position found (not a simple number)
        for (int i = 0; i < strs.length; i++)
        {
            if (dw.contains(strs[i]) || isYear(strs[i]))
                intpos = i;
            if (intpos == i || isNum(strs[i]))
            {
                if (isYear(strs[i]))
                    intpos = i;
                if (curend == -1)
                    curend = i;
                else
                {
                    if (i - curend <= tolerance) //extending the group
                        curend = i;
                    else //cannot extend the group
                    {
                        if (dlast - dfirst >= 1 && intpos >= dfirst && intpos <= dlast) //suitable group found
                            return true;
                        else //no suitable group, try the next one
                        {
                            curend = i;
                            dfirst = i;
                            dlast = i;
                        }
                                
                    }
                }
                if (dfirst == -1) dfirst = curend;
                dlast = curend;
            }
            
        }
        
        return (dlast - dfirst >= 1);
    }
    
    private boolean isNum(String s)
    {
        try
        {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    private boolean isYear(String s)
    {
        try
        {
            int n = Integer.parseInt(s);
            return n > 1900 && n < 2100;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
