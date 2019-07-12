package avttrue.informator.Thesaurus;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ThesaurusConfig 
{
	// история поиска
	public List<String> SearchHistory = new ArrayList<String>();
	// размер истории поиска
	public int SearchHistorySize = 20;
	// УРЛ для поиска
	public String WebAddress = "http://minecraft-ru.gamepedia.com/index.php?title=";
	// УРЛ для поиска поумолчанию (не редактируемый)
	public String Default_WebAddress = "http://minecraft-ru.gamepedia.com/index.php?title=";
	// способ сравнения строки поиска 
	// (0 - совпадают с начала строки, 1 - совпадают хоть где-то)
	public int SearchLineCompareMode = 1;
	// загружать названия биомов
	public boolean ShowBiomesNames = true;
	// загружать названия блоков
	public boolean ShowBlocksNames = true;
	// загружать названия предметов
	public boolean ShowItemsNames = true;
	// загружать названия зачарований
	public boolean ShowEnchantmentsNames = true;
	// загружать имена сущностей
	public boolean ShowEntityesNames = true;
	// дублировать адрес в личный чат
	public boolean DuplicateAddressInPersonalChat = true;
}
