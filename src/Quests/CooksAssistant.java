package Quests;

import Actions.Actions;
import Sleep.Sleep;
import org.osbot.rs07.api.Chatbox;
import org.osbot.rs07.api.Quests;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.MethodProvider;

public class CooksAssistant extends Quest {


    public CooksAssistant(MethodProvider context){
        super(Quests.Quest.COOKS_ASSISTANT, 29, context);
    }

    private Actions actions = new Actions(context);

    enum QuestLocation {
        LUMBY_KITCHEN (new Area(3205, 3217, 3212, 3212)),
        CHEFS_LOCATION (new Area(3209, 3215, 3208, 3213)),
        BUCKET (new Area(3216, 9624, 3213, 9623)),
        EGG (new Area(3230, 3296, 3233, 3296)),
        MILK (new Area(3253, 3270, 3255, 3268)),
        WHEAT (new Area(3156, 3300, 3159, 3298)),
        TOP_OF_MILL (new Area(3165, 3308, 3165, 3305).setPlane(2)),
        BOTTOM_OF_MILL (new Area(3165, 3305, 3169, 3305));

        final Area area;

        QuestLocation(Area area) {
            this.area = area;
        }


    }

    private boolean usedControls = false;
    private boolean completeQuest = false;

    public int onLoop(){
        switch(questStatus()){
            case 0:
                StartQuest();
                return 100;
            case 1:
                if(completeQuest == false) {
                    getItems();
                }
                else
                    talkToChef();
                return 100;
            case 2:
                context.log("Quest Complete");
                return 100;
        }
        return 100;
    }

    private void StartQuest(){
        context.log("Starting Cook's Assistant");
        if (!QuestLocation.LUMBY_KITCHEN.area.contains(context.myPosition())) {
            context.log("Walking to Kitchen");
            context.getWalking().webWalk(QuestLocation.CHEFS_LOCATION.area);
        }
        else {
            context.log("Talking to Chef");
            if(context.dialogues.inDialogue()){
                actions.dialogueHandler("What's wrong?", "I'm always happy to help a cook in distress.", "Actually, I know where to find this stuff.");
            }
            else {
                actions.interactNpc("Cook", "Talk-to");
            }
        }
    }

    private void getItems(){
        if(!(context.getInventory().contains("Bucket") || context.getInventory().contains("Bucket of milk"))){
            getBucket();
        }
        else if(!context.getInventory().contains("Pot") && !context.getInventory().contains("Pot of flour")){
            getPot();
        }
        else if(!context.getInventory().contains("Bucket of milk")){
            getMilk();
        }
        else if (!context.getInventory().contains("Egg")){
            getEgg();
        }
        else if(!context.getInventory().contains("Pot of flour")){
            getFlour();
        }
        else {
            completeQuest = true;
        }
    }

    private void getFlour(){
        context.log("Getting Flour");
        if(!context.getInventory().contains("Grain") && !startedFlourProcess()){
            getGrain();
        }
        else{
            makeFlour();
        }
    }

    private void getPot(){
        context.log("Getting pot");
        if(!QuestLocation.LUMBY_KITCHEN.area.contains(context.myPlayer())){
            context.getWalking().webWalk(QuestLocation.LUMBY_KITCHEN.area);
        }
        else if(context.getGroundItems().closest("Pot") != null){
            context.getGroundItems().closest("Pot").interact("Take");
            Sleep.sleepUntil(() -> context.getInventory().contains("Pot"), 3000);
        }
        else Sleep.sleepUntil(() -> context.getGroundItems().closest("Pot") != null, 3000);
    }

    private void getGrain(){
        if(!QuestLocation.WHEAT.area.contains(context.myPlayer())){
            context.getWalking().webWalk(QuestLocation.WHEAT.area);
        }
        else if(findWheat() != null) {
            findWheat().interact("Pick");
            Sleep.sleepUntil(() -> context.getInventory().contains("Grain"), 5000);
        }
        else Sleep.sleepUntil(() -> findWheat() != null, 1500);
    }

    private RS2Object findWheat(){
        return context.getObjects().closest(e -> e.hasAction("Pick") && e.getName().contentEquals("Wheat"));
    }

    private void makeFlour(){
        if(!QuestLocation.TOP_OF_MILL.area.contains(context.myPlayer()) && !startedFlourProcess()){
            context.getWalking().webWalk(QuestLocation.TOP_OF_MILL.area);
        }
        else if(!startedFlourProcess()){
            loadHopper();
        }
        else if(!usedControls){
            useControls();
        }
        else if(!QuestLocation.BOTTOM_OF_MILL.area.contains(context.myPlayer())){
            context.getWalking().webWalk(QuestLocation.BOTTOM_OF_MILL.area);
        }
        else if(context.getObjects().closest("Flour bin").getModel().getVerticesCount() != 212){
            actions.useItemOnTarget("Pot", "Flour bin");
        }
    }

    private void loadHopper(){
        if(!context.myPlayer().isAnimating() && !context.myPlayer().isMoving()){
            actions.useItemOnTarget("Grain", "Hopper");
        }
        else {
            Sleep.sleepUntil(() -> startedFlourProcess(), 5000);
        }
    }

    private void useControls(){
        if(context.getChatbox().contains(Chatbox.MessageType.GAME, "You operate the hopper.")){
            context.log("usedControls = true");
            usedControls = true;
            return;
        }
        else if(!context.myPlayer().isAnimating() && !context.myPlayer().isMoving()){
            if(context.getObjects().closest("Hopper controls") != null){
                context.getObjects().closest("Hopper controls").interact("Operate");
                Sleep.sleepUntil(() -> context.getChatbox().contains(Chatbox.MessageType.GAME, "You operate the hopper."), 5000);
            }
        }
        else Sleep.sleepUntil(() ->!context.myPlayer().isAnimating() && !context.myPlayer().isMoving(), 5000);
    }

    private void getEgg(){
        context.log("Getting egg");
        if(!QuestLocation.EGG.area.contains(context.myPlayer())){
            context.getWalking().webWalk(QuestLocation.EGG.area);
        }
        else {
            if(context.getGroundItems().closest("Egg") != null){
                context.getGroundItems().closest("Egg").interact("Take");
                Sleep.sleepUntil(() -> context.getInventory().contains("Egg"), 3000);
            }
            else Sleep.sleepUntil(() -> context.getGroundItems().closest("Egg") != null, 3000);
        }
    }



    private void getBucket(){
        context.log("Getting bucket");
        if(!QuestLocation.BUCKET.area.contains(context.myPlayer())){
            context.getWalking().webWalk(QuestLocation.BUCKET.area);
        }
        else if(context.getGroundItems().closest("Bucket") != null){
            context.getGroundItems().closest("Bucket").interact("Take");
            Sleep.sleepUntil(() -> context.getInventory().contains("Bucket"), 3000);
        }
        else Sleep.sleepUntil(() -> context.getGroundItems().closest("Bucket") != null, 3000);
    }

    private void getMilk(){
        context.log("Getting milk");
        if(!QuestLocation.MILK.area.contains(context.myPlayer())){
            context.getWalking().webWalk(QuestLocation.MILK.area);
        }
        else {
            RS2Object cow = context.getObjects().closest("Dairy cow");
            if(cow != null){
                cow.interact("Milk");
                Sleep.sleepUntil(() -> context.inventory.contains("Bucket of milk"), 5000);
            }
            else context.log("Error: No Cow");
        }
    }

    private void talkToChef(){
        context.log("Talking to cook");
        if (!QuestLocation.LUMBY_KITCHEN.area.contains(context.myPosition())) {
            context.log("Walking to Kitchen");
            context.getWalking().webWalk(QuestLocation.CHEFS_LOCATION.area);
        }
        else {
            context.log("Talking to Chef");
            if(context.dialogues.inDialogue()){
                actions.dialogueHandler();
            }
            else {
                actions.interactNpc("Cook", "Talk-to");
            }
        }
    }

    private boolean startedFlourProcess(){
        if(context.getChatbox().contains(Chatbox.MessageType.GAME, "You put the grain in the hopper.")){
            return true;
        }
        else return false;
    }

}
