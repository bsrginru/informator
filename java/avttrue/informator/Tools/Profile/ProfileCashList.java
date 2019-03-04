package avttrue.informator.Tools.Profile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import avttrue.informator.Informator;

public class ProfileCashList 
{
	private List<ProfileCash> ProfileList = new ArrayList<ProfileCash>();
	private String FilePath = null;
	
	// инициализация
	public void Initialization()
	{
		String cfgfile = Informator.configFile.getConfigFile().getAbsolutePath();
		FilePath = cfgfile.substring(0, cfgfile.lastIndexOf(File.separator)) + 
						File.separator + Informator.PROFILEFILENAME;
		ReadFromFile();
		VerifyCheckTime(Informator.TargetMobBar_OwnerDataPeriod * 86400000);
		WriteToFile();
	}
	
	// поиск к кэше имени по UUID
	public String FindNameByUUID(String uuid)
	{
		try
		{
			Iterator<ProfileCash> iterator = ProfileList.iterator();
			while (iterator.hasNext())
			{
				ProfileCash pc = iterator.next();
				if(pc.Uuid.equals(uuid))
					return pc.Name;
			}
		}
		catch (Exception e) 
        {
        	System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        } 
	    return null;
	}
	
	// размер кэша
	public int GetSize()
	{
		return ProfileList.size();
	}
	
	// добавление записи
	public synchronized void AddProfile(String uuid, String name)
	{
		if (FindNameByUUID(uuid) == null)
		{
			if (name == null || name.isEmpty()) return;
			if (uuid == null || uuid.isEmpty()) return;
			
			Date date = new Date();
			ProfileList.add(new ProfileCash(uuid, name, date.getTime()));
		}
	}
	
	// запись кэша в файл
	public void WriteToFile()
	{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(ProfileList);
		PrintWriter outputstream = null;
		try 
		{
			outputstream = new PrintWriter(new FileWriter(FilePath));
			outputstream.write(json);
			outputstream.close();
			System.out.println("\nProfiles Cash was writed: \"" + FilePath + 
								"\"\nProfiles Cash size = " + String.valueOf(GetSize()));
		} 
		catch (Exception e) 
		{
			if (outputstream != null) outputstream.close();
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	// читаем кэш из файла
	public void ReadFromFile()
	{
		File file = new File(FilePath);
		if (! file.exists()) 
		{
			System.out.println("\nProfiles Cash file not found: \"" + FilePath + "\"");
			return;
		}
		
		Gson gson = new Gson();
		Type type = new TypeToken<ArrayList<ProfileCash>>(){}.getType();
		InputStream inputstream = null;
		try
		{
			inputstream = new FileInputStream(FilePath);
			String cash = new BufferedReader(new InputStreamReader(inputstream))
            		.lines().collect(Collectors.joining("\n"));
			ProfileList = gson.fromJson(cash, type);
			System.out.println("\nProfiles Cash was readed: \"" + FilePath + 
								"\"\nProfiles Cash size = " + String.valueOf(GetSize()));
			inputstream.close();
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
	
	// проверка данных кэша по дате
	public void VerifyCheckTime(long timedelta)
	{
		Date date = new Date();
		long time = date.getTime();
		Iterator<ProfileCash> iterator = ProfileList.iterator();
		while (iterator.hasNext())
    	{
			ProfileCash pc = iterator.next();
			if(time - pc.LastCheck > timedelta) // если устарело, то удаляем
			{
				System.out.println("\nProfile Cash data about gamer \"" + 
									pc.Name + "\" is stale and was removed.");
				iterator.remove();
			}
    	}
	}
}
