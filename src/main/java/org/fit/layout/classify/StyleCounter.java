/**
 * StyleCounter.java
 *
 * Created on 5. 3. 2015, 16:30:24 by burgetr
 */
package org.fit.layout.classify;

import java.util.HashMap;
import java.util.Map;

/**
 * Statistical analyzer of style occurences. Any implementation of the style may be provided,
 * e.g. the default {@code NodeStyle}. The style implementation must correctly implement the
 * {@code equals()} and {@code hashCode()} methods. 
 * 
 * @author burgetr
 */
public class StyleCounter<T>
{
    private Map<T, Integer> styles;
    
    
    public StyleCounter()
    {
        styles = new HashMap<T, Integer>();
    }
    
    public void add(T style)
    {
        Integer cnt = styles.get(style);
        if (cnt == null)
            styles.put(style, 1);
        else
            styles.put(style, cnt+1);
    }
    
    public int getCount(T style)
    {
        Integer cnt = styles.get(style);
        return cnt == null ? 0 : cnt;
    }
    
    public T getMostFrequent()
    {
        T ret = null;
        int freq = 0;
        for (Map.Entry<T, Integer> entry : styles.entrySet())
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
