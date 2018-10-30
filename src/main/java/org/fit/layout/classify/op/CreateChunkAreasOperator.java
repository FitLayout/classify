/**
 * CreateChunkAreasOperator.java
 *
 * Created on 28. 2. 2018, 20:01:54 by burgetr
 */
package org.fit.layout.classify.op;

import java.util.ArrayList;
import java.util.List;

import org.fit.layout.classify.TagOccurrence;
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
            List<Area> toAppend = new ArrayList<>();
            for (Tag t : root.getSupportedTags(0.25f)) //TODO make this configurable
            {
                if (t instanceof TextTag)
                {
                    List<Area> newAreas = createAreaFromTag(root, (TextTag) t);
                    System.out.println(root + " : " + t + " : " + newAreas);
                    for (Area a : newAreas)
                    {
                        a.setName("<chunk:" + t.getValue() + ">");
                        toAppend.add(a);
                    }
                }
            }
            for (Area a : toAppend)
                root.appendChild(a);
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
            List<TagOccurrence> occurences = tg.extract(text);
            for (TagOccurrence occ : occurences)
            {
                System.out.println("occ: " + occ);
                Rectangular r = box.getSubstringBounds(occ.getPosition(), occ.getPosition() + occ.getLength());
                Area newArea = new DefaultArea(r);
                ret.add(newArea);
            }
        }
        return ret;
    }
    
}
