package Quests;

import Actions.Actions;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.RS2Object;

import Sleep.Sleep;
import org.osbot.rs07.script.MethodProvider;



public final class RomeoAndJuliet extends Quest {


    public RomeoAndJuliet(MethodProvider context){
        super(Quests.Quest.ROMEO_JULIET, 144, context);
    }

    private Actions actions = new Actions(context);

    enum QuestLocation {
        JULIETS_HOUSE (new Area(3153, 3430, 3163, 3424).setPlane(1)),
        JULIET (new Area(3157, 3425, 3160, 3425).setPlane(1)),
        ROMEO_IN_SQUARE (new Area(3212, 3428, 3213, 3425)),
        VARROCK_SQUARE (new Area(3203, 3438, 3222, 3410)),
        CHURCH (new Area(3252, 3488, 3259, 3471)),
        CHURCH_FATHER (new Area(3254, 3483, 3255, 3481)),
        APOTHECARYS_SHOP (new Area(3198, 3402, 3192, 3406)),
        EAST_GATE (new Area(3272, 3429, 3275, 3427)),
        EAST_MINE (new Area(3290, 3376, 3293, 3373)),
        BERRYS (new Area(3262, 3374, 3276, 3363)),
        BERRYS_BUSH (new Area(3275, 3368, 3272, 3369));

        final Area area;

        QuestLocation(Area area) {
            this.area = area;
        }

        protected Area entireArea(){
            return this.area;
        }

    }

    @Override
    public int onLoop() {
        switch (questStatus()) {
            case 0:
                startQuest();
                return 100;
            case 20:
                letterToRomeo();
                return 100;
            case 30:
                talkToLawrence();
                return 100;
            case 40:
                talkToApothecary();
                return 100;
            case 50:
                makePotion();
                return 100;
            case 60:
                backToRomeo();
                return 100;
            case 100:
                context.log("Case 100 selected");
                return 100;
        }
        return 100;
    }

    private void startQuest(){
        if (!QuestLocation.JULIETS_HOUSE.entireArea().contains(context.myPosition())) {
            context.log("Walking to Juliet");
            context.getWalking().webWalk(QuestLocation.JULIET.area);
        } else {
            context.log("Talking to Juliet");
            if(context.dialogues.inDialogue()){
                actions.dialogueHandler("Yes I've met him.", "Certainly, I'll do so straight away.");
            }
            else {
                actions.interactNpc("Juliet", "Talk-to");
                Sleep.sleepUntil(() -> context.dialogues.inDialogue(), 3000);
            }
        }
    }

    private void letterToRomeo(){
        if (!QuestLocation.VARROCK_SQUARE.entireArea().contains(context.myPosition())) {
            context.log("Walking to Romeo");
            context.getWalking().webWalk(QuestLocation.ROMEO_IN_SQUARE.area);
        }
        else {
            context.log("Talking to Romeo");
            if(context.dialogues.inDialogue()) {
                actions.dialogueHandler("Ok, thanks.");
            }
            else actions.interactNpc("Romeo", "Talk-to");
        }
    }

    private void talkToLawrence(){
        if (!QuestLocation.CHURCH.entireArea().contains(context.myPosition())) {
            context.log("Walking to Father L");
            context.getWalking().webWalk(QuestLocation.CHURCH_FATHER.area);
        } else {
            context.log("Talking to Father Lawrence");
            if(context.dialogues.inDialogue()) {
                actions.dialogueHandler();
            }
            else actions.interactNpc("Father Lawrence", "Talk-to");

        }
    }

    private void talkToApothecary(){
        if (!QuestLocation.APOTHECARYS_SHOP.entireArea().contains(context.myPosition())) {
            context.log("Walking to Apothecary");
            context.getWalking().webWalk(QuestLocation.APOTHECARYS_SHOP.area);
        } else {
            context.log("Talking to Apothecary");
            if(context.dialogues.inDialogue()) {
                actions.dialogueHandler( "Talk about something else.", "Talk about Romeo & Juliet.", "Ok, thanks", "Click here to continue");
            }
            else actions.interactNpc("Apothecary", "Talk-to");
        }
    }

    private void makePotion() {
        if(actions.cutsceneHandler()){
            context.log("In cutscene");
            return;
        }
        //talk to juliet
        else if (context.getInventory().contains("Cadava potion")){
            context.log("Has potion");
            if (!QuestLocation.JULIETS_HOUSE.entireArea().contains(context.myPosition())) {
                context.log("Walking to Juliet");
                context.getWalking().webWalk(QuestLocation.JULIETS_HOUSE.area);
            }
            else {
                context.log("Talking to Juliet");
                if(context.dialogues.inDialogue()){
                    actions.dialogueHandler();
                }
                else actions.interactNpc("Juliet", "Talk-to");

            }
        }
        else if(context.getInventory().contains("Cadava berries")) {
            context.log("Has cadava berries");
            if (!QuestLocation.APOTHECARYS_SHOP.entireArea().contains(context.myPosition())) {
                context.getWalking().webWalk(QuestLocation.EAST_MINE.area);
                Sleep.sleepUntil(() -> QuestLocation.EAST_MINE.area.contains(context.myPlayer()), 90000);

                context.getWalking().webWalk(QuestLocation.EAST_GATE.area);
                Sleep.sleepUntil(() -> QuestLocation.EAST_GATE.area.contains(context.myPlayer()), 90000);

                context.getWalking().webWalk(QuestLocation.APOTHECARYS_SHOP.area);
                Sleep.sleepUntil(() -> QuestLocation.APOTHECARYS_SHOP.area.contains(context.myPlayer()), 90000);
            } else {
                context.log("talking to apothecary");
                if(context.dialogues.inDialogue()){
                    context.log("in dialogue");
                    actions.dialogueHandler("Talk about something else.", "Talk about Romeo & Juliet.");
                }
                else actions.interactNpc("Apothecary","Talk-to");
            }
        }
        else {
            context.log("No berries");
            //pick berries
            if (QuestLocation.BERRYS.entireArea().contains(context.myPosition())) {
                context.log("Collecting Berries");
                RS2Object bush = context.getObjects().closest(e -> e.getModel().getVerticesCount() == 580 && e.getName().contentEquals("Cadava bush"));
                if(bush != null){
                    bush.interact("Pick-from");
                    Sleep.sleepUntil(() -> context.getInventory().contains("Cadava berries"), 3000);
                }

            }
            //walk to berries
            else {
                context.log("Walking via east gate");
                context.getWalking().webWalk(QuestLocation.EAST_GATE.area);
                Sleep.sleepUntil(() -> QuestLocation.EAST_GATE.area.contains(context.myPlayer()), 90000);

                context.getWalking().webWalk(QuestLocation.EAST_MINE.area);
                Sleep.sleepUntil(() -> QuestLocation.EAST_MINE.area.contains(context.myPlayer()), 90000);

                context.getWalking().webWalk(QuestLocation.BERRYS_BUSH.area);
                Sleep.sleepUntil(() -> QuestLocation.BERRYS_BUSH.area.contains(context.myPlayer()), 90000);
            }
        }
    }

    private void backToRomeo() {
        if(actions.cutsceneHandler()){
            return;
        }
        else if (!QuestLocation.VARROCK_SQUARE.entireArea().contains(context.myPlayer())) {
            context.log("Walking to Romeo");
            context.getWalking().webWalk(QuestLocation.ROMEO_IN_SQUARE.area);
        }
        else {
            context.log("Talking to Romeo");
            if(context.dialogues.inDialogue()){
                actions.dialogueHandler();
            }
            else {
                actions.interactNpc("Romeo", "Talk-to");
            }
        }
    }

}