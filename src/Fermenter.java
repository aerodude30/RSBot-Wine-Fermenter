import org.powerbot.script.Condition;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;

import java.util.concurrent.Callable;

/**
 * Created by christianbartram on 6/27/16.
 */


@Script.Manifest(name = "Wine Fermenter", description = "Converts jugs of water and grapes into fermented wine for fast cooking experience! ")
public class Fermenter extends PollingScript<ClientContext> {

    private static final int WINE_JUG = 1993;
    private static final int WATER_JUG = 1937;
    private static final int GRAPES = 1987;
    private final Component INVENTORY_COMPONENT = ctx.widgets.component(548, 48);

    //enums for each state in the script
    private enum State { BANK, FERMENT }

    private State getState() {
        //if the inventory contains both grapes and the water jugs then lets make some wine!
        System.out.println("If inven contains both grapes and water jugs: " + ctx.inventory.select().contains(ctx.inventory.select().id(WATER_JUG, GRAPES).poll()));
        return ctx.inventory.select().contains(ctx.inventory.select().id(WATER_JUG, GRAPES).poll())  ? State.FERMENT : State.BANK;
    }

    @Override
    public void start() {
        System.out.println("Starting script...");

    }

    @Override
    public void poll() {

        final State state = getState();

        if(state == null) { return; }

        switch (state) {

            case BANK:
                if(ctx.bank.inViewport()) {
                    ctx.bank.open();
                    //check to see if they have more wine and grapes in their bank
            /*
            if(ctx.bank.select().id(WATER_JUG).isEmpty() || ctx.bank.select().id(GRAPES).isEmpty()) {
                System.out.println(ctx.bank.select().id(WATER_JUG).count());
                System.err.println("You've run out of Water and Grapes!");
                ctx.controller.stop();
            }
            */
                    //deposit all the wine into the bank
                    ctx.bank.depositInventory();

                    System.out.println("Withdrawing ingredients....");

                    //withdraw ingredients to make wine
                    ctx.bank.withdraw(WATER_JUG, 14);
                    ctx.bank.withdraw(GRAPES, 14);

                    ctx.bank.close();
                }
                break;

            case FERMENT:
                //make sure inventory tab is open
                if(!INVENTORY_COMPONENT.visible()) {
                    System.out.println("Opening inventory...");
                    INVENTORY_COMPONENT.click();
                }

                if(ctx.inventory.select().id(GRAPES).poll().interact("Use", ctx.inventory.select().id(WATER_JUG).poll().name())) {

                    System.out.println("Making wine!");
                    ctx.widgets.component(162, 546).interact("Make all");

                    //while we are making the wines wait
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.inventory.select().id(WINE_JUG).count() >= 13;
                        }
                    }, 300, 15);

                }
                break;

        }
    }
}
