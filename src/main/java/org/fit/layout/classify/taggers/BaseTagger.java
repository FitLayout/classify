/**
 * BaseTagger.java
 *
 * Created on 17. 2. 2016, 18:03:44 by burgetr
 */
package org.fit.layout.classify.taggers;

import org.fit.layout.classify.Tagger;
import org.fit.layout.impl.BaseParametrizedOperation;

/**
 * A base implementation of a tagger with no parametres.
 * @author burgetr
 */
public abstract class BaseTagger extends BaseParametrizedOperation implements Tagger
{

    @Override
    public String[] getParamNames()
    {
        return new String[]{};
    }

    @Override
    public ValueType[] getParamTypes()
    {
        return new ValueType[]{};
    }

}
