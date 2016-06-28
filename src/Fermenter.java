import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by christianbartram on 6/27/16.
 */


@Script.Manifest(name = "Wine Fermenter", description = "Converts jugs of water and grapes into fermented wine for fast cooking experience! ")
public class Fermenter extends PollingScript<ClientContext> {

    List<Task> tasks =  new ArrayList<Task>();


    @Override
    public void start() {
        tasks.addAll(Arrays.asList(new Ferment(ctx), new Bank(ctx)));
    }

    @Override
    public void poll() {
        for(Task taskList : tasks) {
            if(taskList.activate()) {
                taskList.execute();
            }
        }
    }
}
