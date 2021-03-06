package org.fit.layout.classify.taggers;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fit.layout.classify.TagOccurrence;
import org.fit.layout.classify.TextTag;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

public class TitleTagger extends BaseTagger
{
    private static final float YES = 0.6f;
    private static final float COULDBE = 0.1f;
    private static final float NO = 0.0f;
    
    /** Minimal number of words required in the title */
    private int minWords = 3;
    /** Minimal length of a word */
    private int minWordLength = 3;
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

    public int getMinWords()
    {
        return minWords;
    }

    public void setMinWords(int minWords)
    {
        this.minWords = minWords;
    }

    @Override
    public float belongsTo(Area node)
    {
        if (node.isLeaf())
        {
            String text = getText(node);
            if (areaexpr.matcher(text).lookingAt()) //check the allowed text start
            {
                //check if there is a substring with the allowed format
                Matcher match = titleexpr.matcher(text);
                float ret = NO;
                while (match.find())
                {
                    String s = match.group();
                    String[] words = s.split("\\s+");
                    if (!containsBlacklistedWord(words))
                    {
                        if (wordCount(words) >= minWords) 
                            ret = YES;
                        else
                            ret = Math.max(ret, COULDBE);
                    }
                }
                return ret;
            }
        }
        return NO;
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
    public List<TagOccurrence> extract(String src)
    {
        List<TagOccurrence> ret = new ArrayList<>();
        
        Matcher match = titleexpr.matcher(src);
        while (match.find())
        {
            TagOccurrence occ = new TagOccurrence(match.group(), match.start(), COULDBE);
            String[] words = occ.getText().split("\\s+");
            if (wordCount(words) >= minWords)
                occ.setSupport(YES);
            ret.add(occ);
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
    
    protected int wordCount(String[] words)
    {
        int cnt = 0;
        for (String w : words)
        {
            if (w.length() >= minWordLength)
                cnt++;
        }
        return cnt;
    }
    
}
