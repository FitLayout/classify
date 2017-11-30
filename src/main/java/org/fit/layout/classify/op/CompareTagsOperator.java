/**
 * CompareTagsOperator.java
 *
 * Created on 29. 4. 2016, 22:04:23 by burgetr
 */
package org.fit.layout.classify.op;

import java.util.HashSet;
import java.util.Set;

import org.fit.layout.impl.BaseOperator;
import org.fit.layout.impl.DefaultTag;
import org.fit.layout.model.Area;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Tag;

/**
 * Compares the assigned tags of two different types and prits out statistics.
 *  
 * @author burgetr
 */
public class CompareTagsOperator extends BaseOperator
{
    protected final String[] paramNames = {"srcType", "destType"};
    protected final ValueType[] paramTypes = {ValueType.STRING, ValueType.STRING};
    
    private String srcType;
    private String destType;
    
    //statistics
    private int tp, fp, tn, fn;
    
    public CompareTagsOperator()
    {
        srcType = "FitLayout.Annotate";
        destType = "FitLayout.VisualTag";
    }
    
    public CompareTagsOperator(String srcType, String destType)
    {
        this.srcType = srcType;
        this.destType = destType;
    }

    @Override
    public String getId()
    {
        return "FitLayout.Tag.Compare";
    }
    
    @Override
    public String getName()
    {
        return "Compare tags";
    }

    @Override
    public String getDescription()
    {
        return "Compares the assigned tags of two different types and prits out statistics";
    }

    @Override
    public String getCategory()
    {
        return "output";
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

    public String getSrcType()
    {
        return srcType;
    }

    public void setSrcType(String srcType)
    {
        this.srcType = srcType;
    }

    public String getDestType()
    {
        return destType;
    }

    public void setDestType(String destType)
    {
        this.destType = destType;
    }
    
    public void printStatistics()
    {
        double p = 0;
        double r = 0;
        double f = 0;
        if (tp + fp > 0) p = (double) tp / (tp + fp);
        if (tp + fn > 0) r = (double) tp / (tp + fn);
        if (p + r > 0) f = 2 * ((p * r) / (p + r));
        
        System.out.println("TP=" + tp + " FP=" + fp + " TN=" + tn + " FN=" + fn);
        System.out.println("P=" + p + " R=" + r + " F=" + f);
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
        //reset counters
        tp = fp = tn = fn = 0;
        //traverse the tree
        recursiveCheckAreas(root);
        printStatistics();
    }
    
    private void recursiveCheckAreas(Area root)
    {
        Set<String> names = new HashSet<String>();
        for (Tag tag : root.getTags().keySet())
        {
            if (srcType.equals(tag.getType()) || destType.equals(tag.getType()))
                names.add(tag.getValue());
        }
        for (String name : names)
        {
            checkTag(root, name);
        }
        
        for (int i = 0; i < root.getChildCount(); i++)
            recursiveCheckAreas(root.getChildArea(i));
    }
    
    private void checkTag(Area a, String name)
    {
        Tag tsrc = new DefaultTag(srcType, name);
        Tag tdest = new DefaultTag(destType, name);
        boolean hasSrc = a.hasTag(tsrc);
        boolean hasDest = a.hasTag(tdest);
        if (hasSrc || hasDest)
            System.out.println("TAG;;" + a.getText() + ";;" + name );
        if (hasDest)
        {
            if (hasSrc) tp++; //true positive
            else fp++; //false positive
        }
        else
        {
            if (hasSrc) fn++; //false negative
            else tn++; //true negative
        }
    }
    
}
