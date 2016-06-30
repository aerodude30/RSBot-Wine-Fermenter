import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.ItemQuery;

import java.awt.*;
import java.util.concurrent.Callable;

/**
 * Created by christianbartram on 6/27/16.
 */


@Script.Manifest(name = "Wine Fermenter", description = "Converts jugs of water and grapes into fermented wine for fast cooking experience! ")
public class Fermenter extends PollingScript<ClientContext> implements PaintListener {

    private static final int WINE_JUG = 882, WATER_JUG = 1937, GRAPES = 1987; //1993
    private final ItemQuery<Item> wineItems = ctx.inventory.select().id(WATER_JUG, WINE_JUG); //todo change to grapes
    private final Component INVENTORY_COMPONENT = ctx.widgets.component(548, 48);
    private long startTime;
    private String status = "Waiting to start...";
    private int startExperience = 0, currentLevel = 0;
    private Util util = new Util();

    //enums for each state in the script
    private enum State { BANK, FERMENT }

    private State getState() {
        //if the inventory contains both grapes and the water jugs then lets make some wine!
        return ctx.inventory.select().id(WATER_JUG, GRAPES).count() > 0 ? State.FERMENT : State.BANK;
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
        startExperience = ctx.skills.experience(7);

        if(!INVENTORY_COMPONENT.visible()) {
            if(ctx.bank.opened()) {
                status = "Closing Bank.";
                ctx.bank.close();
            }
            status = "Opening inventory...";
            INVENTORY_COMPONENT.click();
        }

    }

    @Override
    public void poll() {
        final State state = getState();
        currentLevel = ctx.skills.level(7);

        if(state == null) return;

        switch (state) {

            case BANK:
                if(ctx.bank.inViewport() && !ctx.bank.opened()) {
                    ctx.camera.turnTo(ctx.npcs.select().id(2897, 3227).shuffle().poll());
                    ctx.bank.open();

                    if(ctx.inventory.count() != 0){
                        ctx.bank.depositInventory();
                    }

                    status = "Withdrawing ingredients...";

                    //withdraw ingredients to make wine
                    ctx.bank.withdraw(WATER_JUG, 14);
                    ctx.bank.withdraw(WINE_JUG, 14);//todo WINE_JUG change to grapes

                    if(ctx.inventory.select().id(WATER_JUG).count() > 0 && ctx.inventory.select().id(WINE_JUG).count() > 0) { //todo change to grapes
                        ctx.inventory.select().id(WINE_JUG).poll().interact("Use", ctx.inventory.select().id(WATER_JUG).poll().name()); //todo change WINE_JUG to grapes

                            status = "Making wine!";
                            ctx.widgets.component(162, 546).interact("Make all");

                            Condition.wait(new Callable<Boolean>() {
                                @Override
                                public Boolean call() throws Exception {
                                    return ctx.inventory.select().id(GRAPES).count() == 0;
                                }
                            }, 300, 15);

                        //we have what we need to make some more wine
                        ctx.bank.close();
                    } else {
                        status = "Your out of ingredients!";
                        //ctx.controller.stop();
                    }
                    break;
                }
                break;

            case FERMENT:
                if(ctx.inventory.select().id(WINE_JUG).poll().interact("Use", ctx.inventory.select().id(WATER_JUG).poll().name())) { //todo change WINE_JUG to grapes

                    status = "Making wine!";
                    ctx.widgets.component(162, 546).interact("Make all");

                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return ctx.inventory.select().id(GRAPES).count() == 0;
                        }
                    }, 300, 15);
                }
                break;

            default:
                break;
        }
    }


    @Override
    public void repaint(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        int expGained = ctx.skills.experience(7) - startExperience;

        g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF));
        g.setColor(new Color(136, 136, 136, 117));
        g.fillRect(3, 3, 175, 145);
        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(51, 153, 255));
        g.drawRect(3, 3, 175, 145);
        g.drawLine(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y, 20, 20);
        g.setColor(new Color(51, 153, 255));
        g.drawLine(12, 31, 134, 31);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 14));
        g.drawString("Wine Fermenter", 19, 25);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 11));
        g.drawString("Time Running: " , 13, 48);
        g.drawString("Cooking Exp Gained: ", 14, 65);
        g.drawString("Cooking/hour: ", 14, 84);
        g.drawString("Starting Level: ", 15, 103);
        g.drawString("Current Level: ", 15, 123);
        g.drawString("Status: ", 16, 141);
        g.drawString(util.runtime(startTime), 85, 49);
        g.drawString(String.valueOf(expGained), 120, 65);
        g.drawString(util.perHour(expGained, startExperience) + " (" + util.formatNumber(expGained) + ")", 86, 84);
        g.drawString(String.valueOf(ctx.skills.level(7)), 90, 103);
        g.drawString(String.valueOf(currentLevel), 86, 123);
        g.drawString(status, 52, 141);
    }
}
