import java.util.Map;

import org.checkerframework.ddbrequest.constantkeys.qual.*;

class PolyTests {
    @PolyConstantKeys Map<String, Object> id(@PolyConstantKeys Map<String, Object> in) {
        return in;
    }

    @ConstantKeys({"x", "y"}) Map<String, Object> callPoly(@ConstantKeys({"x", "y"}) Map<String, Object> in) {
        return id(in);
    }
}