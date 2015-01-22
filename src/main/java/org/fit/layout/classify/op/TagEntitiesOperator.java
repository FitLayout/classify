/**
 * TagEntitiesOperator.java
 *
 * Created on 22. 1. 2015, 16:02:09 by burgetr
 */
package org.fit.layout.classify.op;

import org.fit.layout.classify.Tagger;
import org.fit.layout.classify.TreeTagger;
import org.fit.layout.classify.taggers.DateTagger;
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
public class TagEntitiesOperator extends BaseOperator
{
    protected final String[] paramNames = {};
    protected final ValueType[] paramTypes = {};
    
    protected TreeTagger tagger;
    

    public TagEntitiesOperator()
    {
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
        return "..."; //TODO
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

    //==============================================================================

    @Override
    public void apply(AreaTree atree)
    {
        apply(atree, atree.getRoot());
    }

    @Override
    public void apply(AreaTree atree, Area root)
    {
        Tagger tTime = new TimeTagger();
        Tagger tDate = new DateTagger();
        Tagger tPersons = new PersonsTagger(1);
        Tagger tTitle = new TitleTagger();
        
        tagger = new TreeTagger(root);
        tagger.addTagger(tTime);
        tagger.addTagger(tDate);
        tagger.addTagger(tPersons);
        tagger.addTagger(tTitle);
        tagger.tagTree();
    }

}
