package avttrue.informator.Tools;

import java.nio.charset.Charset;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import net.minecraft.util.text.translation.I18n;

public class TxtRes 
{
	//private static final Logger LOGGER = LogManager.getLogger();

//
// Возвращает название зачарования по EID
//	
	public static String GetEnchantmentNameByEID(int EID)
	{
		switch(EID)
		{	
			//Armour
			case 0: return I18n.translateToLocal("enchantment.protect.all");
			case 1: return I18n.translateToLocal("enchantment.protect.fire");
			case 2: return I18n.translateToLocal("enchantment.protect.fall");
			case 3: return I18n.translateToLocal("enchantment.protect.explosion");	
			case 4: return I18n.translateToLocal("enchantment.protect.projectile");
			case 5: return I18n.translateToLocal("enchantment.oxygen");
			case 6: return I18n.translateToLocal("enchantment.waterWorker");
			case 7: return I18n.translateToLocal("enchantment.thorns");
			case 8: return I18n.translateToLocal("enchantment.waterWalker");
			case 9: return I18n.translateToLocal("enchantment.frostWalker");
			
			//Weapons
			case 16: return I18n.translateToLocal("enchantment.damage.all");
			case 17: return I18n.translateToLocal("enchantment.damage.undead");
			case 18: return I18n.translateToLocal("enchantment.damage.arthropods");
			case 19: return I18n.translateToLocal("enchantment.knockback");
			case 20: return I18n.translateToLocal("enchantment.fire");
			case 21: return I18n.translateToLocal("enchantment.lootBonus");
			
			//Tools
			case 32: return I18n.translateToLocal("enchantment.digging");
			case 33: return I18n.translateToLocal("enchantment.untouching");
			case 35: return I18n.translateToLocal("enchantment.lootBonusDigger");
				
			//Bows
			case 48: return I18n.translateToLocal("enchantment.arrowDamage");
			case 49: return I18n.translateToLocal("enchantment.arrowKnockback");
			case 50: return I18n.translateToLocal("enchantment.arrowFire");
			case 51: return I18n.translateToLocal("enchantment.arrowInfinite");	
			
			//Fishing Rods
			case 61: return I18n.translateToLocal("enchantment.lootBonusFishing");
			case 62: return I18n.translateToLocal("enchantment.fishingSpeed");
				
			//Common
			case 34: return I18n.translateToLocal("enchantment.durability");
			case 70: return I18n.translateToLocal("enchantment.mending");
		}
		//не найдено	
		return Integer.toString(EID);
	}
	
//
// преобразует арабскую цифру в латинскую
//
	public static String ArabToLatinNumber(int number)
	{
		switch(number)
		{
			case 1: return "I";
			case 2: return "II";
			case 3: return "III";
			case 4: return "IV";
			case 5: return "V";
			case 6: return "VI";
			case 7: return "VII";
			case 8: return "VIII";
			case 9: return "IX";
			case 10: return "X";
		}
		// прочее
		return Integer.toString(number);
	}

	//
	// получает ключ в файле локализации и дефолтное значение, а возвращает значение по ключу
	//	
	public static String GetLocalText(String key, String defvalue)
	{
		//LOGGER.info("GetLocalText: key="+key+"; defvalue="+defvalue);
		String s = I18n.translateToLocal(key);
		if (s.equals(key)) 
			return defvalue;
		else 
			return s;
	}
	
	//
	// возвращает название профессии селянина по ID
	//
	
	public static String GetVillagerProfession(int id_profession)
	{
		switch (id_profession)
        {
            case 0: return I18n.translateToLocal("avttrue.informator.21");
            case 1: return I18n.translateToLocal("avttrue.informator.22");
            case 2: return I18n.translateToLocal("avttrue.informator.23");
            case 3: return I18n.translateToLocal("avttrue.informator.24");
            case 4: return I18n.translateToLocal("avttrue.informator.25");            	
        }

        return "Profession " + id_profession;
	}
	
	public static String RemoveFormat(String s)
	{
		byte[] bytes = s.getBytes();
		String retValue = new String(bytes, Charset.forName("UTF-8"));
		//retValue = s.replaceAll("\u00A7[0-9a-fk-or]", "").replace("\n", "");
		retValue = s.replace("\u00A70", "")
					.replace("\u00A71", "")
					.replace("\u00A72", "")
					.replace("\u00A73", "")
					.replace("\u00A74", "")
					.replace("\u00A75", "")
					.replace("\u00A76", "")
					.replace("\u00A77", "")
					.replace("\u00A78", "")
					.replace("\u00A79", "")
					.replace("\u00A7a", "")
					.replace("\u00A7b", "")
					.replace("\u00A7c", "")
					.replace("\u00A7d", "")
					.replace("\u00A7e", "")
					.replace("\u00A7f", "")
					.replace("\u00A7k", "")
					.replace("\u00A7l", "")
					.replace("\u00A7m", "")
					.replace("\u00A7n", "")
					.replace("\u00A7o", "")
					.replace("\u00A7r", "")
					.replace("\n", "");
		//System.out.println("\n" + s + " | " + retValue);
		return retValue;
	}
}

