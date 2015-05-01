/**
 * StyleCounter.java
 *
 * Created on 5. 3. 2015, 16:30:24 by burgetr
 */
package org.fit.layout.classify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
    
    public List<T> getMostFrequentAll()
    {
        List<T> ret = new Vector<T>();
        int maxfreq = 0;
        for (Map.Entry<T, Integer> entry : styles.entrySet())
        {
            if (entry.getValue() > maxfreq)
                maxfreq = entry.getValue();
        }
        for (Map.Entry<T, Integer> entry : styles.entrySet())
        {
            if (entry.getValue() == maxfreq)
                ret.add(entry.getKey());
        }
        return ret;
    }
    
    //==============================================================================================
    
    
}
