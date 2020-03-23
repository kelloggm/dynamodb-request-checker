import com.google.common.collect.ImmutableMap;

import java.util.Map;

import org.checkerframework.ddbrequest.constantkeys.qual.*;

class GuavaImmutableMapExample {
    void test() {
        @ConstantKeys({"x"}) Map m1 = ImmutableMap.of("x", null);
        @ConstantKeys({"x", "y"}) Map m2 = ImmutableMap.of("x", null, "y", null);
        @ConstantKeys({"x", "y", "z"}) Map m3 = ImmutableMap.of("x", null, "y", null, "z", null);
        @ConstantKeys({"x", "y", "z", "w"}) Map m4 = ImmutableMap.of("x", null, "y", null, "z", null, "w", null);
        @ConstantKeys({"x", "y", "z", "w", "v"}) Map m5 = ImmutableMap.of("x", null, "y", null, "z", null, "w", null, "v", null);
    }
}