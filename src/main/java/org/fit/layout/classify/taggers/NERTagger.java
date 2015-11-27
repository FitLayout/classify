/**
 * NERTagger.java
 *
 * Created on 28. 11. 2015, 0:34:14 by burgetr
 */
package org.fit.layout.classify.taggers;

import org.fit.layout.classify.TreeTagger;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;

/**
 * 
 * @author burgetr
 */
public abstract class NERTagger
{

    public AbstractSequenceClassifier<?> getClassifier()
    {
        return TreeTagger.getSharedClassifier();
    }
    
}
