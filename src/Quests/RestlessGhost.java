package Quests;

import Actions.Actions;
import Sleep.Sleep;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.MethodProvider;

public class RestlessGhost extends Quest{

    public RestlessGhost(MethodProvider context){
        super(Quests.Quest.THE_RESTLESS_GHOST, 107, context);
    }

    private Actions actions = new Actions(context);

    enum QuestLocation {
        START_LOCATION (new Area(3247, 3204, 3240, 3215)),
        URHNEY_LOCATION (new Area(3144, 3173, 3151, 3177)),
        COFFIN_LOCATION (new Area(3247, 3195, 3252, 3190)),
        ALTER_LOCATION (new Area(3121, 9568, 3121, 9565));

        final Area area;

        QuestLocation(Area area) {
            this.area = area;
        }

    }

    public int onLoop(){
        switch(questStatus()){
            case 0:
                startQuest();
                return 100;
            case 1:
                talkToUrhney();
                return 100;
            case 2:
                talkToGhost();
                return 100;
            case 3:
                getSkull();
                return 100;
            case 4:
                returnSkull();
                return 100;
            case 5:
                context.log("Quest Complete");
                return 100;

        }
        return 100;
    }

    private void startQuest(){
        if(!QuestLocation.START_LOCATION.area.contains(context.myPlayer())) context.getWalking().webWalk(QuestLocation.START_LOCATION.area);

        else if(context.dialogues.inDialogue())actions.dialogueHandler("I'm looking for a quest!","Ok, let me help then.");

        else actions.interactNpc("Father Aereck", "Talk-to");
    }

    private void talkToUrhney(){
        if(!QuestLocation.URHNEY_LOCATION.area.contains(context.myPlayer())) context.getWalking().webWalk(QuestLocation.URHNEY_LOCATION.area);

        else if(context.dialogues.inDialogue())actions.dialogueHandler("Father Aereck sent me to talk to you.","He's got a ghost haunting his graveyard.");

        else actions.interactNpc("Father Urhney", "Talk-to");
    }

    private void talkToGhost(){
        if(!context.getEquipment().contains("Ghostspeak amulet")){
            if(context.getInventory().contains("Ghostspeak amulet")) context.getInventory().getItem("Ghostspeak amulet").interact("Wear");

            else context.log("Error: No Ghostspeak Amulet");
        }
        else if(!QuestLocation.COFFIN_LOCATION.area.contains(context.myPlayer())) context.getWalking().webWalk(QuestLocation.COFFIN_LOCATION.area);

        else if (context.dialogues.inDialogue()) actions.dialogueHandler("Yep, now tell me what the problem is.");

        else if(context.getNpcs().closest("Restless Ghost") != null) actions.interactNpc("Restless Ghost", "Talk-to");

        else if(context.getObjects().closest("Coffin") != null) {
            context.getObjects().closest("Coffin").interact("Search");
            Sleep.sleepUntil(() -> context.getNpcs().closest("Restless Ghost") != null, 5000);
        }
    }

    private void getSkull(){
        if(!QuestLocation.ALTER_LOCATION.area.contains(context.myPlayer())) context.getWalking().webWalk(QuestLocation.ALTER_LOCATION.area);

        else if(context.getObjects().closest("Altar") != null) context.getObjects().closest("Altar").interact("Search");
    }

    private void returnSkull(){
        if(!QuestLocation.COFFIN_LOCATION.area.contains(context.myPlayer())) context.getWalking().webWalk(QuestLocation.COFFIN_LOCATION.area);

        if(context.getObjects().closest("Coffin").getModel().getVerticesCount() == 84) {
            context.getObjects().closest("Coffin").interact("Open");
            Sleep.sleepUntil(() -> context.getObjects().closest("Coffin").getModel().getVerticesCount() != 84, 5000);
        }

        else actions.useItemOnTarget("Ghost's skull", "Coffin");
    }
}
