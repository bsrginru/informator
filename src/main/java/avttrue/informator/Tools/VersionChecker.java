package avttrue.informator.Tools;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import avttrue.informator.Informator;

// здесь производится сверка версий мода - текущей и из файла по УРЛу
public class VersionChecker implements Runnable
{
	private boolean isLatestVersion = true;
	private String latestVersion = "";
	
	@Override
    public void run() 
    {
        InputStream in = null;
        try 
        {
            in = new URL(Informator.INFORVERSIONCHECKER_URL).openStream();
        } 
        catch (Exception e) 
        {
        	System.out.println(e.getMessage());
            e.printStackTrace();
        } 

        try 
        {
            latestVersion = IOUtils.readLines(in).get(0);
        } 
        catch (Exception e) 
        {
        	System.out.println(e.getMessage());
            e.printStackTrace();
        } 
        finally 
        {
            IOUtils.closeQuietly(in);
        }
        
        if (latestVersion.isEmpty())
        	return;
        
        isLatestVersion = Informator.MODVER.equals(latestVersion);
        
        System.out.println("\nLatest mod version = \"" + latestVersion + "\" (" + isLatestVersion + ")");
   }
    
    public boolean isLatestVersion()
    {
     return this.isLatestVersion;
    }
    
    public String getLatestVersion()
    {
     return this.latestVersion;
    }
}

