/**
 * PersonsTagger.java
 *
 * Created on 11.11.2011, 14:20:49 by burgetr
 */
package org.fit.layout.classify.taggers;

import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.fit.layout.classify.TagImpl;
import org.fit.layout.classify.Tagger;
import org.fit.layout.classify.TreeTagger;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.util.Triple;

/**
 * NER-based personal name area tagger. It tags the areas that contain at least the specified number of personal names. 
 * @author burgetr
 */
public class PersonsTagger implements Tagger
{
    /** The expression describing the allowed format of the title continuation */
    protected Pattern contexpr = Pattern.compile("[A-Z][A-Za-z]"); 

    private int mincnt;
    private AbstractSequenceClassifier<?> classifier;
    
    /**
     * Construct a new tagger.
     * @param mincnt the minimal count of the personal names detected in the area necessary for tagging this area.
     */
    public PersonsTagger(int mincnt)
    {
        this.mincnt = mincnt;
        classifier = TreeTagger.sharedClassifier;
    }

    public TagImpl getTag()
    {
        return new TagImpl("persons", this);
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
            List<Triple<String,Integer,Integer>> list = classifier.classifyToCharacterOffsets(text);
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
    
    public Vector<String> extract(String src)
    {
        Vector<String> ret = new Vector<String>();
        List<Triple<String,Integer,Integer>> list = classifier.classifyToCharacterOffsets(src);
        for (Triple<String,Integer,Integer> t : list)
        {
            if (t.first().equals("PERSON"))
                ret.add(src.substring(t.second(), t.third()));
        }
        return ret;
    }
    
    //=================================================================================================
    
}
