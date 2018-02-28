/**
 * CreateChunkAreasOperator.java
 *
 * Created on 28. 2. 2018, 20:01:54 by burgetr
 */
package org.fit.layout.classify.op;

import java.util.ArrayList;
import java.util.List;

import org.fit.layout.classify.Tagger;
import org.fit.layout.classify.TextTag;
import org.fit.layout.impl.BaseOperator;
import org.fit.layout.impl.DefaultArea;
import org.fit.layout.model.Area;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.Box;
import org.fit.layout.model.Rectangular;
import org.fit.layout.model.Tag;

/**
 * Creates artificial visual areas from the tagged chunks.
 * 
 * @author burgetr
 */
public class CreateChunkAreasOperator extends BaseOperator
{

    public CreateChunkAreasOperator()
    {
    }
    
    @Override
    public String getId()
    {
        return "FitLayout.Tag.Chunks";
    }
    
    @Override
    public String getName()
    {
        return "Create areas from tagged chunks";
    }

    @Override
    public String getDescription()
    {
        return "Creates artificial visual areas from the tagged chunks";
    }

    @Override
    public String getCategory()
    {
        return "classification";
    }
   
    //==============================================================================

    @Override
    public void apply(AreaTree atree)
    {
        apply(atree, atree.getRoot());
    }
    
    @Override
    public void apply(AreaTree atree, Area root)
    {
        recursiveScan(root);
    }
    
    private void recursiveScan(Area root)
    {
        if (root.isLeaf())
        {
            for (Tag t : root.getSupportedTags(0.25f)) //TODO make this configurable
            {
                if (t instanceof TextTag)
                {
                    List<Area> newAreas = createAreaFromTag(root, (TextTag) t);
                    System.out.println(root + " : " + t + " : " + newAreas);
                }
            }
        }
        else
        {
            for (Area child : root.getChildren())
                recursiveScan(child);
        }
    }
    
    private List<Area> createAreaFromTag(Area a, TextTag t)
    {
        List<Area> ret = new ArrayList<>();
        Tagger tg = t.getSource();
        System.out.println("Tagger: " + tg);
        for (Box box : a.getBoxes())
        {
            String text = box.getOwnText();
            List<String> occurences = tg.extract(text);
            int last = 0;
            for (String occ : occurences)
            {
                System.out.println("occ: " + occ);
                int pos = text.indexOf(occ, last);
                if (pos != -1)
                {
                    Rectangular r = box.getSubstringBounds(pos, pos + occ.length());
                    Area newArea = new DefaultArea(r);
                    ret.add(newArea);
                    last = pos + occ.length();
                }
            }
        }
        return ret;
    }
    
}
