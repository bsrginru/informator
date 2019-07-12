/*
 *  Класс окна для работы с настройками
 */
package avttrue.informator.Thesaurus;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.charset.Charset;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.ListSelectionModel;

import avttrue.informator.Tools.TxtRes;

public class ThesaurusSettingsWindow 
{
	private static JDialog SettingsFrame;
	private static JCheckBox cbShowBiomesNames;
	private static JCheckBox cbShowBlocksNames;
	private static JCheckBox cbShowItemsNames;
	private static JCheckBox cbDuplicateAddressInChat;
	private static JCheckBox cbShowEnchantmentsNames;
	private static JCheckBox cbShowEntityesNames;
	private static JTextField tfWEB_URL;
	private static JComboBox cbSLCM;
	// создание окна
    public static void CreateSettingsWindow()
    {
    	
    	JDialog.setDefaultLookAndFeelDecorated(true);
    	SettingsFrame = new JDialog(ThesaurusWindow.mainFrame);
    	SettingsFrame.setModal(true);
    	SettingsFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    	SettingsFrame.addWindowListener(new WindowAdapter() 
    	{
    		@Override
    		public void windowClosed(WindowEvent e) 
            {
    			SettingsFrame.dispose();
            }
        });
    	
    	SettingsFrame.setTitle(TxtRes.GetLocalText("avttrue.thesaurus.5", "Settings"));
    	int sizeWidth = 640;
    	int sizeHeight = 480;
    	SettingsFrame.setBounds(0, 0, sizeWidth, sizeHeight);
    	SettingsFrame.setLocationRelativeTo(null);
    	
    	JPanel buttonPane = new JPanel();
    	buttonPane.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createRaisedBevelBorder(),
    			BorderFactory.createLoweredBevelBorder()));
    	
    	
    	JPanel controlPanel = new JPanel();
    	controlPanel.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
    	
    	JScrollPane scrollPane = new JScrollPane(controlPanel);
    	scrollPane.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
    	scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    	scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	
    	GridBagLayout gbl = new GridBagLayout();
    	GridBagConstraints gbc = new GridBagConstraints();
    	controlPanel.setLayout(gbl);
    	gbc.anchor = GridBagConstraints.NORTHWEST;
    	gbc.fill   = GridBagConstraints.NONE;  
    	gbc.gridheight = 1;
    	gbc.gridwidth  = GridBagConstraints.REMAINDER; 
    	gbc.gridx = GridBagConstraints.RELATIVE; 
    	gbc.gridy = GridBagConstraints.RELATIVE; 
    	gbc.ipadx = 0;
    	gbc.ipady = 10;
    	gbc.weightx = 0.0;
    	gbc.weighty = 0.0;
    	
    	// WEB_URL заголовок
    	JLabel lWEB_URL = new JLabel(TxtRes.GetLocalText("avttrue.thesaurus.6", "Web-address Wiki") + ":");
    	gbc.insets = new Insets(0, 5, 0, 0);
    	gbl.setConstraints(lWEB_URL, gbc);
    	controlPanel.add(lWEB_URL);
    	
    	// WEB_URL
    	JPanel pWEB_URL = new JPanel();
    	pWEB_URL.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createLoweredBevelBorder(),
				BorderFactory.createRaisedBevelBorder()));
    	tfWEB_URL = new JTextField(40);
    	tfWEB_URL.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
    	tfWEB_URL.setText(ThesaurusWindow.thesaurusConfig.WebAddress);
    	pWEB_URL.add(tfWEB_URL);
    	
    	JButton DefaultButton = new JButton();
    	DefaultButton.setToolTipText(TxtRes.GetLocalText("avttrue.thesaurus.15", "Return default value"));
    	
    	if(ThesaurusWindow.defaultButtonIcon != null)
    	{
    		DefaultButton.setIcon(new ImageIcon(ThesaurusWindow.defaultButtonIcon));
    	}
    	DefaultButton.setVisible(true);
    	DefaultButton.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
            {
        		tfWEB_URL.setText(ThesaurusWindow.thesaurusConfig.Default_WebAddress);
            }
         });
    	pWEB_URL.add(DefaultButton);
    	
    	gbc.insets = new Insets(0, 5, 0, 0);
    	gbl.setConstraints(pWEB_URL, gbc);
    	controlPanel.add(pWEB_URL);
    	
    	// SearchLineCompareMode заголовок
    	JLabel lSLCM = new JLabel(TxtRes.GetLocalText("avttrue.thesaurus.7", "Search mode in local thesaurus") + ":");
    	gbc.insets = new Insets(0, 5, 0, 0);
    	gbl.setConstraints(lSLCM, gbc);
    	controlPanel.add(lSLCM);
    	
    	// SearchLineCompareMode
    	cbSLCM = new JComboBox();
    	cbSLCM.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
    	cbSLCM.setModel(new DefaultComboBoxModel());
    	((DefaultComboBoxModel) cbSLCM.getModel()).addElement(TxtRes.GetLocalText("avttrue.thesaurus.13", "Search in start line"));
    	((DefaultComboBoxModel) cbSLCM.getModel()).addElement(TxtRes.GetLocalText("avttrue.thesaurus.14", "Search in any place line"));
    	cbSLCM.setSelectedIndex(ThesaurusWindow.thesaurusConfig.SearchLineCompareMode);
    	cbSLCM.setEditable(false);
    	gbc.insets = new Insets(0, 5, 0, 0);
    	gbl.setConstraints(cbSLCM, gbc);
    	controlPanel.add(cbSLCM);
    	
    	// DuplicateAddressInChat
    	cbDuplicateAddressInChat = new JCheckBox(TxtRes.GetLocalText("avttrue.thesaurus.16", "Duplicate address in the personal chat"));
    	cbDuplicateAddressInChat.setHorizontalTextPosition(JCheckBox.RIGHT);
    	gbc.insets = new Insets(0, 5, 0, 0);
    	gbl.setConstraints(cbDuplicateAddressInChat, gbc);
    	cbDuplicateAddressInChat.setSelected(ThesaurusWindow.thesaurusConfig.DuplicateAddressInPersonalChat);
    	controlPanel.add(cbDuplicateAddressInChat);
    	
    	// ShowEntityesNames
    	cbShowEntityesNames = new JCheckBox(TxtRes.GetLocalText("avttrue.thesaurus.19", "Load entityes names"));
    	cbShowEntityesNames.setHorizontalTextPosition(JCheckBox.RIGHT);
    	gbc.insets = new Insets(0, 5, 0, 0);
    	gbl.setConstraints(cbShowEntityesNames, gbc);
    	cbShowEntityesNames.setSelected(ThesaurusWindow.thesaurusConfig.ShowEntityesNames);
    	controlPanel.add(cbShowEntityesNames);
    	
    	// ShowBiomesNames
    	cbShowBiomesNames = new JCheckBox(TxtRes.GetLocalText("avttrue.thesaurus.8", "Load biomes names"));
    	cbShowBiomesNames.setHorizontalTextPosition(JCheckBox.RIGHT);
    	gbc.insets = new Insets(0, 5, 0, 0);
    	gbl.setConstraints(cbShowBiomesNames, gbc);
    	cbShowBiomesNames.setSelected(ThesaurusWindow.thesaurusConfig.ShowBiomesNames);
    	controlPanel.add(cbShowBiomesNames);
    	
    	//ShowBlocksNames
    	cbShowBlocksNames = new JCheckBox(TxtRes.GetLocalText("avttrue.thesaurus.9", "Load blocks names"));
    	cbShowBlocksNames.setHorizontalTextPosition(JCheckBox.RIGHT);
    	gbc.insets = new Insets(0, 5, 0, 0);
    	gbl.setConstraints(cbShowBlocksNames, gbc);
    	cbShowBlocksNames.setSelected(ThesaurusWindow.thesaurusConfig.ShowBlocksNames);
    	controlPanel.add(cbShowBlocksNames);
    	
    	//ShowItemsNames
    	cbShowItemsNames = new JCheckBox(TxtRes.GetLocalText("avttrue.thesaurus.10", "Load items names"));
    	cbShowItemsNames.setHorizontalTextPosition(JCheckBox.RIGHT);
    	gbc.insets = new Insets(0, 5, 0, 0);
    	gbl.setConstraints(cbShowItemsNames, gbc);
    	cbShowItemsNames.setSelected(ThesaurusWindow.thesaurusConfig.ShowItemsNames);
    	controlPanel.add(cbShowItemsNames);
    	
    	//ShowEnchantmentsNames
    	cbShowEnchantmentsNames = new JCheckBox(TxtRes.GetLocalText("avttrue.thesaurus.11", "Load enchantments names"));
    	cbShowEnchantmentsNames.setHorizontalTextPosition(JCheckBox.RIGHT);
    	gbc.insets = new Insets(0, 5, 0, 0);
    	gbl.setConstraints(cbShowEnchantmentsNames, gbc);
    	cbShowEnchantmentsNames.setSelected(ThesaurusWindow.thesaurusConfig.ShowEnchantmentsNames);
    	controlPanel.add(cbShowEnchantmentsNames);
    	
    	// содержание панели с кнопками
    	// Accept
    	JButton AcceptButton = new JButton(TxtRes.GetLocalText("avttrue.thesaurus.12", "Accept"));
    	AcceptButton.setSize(150, 60);
    	if(ThesaurusWindow.acceptButtonIcon != null)
    	{
    		AcceptButton.setIcon(new ImageIcon(ThesaurusWindow.acceptButtonIcon));
    	}
    	
   		AcceptButton.setVisible(true);
    	AcceptButton.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
            {
        		AcceptButtonClick();
            }
         });
    	
    	// CANCEL
    	JButton CancelButton = new JButton(TxtRes.GetLocalText("avttrue.thesaurus.3", "Cancel"));
    	CancelButton.setSize(150, 60);
    	if(ThesaurusWindow.cancelButtonIcon != null)
    	{
    		CancelButton.setIcon(new ImageIcon(ThesaurusWindow.cancelButtonIcon));
    	}
    	CancelButton.setVisible(true);
    	CancelButton.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
            {
        		SettingsFrame.dispose();
            }
         });
    	buttonPane.add(CancelButton);
        buttonPane.add(AcceptButton);
        SettingsFrame.getContentPane().add(buttonPane, BorderLayout.SOUTH);
        SettingsFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);
    	SettingsFrame.setVisible(true);
    }
    
    // Нажали ОК кнопку
    private static void AcceptButtonClick()
    {
    	String wa = new String(tfWEB_URL.getText().getBytes(), 
								Charset.forName("UTF-8"));
    	ThesaurusWindow.thesaurusConfig.WebAddress = wa;
    	ThesaurusWindow.thesaurusConfig.SearchLineCompareMode = cbSLCM.getSelectedIndex();
    	ThesaurusWindow.thesaurusConfig.DuplicateAddressInPersonalChat = cbDuplicateAddressInChat.isSelected();
    	ThesaurusWindow.thesaurusConfig.ShowBiomesNames = cbShowBiomesNames.isSelected();
    	ThesaurusWindow.thesaurusConfig.ShowBlocksNames = cbShowBlocksNames.isSelected();
    	ThesaurusWindow.thesaurusConfig.ShowEnchantmentsNames = cbShowEnchantmentsNames.isSelected();
    	ThesaurusWindow.thesaurusConfig.ShowItemsNames = cbShowItemsNames.isSelected();
    	ThesaurusWindow.thesaurusConfig.ShowEntityesNames = cbShowEntityesNames.isSelected();
    	ThesaurusWindow.SaveConfig();
    	SettingsFrame.dispose();
    	ThesaurusWindow.LoadContentToTextList(ThesaurusWindow.GetNamesList());
    }
}
