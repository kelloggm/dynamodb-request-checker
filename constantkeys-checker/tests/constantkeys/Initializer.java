import java.util.Map;
import java.util.HashMap;

import org.checkerframework.ddbrequest.constantkeys.qual.*;

// @skip-test because the CF doesn't support this initialization block pattern in an anon. class

class Initializer {
    void test() {
        @ConstantKeys({"#type"}) Map<String, String> names = new HashMap<String, String>() {
            {
                put("#type", "type");
            }
        };
    }
}