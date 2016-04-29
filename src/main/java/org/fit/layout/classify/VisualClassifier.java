/**
 * VisualClassifier.java
 *
 * Created on 8.12.2009, 22:45:48 by radek
 */
package org.fit.layout.classify;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import org.fit.layout.model.Area;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;

/**
 * @author radek
 *
 */
public class VisualClassifier
{
    private static Logger log = LoggerFactory.getLogger(VisualClassifier.class);
    
	private Filter filter;
	private AbstractClassifier classifier;
	private Instances trainset;
	private Instances testset;
	private HashMap<Area, Instance> mapping; //mapping testing instances to area tree nodes
	private Area testRoot;
	private FeatureExtractor features;
	
	/**
	 * Creates the classifier and trains it with the given training ARFF file.
	 *  
	 * @param trainfile path to the training ARFF file. Use the {@code res:} prefix for denoting
	 * classpath resources (e.g. {@code res:train.arff}).
	 * @param classindex index of the class attribute in the ARFF file
	 */
	public VisualClassifier(String trainfile, int classindex)
	{
		train(trainfile, classindex);
	}
	
	/**
	 * Classifies the areas in an area tree.
	 * 
	 * @param root the root node of the area tree
	 */
	public void classifyTree(Area root, FeatureExtractor features)
	{
	    if (classifier != null)
	    {
	        System.out.print("tree visual classification...");
	        testRoot = root;
	        this.features = features;
    	    //create a new empty set with the same header as the training set
    	    testset = new Instances(trainset, 0);
    	    //create an empty mapping
    	    mapping = new HashMap<Area, Instance>();
    	    //fill the set with the data
    	    recursivelyExtractAreaData(testRoot);
    	    System.out.println("done");
	    }
	}
	
    public String classifyArea(Area area)
    {
        if (mapping != null)
        {
            Instance data = mapping.get(area);
            if (data != null)
            {
                try {
                    double n = classifier.classifyInstance(data);
                    return getClassName((int) n);
                } catch (Exception e) {
                    System.out.println("classifyArea: error: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
            else
                return null;
        }
        else
            return null;
    }
    
    public double[] distributionForArea(Area area)
    {
        if (mapping != null)
        {
            Instance data = mapping.get(area);
            if (data != null)
            {
                try {
                    return classifier.distributionForInstance(data);
                } catch (Exception e) {
                    System.out.println("classifyArea: error: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
            else
                return null;
        }
        else
            return null;
    }
    
    public String getClassName(int index)
    {
        return trainset.classAttribute().value(index);
    }
	
	
	//==================================================================
	
	private void train(String resource, int classindex)
	{
        try
        {
            //analyze the path
            InputStream is;
            if (resource.startsWith("res:"))
                is = getClass().getResourceAsStream("/" + resource.substring(4));
            else
                is = new FileInputStream(resource);

            if (is == null)
            {
                log.error("Couldn't open training file {}", resource);
                return;
            }
            
            //open the data file
            DataSource source = new DataSource(is);
            Instances tdata = source.getDataSet();
            tdata.setClassIndex(classindex);
            
            //initialize the filter
            System.err.print("filter...");
            filter = new weka.filters.unsupervised.attribute.Standardize();
            //filter = new weka.filters.unsupervised.attribute.Normalize();
            //filter.setInputFormat(tdata);
            
            //filter = new weka.filters.unsupervised.attribute.Remove();
            //((weka.filters.unsupervised.attribute.Remove) filter).setAttributeIndices("1,25"); //do not include ID and MARKEDNESS
            //filter.setInputFormat(tdata);
            //trainset = Filter.useFilter(tdata, remove);
            trainset = tdata;
            
            //build the classifier
            System.err.print("build...");
            //AbstractClassifier cls = new weka.classifiers.bayes.NaiveBayes();
            //AbstractClassifier cls = new weka.classifiers.functions.MultilayerPerceptron();
            //cls.setOptions(weka.core.Utils.splitOptions("-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a"));
            AbstractClassifier cls = new weka.classifiers.trees.J48();
            cls.setOptions(weka.core.Utils.splitOptions("-C 0.25 -M 2"));
            //AbstractClassifier cls = new weka.classifiers.functions.LibSVM();
            //cls.setOptions(weka.core.Utils.splitOptions("-S 0 -K 2 -D 3 -G 0.5 -R 0.0 -N 0.5 -M 40.0 -C 128.0 -E 0.0010 -P 0.1"));
            
            FilteredClassifier fc = new FilteredClassifier();
            fc.setFilter(filter);
            fc.setClassifier(cls);
            
            classifier = fc;
            classifier.buildClassifier(trainset);
            
            if (cls instanceof weka.classifiers.trees.J48)
                System.out.println(((weka.classifiers.trees.J48) cls).toString());
            
            is.close();
            
        } catch (Exception e) {
            classifier = null;
            log.error("Classifier training failed: " + e.getMessage());
            e.printStackTrace();
        }
	}
	
	private void recursivelyExtractAreaData(Area root)
	{
	    //describe the area and add to the testing set
	    Instance data = features.getAreaFeatures(root, testset);
	    testset.add(data);
	    //store the mapping
	    mapping.put(root, data);
	    //repeat recursively for subareas
	    for (int i = 0; i < root.getChildCount(); i++)
	        recursivelyExtractAreaData(root.getChildArea(i));
	}
	
}
