/**
 * TagEntitiesOperator.java
 *
 * Created on 22. 1. 2015, 16:02:09 by burgetr
 */
package org.fit.layout.classify.op;

import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Vector;

import org.fit.layout.api.ScriptObject;
import org.fit.layout.classify.Tagger;
import org.fit.layout.classify.TreeTagger;
import org.fit.layout.classify.taggers.DateTagger;
import org.fit.layout.classify.taggers.LocationsTagger;
import org.fit.layout.classify.taggers.PersonsTagger;
import org.fit.layout.classify.taggers.TimeTagger;
import org.fit.layout.classify.taggers.TitleTagger;
import org.fit.layout.impl.BaseOperator;
import org.fit.layout.model.Area;
import org.fit.layout.model.AreaTree;

/**
 * 
 * @author burgetr
 */
public class TagEntitiesOperator extends BaseOperator implements ScriptObject
{
    protected final String[] paramNames = {};
    protected final ValueType[] paramTypes = {};
    
    private TreeTagger tagger;
    private List<Tagger> taggers;

    
    public TagEntitiesOperator()
    {
        initTaggers();
    }
    
    @Override
    public String getId()
    {
        return "FitLayout.Tag.Entities";
    }
    
    @Override
    public String getName()
    {
        return "Tag entities";
    }

    @Override
    public String getDescription()
    {
        return "Recognizes entities in area text using different taggers"
                + "and adds the corresponding tags to the areas.";
    }

    @Override
    public String[] getParamNames()
    {
        return paramNames;
    }

    @Override
    public ValueType[] getParamTypes()
    {
        return paramTypes;
    }

    /**
     * Registers a new tagger that should be used by this operator.
     * @param tagger the tagger instance to be added
     */
    public void addTagger(Tagger tagger)
    {
        taggers.add(tagger);
    }
    
    /**
     * Unregisters all taggers from the operator.
     */
    public void clearTaggers()
    {
        taggers.clear();
    }
    
    protected void initTaggers()
    {
        Tagger tTime = new TimeTagger();
        Tagger tDate = new DateTagger();
        Tagger tPersons = new PersonsTagger(1);
        Tagger tLoc = new LocationsTagger(1);
        Tagger tTitle = new TitleTagger();
        
        taggers = new Vector<Tagger>();
        taggers.add(tTime);
        taggers.add(tDate);
        taggers.add(tPersons);
        taggers.add(tLoc);
        taggers.add(tTitle);
    }
    
    //==============================================================================

    @Override
    public void apply(AreaTree atree)
    {
        apply(atree, atree.getRoot());
    }

    @Override
    public void apply(AreaTree atree, Area root)
    {
        tagger = new TreeTagger(root);
        for (Tagger t : taggers)
            tagger.addTagger(t);
        tagger.tagTree();
    }

    @Override
    public String getVarName()
    {
        return "taggers";
    }

    @Override
    public void setIO(Reader in, Writer out, Writer err)
    {
    }

}
