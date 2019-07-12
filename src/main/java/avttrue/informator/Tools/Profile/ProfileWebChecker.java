package avttrue.informator.Tools.Profile;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/*
 * Этот класс извлекает данные игрока (имя) по UUID с сайта Можангов 
 * https://api.mojang.com/user/profiles/<uuid>/names
 * 
 */

public class ProfileWebChecker extends Thread
{
	private String ProfilesURL = "https://api.mojang.com/user/profiles/%1$s/names";
	private volatile String Uuid = null;
	private ProfileCashList Cash = null;
	private int CheckInterval = 500; // интервал опроса
	
	public ProfileWebChecker(ProfileCashList cash)
	{
		Cash = cash;
	}
	
	public synchronized void SetUUID(String uuid)
	{
		Uuid = uuid;
	}
	
	public synchronized String GetUUID()
	{
		return Uuid;
	}
	
	public void run() 
    {
		while(true)
		{
			ReadDataFromWeb();
			try 
			{
				sleep(CheckInterval);
			} 
			catch (Exception e) 
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
    }
	
	// TODO лезем на сайт и читаем данные профиля
	private void ReadDataFromWeb()
	{
		String uuid = GetUUID();
		
		if (uuid == null || uuid.isEmpty()) 
		{
			return;
		}
		
		InputStream inputstream = null;
        try 
        {
        	String Url = String.format(ProfilesURL, uuid.replace("-", ""));
        	System.out.println("\nRead gamer name from: " + Url);
        	inputstream = new URL(Url).openStream();
        } 
        catch (Exception e) 
        {
        	System.out.println(e.getMessage());
            e.printStackTrace();
        } 
        
        try 
        {
        	if (inputstream != null)
        	{
        		String Profile = IOUtils.toString(inputstream, StandardCharsets.UTF_8);
        		Cash.AddProfile(uuid, GetNameFromProfile(Profile));
        		Cash.WriteToFile();
        	}
        } 
        catch (Exception e) 
        {
        	// не смогли прочесть с сайта по УРЛу, скорее всего UUID фейковый
        	//System.out.println(e.getMessage());
            //e.printStackTrace();
        } 
        finally 
        {
            IOUtils.closeQuietly(inputstream);
            SetUUID(null);
        }
	}
	
	// TODO расшифровывание имени из json профиля
	private String GetNameFromProfile(String profile)
	{
		if (profile == null || profile.isEmpty()) return null;
		
		String Name = null;
		try
		{
			Gson gson = new Gson(); //https://github.com/google/gson
			Type type = new TypeToken<ArrayList<ProfileWebData>>(){}.getType();
			List<ProfileWebData> listPWD = gson.fromJson(profile, type);
		
			long changedToAt = -1;
			Iterator<ProfileWebData> iterator = listPWD.iterator();
    	    while (iterator.hasNext())
        	{
    	    	ProfileWebData pwd = iterator.next();
    	    	if (changedToAt < pwd.changedToAt) // берём с самой свежей датой
    	    		Name = pwd.name;
        	}
    	    System.out.println("\nGamer name: " + Name);
    	    return Name;
		}
		catch (Exception e) 
		{
	    	System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}

