/**
 * TimeTagger.java
 *
 * Created on 11.11.2011, 11:20:20 by burgetr
 */
package org.fit.layout.classify.taggers;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fit.layout.classify.TextTag;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

/**
 * This tagger tags the areas that contain some time expressions.
 * 
 * @author burgetr
 */
public class TimeTagger extends BaseTagger
{
    protected Pattern[] timeexpr = {Pattern.compile("[0-2]?[0-9][:\\.][0-5][0-9]([ap])?m?")};
    
    @Override
    public String getId()
    {
        return "FITLayout.Tag.Time";
    }

    @Override
    public String getName()
    {
        return "Times";
    }

    @Override
    public String getDescription()
    {
        return "Tags the areas that contain some time expressions";
    }
    
    @Override
    public TextTag getTag()
    {
        return new TextTag("time", this);
    }

    @Override
    public double getRelevance()
    {
        return 0.95;
    }
    
    @Override
    public boolean belongsTo(Area node)
    {
        if (node.isLeaf())
        {
            String text = node.getText();
            String[] words = text.split("\\s+");
            for (String s : words)
            {
                for (Pattern p : timeexpr)
                {
                    if (p.matcher(s).lookingAt()) 
                        return true;
                }
            }
        }
        return false;
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
    
    @Override
    public boolean mayCoexistWith(Tag other)
    {
        return true;
    }
    
    @Override
    public List<String> extract(String src)
    {
        Vector<String> ret = new Vector<String>();
        
        String[] words = src.toLowerCase().split("[^0-9:\\.apm]");
        for (String s : words)
        {
            for (Pattern p : timeexpr)
            {
                Matcher match = p.matcher(s);
                if (match.lookingAt())
                {
                    ret.add(match.group());
                }
            }
        }
        
        return ret;
    }
    
    //=================================================================================================
}
