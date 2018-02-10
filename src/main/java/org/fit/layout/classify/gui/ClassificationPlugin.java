/**
 * ClassificationPlugin.java
 *
 * Created on 23. 1. 2015, 21:44:40 by burgetr
 */
package org.fit.layout.classify.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.fit.layout.api.AreaTreeOperator;
import org.fit.layout.api.ServiceManager;
import org.fit.layout.classify.FeatureExtractor;
import org.fit.layout.classify.op.VisualClassificationOperator;
import org.fit.layout.gui.AreaSelectionListener;
import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

import weka.core.Instance;
import weka.core.Instances;

/**
 * 
 * @author burgetr
 */
public class ClassificationPlugin implements BrowserPlugin, AreaSelectionListener
{
    private Browser browser;
    private FeatureExtractor features;
    private Instances dataset;
    
    private JToolBar toolbar;
    private JButton tagsButton;
    private JButton classesButton;
    private JScrollPane featuresScroll;
    private JTable featuresTable;

    @Override
    public boolean init(Browser browser)
    {
        this.browser = browser;
        this.browser.addToolBar(getToolbar());
        this.browser.addInfoPanel(getFeaturesScroll(), 0.5);
        this.browser.addAreaSelectionListener(this);
        return true;
    }
    
    public FeatureExtractor getFeatureExtractor()
    {
        if (features == null)
        {
            AreaTreeOperator vcls = ServiceManager.findAreaTreeOperators().get("FitLayout.Tag.Visual");
            if (vcls != null && vcls instanceof VisualClassificationOperator)
            {
                features = ((VisualClassificationOperator) vcls).getFeatures();
                dataset = features.createEmptyDataset();
            }
        }
        return features;
    }
    
    //=================================================================
    
    private JToolBar getToolbar()
    {
        if (toolbar == null)
        {
            toolbar = new JToolBar("Classification");
            toolbar.add(getTagsButton());
            toolbar.add(getClassesButton());
        }
        return toolbar;
    }

    private JButton getTagsButton()
    {
        if (tagsButton == null)
        {
            tagsButton = new JButton("Tags");
            tagsButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent arg0)
                {
                    Area node = browser.getSelectedArea();
                    if (node != null)
                        colorizeTags(node, "FitLayout.TextTag");
                }
            });
        }
        return tagsButton;
    }
    
    private JButton getClassesButton()
    {
        if (classesButton == null)
        {
            classesButton = new JButton("Classes");
            classesButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    Area node = browser.getSelectedArea();
                    if (node != null)
                        colorizeTags(node, "FitLayout.VisualTag");
                }
            });
        }
        return classesButton;
    }
    
    private JScrollPane getFeaturesScroll()
    {
        if (featuresScroll == null)
        {
            featuresScroll = new JScrollPane();
            featuresScroll.setViewportView(getFeaturesTable());
        }
        return featuresScroll;
    }
    
    private JTable getFeaturesTable()
    {
        if (featuresTable == null)
        {
            featuresTable = new JTable();
        }
        return featuresTable;
    }
    
    //=================================================================
    
    private void colorizeTags(Area root, String type)
    {
        recursiveColorizeTags(root, type);
        browser.updateDisplay();
    }
    
    private void recursiveColorizeTags(Area root, String type)
    {
        //find tags of the given type
        Set<Tag> tags = new HashSet<Tag>();
        for (Tag tag : root.getSupportedTags(0.3f)) //TODO make configurable?
        {
            if (tag.getType().equals(type))
                tags.add(tag);
        }
        //display the tags
        browser.getOutputDisplay().colorizeByTags(root, tags);
        for (Area child : root.getChildren())
            recursiveColorizeTags(child, type);
    }

    //=================================================================
    
    @Override
    public void areaSelected(Area area)
    {
        if (area != null)
        {
            //ensure that some tree root is set in the feature extractor
            FeatureExtractor fe = getFeatureExtractor();
            if (fe.getTreeRoot() == null)
                fe.setTree(browser.getAreaTree().getRoot());
            //classify the instance
            Instance data = getFeatureExtractor().getAreaFeatures(area, dataset);
            //display the result
            Vector<Vector <String>> fvals = new Vector<Vector <String>>();
            for (int i = 0; i < data.numAttributes(); i++)
            {
                String name = data.attribute(i).name();
                String value;
                if (data.attribute(i).isNumeric())
                    value = String.valueOf(data.value(i));
                else
                    value = data.stringValue(i);
                fvals.add(infoTableData(name, value));
            }
            getFeaturesTable().setModel(new DefaultTableModel(fvals, infoTableData("Property", "Value")));
        }
        else //no area selected
        {
            TableModel model = getFeaturesTable().getModel();
            if (model != null && model instanceof DefaultTableModel)
                ((DefaultTableModel) model).setRowCount(0);
        }
    }
    
    private Vector<String> infoTableData(String prop, String value)
    {
        Vector<String> cols = new Vector<String>(2);
        cols.add(prop);
        cols.add(value);
        return cols;
    }

}
