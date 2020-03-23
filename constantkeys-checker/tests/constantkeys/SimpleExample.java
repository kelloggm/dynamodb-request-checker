import java.util.Map;
import java.util.HashMap;

import org.checkerframework.ddbrequest.constantkeys.qual.*;

class SimpleExample {
    @ConstantKeys({"x", "y"}) Map<String, Object> test_ok() {
        Map<String, Object> result = new HashMap<>();
        result.put("x", null);
        result.put("y", null);
        return result;
    }

    @ConstantKeys({"x", "y"}) Map<String, Object> test_bad() {
        Map<String, Object> result = new HashMap<>();
        result.put("x", null);
        // :: error: return.type.incompatible
        return result;
    }

    @ConstantKeys({"x", "y"}) Map<String, Object> test_ok2() {
        Map<String, Object> result = new HashMap<>();
        result.putIfAbsent("x", null);
        result.putIfAbsent("y", null);
        return result;
    }

    @ConstantKeys({"x", "y"}) Map<String, Object> test_bad2() {
        Map<String, Object> result = new HashMap<>();
        result.putIfAbsent("x", null);
        // :: error: return.type.incompatible
        return result;
    }
}