import org.powerbot.script.Condition;
import org.powerbot.script.Random;

/**
 * Created by cjb on 7/3/16.
 * This class provides the basic methods to make the script appear more
 * human and break any patterns that the script forms.
 */
public class AntiPattern extends Fermenter {

    Random rng = new Random();

    public AntiPattern(boolean pattern) {
        if(pattern) {
            System.out.println("Checking random skill");
            checkSkill(Random.nextInt(1, 20));
        } else {
            System.out.println("Examining nearby object");
            examineObject();
        }

    }

    private void checkSkill(int skillNumber) {
        //open skill tab and hover over the random skill
        ctx.widgets.component(548, 53).click();
        ctx.widgets.component(320, skillNumber).hover();

        Condition.sleep(Random.nextInt(600, 800));

    }

    private void examineObject() {
        if(ctx.groundItems.select().poll() != null) {
            ctx.groundItems.select().poll().interact("Examine");
            Condition.sleep(Random.nextInt(300, 500));
        }
    }




}
