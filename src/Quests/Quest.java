package Quests;


import org.osbot.rs07.api.Quests;
import org.osbot.rs07.script.MethodProvider;

public abstract class Quest{

    protected MethodProvider context;

    private final Quests.Quest thisQuest;
    private final int configID;

    public abstract int onLoop() throws InterruptedException;

    public Quest(final Quests.Quest quest, final int ConfigID, MethodProvider ctx) {
        this.context = ctx;
        thisQuest = quest;
        configID = ConfigID;
    }

    public final int questStatus(){
        return context.getConfigs().get(configID);
    }

    public Quests.Quest getThisQuest() {
        return thisQuest;
    }
}