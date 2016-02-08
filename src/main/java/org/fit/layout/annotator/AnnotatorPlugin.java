package org.fit.layout.annotator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.fit.layout.api.PageSet;
import org.fit.layout.api.PageSetStorage;
import org.fit.layout.api.PageStorage;
import org.fit.layout.api.ServiceManager;
import org.fit.layout.gui.AreaSelectionListener;
import org.fit.layout.gui.Browser;
import org.fit.layout.gui.BrowserPlugin;
import org.fit.layout.gui.GUIUpdateListener;
import org.fit.layout.gui.TreeListener;
import org.fit.layout.impl.DefaultTag;
import org.fit.layout.model.Area;
import org.fit.layout.model.AreaTree;
import org.fit.layout.model.LogicalAreaTree;
import org.fit.layout.model.Page;
import org.fit.layout.model.Tag;

import javax.swing.JComboBox;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;



/**
 * A GUI plugin for manual annotation of visual areas by tags.
 * 
 * @author milicka
 * @author burgetr
 */
public class AnnotatorPlugin implements BrowserPlugin, AreaSelectionListener, TreeListener, GUIUpdateListener
{
	private Browser browser;
	private PageStorage pageStorage;
	private PageSetStorage setStorage;
    private String[] tags = new String[] {"h1","h2","h3","perex","paragraph","title","date","person" };
    private Area selectedArea;

	private JPanel pnl_mainPanel;
	private JPanel pnl_selection;
	private JComboBox<String> cbx_tagSelector;
	private JPanel pnl_control;
	private JButton btn_addTag;
	private JButton btn_removeTag;
	private JScrollPane scrl_tagTable;
	private JTable tagTable;
	private DefaultTableModel tagTableModel;
	private JPanel pnl_settings;
	private JCheckBox chckbxHighlightTags;
	private JPanel pnl_info;
	private JTextField txtType;
	private JPanel storageButtonPanel;
	private JButton btnPrevious;
	private JButton btnNext;
	private JLabel lblSelectionStatus;
	private JButton btnSave;
	private JButton btnSaveNext;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public boolean init(Browser browser) 
	{
		this.browser = browser;
		this.browser.addToolPanel("Annotator", getPnl_mainPanel());
		this.browser.addAreaSelectionListener(this);
		
		//choose the first available service that implements both the PageStorage and PageSetStorage interfaces
		for (PageStorage service : ServiceManager.findPageStorages().values())
		{
		    if (service instanceof PageSetStorage)
		    {
		        pageStorage = service;
		        setStorage = (PageSetStorage) service;
		        service.registerGUIUpdateListener(this);
		        break;
		    }
		}
		updateStorageStatus();
		
		return true;
	}

    @Override
    public void areaSelected(Area area)
    {
        selectedArea = area;
        updateTableModel();
        getBtn_addTag().setEnabled(area != null);
    }

    @Override
    public void pageRendered(Page page)
    {
        updateGUI();
    }

    @Override
    public void areaTreeUpdated(AreaTree tree)
    {
        updateGUI();
    }

    @Override
    public void logicalAreaTreeUpdated(LogicalAreaTree tree)
    {
        updateGUI();
    }

    @Override
    public void updateGUI()
    {
        updateStorageStatus();
        highlightTags();
    }

    private void updateTableModel()
    {
        //remove all rows
        tagTableModel.setRowCount(0);
        //add the tag rows
        if (selectedArea != null)
        {
            for (Map.Entry<Tag, Float> tagEntry : selectedArea.getTags().entrySet())
            {
                Tag tag = tagEntry.getKey();
                Float support = tagEntry.getValue();
                tagTableModel.addRow(new Object[] { tag.getType(), tag.getValue(), support });
            }
        }
    }
    
    private void updateStorageStatus()
    {
        if (setStorage != null)
        {
            PageSet pset = setStorage.getCurrentPageSet();
            int sel = setStorage.getCurrentIndex();
            if (pset == null)
                lblSelectionStatus.setText("No page set loaded");
            else if (sel == -1)
                lblSelectionStatus.setText("Page set " + pset.getName() + ": No page loaded");
            else
                lblSelectionStatus.setText("Page set " + pset.getName() + ": Page " + (sel+1) + " / " + setStorage.getTotalCount());
            
            btnPrevious.setEnabled(setStorage.previousPageAvailable());
            btnNext.setEnabled(setStorage.nextPageAvailable());
            if (pageStorage.updateAvailable())
            {
                btnSave.setText("Save changes");
                btnSave.setEnabled(true);
                btnSaveNext.setEnabled(setStorage.nextPageAvailable());
            }
            else if (pageStorage.saveAvailable())
            {
                btnSave.setText("Save as new");
                btnSave.setEnabled(true);
                btnSaveNext.setEnabled(false);
            }
            else
            {
                btnSave.setEnabled(false);
                btnSaveNext.setEnabled(false);
            }
        }
        else
        {
            lblSelectionStatus.setText("(no storage available)");
            btnPrevious.setEnabled(false);
            btnNext.setEnabled(false);
            btnSave.setEnabled(false);
            btnSaveNext.setEnabled(false);
        }
    }
    
    public void highlightTags()
    {
        if (getChckbxHighlightTags().isSelected())
        {
            browser.redrawPage();
            if (browser.getAreaTree() != null && browser.getAreaTree().getRoot() != null)
            {
                String type = getTxtType().getText();
                recursiveColorizeTags(browser.getAreaTree().getRoot(), type);
            }
            browser.updateDisplay();
        }
    }
    
    private void recursiveColorizeTags(Area root, String type)
    {
        //find tags of the given type
        Set<Tag> tags = new HashSet<Tag>();
        for (Tag tag : root.getTags().keySet())
        {
            if (tag.getType().equals(type))
                tags.add(tag);
        }
        //display the tags
        browser.getOutputDisplay().colorizeByTags(root, tags);
        for (Area child : root.getChildAreas())
            recursiveColorizeTags(child, type);
    }

    //===========================================================================
    
    private JPanel getPnl_mainPanel()
    {
        if (pnl_mainPanel == null)
        {
            pnl_mainPanel = new JPanel();
            
            GridBagLayout gbl_pathsPanel = new GridBagLayout();
            gbl_pathsPanel.columnWeights = new double[] { 0.0, 1.0, 1.0 };
            gbl_pathsPanel.rowWeights = new double[] { 0.05, 0.05, 0.0, 0.0 };
            pnl_mainPanel.setLayout(gbl_pathsPanel);
            
            GridBagConstraints gbc_selection = new GridBagConstraints();
            gbc_selection.insets = new Insets(0, 0, 5, 5);
            gbc_selection.fill = GridBagConstraints.HORIZONTAL;
            gbc_selection.gridx = 0;
            gbc_selection.gridy = 0;
            pnl_mainPanel.add(getPnl_selection(), gbc_selection);
            
            GridBagConstraints gbc_control = new GridBagConstraints();
            gbc_control.insets = new Insets(0, 0, 5, 5);
            gbc_control.fill = GridBagConstraints.HORIZONTAL;
            gbc_control.gridx = 0;
            gbc_control.gridy = 1;
            pnl_mainPanel.add(getPnl_control(), gbc_control);
            
            GridBagConstraints gbc_extractionScroll = new GridBagConstraints();
            gbc_extractionScroll.insets = new Insets(0, 0, 0, 5);
            gbc_extractionScroll.weighty = 1.0;
            gbc_extractionScroll.gridheight = 4;
            gbc_extractionScroll.fill = GridBagConstraints.BOTH;
            gbc_extractionScroll.gridx = 1;
            gbc_extractionScroll.gridy = 0;
            pnl_mainPanel.add(getScrl_tagTable(), gbc_extractionScroll);
            GridBagConstraints gbc_pnl_settings = new GridBagConstraints();
            gbc_pnl_settings.insets = new Insets(0, 0, 5, 5);
            gbc_pnl_settings.fill = GridBagConstraints.BOTH;
            gbc_pnl_settings.gridx = 0;
            gbc_pnl_settings.gridy = 2;
            pnl_mainPanel.add(getPnl_settings(), gbc_pnl_settings);
            GridBagConstraints gbc_pnl_info = new GridBagConstraints();
            gbc_pnl_info.insets = new Insets(0, 0, 5, 0);
            gbc_pnl_info.weighty = 1.0;
            gbc_pnl_info.weightx = 1.0;
            gbc_pnl_info.gridheight = 2;
            gbc_pnl_info.fill = GridBagConstraints.BOTH;
            gbc_pnl_info.gridx = 2;
            gbc_pnl_info.gridy = 0;
            pnl_mainPanel.add(getPanel_1(), gbc_pnl_info);
            GridBagConstraints gbc_lblSelectionStatus = new GridBagConstraints();
            gbc_lblSelectionStatus.anchor = GridBagConstraints.SOUTHEAST;
            gbc_lblSelectionStatus.insets = new Insets(0, 0, 5, 5);
            gbc_lblSelectionStatus.gridx = 2;
            gbc_lblSelectionStatus.gridy = 2;
            pnl_mainPanel.add(getLblSelectionStatus(), gbc_lblSelectionStatus);
            GridBagConstraints gbc_storageButtonPanel = new GridBagConstraints();
            gbc_storageButtonPanel.anchor = GridBagConstraints.EAST;
            gbc_storageButtonPanel.fill = GridBagConstraints.VERTICAL;
            gbc_storageButtonPanel.gridx = 2;
            gbc_storageButtonPanel.gridy = 3;
            pnl_mainPanel.add(getStorageButtonPanel(), gbc_storageButtonPanel);
            
            
        }
        return pnl_mainPanel;
    }

    private JPanel getPnl_selection() {
    	if(pnl_selection==null) {
    		pnl_selection = new JPanel();
    		GridBagLayout gbl_pnl_selection = new GridBagLayout();
    		gbl_pnl_selection.columnWeights = new double[]{1.0};
    		gbl_pnl_selection.rowWeights = new double[]{0.0, 0.0};
    		pnl_selection.setLayout(gbl_pnl_selection);
    		GridBagConstraints gbc_txtType = new GridBagConstraints();
    		gbc_txtType.fill = GridBagConstraints.BOTH;
    		gbc_txtType.insets = new Insets(5, 5, 0, 5);
    		gbc_txtType.gridx = 0;
    		gbc_txtType.gridy = 0;
    		pnl_selection.add(getTxtType(), gbc_txtType);
    		GridBagConstraints gbc_cbx_tagSelector = new GridBagConstraints();
    		gbc_cbx_tagSelector.insets = new Insets(0, 5, 0, 5);
    		gbc_cbx_tagSelector.fill = GridBagConstraints.HORIZONTAL;
    		gbc_cbx_tagSelector.gridx = 0;
    		gbc_cbx_tagSelector.gridy = 1;
    		pnl_selection.add(getCbx_tagSelection(), gbc_cbx_tagSelector);
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
	
    private JPanel getPnl_control() {
    	if(pnl_control==null) {
    		pnl_control = new JPanel();
    		pnl_control.setLayout( new FlowLayout(FlowLayout.CENTER) );
    		pnl_control.add(getBtn_addTag() );
    		pnl_control.add(getBtn_removeTag());
    	}
    	return pnl_control;
    }

	private JButton getBtn_addTag() {
		if (btn_addTag == null) {
			
			btn_addTag = new JButton("Assign");
			btn_addTag.setEnabled(false);
			btn_addTag.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) 
				{
				    if (selectedArea != null)
				    {
				        Tag tag = new DefaultTag(txtType.getText(), cbx_tagSelector.getSelectedItem().toString());
				        selectedArea.addTag(tag, 1.0f);
				        updateTableModel();
				        highlightTags();
				    }
				}
			});
		}
		return btn_addTag;
	}

	private JButton getBtn_removeTag() {
		if (btn_removeTag == null) {
			
			btn_removeTag = new JButton("Remove");
			btn_removeTag.setEnabled(false);
			btn_removeTag.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent arg0) 
				{
				    int row = tagTable.getSelectedRow();
				    if (row != -1)
				    {
				        Tag tag = new DefaultTag(tagTableModel.getValueAt(row, 0).toString(),
				                tagTableModel.getValueAt(row, 1).toString());
				        if (selectedArea != null)
				            selectedArea.removeTag(tag);
				        updateTableModel();
                        highlightTags();
				    }
				}
			});
		}
		return btn_removeTag;
	}

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
        	tagTableModel = new DefaultTableModel() {
                private static final long serialVersionUID = 1L;
                public boolean isCellEditable(int row, int column) 
                {
                    return false;
                };
            };
            tagTableModel.addColumn("Type"); 
        	tagTableModel.addColumn("Name"); 
        	tagTableModel.addColumn("Support");
        	
            tagTable = new JTable(tagTableModel);
            tagTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tagTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent ev)
                {
                    getBtn_removeTag().setEnabled(tagTable.getSelectedRow() != -1);
                }
            });
        }
        return tagTable;
    }
    
    private JPanel getPnl_settings() 
    {
        if (pnl_settings == null) {
        	pnl_settings = new JPanel();
        	pnl_settings.add(getChckbxHighlightTags());
        }
        return pnl_settings;
    }
    
    private JCheckBox getChckbxHighlightTags() 
    {
        if (chckbxHighlightTags == null) {
        	chckbxHighlightTags = new JCheckBox("Highlight tags");
        	chckbxHighlightTags.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    if (chckbxHighlightTags.isSelected())
                    {
                        highlightTags();
                    }
                    else
                    {
                        browser.redrawPage();
                        browser.updateDisplay();
                    }
                }
        	});
        }
        return chckbxHighlightTags;
    }
    
    private JPanel getPanel_1() 
    {
        if (pnl_info == null) {
        	pnl_info = new JPanel();
        }
        return pnl_info;
    }
    
    private JTextField getTxtType() 
    {
        if (txtType == null) {
        	txtType = new JTextField();
        	txtType.setText("FitLayout.Annotate");
        	txtType.setColumns(10);
        }
        return txtType;
    }

    private JPanel getStorageButtonPanel() {
        if (storageButtonPanel == null) {
        	storageButtonPanel = new JPanel();
        	FlowLayout flowLayout = (FlowLayout) storageButtonPanel.getLayout();
        	flowLayout.setAlignment(FlowLayout.RIGHT);
        	storageButtonPanel.add(getBtnSaveNext());
        	storageButtonPanel.add(getBtnSave());
        	storageButtonPanel.add(getBtnPrevious());
        	storageButtonPanel.add(getBtnNext());
        }
        return storageButtonPanel;
    }
    private JButton getBtnPrevious() {
        if (btnPrevious == null) {
        	btnPrevious = new JButton("<");
        	btnPrevious.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) 
        	    {
                    if (setStorage != null && setStorage.previousPageAvailable())
                    {
                        setStorage.loadPrevious();
                        updateStorageStatus();
                    }
        	    }
        	});
        }
        return btnPrevious;
    }
    private JButton getBtnNext() {
        if (btnNext == null) {
        	btnNext = new JButton(">");
        	btnNext.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent arg0) 
        	    {
        	        if (setStorage != null && setStorage.nextPageAvailable())
        	        {
        	            setStorage.loadNext();
        	            updateStorageStatus();
        	        }
        	    }
        	});
        }
        return btnNext;
    }
    private JLabel getLblSelectionStatus() {
        if (lblSelectionStatus == null) {
        	lblSelectionStatus = new JLabel("No page selected");
        }
        return lblSelectionStatus;
    }
    private JButton getBtnSave() {
        if (btnSave == null) {
        	btnSave = new JButton("Save");
        	btnSave.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) 
        	    {
        	    }
        	});
        }
        return btnSave;
    }
    private JButton getBtnSaveNext() {
        if (btnSaveNext == null) {
        	btnSaveNext = new JButton("Save&Next");
        	btnSaveNext.addActionListener(new ActionListener() {
        	    public void actionPerformed(ActionEvent e) 
        	    {
        	    }
        	});
        }
        return btnSaveNext;
    }
}
