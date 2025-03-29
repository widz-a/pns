package wida.pns;

public class PnsConfig {

    //@ConfigEntry.Category("main")
    //@ConfigEntry.Gui.CollapsibleObject
    public PunchConfig punch = new PunchConfig();
    public static class PunchConfig {
        public int delay = 143;
    }

    //@ConfigEntry.Category("main")
    //@ConfigEntry.Gui.CollapsibleObject
    public NodConfig nod = new NodConfig();
    public static class NodConfig {
        public int speed = 40;
        public float degreeUp = 60;
        public float degreeDown = -60;
        public int delayUp = 0;
        public int delayDown = 0;

    }

    //@ConfigEntry.Category("main")
    //@ConfigEntry.Gui.CollapsibleObject
    public SneakConfig sneak = new SneakConfig();
    public static class SneakConfig {
        public int holdDelay = 100;
        public long releaseDelay = 100;
    }
}