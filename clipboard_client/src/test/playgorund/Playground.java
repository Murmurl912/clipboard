package playgorund;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;


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
