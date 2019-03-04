package avttrue.informator.Tools.Surface;

public class SurfaceOverlay
{
	
	public int x;
	public int z;
	public double y;
	public int lightLevel;
	public boolean myChunk;
	
	public SurfaceOverlay(int x, double y, int z, int lightLevel, boolean myChunk) 
	{
		this.x = x;
		this.y = y + 0.01;
		this.z = z;
		this.lightLevel = lightLevel;
		this.myChunk = myChunk;
	}
	
}
