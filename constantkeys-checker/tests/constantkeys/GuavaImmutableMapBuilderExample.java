import com.google.common.collect.ImmutableMap;

import java.util.Map;

import org.checkerframework.ddbrequest.constantkeys.qual.*;

@SuppressWarnings("unchecked")
class GuavaImmutableMapBuilder {
    void testGuavaBuilder() {

        ImmutableMap.@ConstantKeys("#type") Builder b = ImmutableMap.<String, String>builder().put("#type", "type");

        @ConstantKeys("#type") Map<String, String> names0 = b.build();

        @ConstantKeys("#type") Map<String, String> names =
                ImmutableMap.<String, String>builder().put("#type", "type").build();
    }
}