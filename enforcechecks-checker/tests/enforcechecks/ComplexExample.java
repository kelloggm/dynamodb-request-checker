import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.google.common.collect.ImmutableMap;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.ddbrequest.constantkeys.qual.PolyConstantKeys;

class ComplexExample {
    Object listImpl(final String key,
                    final @Nullable Integer limit,
                    final Object start,
                    final Object end) {
        final Map<String, String> expressionAttributeNames = new HashMap<>();
        final Map<String, Object> expressionAttributeValues = new HashMap<>();

        final StringBuilder keyConditionExpressionBuilder = new StringBuilder();
        keyConditionExpressionBuilder.append("#hk = :h");
        expressionAttributeNames.put("#hk", null);
        expressionAttributeValues.put(":h", key);

        if (start != null && end != null) {
            keyConditionExpressionBuilder.append(" AND #rk BETWEEN :s AND :e");
            expressionAttributeNames.put("#rk", null);
            expressionAttributeValues.put(":s", start.toString());
            expressionAttributeValues.put(":e", end.toString());
        } else if (start != null) {
            keyConditionExpressionBuilder.append(" AND #rk >= :s");
            expressionAttributeNames.put("#rk", null);
            expressionAttributeValues.put(":s", start.toString());
        } else if (end != null) {
            keyConditionExpressionBuilder.append(" AND #rk <= :e");
            expressionAttributeNames.put("#rk", null);
            expressionAttributeValues.put(":e", end.toString());
        }

        // Have dynamo filter out expired timestamps so we can avoid transferring data we wont use.
        final String filterExpression = "attribute_not_exists(#ttl) OR #ttl > :n";
        expressionAttributeNames.put("#ttl", null);
        expressionAttributeValues.put(":n", null);

        final QueryRequest request =
                QueryRequest.builder()
                        .tableName("m_tableName")
                        .keyConditionExpression(keyConditionExpressionBuilder.toString())
                        .filterExpression(filterExpression)
                        .expressionAttributeNames(expressionAttributeNames)
                        .expressionAttributeValues(fromSimpleMap(expressionAttributeValues))
                        .limit(limit)
                        .build();

        return null;
    }

    @SuppressWarnings("constantkeys") // the output has the same keys as the input
    public static @PolyConstantKeys Map<String, AttributeValue> fromSimpleMap(
            @PolyConstantKeys Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        // row with multiple attributes
        Map<String, AttributeValue> result = new LinkedHashMap<String, AttributeValue>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            result.put(entry.getKey(), toAttributeValue(entry.getValue()));
        }
        return result;
    }

    public static AttributeValue toAttributeValue(Object value) {
        // this is actually like a 100-line method, but the result isn't relevant to this test
        return null;
    }
}