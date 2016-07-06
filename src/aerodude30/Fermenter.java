package aerodude30;

import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Random;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Npc;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by christianbartram on 6/27/16.
 */
@Script.Manifest(name = "Wine Fermenter", description = "Converts jugs of water and grapes into fermented wine for fast cooking experience!", properties = "author=aerodude30; topic=1316401; client=4;")
public class Fermenter extends PollingScript<ClientContext> implements PaintListener {

    private static final int WATER_JUG = 1937, GRAPES = 1987; //1993
    private long startTime;
    private String status = "Waiting to start...";
    private int startExperience = 0, currentLevel = 0;
    private Random rnd = new Random();
    private Util util = new Util();
    Controller controller = ctx.controller;

    //enums for each state in the script
    private enum State {BANK, FERMENT}

    private State getState() {
        return ctx.inventory.select().id(WATER_JUG).count() > 0 ? State.FERMENT : State.BANK;
    }

    @Override
    public void start() {
        startTime = System.currentTimeMillis();
        startExperience = ctx.skills.experience(7);

        GUI g = new GUI();
        g.pack();
        g.setVisible(true);

        try {
            new JSB().initialize("Wine aerodude30.Fermenter", "1.0", status, g.username.getText(), g.email.getText(), "sk_live_82c064b0dd1c62ba83bca66634ab8c42")
                    .actionListener(new Actionable() {
                        @Override
                        public void customScriptAction() {
                            bank();
                        }
                    }, "Emergency Bank")
                    .setSkillExperience("Cooking", ctx.skills.experience(7) - startExperience)
                    .sendData()
                    .onStop(true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!ctx.game.tab(Game.Tab.INVENTORY)) {
            if(ctx.bank.opened()) {
                status = "Closing Bank.";
                ctx.bank.close();
            }
            status = "Opening inventory...";
            ctx.widgets.component(548, 55).click();
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

                    //Anti-Pattern Feature to sometimes choose to open the bank booth and sometimes choose to interact with the banker himself
                    Boolean bankOrBanker =  Random.nextBoolean();

                    if(bankOrBanker) {
                        ctx.bank.open();
                    } else {
                        status = "Interacting with Banker NPC";
                        final Npc banker = ctx.npcs.select().id(2897).shuffle().poll();
                        ctx.camera.turnTo(banker);
                        banker.interact("Bank", banker.name());
                    }

                    if(ctx.inventory.count() != 0){
                        ctx.bank.depositInventory();
                    }

                    status = "Withdrawing ingredients...";

                    ctx.bank.withdraw(WATER_JUG, 14);
                    ctx.bank.withdraw(GRAPES, 14);


                    if(ctx.inventory.select().id(WATER_JUG).count() > 0 && ctx.inventory.select().id(GRAPES).count() > 0) {
                        ctx.bank.close();
                    } else {
                        status = "Out of required items!";
                       // ctx.controller.stop();
                    }
                } else {

                    if(ctx.players.local().tile().distanceTo(ctx.bank.nearest()) >= 5) {
                        status = "Moving closer...";
                        ctx.movement.step(ctx.bank.nearest());
                    }
                }
                break;

            case FERMENT:
                status = "Making wine!";

                ctx.inventory.select().id(1937).poll().interact("Use");
                ctx.inventory.select().id(1987).poll().click();

                ctx.widgets.component(162, 546).interact("Make all");

                //SDN compiles on Java 6 (no lambda)
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        new AntiPattern(Random.nextBoolean());
                        return ctx.inventory.select().id(GRAPES).count() == 0;
                    }
                }, 300, 15);

                break;

            default:
                break;
        }
    }

    /**
     * Aeroscripts Bank method(). This method executes when a user using Aeroscripts
     * clicks the script action button bank.
     */
    public void bank() {
        System.out.println("Banking....");
        Boolean bankOrBanker =  Random.nextBoolean();

        if(bankOrBanker) {
            ctx.bank.open();
        } else {
            status = "Interacting with Banker NPC";
            final Npc banker = ctx.npcs.select().id(2897).shuffle().poll();
            ctx.camera.turnTo(banker);
            banker.interact("Bank", banker.name());
        }

        if(ctx.inventory.count() != 0){
            ctx.bank.depositInventory();
        }

        status = "Withdrawing ingredients...";

        ctx.bank.withdraw(WATER_JUG, 14);
        ctx.bank.withdraw(GRAPES, 14);


        if(ctx.inventory.select().id(WATER_JUG).count() > 0 && ctx.inventory.select().id(GRAPES).count() > 0) {
            ctx.bank.close();
        } else {
            status = "Out of required items!";
            controller.stop();
        }

        if(ctx.players.local().tile().distanceTo(ctx.bank.nearest()) >= 5) {
            status = "Moving closer...";
            ctx.movement.step(ctx.bank.nearest());
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
        g.setColor(new Color(51, 153, 255));
        g.drawLine(12, 31, 134, 31);
        g.setColor(new Color(255, 255, 255));
        g.setFont(new Font("Arial", 0, 14));
        g.drawString("Wine aerodude30.Fermenter", 19, 25);
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
