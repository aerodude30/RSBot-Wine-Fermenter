import org.powerbot.script.rt4.ClientContext;

/**
 * Created by cjb on 6/27/16.
 */

public class Bank extends Task<ClientContext> {

    private static final int WINE_JUG = 1993;//or 1994
    private static final int WATER_JUG = 1937;//or 1938
    private static final int GRAPES = 1987;//or 1988

    public Bank(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
       return !ctx.inventory.contains(ctx.inventory.select().id(WINE_JUG).poll()); //wine jug
    }

    @Override
    public void execute() {
        if(ctx.bank.inViewport()) {
            ctx.bank.open();
            //check to see if they have more wine and grapes in their bank
            if(ctx.bank.select().id(WATER_JUG).isEmpty() || ctx.bank.select().id(GRAPES).isEmpty()) {
                System.err.println("You've run out of Water and Grapes!");
                ctx.controller.stop();
            }
            //deposit all the wine into the bank
            ctx.bank.depositInventory();

            System.out.println("Withdrawing ingredients....");
            //withdraw ingredients to make wine
            ctx.bank.withdraw(WINE_JUG, 14);
            ctx.bank.withdraw(GRAPES, 14);

            ctx.bank.close();
        }
    }


}
