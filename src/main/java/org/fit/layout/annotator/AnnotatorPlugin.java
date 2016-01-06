package org.fit.layout.annotator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.fit.layout.gui.AreaSelectionListener;
import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.impl.DefaultArea;
import org.fit.layout.model.Area;
import org.fit.layout.model.Tag;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;



/**
 * Plugin enables area tree annotation
 * 
 * @author milicka
 *
 */
public class AnnotatorPlugin implements BrowserPlugin {

	Browser browser;
	private JPanel pnl_mainPanel;
	private JPanel pnl_selection;
	private JComboBox<String> cbx_tagSelector;
	private JPanel pnl_control;
	private JButton btn_toogleEnableAnotation;
	private JButton btn_clearAnotation;
	private AnnotatorAreaListener listener;
	private JScrollPane scrl_tagTable;
	private JTable tagTable;
	private DefaultTableModel tagModelTable;
	
	
	private Boolean enableAnotation = false;
	private String[] tags = new String[] {"h1","h2","h3","perex","paragraph","title","date","person" };
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public boolean init(Browser browser) {
		this.browser = browser;
		
		this.browser.addStructurePanel("Annotator", getPnl_mainPanel());
		
		this.browser.addAreaSelectionListener(getListener_areaSelection());
		return true;
	}

	private AreaSelectionListener getListener_areaSelection () {
		
		if(listener == null) {
			listener = new AnnotatorAreaListener() { 
				@Override
				public void areaSelected(Area area) {
					super.areaSelected(area);
					
					if(!enableAnotation) {
						return;
					}
						
					String actualTag = getCbx_tagSelection().getSelectedItem().toString();
					AnnotatorTag aTag = new AnnotatorTag(actualTag);
					
					if( area.hasTag(aTag) ) {
						removeTag((DefaultArea)area, aTag);
					}
					else {
						addTag(area, aTag);
					}
				}
			};
		}
		
		return listener;
	}
	
    private JPanel getPnl_mainPanel()
    {
        if (pnl_mainPanel == null)
        {
            pnl_mainPanel = new JPanel();
            
            GridBagLayout gbl_pathsPanel = new GridBagLayout();
            gbl_pathsPanel.columnWidths = new int[] { 0, 0 };
            gbl_pathsPanel.rowHeights = new int[] { 0, 0, 0 };
            gbl_pathsPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
            gbl_pathsPanel.rowWeights = new double[] { 0.05, 0.05, 1.0,
                    Double.MIN_VALUE };
            pnl_mainPanel.setLayout(gbl_pathsPanel);
            
            GridBagConstraints gbc_selection = new GridBagConstraints();
            gbc_selection.insets = new Insets(0, 0, 5, 0);
            gbc_selection.fill = GridBagConstraints.BOTH;
            gbc_selection.gridx = 0;
            gbc_selection.gridy = 0;
            pnl_mainPanel.add(getPnl_selection(), gbc_selection);
            
            GridBagConstraints gbc_control = new GridBagConstraints();
            gbc_control.fill = GridBagConstraints.HORIZONTAL;
            gbc_control.gridx = 0;
            gbc_control.gridy = 1;
            pnl_mainPanel.add(getPnl_control(), gbc_control);
            
            GridBagConstraints gbc_extractionScroll = new GridBagConstraints();
            gbc_extractionScroll.fill = GridBagConstraints.BOTH;
            gbc_extractionScroll.gridx = 0;
            gbc_extractionScroll.gridy = 2;
            pnl_mainPanel.add(getScrl_tagTable(), gbc_extractionScroll);
            
            
        }
        return pnl_mainPanel;
    }

    
    
    //selection panel ====================
    private JPanel getPnl_selection() {
    	if(pnl_selection==null) {
    		pnl_selection = new JPanel();
    		pnl_selection.setLayout( new FlowLayout(FlowLayout.CENTER) );
    		pnl_selection.add(getCbx_tagSelection());
    	}
    	return pnl_selection;
    }
    
	private JComboBox<String> getCbx_tagSelection() {
		if (cbx_tagSelector == null) {
			cbx_tagSelector = new JComboBox<String>();
			
			for(String tag: this.tags) {
				cbx_tagSelector.addItem(tag);
			}
		}
		return cbx_tagSelector;
	}
	
	
	
	//control panel ====================
    private JPanel getPnl_control() {
    	if(pnl_control==null) {
    		pnl_control = new JPanel();
    		pnl_control.setLayout( new FlowLayout(FlowLayout.CENTER) );
    		pnl_control.add(getBtn_toogleEnableAnotation() );
    		pnl_control.add(getBtn_clearAnotation());
    	}
    	return pnl_control;
    }

	private JButton getBtn_toogleEnableAnotation() {
		if (btn_toogleEnableAnotation == null) {
			enableAnotation = false;
			
			btn_toogleEnableAnotation = new JButton("Enable anotation");
			btn_toogleEnableAnotation.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(!enableAnotation) {
						enableAnotation = true;
						btn_toogleEnableAnotation.setText("Disable anotation");
						tagTable.setBackground(Color.white);
					}
					else {
						enableAnotation = false;
						btn_toogleEnableAnotation.setText("Enable anotation");
						tagTable.setBackground(Color.lightGray);
					}
				}
			});
		}
		return btn_toogleEnableAnotation;
	}

	private JButton getBtn_clearAnotation() {
		if (btn_clearAnotation == null) {
			
			btn_clearAnotation = new JButton("Clear");
			btn_clearAnotation.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent arg0) 
				{
					for(int i=tagModelTable.getRowCount(); i>0; i--) 
					{
						String tag = (String)tagModelTable.getValueAt(i-1, 0);
						DefaultArea area = (DefaultArea)tagModelTable.getValueAt(i-1, 1);
						removeTag(area, new AnnotatorTag(tag));
					}
						
				}
			});
		}
		return btn_clearAnotation;
	}

	
	
	//anotation list ========================
	private JScrollPane getScrl_tagTable()
    {
        if (scrl_tagTable == null)
        {
            scrl_tagTable = new JScrollPane();
            scrl_tagTable.setViewportView(getTbl_classificationTags());
        }
        return scrl_tagTable;
    }

    private JTable getTbl_classificationTags()
    {
        if (tagTable == null)
        {
        	tagModelTable = new DefaultTableModel(); 
        	tagModelTable.addColumn("Tag"); 
        	tagModelTable.addColumn("Content");
        	
            tagTable = new JTable(tagModelTable);
        }
        return tagTable;
    }
    
    
    
    //operation function ===========================
	
	/**
	 * Adds tag to GUI table and area object
	 * @param area
	 * @param tag
	 */
	private void addTag(Area area, Tag tag) 
	{
		area.addTag(tag, 1.0f);
		tagModelTable.addRow(new Object[] { (getCbx_tagSelection().getSelectedItem()).toString(), area });
	}
	
	/**
	 * Does complete removing from GUI table and area object
	 * @param area
	 * @param tag
	 */
	private void removeTag(DefaultArea area, AnnotatorTag tag) 
	{
		//Removes tag from the specific area object
		if (area.containsTag(tag)) {
			area.removeTag(tag);
		}

		//Removes table row for defined area and tag
		for( int i=0; i<tagModelTable.getRowCount(); i++ ) 
		{
			Area aa = (Area)tagModelTable.getValueAt(i, 1);
			String tagString = (String)tagModelTable.getValueAt(i, 0);
	
			if(area.equals(aa) && tag.equals( new AnnotatorTag(tagString) )) {
				tagModelTable.removeRow(i);
			}
		}
	}
}
