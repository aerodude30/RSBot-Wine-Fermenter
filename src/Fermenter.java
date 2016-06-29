import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;

import java.awt.*;
import java.util.concurrent.Callable;

/**
 * Created by christianbartram on 6/27/16.
 */


@Script.Manifest(name = "Wine Fermenter", description = "Converts jugs of water and grapes into fermented wine for fast cooking experience! ")
public class Fermenter extends PollingScript<ClientContext> implements PaintListener {

    private static final int WINE_JUG = 1993, WATER_JUG = 1937, GRAPES = 1987;
    private final Component INVENTORY_COMPONENT = ctx.widgets.component(548, 48);
    private long startTime;
    private String status = ""; //initialize status to blank to avoid potential NPE
    private int startExperience = 0, currentLevel = 0;
    //private final Timer RUNTIME = new Timer(0); use RUNTIME.toElapsedString()
    private Util util = new Util();


    //enums for each state in the script
    private enum State { BANK, FERMENT }

    private State getState() {
        //if the inventory contains both grapes and the water jugs then lets make some wine!
        return ctx.inventory.select().contains(ctx.inventory.select().id(WATER_JUG).poll())  ? State.FERMENT : State.BANK;
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
        startExperience = ctx.skills.experience(7);
    }

    @Override
    public void poll() {
        final State state = getState();
        currentLevel = ctx.skills.level(7);

        if(state == null) return;

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

                    status = "Withdrawing ingredients...";

                    //withdraw ingredients to make wine
                    ctx.bank.withdraw(WATER_JUG, 14);
                    ctx.bank.withdraw(GRAPES, 14);

                    ctx.bank.close();
                    break;
                }
                break;

            case FERMENT:
                if(!INVENTORY_COMPONENT.visible()) {
                    status = "Opening inventory...";
                    INVENTORY_COMPONENT.click();
                }

                if(ctx.inventory.select().id(GRAPES).poll().interact("Use", ctx.inventory.select().id(WATER_JUG).poll().name())) {

                    status = "Making wine!";
                    ctx.widgets.component(162, 546).interact("Make all");

                    //while we are making the wines wait
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            //we stop waiting when we have no more grapes left in the inventory
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
        g.fillRect(3, 3, 148, 145);
        g.setStroke(new BasicStroke(3));
        g.setColor(new Color(51, 153, 255));
        g.drawRect(3, 3, 148, 145);
        g.setColor(new Color(51, 153, 255));
        g.drawLine(12, 31, 134, 31);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 14));
        g.drawString("Wine Fermenter", 19, 25);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 11));
        g.drawString("Time Running: " , 13, 48);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 11));
        g.drawString("Cooking Exp Gained: ", 14, 65);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 11));
        g.drawString("Cooking/hour: ", 14, 84);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 11));
        g.drawString("Starting Level: ", 15, 103);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 11));
        g.drawString("Current Level: ", 15, 123);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 11));
        g.drawString("Status: ", 16, 141);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 11));

        g.drawString(util.runtime(startTime), 83, 43);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 11));

        g.drawString(String.valueOf(expGained), 114, 59);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 11));

        g.drawString(util.perHour(expGained, startExperience) + "(" + util.formatNumber(expGained), 83, 79);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 11));

        g.drawString(String.valueOf(ctx.skills.level(7)), 88, 96);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 11));

        g.drawString(String.valueOf(currentLevel), 84, 118);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 11));

        g.drawString(status, 52, 135);
    }
}
