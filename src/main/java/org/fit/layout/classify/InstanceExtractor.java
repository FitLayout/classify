/**
 * InstanceExtractor.java
 *
 * Created on 26. 2. 2015, 15:04:54 by burgetr
 */
package org.fit.layout.classify;

import java.io.File;
import java.io.IOException;

import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 * Extracts instances from a page.
 * 
 * @author burgetr
 */
public class InstanceExtractor
{
    private static Logger log = LoggerFactory.getLogger(InstanceExtractor.class);
    
    private FeatureExtractor features;
    private String tagType;
    private String defaultClass;
    private Instances data;
    
    
    public InstanceExtractor(FeatureExtractor features, String tagType)
    {
        this.features = features;
        this.tagType = tagType;
        
        data = features.createEmptyDataset();
        defaultClass = "none";
    }

    public void setDefaultClass(String defaultClass)
    {
        this.defaultClass = defaultClass;
    }

    public Instances getData()
    {
        return data;
    }

    public void clear()
    {
        data = features.createEmptyDataset();
    }
    
    public void extractInstances(Area root)
    {
        features.setTree(root);
        recursiveExtractInstances(root, data);
    }

    public void save(String filename)
    {
        try
        {
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(new File(filename));
            saver.writeBatch();
        } catch (IOException e) {
            log.error("Couldn't save to " + filename + " : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public int count()
    {
        return data.numInstances();
    }
    
    //===================================================================================
    
    protected void recursiveExtractInstances(Area root, Instances dest)
    {
        Instance newinst = features.getAreaFeatures(root, dest);
        
        String cls = defaultClass;
        for (Tag tag : root.getTags().keySet())
        {
            if (tag.getType().equals(tagType))
                cls = tag.getValue();
        }
        newinst.setClassValue(cls);
        dest.add(newinst);
        
        for (int i = 0; i < root.getChildCount(); i++)
            recursiveExtractInstances(root.getChildArea(i), dest);
    }
    
    
}
