/**
 * VisualClassifier.java
 *
 * Created on 8.12.2009, 22:45:48 by radek
 */
package org.fit.layout.classify;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.fit.layout.classify.taggers.DateTagger;
import org.fit.layout.classify.taggers.PersonsTagger;
import org.fit.layout.classify.taggers.TimeTagger;
import org.fit.layout.classify.taggers.TitleTagger;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.DenseInstance;
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
	private Filter filter;
	private AbstractClassifier classifier;
	private Instances trainset;
	private Instances testset;
	private HashMap<Area, Instance> mapping; //mapping testing instances to area tree nodes
	private Area testRoot;
	private FeatureAnalyzer features;
	
	private Tagger tTime = new TimeTagger();
	private Tagger tDate = new DateTagger();
	private Tagger tPersons = new PersonsTagger(1);
	private Tagger tTitle = new TitleTagger();

	/**
	 * Creates the classifier and trains it with the given training ARFF file.
	 *  
	 * @param trainfile path to the training ARFF file
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
	public void classifyTree(Area root, FeatureAnalyzer features)
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
                    return "???";
                }
            }
            else
                return "--ninst--";
        }
        else
            return "--nmap--";
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
            //open the data file
            InputStream is = ClassLoader.getSystemResourceAsStream(resource);
            DataSource source = new DataSource(is);
            Instances tdata = source.getDataSet();
            tdata.setClassIndex(classindex);
            
            //initialize the filter
            System.err.print("filter...");
            //filter = new weka.filters.unsupervised.attribute.Standardize();
            //filter = new weka.filters.unsupervised.attribute.Normalize();
            //filter.setInputFormat(tdata);
            
            filter = new weka.filters.unsupervised.attribute.Remove();
            ((weka.filters.unsupervised.attribute.Remove) filter).setAttributeIndices("1,25"); //do not include ID and MARKEDNESS
            filter.setInputFormat(tdata);
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
            
        } catch (Exception e) {
            classifier = null;
            System.err.println("Classifier training failed: " + e.getMessage());
            e.printStackTrace();
        }
	}
	
	private void recursivelyExtractAreaData(Area root)
	{
	    //describe the area and add to the testing set
	    Instance data = computeAreaFeatures(root, testset);
	    testset.add(data);
	    //store the mapping
	    mapping.put(root, data);
	    //repeat recursively for subareas
	    for (int i = 0; i < root.getChildCount(); i++)
	        recursivelyExtractAreaData(root.getChildArea(i));
	}

    /**
     * Obtains all the tags assigned to this area and its child areas (not all descendant areas).
     * @return a set of tags
     */
    protected Set<Tag> getAllTags(Area area)
    {
        Set<Tag> ret = new HashSet<Tag>(area.getTags().keySet());
        for (int i = 0; i < area.getChildCount(); i++)
            ret.addAll(area.getChildArea(i).getTags().keySet());
        return ret;
    }
    
	
	private Instance computeAreaFeatures(Area node, Instances dataset)
	{
	    FeatureVector f = features.getFeatureVector(node);
	    
	    Instance inst = new DenseInstance(30);
        inst.setDataset(testset);
	    int i = 0;
	    inst.setValue(i++, 0.0); //id
	    inst.setValue(i++, 0.0); //class
	    inst.setValue(i++, f.getFontSize() * 100);
	    inst.setValue(i++, f.getWeight());
        inst.setValue(i++, f.getStyle());
        inst.setValue(i++, f.isReplaced()?1:0);
        inst.setValue(i++, f.getAabove());
        inst.setValue(i++, f.getAbelow());
        inst.setValue(i++, f.getAleft());
        inst.setValue(i++, f.getAright());
        inst.setValue(i++, f.getNlines());
        inst.setValue(i++, 1); //TODO count columns
        inst.setValue(i++, f.getDepth());
        inst.setValue(i++, f.getTlength());
        inst.setValue(i++, f.getPdigits());
        inst.setValue(i++, f.getPlower());
        inst.setValue(i++, f.getPupper());
        inst.setValue(i++, f.getPspaces());
        inst.setValue(i++, f.getPpunct());
        inst.setValue(i++, f.getRelx());
        inst.setValue(i++, f.getRely());
        inst.setValue(i++, f.getTlum());
        inst.setValue(i++, f.getBglum());
        inst.setValue(i++, f.getContrast());
        inst.setValue(i++, f.getMarkedness());
        inst.setValue(i++, f.getCperc());
        Set<Tag> tags = getAllTags(node);
        inst.setValue(i++, tags.contains(tDate.getTag())?"true":"false");
        inst.setValue(i++, tags.contains(tTime.getTag())?"true":"false");
        inst.setValue(i++, tags.contains(tPersons.getTag())?"true":"false");
        inst.setValue(i++, tags.contains(tTitle.getTag())?"true":"false");
	    
	    return inst;
	}
	
}
