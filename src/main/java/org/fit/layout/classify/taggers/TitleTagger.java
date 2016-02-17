package org.fit.layout.classify.taggers;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fit.layout.classify.TextTag;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

public class TitleTagger extends BaseTagger
{
    protected final int MIN_WORDS = 3;
    /** The expression the whole area must start with */
    protected Pattern areaexpr = Pattern.compile("[A-Z0-9]"); //uppercase or number
    /** The expression describing the allowed title format */
    protected Pattern titleexpr = Pattern.compile("[A-Z][A-Za-z\\s\\.\\:\\-\\p{Pd}]*");  //p{Pd} ~ Unicode Punctuation-dashes category
    /** The expression describing the allowed format of the title continuation */
    protected Pattern contexpr = Pattern.compile("[A-Za-z\\s\\.\\:\\-\\p{Pd}]+"); 

    /** Words that are not allowed in the presentation title */
    protected Vector<String> blacklist;
    
    public TitleTagger()
    {
        blacklist = new Vector<String>();
        blacklist.add("session");
        blacklist.add("chair");
    }
    
    @Override
    public String getId()
    {
        return "FITLayout.Tag.Title";
    }

    @Override
    public String getName()
    {
        return "Titles";
    }

    @Override
    public String getDescription()
    {
        return "General paper or news titles";
    }
    
    @Override
    public TextTag getTag()
    {
        return new TextTag("title", this);
    }

    @Override
    public double getRelevance()
    {
        return 0.6;
    }
    
    @Override
    public boolean belongsTo(Area node)
    {
        if (node.isLeaf())
        {
            String text = getText(node);
            if (areaexpr.matcher(text).lookingAt()) //check the allowed text start
            {
                //check if there is a substring with the allowed format
                Matcher match = titleexpr.matcher(text);
                while (match.find())
                {
                    String s = match.group();
                    String[] words = s.split("\\s+");
                    if (words.length >= MIN_WORDS && !containsBlacklistedWord(words))
                        return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean allowsContinuation(Area node)
    {
        if (node.isLeaf())
        {
            String text = node.getText().trim();
            if (contexpr.matcher(text).lookingAt()) //must start with the allowed format
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allowsJoining()
    {
        return true;
    }
    
    @Override
    public boolean mayCoexistWith(Tag other)
    {
        return (!other.getValue().equals("session"));
    }
    
    @Override
    public List<String> extract(String src)
    {
        Vector<String> ret = new Vector<String>();
        
        Matcher match = titleexpr.matcher(src);
        while (match.find())
        {
            String s = match.group();
            String[] words = s.split("\\s+");
            if (words.length >= MIN_WORDS)
                ret.add(s);
        }
        
        return ret;
    }
    
    //=================================================================================================
    
    protected String getText(Area node)
    {
        String s = node.getText().trim();
        //if (s.contains("\""))
        //    System.out.println("jo!");
        s = s.replaceAll("^[\\\"\\p{Pi}]+", "");
        s = s.replaceAll("[\\\"\\p{Pf}]+$", "");
        return s;
    }
    
    protected boolean containsBlacklistedWord(String[] words)
    {
        for (String w : words)
        {
            if (blacklist.contains(w.toLowerCase()))
                return true; 
        }
        return false;
    }
    
    
}
