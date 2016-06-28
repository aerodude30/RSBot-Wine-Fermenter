import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;
/**
 * Created by christianbartram on 6/27/16.
 */
public abstract class Task<C extends ClientContext> extends ClientAccessor {

    public Task(C ctx) {
        super(ctx);
    }

    public abstract boolean activate();
    public abstract void execute();
}
