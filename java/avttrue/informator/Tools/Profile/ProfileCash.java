package avttrue.informator.Tools.Profile;

// используется в локальном кэшировании профилей как базовая структура
public class ProfileCash 
{
	public String Uuid = null;
	public String Name = null;
	public long LastCheck = 0;
	
	public ProfileCash(String uuid, String name, long lastcheck)
	{
		Uuid = uuid;
		Name = name;
		LastCheck = lastcheck;
	}
}
