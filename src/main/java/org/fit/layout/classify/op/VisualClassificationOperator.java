/**
 * VisualClassificationOperator.java
 *
 * Created on 22. 1. 2015, 23:05:31 by burgetr
 */
package org.fit.layout.classify.op;

import org.fit.layout.classify.FeatureExtractor;
import org.fit.layout.classify.VisualClassifier;
import org.fit.layout.classify.VisualTag;
import org.fit.layout.classify.articles.ArticleFeatureExtractor;
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
    private final String[] paramNames = {"trainFile", "classIndex", "classDistribution"};
    private final ValueType[] paramTypes = {ValueType.STRING, ValueType.INTEGER, ValueType.BOOLEAN};
    
    private String trainFile;
    private int classIndex;
    private boolean classDistribution;
    
    private FeatureExtractor features;
    private VisualClassifier vcls;
    

    public VisualClassificationOperator()
    {
        features = new ArticleFeatureExtractor();
        classDistribution = false;
    }
    
    public VisualClassificationOperator(String trainFile, int classIndex, boolean classDistribution)
    {
        this.trainFile = trainFile;
        this.classIndex = classIndex;
        this.classDistribution = classDistribution;
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
        return "Adds tags to the visual areas based on the result of classification by a selected pre-trained classifier.";
    }

    @Override
    public String getCategory()
    {
        return "classification";
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

    public boolean getClassDistribution()
    {
        return classDistribution;
    }

    public void setClassDistribution(boolean classDistribution)
    {
        this.classDistribution = classDistribution;
    }

    public FeatureExtractor getFeatures()
    {
        return features;
    }

    public void setFeatures(FeatureExtractor features)
    {
        this.features = features;
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
        features.setTree(root);
        System.out.println("SETTING ROOT");
        //create and train classifier
        vcls = new VisualClassifier(trainFile, classIndex);
        vcls.classifyTree(root, features);
        //add tags based on the classification
        recursivelyAddTags(root);
    }
    
    private void recursivelyAddTags(Area root)
    {
        if (!classDistribution)
        {
            String cls = vcls.classifyArea(root);
            if (cls != null && !cls.isEmpty() && !cls.equals("none"))
            {
                VisualTag tag = new VisualTag(cls);
                root.addTag(tag, 0.9f);
            }
        }
        else
        {
            double[] dist = vcls.distributionForArea(root);
            if (dist != null)
            {
                for (int i = 0; i < dist.length; i++)
                {
                    if (dist[i] >= 0.1f)
                    {
                        String cname = vcls.getClassName(i);
                        if (!cname.equals("none"))
                        {
                            VisualTag tag = new VisualTag(cname);
                            root.addTag(tag, (float) dist[i]);
                        }
                    }
                }
            }
        }
        
        for (int i = 0; i < root.getChildCount(); i++)
            recursivelyAddTags(root.getChildArea(i));
    }
}
