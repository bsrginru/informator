package avttrue.informator.tools;

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

import avttrue.informator.config.ModSettings;

public class UsernamesCache
{
    private List<ProfileCash> profileList = new ArrayList<ProfileCash>();
    private String filePath = null;

    // инициализация
    public void initialization(final String modConfigFileName)
    {
        if (filePath != null) return; // однократная инициализация
        filePath = modConfigFileName.replaceAll(".toml", ".cashed_profiles.json");
        if (filePath == modConfigFileName) filePath += ".cashed_profiles.json"; // сменилось расширение файла с настройкми?
        filePath = "." + File.separator + "config" + File.separator + filePath;
        readFromFile();
        if (cleanObsoleteCache())
        {
            writeToFile();
        }
    }

    // поиск в кэше имени по UUID
    public String findNameByUUID(final String uuid)
    {
        try
        {
            Iterator<ProfileCash> iterator = profileList.iterator();
            while (iterator.hasNext())
            {
                ProfileCash pc = iterator.next();
                if(pc.uuid.equals(uuid))
                {
                    pc.expiresOn = (new Date()).getTime() + ModSettings.TARGET.TargetMobBar_OwnerNamesCacheDays.get() * 86400000;
                    return pc.name;
                }
            }
        }
        catch (Exception e) 
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } 
        return null;
    }

    // размер кэша
    private int getSize()
    {
        return profileList.size();
    }

    // добавление записи
    public synchronized void addProfile(final String uuid, final String name)
    {
        if (name == null || name.isEmpty()) return;
        if (uuid == null || uuid.isEmpty()) return;
        if (findNameByUUID(uuid) == null)
        {
            final long expiresOn = (new Date()).getTime() + ModSettings.TARGET.TargetMobBar_OwnerNamesCacheDays.get() * 86400000;
            profileList.add(new ProfileCash(uuid, name, expiresOn));
            writeToFile();
        }
    }

    // запись кэша в файл
    private void writeToFile()
    {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String json = gson.toJson(profileList);
        PrintWriter outputstream = null;
        try 
        {
            outputstream = new PrintWriter(new FileWriter(filePath));
            outputstream.write(json);
            outputstream.close();
            System.out.println("\nProfiles Cash was writed: \"" + filePath + "\"\nProfiles Cash size = " + String.valueOf(getSize()));
        } 
        catch (Exception e) 
        {
            if (outputstream != null) outputstream.close();
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // читаем кэш из файла
    private void readFromFile()
    {
        File file = new File(filePath);
        if (!file.exists()) 
        {
            System.out.println("\nProfiles Cash file not found: \"" + filePath + "\"");
            return;
        }
        
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ProfileCash>>(){}.getType();
        InputStream inputstream = null;
        try
        {
            inputstream = new FileInputStream(filePath);
            String cash = new BufferedReader(new InputStreamReader(inputstream))
                    .lines().collect(Collectors.joining("\n"));
            profileList = gson.fromJson(cash, type);
            System.out.println("\nProfiles Cash was readed: \"" + filePath + "\"\nProfiles Cash size = " + String.valueOf(getSize()));
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
    private boolean cleanObsoleteCache()
    {
        boolean res = false;
        final long time = (new Date()).getTime();
        Iterator<ProfileCash> iterator = profileList.iterator();
        while (iterator.hasNext())
        {
            ProfileCash pc = iterator.next();
            if (pc.expiresOn < time) // если устарело, то удаляем
            {
                System.out.println("\nProfiles Cash about gamer \"" + pc.name + "\" is stale and was removed.");
                iterator.remove();
                res = true;
            }
        }
        return res;
    }

    // используется в локальном кэшировании профилей как базовая структура
    private class ProfileCash
    {
        public String uuid = null;
        public String name = null;
        public long expiresOn = 0;

        public ProfileCash(String uuid, String name, long expiresOn)
        {
            this.uuid = uuid;
            this.name = name;
            this.expiresOn = expiresOn;
        }
    }
}
