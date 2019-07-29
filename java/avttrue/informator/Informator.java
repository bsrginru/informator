/*
 * 
 * На этом моде я изучаю джаву и форж
 * 
 */

package avttrue.informator;

import java.util.Date;

import avttrue.informator.Events.OnClientConnectedToServer;
import avttrue.informator.Events.OnClientTick;
import avttrue.informator.Events.OnKeyInput;
import avttrue.informator.Events.OnPlayerTick;
import avttrue.informator.Events.OnRenderGameOverlay;
import avttrue.informator.Events.OnRenderWorldLastEvent;
import avttrue.informator.Tools.TxtRes;
import avttrue.informator.Tools.VersionChecker;
import avttrue.informator.Tools.Profile.ProfileCashList;
import avttrue.informator.Tools.Profile.ProfileWebChecker;
import avttrue.informator.Tools.Surface.OverlayRenderer;
import avttrue.informator.Tools.Surface.SurfaceChecker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = Informator.MODID, 
	name = Informator.MODNAME, 
	version =Informator.MODVER,
	useMetadata = true, 
	guiFactory = "avttrue.informator.GUIFactory")

public class Informator
{
	public static final String MODID = "avttrue_informator";
    public static final String MODNAME = "AVTTRUE:Informator";
    public static final String MODVER = "[1.12] 0.9.0";
    
    public static final String INFORMATORDOWNLOAD_URL = "https://goo.gl/vSq65V";
    public static final String MINECRAFTING_URL = "http://www.minecrafting.ru";
    public static final String INFORVERSIONCHECKER_URL = "http://avttrue.ucoz.net/version_file";
    public static final String PROFILEFILENAME = "InformatorProfilesCash.json";
    
	
    public static Configuration configFile;
    
    public static VersionChecker versionChecker;
    public static boolean haveWarnedVersionOutOfDate = false;
    
    @Mod.Instance(value = Informator.MODID)
    public static Informator instance;
    
    // используется для контроля игрового времени
    public static long worldTime = -1;
    
    // используется в рассчётах скорости
    public static double oldPayerX = 0;
    public static double oldPayerY = 0;
    public static double oldPayerZ = 0;
    public static long lastXYZTick = -1;
    public static double Speed = 0;
    
    // испольуется в показе (с задержкой) информации о мобе
    public static EntityLivingBase lastmob = null;
    public static Date lastmobtime = new Date();
    
    //Global
    public static boolean Global_HideInDebugMode;
    public static boolean Global_ShowPanel;
    public static int Global_DistanceView;
    public static boolean Gobal_ON = true;
    
    //HeldItemBar
    public static boolean HeldItemDetails_Show;
    public static int HeldItemDetails_xPos;
    public static int HeldItemDetails_yPos;
    public static int HeldItemDetails_DamageAlarm;
    public static String HeldItemDetails_alignMode;
    
    //InfoBlockBar
    public static boolean InfoBlockBar_Show;
    public static boolean InfoBlockBar_ShowIcons;
    public static int InfoBlockBar_xPos;
    public static int InfoBlockBar_yPos;
    public static String InfoBlockBar_alignMode;
    public static boolean InfoBlockBar_ShowName;
    
    //TimeBar
    public static boolean TimeBar_Show;
    public static int TimeBar_xPos;
    public static int TimeBar_yPos;
    public static boolean TimeBarMoon_Show;
    public static boolean TimeBarWeather_Show;
    public static String TimeBar_alignMode;
    
    //SpeedBar
    public static boolean SpeedBar_Show;
    public static int SpeedBar_xPos;
    public static int SpeedBar_yPos;
    
    // EnchantBar
    public static int EnchantBar_yPos;
    public static boolean EnchantBar_Show;
    public static boolean EnchantBar_ShowHands;
    public static boolean EnchantBar_ShowBody;
    
    // TargetMob Bar
    public static int TargetMobBar_WidthScreenPercentage;
    public static int TargetMobBar_yPos;
    public static int TargetMobBar_xPos;
    public static boolean TargetMobBar_Show;
    public static boolean TargetMobBar_DrawMobPortrait;
    public static boolean TargetMobBar_DrawBuffIcon;
    public static String TargetMobBar_alignMode;
    public static int TargetMobBar_ViewDelay;
    public static boolean TargetMobBar_SeachOwnerInWeb;
    public static int TargetMobBar_OwnerDataPeriod;
    
    //Light Level Indicator
    public static boolean LightLevelIndicatorShowChunkBorder; // показывать границы чанков
    public static boolean LightLevelIndicatorShowDigital; // показывать цифры
    public static boolean LightLevelIndicatorShowAllValues; // показывать и тёмное и светлое
    public static int LightLevelIndicatorRadius;      // радиус отрисовки
    
    // отображать в чате подобранный опыт
    public static int playertotalxp = -1;
    public static boolean PickupedXP_Show;
    
    // используется для кэширования результатов поиска игроков на сайте
    public volatile static ProfileCashList ProfileCashListFromWeb = new ProfileCashList(); 
    // поток проверки имён на api.mojang.com
    public static ProfileWebChecker PWC = null;
    public Thread ProfileWebCheckerThread = null;
    // поток проверки свойств поверхности
    public static SurfaceChecker surfaceChecker = null;   // поток
    public static boolean SurfaceCheckerIsActive = false; // включено
    public static OverlayRenderer overlayRenderer = null; // Отрисовка
   
    
    // TODO onFMLPreInitialization()
    @Mod.EventHandler
    public void onFMLPreInitialization(FMLPreInitializationEvent event)
    {
    	configFile = new Configuration(event.getSuggestedConfigurationFile());
    	syncAllConfig();
    	KeyBindings.Initialization();
    	ProfileCashListFromWeb.Initialization();
    	
    	MinecraftForge.EVENT_BUS.register(instance);
     }
    
    // TODO onFMLInitialization()
    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(new OnKeyInput());
    	MinecraftForge.EVENT_BUS.register(new OnRenderGameOverlay());
    	MinecraftForge.EVENT_BUS.register(new OnClientTick());
    	MinecraftForge.EVENT_BUS.register(new OnPlayerTick());
    	MinecraftForge.EVENT_BUS.register(new OnRenderWorldLastEvent());
    	MinecraftForge.EVENT_BUS.register(new OnClientConnectedToServer());
    }
    
    // TODO onFMLPostInitialization()
    @Mod.EventHandler
    public void onFMLPostInitialization(FMLPostInitializationEvent event) 
    {
    	// проверяем наличие обновлений
    	versionChecker = new VersionChecker();
    	Thread versionCheckThread = new Thread(versionChecker, "Version Check");
    	versionCheckThread.setPriority(Thread.MIN_PRIORITY);
    	versionCheckThread.start(); 
    	
    	// стартует поток проверки имён пользователей с сайта
    	PWC = new ProfileWebChecker(ProfileCashListFromWeb);
    	ProfileWebCheckerThread = new Thread(PWC, "Profile Web Checker");
    	ProfileWebCheckerThread.setPriority(Thread.MIN_PRIORITY);
    	ProfileWebCheckerThread.start(); 
    	
    	// стартует поток проверки поверхности
    	overlayRenderer = new OverlayRenderer();
    	surfaceChecker = new SurfaceChecker();
    	LaunchSurfaceChecker();
    	
    }
     
    // TODO onConfigChanged()
    @SubscribeEvent 
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) 
    {
        if(eventArgs.getModID().equals(this.MODID))
    	{
    		syncAllConfig();
    	}
        
        // приостанавливаю поток ProfileWebCheckerThread, если выключена опция SeachOwnerInWeb
        if(TargetMobBar_SeachOwnerInWeb)
        	ProfileWebCheckerThread.resume();
        else
        	ProfileWebCheckerThread.suspend();
        System.out.println("\nProfileWebCheckerThread: " + ProfileWebCheckerThread.getState());
    }
    
    // TODO syncAllConfig()
    public static void syncAllConfig()
    {
    	String gen_category = Configuration.CATEGORY_GENERAL;
    	// Global
    	Global_HideInDebugMode = configFile.getBoolean("* Global Hide In Debug Mode", gen_category, true, 
    			TxtRes.GetLocalText("avttrue.informator.11", "Hide In Debug Mode (F3)"));
    	Global_ShowPanel = configFile.getBoolean("* Global Show BG Panel", gen_category, true, 
    			TxtRes.GetLocalText("avttrue.informator.12","Show Background Panel"));
    	Global_DistanceView = configFile.getInt("* Global Distance of View", gen_category, 32, 1, 64, 
    			TxtRes.GetLocalText("avttrue.informator.13","Distance of View"));
    	
    	//HeldItemBar
    	HeldItemDetails_xPos =  configFile.getInt("Held Item Bar Custom X Position", gen_category, 0, 0, 9999, 
    			"Custom X position for HeldItemBar");
    	HeldItemDetails_yPos =  configFile.getInt("Held Item Bar Custom Y Position", gen_category, 54, 0, 9999, 
    			"Custom Y position for HeldItemBar");
    	HeldItemDetails_DamageAlarm =  configFile.getInt("Held Item Bar Damage Alarm", gen_category, 10, 1, 99, 
    			TxtRes.GetLocalText("avttrue.informator.50","Damage Alarm (in %) for Armor & Held Items"));
    	HeldItemDetails_Show = configFile.getBoolean("Held Item Show Bar", gen_category, true, 
    			"Show HeldItemBar");
    	HeldItemDetails_alignMode = configFile.getString(ConfigElement.HELDITEM_ALIGN_MODE.key(), 
														gen_category, "Default", 
														ConfigElement.HELDITEM_ALIGN_MODE.desc(),
														ConfigElement.HELDITEM_ALIGN_MODE.validStrings());
    	
    	//InfoBlockBar
    	InfoBlockBar_Show = configFile.getBoolean("InfoBlockBar Show Bar", gen_category, true, 
    			"Show InfoBlockBar");
    	InfoBlockBar_ShowName = configFile.getBoolean("InfoBlockBar Show Block Name", gen_category, true, 
    			"Show Block Name");
    	InfoBlockBar_ShowIcons = configFile.getBoolean("InfoBlockBar Show Icons", gen_category, true, 
    			"Show Icons");
    	InfoBlockBar_yPos = configFile.getInt("InfoBlockBar Custom Y Position", gen_category, 0, 0, 9999, 
    			"Custom Y position for InfoBlockBar");
    	InfoBlockBar_xPos = configFile.getInt("InfoBlockBar Custom X Position", gen_category, 0, 0, 9999, 
    			"Custom X position for InfoBlockBar");
    	InfoBlockBar_alignMode = configFile.getString(ConfigElement.INFOBLOCK_ALIGN_MODE.key(), 
													gen_category, "BottomRight", 
													ConfigElement.INFOBLOCK_ALIGN_MODE.desc(),
													ConfigElement.INFOBLOCK_ALIGN_MODE.validStrings());
    	
    	//TimeBar
    	TimeBar_Show = configFile.getBoolean("TimeBar Show Bar", gen_category, true, 
    			"Show TimeBar");
    	TimeBarMoon_Show = configFile.getBoolean("TimeBar Show Moon Bar", gen_category, true, 
    			"Show MoonBar");
    	TimeBarWeather_Show = configFile.getBoolean("TimeBar Show Weather Bar", gen_category, true, 
    			"Show WeatherBar");
    	TimeBar_yPos = configFile.getInt("TimeBar Custom Y Position", gen_category, 0, 0, 9999, 
    			"Custom Y position for TimeBar");
    	TimeBar_xPos = configFile.getInt("TimeBar Custom X Position", gen_category, 0, 0, 9999, 
    			"Custom X position for TimeBar");
    	TimeBar_alignMode = configFile.getString(ConfigElement.CLOCK_ALIGN_MODE.key(), 
												gen_category, "TopLeft", 
												ConfigElement.CLOCK_ALIGN_MODE.desc(),
												ConfigElement.CLOCK_ALIGN_MODE.validStrings());
    	
    	//SpeedBar
    	SpeedBar_Show = configFile.getBoolean("SpeedBar Show Bar", gen_category, true, 
    			"Show SpeedBar");
    	SpeedBar_yPos = configFile.getInt("SpeedBar Custom Y Position", gen_category, 36, 0, 9999, 
    			"Custom Y position for SpeedBar");
    	SpeedBar_xPos = configFile.getInt("SpeedBar Custom X Position", gen_category, 0, 0, 9999, 
    			"Custom X position for SpeedBar");
    	
    	//EnchantBar
    	EnchantBar_Show = configFile.getBoolean("EnchantBar Show Bar", gen_category, true, 
    			"Show EnchantBar");
    	EnchantBar_yPos = configFile.getInt("EnchantBar Custom Y Position", gen_category, 85, 0, 9999, 
    			"Custom Y position for EnchantBar");
    	EnchantBar_ShowHands = configFile.getBoolean("EnchantBar Show Hands", gen_category, true, 
    			TxtRes.GetLocalText("avttrue.informator.49","Show enchants for items in hands"));
    	EnchantBar_ShowBody = configFile.getBoolean("EnchantBar Show Body", gen_category, true, 
    			TxtRes.GetLocalText("avttrue.informator.48","Show enchants for items on body"));
    	
    	//TargetMobBar
    	TargetMobBar_WidthScreenPercentage = configFile.getInt("TargetMobBar Width Screen Percentage", gen_category, 30, 10, 100, 
    			TxtRes.GetLocalText("avttrue.informator.55","Width in percent of screen width for TargetMobBar"));
    	TargetMobBar_yPos = configFile.getInt("TargetMobBar Custom Y Position", gen_category, 0, 0, 9999, 
    			"Custom Y position for TargetMobBar");
    	TargetMobBar_xPos = configFile.getInt("TargetMobBar Custom X Position", gen_category, 0, 0, 9999, 
    			"Custom X position for TargetMobBar");
    	TargetMobBar_Show = configFile.getBoolean("TargetMobBar Show Bar", gen_category, true, 
    			"Show TargetMob Bar");
    	TargetMobBar_DrawMobPortrait = configFile.getBoolean("TargetMobBar Draw Mob Portrait", 
    			gen_category, true, "Draw Mob Portrait");
    	TargetMobBar_DrawBuffIcon = configFile.getBoolean("TargetMobBar Draw Buff Icons", 
    			gen_category, true, "Draw Buff Icons");
    	TargetMobBar_ViewDelay = configFile.getInt("TargetMobBar View Delay", gen_category, 3, 0, 600, 
    			TxtRes.GetLocalText("avttrue.informator.47","View Delay for TargetMobBar (sec)"));
    	TargetMobBar_alignMode = configFile.getString(ConfigElement.TARGETMOB_ALIGN_MODE.key(), 
				gen_category, "TopCenter", 
				ConfigElement.TARGETMOB_ALIGN_MODE.desc(),
				ConfigElement.TARGETMOB_ALIGN_MODE.validStrings());
    	TargetMobBar_SeachOwnerInWeb = configFile.getBoolean("TargetMobBar Searh Mob Owner on Web", gen_category, true, 
    			TxtRes.GetLocalText("avttrue.informator.40","Searh Mob Owner on Web"));
    	TargetMobBar_OwnerDataPeriod = configFile.getInt("TargetMobBar Owner Data Period", gen_category, 7, 0, 30, 
    			TxtRes.GetLocalText("avttrue.informator.51","Shelf life data about the owner (in days)"));
    	
    	//LightLevelIndicatorShowDigits
    	LightLevelIndicatorShowDigital = configFile.getBoolean("Light Level Indicator Show As Digital", gen_category, false, 
    			TxtRes.GetLocalText("avttrue.informator.54","Digital light level indicator"));
    	LightLevelIndicatorShowChunkBorder = configFile.getBoolean("Light Level Indicator Show Chunk Border", gen_category, true, 
    			TxtRes.GetLocalText("avttrue.informator.41","Show Chunk Border At Light Level Indication"));
    	LightLevelIndicatorShowAllValues = configFile.getBoolean("Light Level Indicator Show All Values", gen_category, true, 
    			TxtRes.GetLocalText("avttrue.informator.45","Show All Values For Light Level"));
    	LightLevelIndicatorRadius = configFile.getInt("Light Level Indicator Radius", gen_category, 2, 1, 16, 
    			TxtRes.GetLocalText("avttrue.informator.46","Radius in Chanks For Light Level Indication"));
    	
    	//PickupedXP_Show
    	PickupedXP_Show = configFile.getBoolean("Pickuped XP Show", gen_category, true, 
    			TxtRes.GetLocalText("avttrue.informator.42","Show Pickuped XP Information"));
    	
    	if(configFile.hasChanged())
         {
        	 configFile.save();
         }

       System.out.println("\nConfigFile: \"" + configFile.getConfigFile().getPath() + "\"");
    }

    // TODO LaunchSurfaceChecker
    public static void LaunchSurfaceChecker() 
    {
		for (int i = 0; i < 3; i++) 
		{
			if (surfaceChecker.isAlive()) return;
			try 
			{
				surfaceChecker.setPriority(Thread.MIN_PRIORITY);
				surfaceChecker.start();
			} 
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
				surfaceChecker = new SurfaceChecker();
			}
		}
	}
}