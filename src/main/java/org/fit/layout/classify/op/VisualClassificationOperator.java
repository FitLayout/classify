/**
 * VisualClassificationOperator.java
 *
 * Created on 22. 1. 2015, 23:05:31 by burgetr
 */
package org.fit.layout.classify.op;

import org.fit.layout.classify.FeatureAnalyzer;
import org.fit.layout.classify.VisualClassifier;
import org.fit.layout.classify.VisualTag;
import org.fit.layout.impl.BaseOperator;
import org.fit.layout.model.Area;
import org.fit.layout.model.AreaTree;

/**
 * An operator that assigns the tags to the tree nodes based on the
 * visual classification.
 * 
 * @author burgetr
 */
public class VisualClassificationOperator extends BaseOperator
{
    private final String[] paramNames = {"trainFile", "classIndex"};
    private final ValueType[] paramTypes = {ValueType.STRING, ValueType.INTEGER};
    
    private String trainFile;
    private int classIndex;
    
    private FeatureAnalyzer features;
    private VisualClassifier vcls;
    

    public VisualClassificationOperator()
    {
    }
    
    public VisualClassificationOperator(String trainFile, int classIndex)
    {
        this.trainFile = trainFile;
        this.classIndex = classIndex;
    }

    @Override
    public String getId()
    {
        return "FitLayout.Tag.Visual";
    }
    
    @Override
    public String getName()
    {
        return "Tag visual classes";
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

    public String getTrainFile()
    {
        return trainFile;
    }

    public void setTrainFile(String trainFile)
    {
        this.trainFile = trainFile;
    }

    public int getClassIndex()
    {
        return classIndex;
    }

    public void setClassIndex(int classIndex)
    {
        this.classIndex = classIndex;
    }

    public FeatureAnalyzer getFeatures()
    {
        return features;
    }

    public VisualClassifier getVisualClassifier()
    {
        return vcls;
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
        //visual features
        features = new FeatureAnalyzer(root);
        //create and train classifier
        vcls = new VisualClassifier(trainFile, classIndex);
        vcls.classifyTree(root, features);
        //add tags based on the classification
        recursivelyAddTags(root);
    }
    
    private void recursivelyAddTags(Area root)
    {
        String cls = vcls.classifyArea(root);
        if (cls != null && !cls.isEmpty() && !cls.equals("none"))
        {
            VisualTag tag = new VisualTag(cls);
            root.addTag(tag, 0.9f); //TODO obtain relevance form classifier?
        }
        for (int i = 0; i < root.getChildCount(); i++)
            recursivelyAddTags(root.getChildArea(i));
    }
}
