/**
 * PersonsTagger.java
 *
 * Created on 11.11.2011, 14:20:49 by burgetr
 */
package org.fit.layout.classify.taggers;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.fit.layout.classify.TextTag;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

import edu.stanford.nlp.util.Triple;

/**
 * NER-based location name area tagger. It tags the areas that contain at least the specified number of location names. 
 * @author burgetr
 */
public class LocationsTagger extends NERTagger
{
    private int mincnt;
    
    public LocationsTagger()
    {
        mincnt = 1;
    }
    
    /**
     * Construct a new tagger.
     * @param mincnt the minimal count of the location names detected in the area necessary for tagging this area.
     */
    public LocationsTagger(int mincnt)
    {
        this.mincnt = mincnt;
    }

    @Override
    public String getId()
    {
        return "FITLayout.Tag.Location";
    }

    @Override
    public String getName()
    {
        return "Locations";
    }

    @Override
    public String getDescription()
    {
        return "NER-based location name area tagger. It tags the areas that contain at least the specified number of location names";
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

    @Override
    public TextTag getTag()
    {
        return new TextTag("locations", this);
    }

    @Override
    public double getRelevance()
    {
        return 0.8;
    }
    
    @Override
    public boolean belongsTo(Area node)
    {
        if (node.isLeaf())
        {
            String text = node.getText();
            List<Triple<String,Integer,Integer>> list = getClassifier().classifyToCharacterOffsets(text);
            int cnt = 0;
            for (Triple<String,Integer,Integer> t : list)
            {
                if (t.first().equals("LOCATION"))
                    cnt++;
                if (cnt >= mincnt)
                    return true;
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
        return true;
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
        List<Triple<String,Integer,Integer>> list = getClassifier().classifyToCharacterOffsets(src);
        for (Triple<String,Integer,Integer> t : list)
        {
            if (t.first().equals("LOCATION"))
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
