import org.powerbot.script.rt4.ClientContext;
/**
 * Created by christianbartram on 6/27/16.
 */
public class Ferment extends Task<ClientContext> {

    public Ferment(ClientContext ctx) {
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
