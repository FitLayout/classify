/**
 * FeatureExtractor.java
 *
 * Created on 25. 2. 2015, 14:48:42 by burgetr
 */
package org.fit.layout.classify;

import org.fit.layout.model.Area;

import weka.core.Instance;
import weka.core.Instances;

/**
 * A generic feature extractor. It is able to extract the training data values from an area. 
 * 
 * @author burgetr
 */
public interface FeatureExtractor
{

    /**
     * Initializes the extractor to use the tree with the given root node.
     * @param rootNode the new area tree root node
     */
    public void setTree(Area rootNode);
    
    /**
     * Creates a classification data instance from the given area.
     * @param area the area whose features should be computed
     * @param dataset the data set the created instance should belong to
     * @return the classification data instance
     */
    public Instance getAreaFeatures(Area area, Instances dataset);
    
}
