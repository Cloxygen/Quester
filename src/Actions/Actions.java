package Actions;

import Sleep.Sleep;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.MethodProvider;


public class Actions{

    MethodProvider context;

    public Actions(MethodProvider context) {
        this.context = context;
    }

    public void dialogueHandler(String... choice){
        if(context.getDialogues().isPendingOption()){
            try {
                context.getDialogues().completeDialogue(choice);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else if(context.getDialogues().isPendingContinuation()){
            context.getDialogues().clickContinue();
        }
        else Sleep.sleepUntil(() -> !context.getDialogues().inDialogue(), 3000);
    }

    public void interactNpc(String name, String action){

        NPC npc = context.getNpcs().closest(name);

        if(npc != null){
            if(context.getMap().canReach(npc)){
                npc.interact(action);
                Sleep.sleepUntil(() -> context.getDialogues().inDialogue(), 1500);
            }
            else context.getWalking().webWalk(npc.getPosition());
        }
    }

    public boolean cutsceneHandler(){
        if(!context.getWidgets().get(548,58).isVisible()){
            if(context.getDialogues().inDialogue()){
                dialogueHandler();
                return true;
            }
            else {
                Sleep.sleepUntil(() -> context.getDialogues().inDialogue(), 1000);
                return true;
            }
        }
        else return false;
    }

    public void useItemOnTarget(String item, String target){
        if(context.getInventory().contains(item)){
            if(!context.getInventory().interact("Use", item)){
                context.getInventory().interact("Use",item);
            }
            else {
                RS2Object object = context.getObjects().closest(target);
                if(object != null){
                    object.interact("Use");
                    Sleep.sleepUntil(() -> context.myPlayer().isAnimating() && !context.myPlayer().isMoving(), 5000);
                }
                else context.log("Error: No target found");
            }
        }
        else context.log("Error: No item found");
    }
}
