
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Inventory;
import org.powerbot.script.rt4.Item;

import java.util.concurrent.Callable;

/**
 * Created by christianbartram on 6/27/16.
 */

public class Ferment extends Task<ClientContext> {

    private final Inventory inventory = ctx.inventory;
    private final Item WATER_JUG = inventory.select().id(1937).poll();
    private final Item GRAPES = inventory.select().id(1987).poll();
    private final Component INVENTORY_COMPONENT = ctx.widgets.component(548, 48);

    public Ferment(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
       return inventory.contains(WATER_JUG) && inventory.contains(GRAPES) && ctx.players.local().animation() == -1;
    }

    @Override
    public void execute() {
        //make sure inventory tab is open
        if(!INVENTORY_COMPONENT.visible()) {
            System.out.println("Opening inventory...");
            INVENTORY_COMPONENT.click();
        }

        if(GRAPES.interact("Use", WATER_JUG.name())) {
            System.out.println("Making wine!");
            //while we are making the wines wait
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return INVENTORY_COMPONENT.visible();
                }
            }, 300, 15);

        }

        //used to prevent spam clicking
        if (ctx.varpbits.varpbit(1175) > 0) {
           // break;
        }
    }

}
