/**
 * VisualTag.java
 *
 * Created on 27. 11. 2014, 22:57:22 by burgetr
 */
package org.fit.layout.classify;

import org.fit.layout.impl.DefaultTag;

/**
 * A tag assigned using visual classification.
 *  
 * @author burgetr
 */
public class VisualTag extends DefaultTag
{

    public VisualTag(String value)
    {
        super(value);
        setType("org.fit.layout.classify.VisualTag");
    }

}
