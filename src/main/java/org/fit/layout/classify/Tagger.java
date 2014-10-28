/**
 * Tagger.java
 *
 * Created on 11.11.2011, 11:22:29 by burgetr
 */
package org.fit.layout.classify;

import java.util.Vector;

/**
 * A generic tagger that is able to assign tags to areas.
 * 
 * @author burgetr
 */
public interface Tagger
{

    /**
     * Obtains the tag that this tagger assigns to the areas.
     * @return the tag string
     */
    public Tag getTag();

    /**
     * Obtains the relevance of the tagger.
     * @return the relevance  (0.0 no relevance, 1.0 means absolutely sure)
     */
    public double getRelevance();
    
    /**
     * Checks whether the area may be tagged with the tag. This method does not actually assign the tag to the area.
     * @param node The examined area node.
     * @return <code>true</code> if the area should be tagged with the tag
     */
    public boolean belongsTo(AreaNode node);
    
    /**
     * Checks whether the area may be a continuation of a previously started area tagged with this tag.
     * @param node The examined area node.
     * @return <code>true</code> if the area may be a continuation of a tagged area
     */
    public boolean allowsContinuation(AreaNode node);
    
    /**
     * Checks whether the tag may be used for joining the areas in the visual area tree.
     * @return <code>true</code> if the tag may be used for joining.
     */
    public boolean allowsJoining();
    
    /**
     * Check if the area tagged with this tag may be tagged with another tag. If not, this tag won't be used
     * for the areas already tagged with another tag.
     */
    public boolean mayCoexistWith(Tag other);
    
    /**
     * Extracts the parts of a source string that correspond to this tag.
     * @param src The source string. 
     * @return A vector of extracted strings.
     */
    public Vector<String> extract(String src);
    
}
