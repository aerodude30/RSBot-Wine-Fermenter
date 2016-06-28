import org.powerbot.script.rt4.ClientContext;

/**
 * Created by christianbartram on 6/27/16.
 */
public class Bank extends Task<ClientContext> {

    public Bank(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        return false;
    }

    @Override
    public void execute() {

    }


}
