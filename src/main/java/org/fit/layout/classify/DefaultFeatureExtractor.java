/**
 * DefaultFeatureExtractor.java
 *
 * Created on 26. 2. 2015, 15:48:54 by burgetr
 */
package org.fit.layout.classify;

import java.io.InputStream;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Implements common routines usable in feature extractors. 
 * 
 * @author burgetr
 */
public abstract class DefaultFeatureExtractor implements FeatureExtractor
{

    protected Instances loadArffDatasetResource(String filename) throws Exception
    {
        if (!filename.startsWith("/"))
            filename = "/" + filename;
        InputStream is = DefaultFeatureExtractor.class.getResourceAsStream(filename);
        DataSource source = new DataSource(is);
        Instances tdata = source.getDataSet();
        tdata.setClassIndex(0);
        return tdata;
    }
    
}
