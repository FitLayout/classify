/**
 * StyleCounter.java
 *
 * Created on 5. 3. 2015, 16:30:24 by burgetr
 */
package org.fit.layout.classify;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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

    /**
     * Obtains all the registered styles and their frequencies.
     * @return A map that assigns a frequency to each unique style.
     */
    public Map<T, Integer> getAll()
    {
    	return styles;
    }
    
    /**
     * Obtains all distinct styles that have been seen independently on their frequencies.
     * @return the set of available styles
     */
    public Set<T> getDistinctStyles()
    {
        return styles.keySet();
    }
    
    /**
     * Obtains the frequent style where the frequency is greater or equal than factor*max_frequency.
     * @return The list of frequent styles
     */
    public List<T> getFrequentSyles(float factor)
    {
        List<T> ret = new ArrayList<T>();
        int maxfreq = 0;
        for (Map.Entry<T, Integer> entry : styles.entrySet())
        {
            if (entry.getValue() > maxfreq)
                maxfreq = entry.getValue();
        }
        for (Map.Entry<T, Integer> entry : styles.entrySet())
        {
            if (entry.getValue() >= factor * maxfreq)
                ret.add(entry.getKey());
        }
        return ret;
    }
    
    /**
     * Obtains all the registered styles and their frequencies, sort by frequenct.
     * @return A map that assigns a frequency to each unique style.
     */
    public Map<T, Integer> getAllSorted()
    {
        Map<T, Integer> map = styles;
        TreeMap<T, Integer> smap = new TreeMap<T, Integer>(new StyleCountComparator(map));
        smap.putAll(map);
        return smap;
    }
    
    //==============================================================================================
    
	@Override
	public String toString() 
	{
		Map<T, Integer> map = getAllSorted();
		StringBuilder ret = new StringBuilder();
		for (Map.Entry<T, Integer> entry : map.entrySet())
		{
			ret.append(entry.getValue()).append("x(");
			ret.append(entry.getKey().toString());
			ret.append(") ");
		}
		return ret.toString();
	}
	
    //==============================================================================================
	
    /**
     * A comparator used for sorting style maps according to the style count.
     */
    class StyleCountComparator implements Comparator<T>
    {
        Map<T, Integer> base;

        public StyleCountComparator(Map<T, Integer> base)
        {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with
        // equals.
        public int compare(T a, T b)
        {
            if (base.get(a) >= base.get(b))
            {
                return -1;
            }
            else
            {
                return 1;
            } // returning 0 would merge keys
        }
    }

    
}
