/**
 * PagesTagger.java
 *
 * Created on 28. 2. 2015, 21:02:19 by burgetr
 */
package org.fit.layout.classify.taggers;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fit.layout.classify.Tagger;
import org.fit.layout.classify.TextTag;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

/**
 * Very simple page numbers tagger. Recognizes the numeric ranges (e.g. 12-24)
 * @author burgetr
 */
public class PagesTagger implements Tagger
{
    protected Pattern pgexpr = Pattern.compile("[1-9][0-9]*(\\s*\\p{Pd}\\s*[1-9][0-9]*)?");
    
    public TextTag getTag()
    {
        return new TextTag("pages", this);
    }

    public double getRelevance()
    {
        return 0.95;
    }
    
    public boolean belongsTo(Area node)
    {
        if (node.isLeaf())
        {
            String text = node.getText();
            Matcher match = pgexpr.matcher(text);
            while (match.find())
            {
                final int ms = match.start();
                final int me = match.end();
                if ((ms == 0 || text.charAt(ms) == ' ') &&
                    (me == text.length() || text.charAt(ms) == ' '))
                {
                    return true;
                }
            }
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
        
        Matcher match = pgexpr.matcher(src);
        while (match.find())
        {
            String s = match.group();
            ret.add(s);
        }
        
        return ret;
    }
}
