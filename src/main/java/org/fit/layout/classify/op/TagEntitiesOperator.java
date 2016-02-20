/**
 * TagEntitiesOperator.java
 *
 * Created on 22. 1. 2015, 16:02:09 by burgetr
 */
package org.fit.layout.classify.op;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fit.layout.api.ParametrizedOperation;
import org.fit.layout.api.ScriptObject;
import org.fit.layout.api.ServiceManager;
import org.fit.layout.classify.Tagger;
import org.fit.layout.classify.TreeTagger;
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
    private Map<String, Tagger> availableTaggers;
    private List<Tagger> usedTaggers;

    
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
        usedTaggers.add(tagger);
    }
    
    /**
     * Unregisters all taggers from the operator.
     */
    public void clearTaggers()
    {
        usedTaggers.clear();
    }
    
    public Tagger findTagger(String id, Map<String, Object> params)
    {
        ParametrizedOperation op = availableTaggers.get(id);
        if (op != null)
            ServiceManager.setServiceParams(op, params);
        return (Tagger) op;
    }
    
    protected void initTaggers()
    {
        availableTaggers = ServiceManager.loadServicesByType(Tagger.class);
        //use all available taggers by default
        usedTaggers = new ArrayList<Tagger>(availableTaggers.values());
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
        for (Tagger t : usedTaggers)
            tagger.addTagger(t);
        tagger.tagTree();
    }

    @Override
    public String getVarName()
    {
        return "entities";
    }

    @Override
    public void setIO(Reader in, Writer out, Writer err)
    {
    }

}
