package avttrue.informator;

import static net.minecraftforge.common.config.Property.Type.BOOLEAN;
import static net.minecraftforge.common.config.Property.Type.INTEGER;
import static net.minecraftforge.common.config.Property.Type.STRING;
import net.minecraftforge.common.config.Property;

public enum ConfigElement
{
    HELDITEM_ALIGN_MODE("Held Item Bar Align Mode",
            "Valid alignment strings are Custom, Default",
            STRING, new String[] { "Custom", "Default"}),
    TARGETMOB_ALIGN_MODE("Target Mob Bar Align Mode",
            "Valid alignment strings are TopCenter, TopRight, TopLeft, Custom",
            STRING, new String[] { "TopCenter", "TopRight", "TopLeft", "Custom"}),
    CLOCK_ALIGN_MODE("Time Bar Align Mode",
            "Valid alignment strings are Custom, BottomTight, TopRight, TopLeft, BottomLeft",
            STRING, new String[] { "Custom", "BottomRight", "TopRight", "TopLeft", "BottomLeft"}),
	INFOBLOCK_ALIGN_MODE("InfoBlock Bar Align Mode",
            "Valid alignment strings are Custom, BottomRight, TopRight, TopLeft, BottomLeft",
            STRING, new String[] { "Custom", "BottomRight", "TopRight", "TopLeft", "BottomLeft"});

    private String        key;
    private String        desc;
    private Property.Type propertyType;
    private String[]      validStrings;

    private ConfigElement(String key, String desc, Property.Type propertyType, String[] validStrings)
    {
        this.key = key;
        this.desc = desc;
        this.propertyType = propertyType;
        this.validStrings = validStrings;
    }

    private ConfigElement(String key, String desc, Property.Type propertyType)
    {
        this(key, desc, propertyType, new String[0]);
    }

    public String key()
    {
        return key;
    }

    public String desc()
    {
        return desc;
    }

    public Property.Type propertyType()
    {
        return propertyType;
    }

    public String[] validStrings()
    {
        return validStrings;
    }
}