package avttrue.informator.Thesaurus;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import avttrue.informator.Informator;
import avttrue.informator.Tools.Functions;
import avttrue.informator.Tools.TxtRes;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.NonNullList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import scala.swing.event.WindowEvent;

public class ThesaurusWindow 
{
	static JDialog mainFrame;
	static JList textList;
	static JTextField searchLine;
	static boolean searchLineWasClocked = false;
	static JButton searchButton;
	static JButton helpButton;
	static JButton menuButton;
		
	// графические ресурсы
	static Image cancelButtonIcon;
	static Image acceptButtonIcon;
	static Image searchButtonIcon;
	static Image setupButtonIcon;
	static Image defaultButtonIcon;
	static Image helpButtonIcon;
	static Image menuButtonIcon;
	
	// настройки
	static ThesaurusConfig thesaurusConfig;
	static String configFilePath = "";
	
	public static void CreateMainWindow()
    {
		thesaurusConfig = new ThesaurusConfig();
		String cfgfile = Informator.configFile.getConfigFile().getAbsolutePath();
		configFilePath = cfgfile.substring(0, cfgfile.lastIndexOf(File.separator)) + 
						File.separator + Informator.THESAURUSCONFIGFILENAME;
		LoadConfig();
		LoadResources();
		JDialog.setDefaultLookAndFeelDecorated(true);
    	mainFrame = new JDialog();
    	mainFrame.setModal(true);
    	mainFrame.setAlwaysOnTop(true);
    	mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    	mainFrame.addWindowListener(new WindowAdapter() 
    	{
    		public void windowClosed(WindowEvent e) 
            {
    			mainFrame.dispose();
            }
        });
    	
    	mainFrame.setTitle(TxtRes.GetLocalText("avttrue.thesaurus.1", "Thesaurus"));
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	int sizeWidth = 640;
    	int sizeHeight = 480;
    	mainFrame.setBounds(0, 0, sizeWidth, sizeHeight);
    	mainFrame.setLocationRelativeTo(null);
    	
    	JPanel buttonPane = new JPanel();
    	buttonPane.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createRaisedBevelBorder(),
    			BorderFactory.createLoweredBevelBorder()));
    	
    	// панель поиска
    	JPanel searchPane = new JPanel();
    	searchPane.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createLoweredBevelBorder(),
				BorderFactory.createRaisedBevelBorder()));
    	// строка поиска
    	searchLine = new JTextField(40);
    	searchLine.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createRaisedBevelBorder(),
				BorderFactory.createLoweredBevelBorder()));
    	DocumentListener dl = new DocumentListener() 
        {
            public void insertUpdate(DocumentEvent event) 
            {
                
            	String s = "";
            	try 
            	{
					s = event.getDocument().getText(0, event.getDocument().getLength());
					SearchLineTextChanged(s);
				} 
            	catch (BadLocationException e){} 
            }
 
            public void removeUpdate(DocumentEvent event) 
            {
            	if(searchLineWasClocked)
            	{
            		searchLineWasClocked = false;
            		return;
            	}
            	String s = "";
            	try 
            	{
					s = event.getDocument().getText(0, event.getDocument().getLength());
					SearchLineTextChanged(s);
				} 
            	catch (BadLocationException e){}
            }
 
            public void changedUpdate(DocumentEvent event) 
            {
            	String s = "";
            	try 
            	{
					s = event.getDocument().getText(0, event.getDocument().getLength());
					SearchLineTextChanged(s);
				} 
            	catch (BadLocationException e) {}
             }
        };
        searchLine.getDocument().addDocumentListener(dl);
        searchPane.add(searchLine);
        // кнопка вызова меню истории поиска
        menuButton = new JButton();
        menuButton.setToolTipText(TxtRes.GetLocalText("avttrue.thesaurus.20", 
        											"Search history"));
        if(menuButtonIcon != null)
    	{
        	menuButton.setIcon(new ImageIcon(menuButtonIcon));
    	}
        menuButton.addActionListener(menuButtonListener);
        menuButton.setVisible(true);
        searchPane.add(menuButton);
        
    	// Панель со списком
    	//Список 
    	textList = new JList();
    	textList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	textList.setSelectedIndex(0);
    	ListSelectionModel selModel = textList.getSelectionModel();
    	textList.setModel(new DefaultListModel());
    	
    	// добавляем элементы в список
    	LoadContentToTextList(GetNamesList());
		
		// выбор элемента списка
    	selModel.addListSelectionListener(new ListSelectionListener() 
    	{
    		@Override
			public void valueChanged(ListSelectionEvent arg) 
			{
    			
			}
    	});
    	// клик по элементу списка
    	textList.addMouseListener(new MouseAdapter() 
    	{
    	    public void mouseClicked(MouseEvent event) 
    	    {
    	    	JList list = (JList)event.getSource();
    	    	//if (event.getClickCount() == 2) 
    	        {
    	        	searchLineWasClocked = true;
    	        	int index = list.locationToIndex(event.getPoint());
    	        	textList.setSelectedIndex(index);
    	        	searchLine.setText((String)textList.getSelectedValue());
    	        	textList.ensureIndexIsVisible(index);
    	        } 
    	    }
    	});
    	
    	textList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	JScrollPane scrollTextPane = new JScrollPane(textList);
    	scrollTextPane.setBorder(BorderFactory.createCompoundBorder(
    			BorderFactory.createRaisedBevelBorder(),
    			BorderFactory.createLoweredBevelBorder()));
    	scrollTextPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    	scrollTextPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    	
    	// содержание панели с кнопками
    	// кнопка Отмена
    	JButton closeButton = new JButton(TxtRes.GetLocalText("avttrue.thesaurus.3", "Close"));
    	closeButton.setToolTipText("<Esc>");
    	closeButton.registerKeyboardAction(cancelListener,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
    	if(cancelButtonIcon != null)
    	{
    		closeButton.setIcon(new ImageIcon(cancelButtonIcon));
    	}
    	closeButton.addActionListener(cancelListener); 
        closeButton.setVisible(true);
    	
        // кнопка искать
    	searchButton = new JButton(TxtRes.GetLocalText("avttrue.thesaurus.4", "Search"));
    	searchButton.setToolTipText("<Enter>");
    	searchButton.registerKeyboardAction(searchListener,
                						KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                						JComponent.WHEN_IN_FOCUSED_WINDOW);
    	if(searchButtonIcon != null)
    	{
    		searchButton.setIcon(new ImageIcon(searchButtonIcon));
    	}
    	searchButton.addActionListener(searchListener);
        searchButton.setVisible(true);
        searchButton.setEnabled(false);
    	
     // кнопка настройка
    	JButton setupButton = new JButton(TxtRes.GetLocalText("avttrue.thesaurus.5", "Setup"));
    	if(setupButtonIcon != null)
    	{
    		setupButton.setIcon(new ImageIcon(setupButtonIcon));
    	}
    	setupButton.addActionListener(setupListener); 
    	setupButton.setVisible(true);
    	    	
       // кнопка помощь
       	helpButton = new JButton();
       	if(helpButtonIcon != null)
       	{
       		helpButton.setIcon(new ImageIcon(helpButtonIcon));
       	}
       	helpButton.addActionListener(helpListener); 
       	helpButton.setVisible(true);
    	
    	buttonPane.add(closeButton);
    	buttonPane.add(searchButton);
    	buttonPane.add(setupButton);
    	buttonPane.add(helpButton);
    	mainFrame.getContentPane().add(buttonPane, BorderLayout.SOUTH);
    	mainFrame.getContentPane().add(searchPane, BorderLayout.NORTH);
    	mainFrame.getContentPane().add(scrollTextPane, BorderLayout.CENTER);
    	mainFrame.setVisible(true);
    }
	
	// нажатие кнопки Отмена
	static ActionListener cancelListener = new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
        {
			mainFrame.dispose();
        }
    };
    
    // нажатие кнопки искать
    static ActionListener searchListener = new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
        {
			String sl = new String(searchLine.getText().getBytes(), 
									Charset.forName("UTF-8"));
			thesaurusConfig.SearchHistory.remove(sl);
			thesaurusConfig.SearchHistory.add(sl);
			while(thesaurusConfig.SearchHistory.size() > thesaurusConfig.SearchHistorySize)
			{
				thesaurusConfig.SearchHistory.remove(0);
			}
			SaveConfig();
			
			String webAddress = thesaurusConfig.WebAddress + searchLine.getText();
			mainFrame.dispose();
			Functions.openWebLink(webAddress, thesaurusConfig.DuplicateAddressInPersonalChat);
        }
    };
 // нажатие кнопки Настройка
    static ActionListener setupListener = new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
        {
			ThesaurusSettingsWindow.CreateSettingsWindow();
        }
    };
 // нажатие кнопки история поиска
 	static ActionListener menuButtonListener = new ActionListener() 
 	{
 		public void actionPerformed(ActionEvent e) 
         {
 			JPopupMenu popupMenu = new JPopupMenu();
 			Iterator<String> iterator = thesaurusConfig.SearchHistory.iterator();
 			while (iterator.hasNext())
 			{
 				JMenuItem subItem = new JMenuItem(iterator.next());
 				subItem.setPreferredSize(new Dimension(searchLine.getWidth(), 
							subItem.getPreferredSize().height));
 				subItem.addActionListener(new ActionListener() 
 		    	{
 		    	    @Override
 					public void actionPerformed(ActionEvent e) 
 		    	    {
 		    	    	textList.setSelectedIndex(0);
 		    	    	searchLine.setText(((JMenuItem)e.getSource()).getText());
 		    	    }
 		    	});
 				popupMenu.add(subItem);
 			}
 			
 			popupMenu.show(searchLine, 0, searchLine.getHeight());
         }
     };
    
 // нажатие кнопки Помощь
    static ActionListener helpListener = new ActionListener() 
	{
		public void actionPerformed(ActionEvent e) 
        {
			JPopupMenu popup = new JPopupMenu();
			JMenuItem aboutItem = new JMenuItem(TxtRes.GetLocalText("avttrue.thesaurus.17", "About..."));
			aboutItem.addActionListener(new ActionListener() 
	    	{
	    	    @Override
				public void actionPerformed(ActionEvent e) 
	    	    {
	    	    	JOptionPane.showMessageDialog(mainFrame, 
        					TxtRes.GetLocalText("avttrue.megachat.29", "Welcome to russian Minecraft community") + 
        						"\nhttp://www.minecrafting.ru\n\n" + 
        						Informator.MODNAME + " " + Informator.MODVER,
        					TxtRes.GetLocalText("avttrue.thesaurus.17", "About..."), 
        					JOptionPane.INFORMATION_MESSAGE);					
				}
	    	});
			popup.add(aboutItem);
			JMenuItem linkItem = new JMenuItem(TxtRes.GetLocalText("avttrue.thesaurus.18", "Visit www.minecrafting.ru"));
			linkItem.addActionListener(new ActionListener() 
	    	{
	    	    @Override
				public void actionPerformed(ActionEvent e) 
	    	    {
	    	    	Functions.openWebLink(Informator.MINECRAFTING_URL, false);
				}
	    	});
			popup.add(linkItem);
			
			popup.show(helpButton, 0, helpButton.getHeight());
        }
    };
    
// редактирование строки поиска
	private static void SearchLineTextChanged(String s)
	{
		String sl = s.toLowerCase();
		searchButton.setEnabled(!sl.isEmpty());
		if(textList.getSelectedIndex() > 0)
		{
			String tl = ((String)textList.getSelectedValue()).toLowerCase();
			if(sl.equals(tl))
			{
				return;
			}
		}
		
		if(sl.isEmpty())
		{
			textList.setSelectedIndex(0);
			return;
		}
		
		for (int i = 0;  i < textList.getModel().getSize(); i++)
		{
			String ctl = ((String) ((DefaultListModel) textList.getModel()).get(i)).toLowerCase();
			
			if(	(thesaurusConfig.SearchLineCompareMode == 0 && ctl.startsWith(sl)) || 
				(thesaurusConfig.SearchLineCompareMode == 1 && ctl.contains(sl)) )
			{
				textList.setSelectedIndex(i);
				textList.ensureIndexIsVisible(textList.getSelectedIndex());
				return;
			}
		}
		textList.setSelectedIndex(0);
	}
	
// загрузка графических ресурсов, должны находиться в джарнике рядом с классом SpravkaWindow
	static void LoadResources()
	{
		try
		{
			cancelButtonIcon = Toolkit.getDefaultToolkit().getImage(ThesaurusWindow.class.getResource("cancel.png"));
			searchButtonIcon = Toolkit.getDefaultToolkit().getImage(ThesaurusWindow.class.getResource("find.png"));
			setupButtonIcon = Toolkit.getDefaultToolkit().getImage(ThesaurusWindow.class.getResource("setup.png"));
			acceptButtonIcon = Toolkit.getDefaultToolkit().getImage(ThesaurusWindow.class.getResource("yes.png"));
			defaultButtonIcon = Toolkit.getDefaultToolkit().getImage(ThesaurusWindow.class.getResource("undo.png"));
			helpButtonIcon = Toolkit.getDefaultToolkit().getImage(ThesaurusWindow.class.getResource("help.png"));
			menuButtonIcon = Toolkit.getDefaultToolkit().getImage(ThesaurusWindow.class.getResource("down_black.png"));
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.out.println("\nError at loading resources!");
		}		
	}

// возвращает список всех терминов в игре
	static List<String> GetNamesList()
	{
		NonNullList<ItemStack> items = NonNullList.<ItemStack>func_191196_a();
		//LinkedList<ItemStack> items = new NonNullList<ItemStack>();//new NonNullList<ItemStack>();
		List<String> itemNames = new ArrayList<String>();
		if(thesaurusConfig.ShowItemsNames)
		{
			for (Item item : Item.REGISTRY)
			{
				if (item != null)// && item.getCreativeTab() != null)
				{
					item.getSubItems(item, (CreativeTabs)null, items);
				}
			}
		}
	    	
		if(thesaurusConfig.ShowEnchantmentsNames)
		{
			for (Enchantment enchantment : Enchantment.REGISTRY)
			{
				if (enchantment != null && enchantment.type != null)
				{
					Items.ENCHANTED_BOOK.getAll(enchantment, items);
				}
			}
		}
	    	
		try
		{
			Iterator<ItemStack> itemsIterator = items.iterator();
			while (itemsIterator.hasNext())
			{
				ItemStack is = itemsIterator.next();
				String s = is.getTooltip(Minecraft.getMinecraft().thePlayer, false).get(0);
				s = TxtRes.RemoveFormat(s).trim();
				itemNames.remove(s);
				itemNames.add(s);	
			}
		}
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
		
		if(thesaurusConfig.ShowBlocksNames)
		{
			for (Block block : Block.REGISTRY)
			{
				if (block != null)
				{
					String ln = TxtRes.RemoveFormat(block.getLocalizedName()).trim();
					itemNames.remove(ln);
					itemNames.add(ln);
				}
			}
		}
		
		if(thesaurusConfig.ShowBiomesNames)
		{
			for (Biome biome : Biome.REGISTRY)
			{
				if (biome != null)
				{
					String ln = TxtRes.RemoveFormat(biome.getBiomeName()).trim();
					int ind = itemNames.indexOf(ln);
					itemNames.remove(ln);
					itemNames.add(ln);
				}
			}
		}
		if(thesaurusConfig.ShowEntityesNames)
		{
			
			for (ResourceLocation entity : EntityList.getEntityNameList())
			{
				if (entity != null)
				{
					String entStr = entity.toString();
					String resKey = "entity." + entStr + ".name";
					String ln = TxtRes.GetLocalText(resKey, entStr).trim();
					itemNames.remove(ln);
					itemNames.add(ln);
				}
			}
		}
		
	    System.out.println("\nThesaurus size: " + itemNames.size());
	    
	    itemNames.sort(null);
	    return itemNames;
	}	
	
	// загружает текст в TextList
	public static void LoadContentToTextList(List<String> sList)
	{
		((DefaultListModel)textList.getModel()).clear();
		
		if (sList == null)
    		return;
    	
    	Iterator<String> iterator = sList.iterator();
		while (iterator.hasNext())
		{
			String s = iterator.next();
			((DefaultListModel)textList.getModel()).addElement(s);
		}
		mainFrame.setTitle(TxtRes.GetLocalText("avttrue.thesaurus.1", "Thesaurus") + " " +
				String.format(TxtRes.GetLocalText("avttrue.thesaurus.2", "Thesaurus size: %1$s therms"), 
						sList.size()));
	}
	
// загрузить настройки	
	private static void LoadConfig()
	{
		File file = new File(configFilePath);
		if (! file.exists()) 
		{
			System.out.println("\nThesaurus Config file not found: \"" + configFilePath + "\"");
			return;
		}
		
		Gson gson = new Gson();
		InputStream inputstream = null;
		try
		{
			inputstream = new FileInputStream(configFilePath);
			String config = new BufferedReader(new InputStreamReader(inputstream))
                    		.lines().collect(Collectors.joining("\n"));
			thesaurusConfig = gson.fromJson(config, ThesaurusConfig.class);
			inputstream.close();
			System.out.println("\nThesaurus Config was readed: \"" + configFilePath + "\"");
		}
		catch (Exception e) 
		{
	    	System.out.println(e.getMessage());
			e.printStackTrace();
		}
		finally 
        {
            IOUtils.closeQuietly(inputstream);
        }
	}
// сохранить настройки	
	public static void SaveConfig()
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String sGson = gson.toJson(thesaurusConfig);
		PrintWriter outputstream = null;
		try 
		{
			outputstream = new PrintWriter(new FileWriter(configFilePath));
			outputstream.write(sGson);
			outputstream.close();
			System.out.println("\nThesaurus Config was writed: \"" + configFilePath + "\"");
		} 
		catch (Exception e) 
		{
			if (outputstream != null) outputstream.close();
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
