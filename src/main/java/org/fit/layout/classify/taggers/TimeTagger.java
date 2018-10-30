/**
 * TimeTagger.java
 *
 * Created on 11.11.2011, 11:20:20 by burgetr
 */
package org.fit.layout.classify.taggers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fit.layout.classify.TagOccurrence;
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
    private static final float YES = 0.95f;
    private static final float NO = 0.0f;
    
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
    public float belongsTo(Area node)
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
    
    @Override
    public boolean mayCoexistWith(Tag other)
    {
        return true;
    }
    
    @Override
    public List<TagOccurrence> extract(String src)
    {
        List<TagOccurrence> ret = new ArrayList<>();
        
        String[] words = src.toLowerCase().split("[^0-9:\\.apm]");
        int lastIndex = 0;
        for (String s : words)
        {
            for (Pattern p : timeexpr)
            {
                Matcher match = p.matcher(s);
                if (match.lookingAt())
                {
                    String text = match.group();
                    int pos = src.indexOf(text, lastIndex);
                    ret.add(new TagOccurrence(text, pos, YES));
                    lastIndex = pos + 1;
                }
            }
        }
        
        return ret;
    }
    
    @Override
    public List<String> split(String src)
    {
        // TODO splitting is not implemented for this tagger; the whole string is returned
        List<String> ret = new ArrayList<String>(1);
        ret.add(src);
        return ret;
    }

    //=================================================================================================
}
