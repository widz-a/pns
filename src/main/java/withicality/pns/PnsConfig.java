package withicality.pns;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "pns")
public class PnsConfig implements ConfigData {

    @ConfigEntry.Category("main")
    @ConfigEntry.Gui.TransitiveObject
    public PunchConfig punch = new PunchConfig();
    public static class PunchConfig {
        public int delay = 143;
    }

    @ConfigEntry.Category("main")
    @ConfigEntry.Gui.TransitiveObject
    public NodConfig nod = new NodConfig();
    public static class NodConfig {
        public int speed = 40;
        public float degreeUp = 60;
        public float degreeDown = -60;
        public int delayUp = 0;
        public int delayDown = 0;

    }

    @ConfigEntry.Category("main")
    @ConfigEntry.Gui.TransitiveObject
    public SneakConfig sneak = new SneakConfig();
    public static class SneakConfig {
        public int delay = 200;
    }
}