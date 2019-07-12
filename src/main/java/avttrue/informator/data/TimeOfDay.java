package avttrue.informator.data;

public enum TimeOfDay
{
    DAY(0),
    NIGHT(1),
    UNKNOWN(-1);

    public int type = -1;

    private TimeOfDay(int type)
    {
        this.type = type;
    }
}