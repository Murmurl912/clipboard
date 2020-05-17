package playgorund;

import com.example.clipboard.client.helper.PojoCopyHelper;
import com.example.clipboard.client.entity.Content;
import org.junit.jupiter.api.Test;


public class Playground {

    @Test
    public void doTest() throws Exception {
        Content from = new Content();
        from.id = "a";
        from.content = "b";

        Content to = new Content();
        to.id = "b";


        System.out.println("from: " + from);
        System.out.println("to: " + to);

        PojoCopyHelper.merge(from,to);

        System.out.println("from: " + from);
        System.out.println("to: " + to);
    }
}
