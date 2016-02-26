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
    
    
    /**
     * Creates an empty style counter.
     */
    public StyleCounter()
    {
        styles = new HashMap<T, Integer>();
    }
    
    /**
     * Adds a new occurence to the counter.
     * @param style The style to be added.
     */
    public void add(T style)
    {
        Integer cnt = styles.get(style);
        if (cnt == null)
            styles.put(style, 1);
        else
            styles.put(style, cnt+1);
    }
    
    /**
     * Obtains total registered number of occurences of the given style. 
     * @param style the style whose number of occurences should be returned
     * @return
     */
    public int getCount(T style)
    {
        Integer cnt = styles.get(style);
        return cnt == null ? 0 : cnt;
    }
    
    /**
     * Obtains the most frequent style. If there are multiple styles with the same frequency then
     * only one of them is returned.
     * @return The most frequent style or {@code null} when the counter is empty.
     */
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
    
    /**
     * Obtains the most frequent style or styles when multiple of them have the maximal frequency.
     * @return The list of styles with the maximal frequency.
     */
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
