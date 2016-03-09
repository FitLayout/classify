/**
 * PersonsTagger.java
 *
 * Created on 11.11.2011, 14:20:49 by burgetr
 */
package org.fit.layout.classify.taggers;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.fit.layout.classify.TextTag;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

import edu.stanford.nlp.util.Triple;

/**
 * NER-based personal name area tagger. It tags the areas that contain at least the specified number of personal names. 
 * @author burgetr
 */
public class PersonsTagger extends NERTagger
{
    /** The expression describing the allowed format of the title continuation */
    protected Pattern contexpr = Pattern.compile("[A-Z][A-Za-z]"); 

    private int mincnt;
    
    public PersonsTagger()
    {
        mincnt = 1;
    }
    
    /**
     * Construct a new tagger.
     * @param mincnt the minimal count of the personal names detected in the area necessary for tagging this area.
     */
    public PersonsTagger(int mincnt)
    {
        this.mincnt = mincnt;
    }

    @Override
    public String getId()
    {
        return "FITLayout.Tag.Person";
    }

    @Override
    public String getName()
    {
        return "Persons";
    }

    @Override
    public String getDescription()
    {
        return "NER-based personal name area tagger. It tags the areas that contain at least the specified number of personal names.";
    }
    
    @Override
    public String[] getParamNames()
    {
        return new String[]{"mincnt"};
    }

    @Override
    public ValueType[] getParamTypes()
    {
        return new ValueType[]{ValueType.INTEGER};
    }
    
    public int getMincnt()
    {
        return mincnt;
    }

    public void setMincnt(int mincnt)
    {
        this.mincnt = mincnt;
    }

    public TextTag getTag()
    {
        return new TextTag("persons", this);
    }

    public double getRelevance()
    {
        return 0.8;
    }
    
    public boolean belongsTo(Area node)
    {
        if (node.isLeaf())
        {
            String text = node.getText();
            List<Triple<String,Integer,Integer>> list = getClassifier().classifyToCharacterOffsets(text);
            int cnt = 0;
            for (Triple<String,Integer,Integer> t : list)
            {
                if (t.first().equals("PERSON"))
                    cnt++;
                if (cnt >= mincnt)
                    return true;
            }
        }
        return false;
    }

    public boolean allowsContinuation(Area node)
    {
        if (node.isLeaf())
        {
            String text = node.getText().trim();
            if (contexpr.matcher(text).lookingAt()) //must start with something that looks as a name
                return true;
        }
        return false;
    }

    public boolean mayCoexistWith(Tag other)
    {
        return true;
    }
    
    public boolean allowsJoining()
    {
        return true;
    }
    
    public List<String> extract(String src)
    {
        Vector<String> ret = new Vector<String>();
        List<Triple<String,Integer,Integer>> list = getClassifier().classifyToCharacterOffsets(src);
        for (Triple<String,Integer,Integer> t : list)
        {
            if (t.first().equals("PERSON"))
                ret.add(src.substring(t.second(), t.third()));
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
