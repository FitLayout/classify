/**
 * VisualClassifier.java
 *
 * Created on 8.12.2009, 22:45:48 by radek
 */
package org.fit.layout.classify;

import java.util.HashMap;

import org.fit.layout.impl.AreaNode;

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
	private HashMap<AreaNode, Instance> mapping; //mapping testing instances to area tree nodes
	private AreaNode testRoot;
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
	public void classifyTree(AreaNode root, FeatureAnalyzer features)
	{
	    if (classifier != null)
	    {
	        System.out.print("tree visual classification...");
	        testRoot = root;
	        this.features = features;
    	    //create a new empty set with the same header as the training set
    	    testset = new Instances(trainset, 0);
    	    //create an empty mapping
    	    mapping = new HashMap<AreaNode, Instance>();
    	    //fill the set with the data
    	    recursivelyExtractAreaData(testRoot);
    	    System.out.println("done");
	    }
	}
	
    public String classifyArea(AreaNode area)
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
	
	private void train(String trainfile, int classindex)
	{
        try
        {
            //open the data file
            DataSource source = new DataSource(trainfile);
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
	
	private void recursivelyExtractAreaData(AreaNode root)
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

	private Instance computeAreaFeatures(AreaNode node, Instances dataset)
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
        inst.setValue(i++, node.getAllTags().contains(tDate.getTag())?"true":"false");
        inst.setValue(i++, node.getAllTags().contains(tTime.getTag())?"true":"false");
        inst.setValue(i++, node.getAllTags().contains(tPersons.getTag())?"true":"false");
        inst.setValue(i++, node.getAllTags().contains(tTitle.getTag())?"true":"false");
	    
	    return inst;
	}
	
}
