import Quests.*;

import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;

import java.util.ArrayList;


@ScriptManifest(name = "Quester", author = "Cloxygen", version = 1.0, info = "", logo = "")


public class Main extends Script {

    private  ArrayList<Quest> quests = new ArrayList<>();

    private final Quest cooksAssistant = new CooksAssistant(this);
    private final Quest restlessGhost = new RestlessGhost(this);
    private final Quest romeoAndJuliet = new RomeoAndJuliet(this);

    private long startTime;

    @Override
    public void onStart() {
        quests.add(cooksAssistant);
        quests.add(restlessGhost);
        quests.add(romeoAndJuliet);

        startTime = System.currentTimeMillis();
    }

    @Override
    public int onLoop() throws InterruptedException{
        for(Quest quest : quests){
            if(!getQuests().isComplete(quest.getThisQuest()))
                return quest.onLoop();
        }
        return 250;
    }

}
