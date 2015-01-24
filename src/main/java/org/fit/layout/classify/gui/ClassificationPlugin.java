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

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;

/**
 * 
 * @author burgetr
 */
public class ClassificationPlugin implements BrowserPlugin
{
    private Browser browser;
    
    private JToolBar toolbar;
    private JButton tagsButton;
    private JButton classesButton;

    @Override
    public boolean init(Browser browser)
    {
        this.browser = browser;
        this.browser.addToolBar(getToolbar());
        return true;
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
        for (Tag tag : root.getTags().keySet())
        {
            if (tag.getType().equals(type))
                tags.add(tag);
        }
        //display the tags
        browser.getOutputDisplay().colorizeByTags(root, tags);
        for (Area child : root.getChildAreas())
            recursiveColorizeTags(child, type);
    }
    
}
