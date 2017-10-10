/**
 * TreeTagger.java
 *
 * Created on 11.11.2011, 12:56:50 by burgetr
 */
package org.fit.layout.classify;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;


/**
 * This class implements the area tree tagging.
 *
 * @author burgetr
 */
public class TreeTagger /*implements NodeJoinAnalyzer*/
{
    private static Logger log = LoggerFactory.getLogger(TreeTagger.class);
    
    //public static AbstractSequenceClassifier<?> sharedClassifier = CRFClassifier.getClassifierNoExceptions("/opt/java/classifiers/all.3class.distsim.crf.ser.gz");
    //public static AbstractSequenceClassifier<?> sharedClassifier = CRFClassifier.getClassifierNoExceptions(System.getProperty("user.home") + "/tmp/classifiers/all.3class.distsim.crf.ser.gz");
    private static final float MIN_SUPPORT = 0.01f; //minimal returned support to assign the tag at all
    
    private static AbstractSequenceClassifier<?> sharedClassifier;
    
    protected Area root;
    protected Vector<Tagger> taggers;
    
    public TreeTagger(Area root)
    {
        this.root = root;
        taggers = new Vector<Tagger>();
    }
    
    public void addTagger(Tagger tagger)
    {
        taggers.add(tagger);
    }
    
    /**
     * Obtains the list of all tags used by the taggers
     * @return the list of tags
     */
    public List<Tag> getAllTags()
    {
        List <Tag> ret = new Vector<Tag>(taggers.size());
        for (Tagger tagger : taggers)
            ret.add(tagger.getTag());
        return ret;
    }
    
    /**
     * Applies all the taggers to the whole tree.
     */
    public void tagTree()
    {
        tagSubtree(root);
    }

    /**
     * Applies all the taggers a subtree of the area tree.
     * @param root the root node of the subtree
     */
    public void tagSubtree(Area root)
    {
        tagSingleNode(root);
        for (int i = 0; i < root.getChildCount(); i++)
            tagSubtree(root.getChildArea(i));
    }
    
    /**
     * Applies all the taggers to a single tree node.
     * @param area the tree node
     */
    public void tagSingleNode(Area area)
    {
        for (Tagger t : taggers)
        {
            float support = t.belongsTo(area); 
            if (support > MIN_SUPPORT)
                area.addTag(t.getTag(), support);
        }
    }
    
    /**
     * Checks if two logical nodes are joinable. For this, the must
     * <ul>
     * <li>Made of neighboring area nodes (nothing between them)
     * <li>Have the same style
     * <li>Have the same tags or the second one may be a continuation of the first one
     * <li>None of the tags of the second node may refuse joining
     * </ul> 
     * @param l1 the first logical node
     * @param l2 the second logical node
     * @return <code>true</code> if the nodes may be joined
     */
    /*public boolean isJoinable(LogicalNode l1, LogicalNode l2)
    {
        Set<Tag> set1 = l1.getTags();
        Set<Tag> set2 = l2.getTags();

        AreaNode a1 = l1.getLastAreaNode();
        AreaNode a2 = l2.getFirstAreaNode();
        
        
        if ((a1 != null && a2 != null && a1.getNextSibling() == a2) &&  //must be adjacent areas
                l1.getFirstAreaNode().hasSameStyle(l2.getFirstAreaNode())) //require the same style
        {
            for (Tag tag : set2) //check if the second area does not refuse joining
                if (!tag.allowsJoining())
                    return false;
            
            for (Tag tag : set1)
            {
                if ((set2.isEmpty() && tag.allowsContinutation(l2.getFirstAreaNode())) //no tags in set2 but a2 can be a continuation of the previous area
                        || (tag.allowsJoining() && set2.contains(tag))) //both have the same joinable tag
                    return true;
            }
        }
        return false;
    } */   
    
    public static AbstractSequenceClassifier<?> getSharedClassifier()
    {
        if (sharedClassifier == null)
        {
            log.info("Loading resource {}", TreeTagger.class.getResource("/3class.gz"));
            InputStream is;
            try
            {
                is = new GZIPInputStream(TreeTagger.class.getResourceAsStream("/3class.gz"));
                sharedClassifier = CRFClassifier.getClassifier(is);
            } catch (IOException e)
            {
                System.err.println("Load failed: " + e.getMessage());
            } catch (ClassCastException e)
            {
                System.err.println("Load failed: " + e.getMessage());
            } catch (ClassNotFoundException e)
            {
                System.err.println("Load failed: " + e.getMessage());
            }
        }
        return sharedClassifier;
    }
    
}
