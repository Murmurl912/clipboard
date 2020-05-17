package playgorund;

import com.example.clipboard.client.helper.PojoCopyHelper;
import com.example.clipboard.client.entity.Content;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


public class Playground {

    private class Simple {
        public int anInt;

        public Simple() {

        }

        public Simple(int anInt) {
            this.anInt = anInt;
        }

        public int getAnInt() {
            return anInt;
        }

        public void setAnInt(int anInt) {
            this.anInt = anInt;
        }
    }

    @Test
    public void doTest() throws Exception {
        Simple a = new Simple(1);
        Simple b = new Simple();
        BeanUtils.copyProperties(a, b);
    }
}
