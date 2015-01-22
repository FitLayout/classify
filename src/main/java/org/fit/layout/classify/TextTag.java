/**
 * Tag.java
 *
 * Created on 21.11.2011, 13:47:31 by burgetr
 */
package org.fit.layout.classify;

import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;


/**
 * A tag that can be assigned to the visual areas obtained using a text
 * classification by a particular tagger.
 * 
 * @author burgetr
 */
public class TextTag implements Tag
{
    private String value;
    private Tagger source;
    private int level;
    
    public TextTag(String value, Tagger source)
    {
        this.value = value;
        this.source = source;
        this.level = 0;
    }

    @Override
    public String getValue()
    {
        return value;
    }

    public Tagger getSource()
    {
        return source;
    }

    @Override
    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    @Override
    public String getType()
    {
        return "org.fit.layout.classify.TextTag";
    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public int hashCode()
    {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        return value.equals(obj.toString());
    }
    
    public boolean allowsJoining()
    {
        if (source != null)
            return source.allowsJoining();
        else
            return false;
    }
    
    public boolean allowsContinutation(Area node)
    {
        if (source != null)
            return source.allowsContinuation(node);
        else
            return false;
    }
    
}
