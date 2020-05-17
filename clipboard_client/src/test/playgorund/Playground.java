package playgorund;

import com.example.clipboard.client.helper.PojoCopyHelper;
import com.example.clipboard.client.entity.Content;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class Playground {

    private class Simple {
        public int anInt;

        public Simple(int anInt) {
            this.anInt = anInt;
        }
    }

    @Test
    public void doTest() throws Exception {
        ObservableList<Simple> simples = FXCollections.observableArrayList();

        List<Simple> simpleList = Arrays.asList(
                new Simple(0),
                new Simple(1),
                new Simple(2),
                new Simple(3),
                new Simple(4),
                new Simple(5),
                new Simple(6),
                new Simple(7),
                new Simple(8),
                new Simple(9),
                new Simple(10));

        simples.addListener(new ListChangeListener<Simple>() {
            @Override
            public void onChanged(Change<? extends Simple> change) {
                System.out.println(change);
            }
        });

        simples.addAll(simpleList);
        simples.get(0).anInt = 2;

    }
}
