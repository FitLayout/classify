/**
 * StyleCounter.java
 *
 * Created on 5. 3. 2015, 16:30:24 by burgetr
 */
package org.fit.layout.classify;

import java.util.HashMap;
import java.util.Map;

/**
 * Statistical analyzer of styles.
 * 
 * @author burgetr
 */
public class StyleCounter
{
    private Map<NodeStyle, Integer> styles;
    
    
    public StyleCounter()
    {
        styles = new HashMap<NodeStyle, Integer>();
    }
    
    public void add(NodeStyle style)
    {
        Integer cnt = styles.get(style);
        if (cnt == null)
            styles.put(style, 1);
        else
            styles.put(style, cnt+1);
    }
    
    public int getCount(NodeStyle style)
    {
        Integer cnt = styles.get(style);
        return cnt == null ? 0 : cnt;
    }
    
    public NodeStyle getMostFrequent()
    {
        NodeStyle ret = null;
        int freq = 0;
        for (Map.Entry<NodeStyle, Integer> entry : styles.entrySet())
        {
            if (entry.getValue() > freq)
            {
                ret = entry.getKey();
                freq = entry.getValue();
            }
        }
        return ret;
    }
    
    //==============================================================================================
    
    
}
