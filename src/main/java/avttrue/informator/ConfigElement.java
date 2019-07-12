package avttrue.informator;

public enum ConfigElement
{
    HELDITEM_ALIGN_MODE("Held Item Bar Align Mode",
            "Valid alignment strings are Custom, Default",
            new String[] { "Custom", "Default"}),
    TARGETMOB_ALIGN_MODE("Target Mob Bar Align Mode",
            "Valid alignment strings are TopCenter, TopRight, TopLeft, Custom",
            new String[] { "TopCenter", "TopRight", "TopLeft", "Custom"}),
    CLOCK_ALIGN_MODE("Time Bar Align Mode",
            "Valid alignment strings are Custom, BottomTight, TopRight, TopLeft, BottomLeft",
            new String[] { "Custom", "BottomRight", "TopRight", "TopLeft", "BottomLeft"}),
    INFOBLOCK_ALIGN_MODE("InfoBlock Bar Align Mode",
            "Valid alignment strings are Custom, BottomRight, TopRight, TopLeft, BottomLeft",
            new String[] { "Custom", "BottomRight", "TopRight", "TopLeft", "BottomLeft"});

    private String        key;
    private String        desc;
    private String[]      validStrings;

    private ConfigElement(String key, String desc, String[] validStrings)
    {
        this.key = key;
        this.desc = desc;
        this.validStrings = validStrings;
    }

    private ConfigElement(String key, String desc)
    {
        this(key, desc, new String[0]);
    }

    public String key()
    {
        return key;
    }

    public String desc()
    {
        return desc;
    }

        public String[] validStrings()
    {
        return validStrings;
    }
}