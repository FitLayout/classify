/**
 * ConsoleApi.java
 *
 * Created on 8. 2. 2016, 15:21:53 by burgetr
 */
package org.fit.layout.annotator;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import org.fit.layout.api.ScriptObject;
import org.fit.layout.api.ServiceManager;
import org.fit.layout.gui.BrowserPlugin;

/**
 * JavaScript console API for the Annotator plugin. It allows to modify the GUI settings
 * using JavaScript commands.
 * @author burgetr
 */
public class ConsoleApi implements ScriptObject
{
    private AnnotatorPlugin plugin;
    private PrintWriter werr;

    public ConsoleApi()
    {
        this.plugin = null;
    }
    
    @Override
    public String getName()
    {
        return "annotator";
    }

    @Override
    public void setIO(Reader in, Writer out, Writer err)
    {
        werr = new PrintWriter(err);
    }
    
    private AnnotatorPlugin getPlugin()
    {
        if (plugin == null)
        {
            for (BrowserPlugin plug : ServiceManager.findBrowserPlugins())
            {
                if (plug instanceof AnnotatorPlugin)
                    this.plugin = (AnnotatorPlugin) plug;
            }
        }
        return plugin;
    }
    
    public void setTagType(String type)
    {
        if (getPlugin() != null)
            getPlugin().getTxtType().setText(type);
        else
            werr.println("Annotator plugin is not active");
    }
    
    public String getTagType()
    {
        if (getPlugin() != null)
            return getPlugin().getTxtType().getText();
        else
        {
            werr.println("Annotator plugin is not active");
            return "";
        }
    }

    public String[] getTags()
    {
        if (getPlugin() != null)
            return getPlugin().getTags();
        else
        {
            werr.println("Annotator plugin is not active");
            return null;
        }
    }

    public void setTags(String[] tags)
    {
        if (getPlugin() != null)
            getPlugin().setTags(tags);
        else
            werr.println("Annotator plugin is not active");
    }


}
