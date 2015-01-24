/**
 * ClassificationPlugin.java
 *
 * Created on 23. 1. 2015, 21:44:40 by burgetr
 */
package org.fit.layout.classify.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;

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
                    /*Area node = (Area) areaTree.getLastSelectedPathComponent();
                    if (node != null)
                    {
                        colorizeTags(node);
                    }*/
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
                    /*Area node = (Area) areaTree.getLastSelectedPathComponent();
                    if (node != null)
                    {
                        colorizeClasses(node);
                    }*/
                }
            });
        }
        return classesButton;
    }
}
