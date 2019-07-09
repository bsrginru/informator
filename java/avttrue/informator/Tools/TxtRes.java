package avttrue.informator.Tools;

import java.nio.charset.Charset;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import net.minecraft.client.resources.I18n;

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
			case 0: return I18n.format("enchantment.protect.all");
			case 1: return I18n.format("enchantment.protect.fire");
			case 2: return I18n.format("enchantment.protect.fall");
			case 3: return I18n.format("enchantment.protect.explosion");	
			case 4: return I18n.format("enchantment.protect.projectile");
			case 5: return I18n.format("enchantment.oxygen");
			case 6: return I18n.format("enchantment.waterWorker");
			case 7: return I18n.format("enchantment.thorns");
			case 8: return I18n.format("enchantment.waterWalker");
			case 9: return I18n.format("enchantment.frostWalker");
			
			//Weapons
			case 16: return I18n.format("enchantment.damage.all");
			case 17: return I18n.format("enchantment.damage.undead");
			case 18: return I18n.format("enchantment.damage.arthropods");
			case 19: return I18n.format("enchantment.knockback");
			case 20: return I18n.format("enchantment.fire");
			case 21: return I18n.format("enchantment.lootBonus");
			
			//Tools
			case 32: return I18n.format("enchantment.digging");
			case 33: return I18n.format("enchantment.untouching");
			case 35: return I18n.format("enchantment.lootBonusDigger");
				
			//Bows
			case 48: return I18n.format("enchantment.arrowDamage");
			case 49: return I18n.format("enchantment.arrowKnockback");
			case 50: return I18n.format("enchantment.arrowFire");
			case 51: return I18n.format("enchantment.arrowInfinite");	
			
			//Fishing Rods
			case 61: return I18n.format("enchantment.lootBonusFishing");
			case 62: return I18n.format("enchantment.fishingSpeed");
				
			//Common
			case 34: return I18n.format("enchantment.durability");
			case 70: return I18n.format("enchantment.mending");
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
		String s = I18n.format(key);
		if (s.equals(key)) 
			return defvalue;
		else 
			return s;
	}
	
	//
	// возвращает название профессии селянина по ID
	//
	
	public static String GetVillagerProfession(String id_profession)
	{
		if (id_profession == "unemployed") return I18n.format("avttrue.informator.57");
		else if (id_profession == "armorer") return I18n.format("avttrue.informator.24");
		else if (id_profession == "butcher") return I18n.format("avttrue.informator.25");
		else if (id_profession == "cartographer") return I18n.format("avttrue.informator.58");
		else if (id_profession == "cleric") return I18n.format("avttrue.informator.23");
		else if (id_profession == "farmer") return I18n.format("avttrue.informator.21");
		else if (id_profession == "fisherman") return I18n.format("avttrue.informator.59");
		else if (id_profession == "fletcher") return I18n.format("avttrue.informator.60");
		else if (id_profession == "leatherworker") return I18n.format("avttrue.informator.61");
		else if (id_profession == "librarian") return I18n.format("avttrue.informator.22");
		else if (id_profession == "mason") return I18n.format("avttrue.informator.62");
		else if (id_profession == "nitwit") return I18n.format("avttrue.informator.63");
		else if (id_profession == "shepherd") return I18n.format("avttrue.informator.64");
		else if (id_profession == "toolsmith") return I18n.format("avttrue.informator.65");
		else if (id_profession == "weaponsmith") return I18n.format("avttrue.informator.66");
		else if (id_profession == "home") return I18n.format("avttrue.informator.67");
		else if (id_profession == "meeting") return I18n.format("avttrue.informator.68");
        return I18n.format("avttrue.informator.69", id_profession); // Профессия %s
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

