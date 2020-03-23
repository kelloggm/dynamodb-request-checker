import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import com.google.common.collect.ImmutableMap;

// schaef@ reported this to me as a false positive, but type is used as both a name and value so it should
// be defined as both, not just as one.
class AsBothNameAndValue {

    public static void bla() {
        QueryRequest b = QueryRequest.builder()
                .tableName("hello")
                .keyConditionExpression(":foo")
                .expressionAttributeValues(ImmutableMap.of(":foo", null))
                .expressionAttributeNames(ImmutableMap.of("#type", "type"))
                .filterExpression("#type = :type")
                // :: error: (ddb.expression.not.defined)
                .build();
    }

    public static void bla2() {
        QueryRequest b = QueryRequest.builder()
                .tableName("hello")
                .keyConditionExpression(":foo")
                .expressionAttributeValues(ImmutableMap.of(":foo", null))
                .expressionAttributeNames(ImmutableMap.of("#type", "type"))
                .filterExpression("#type = :foo")
                .build();
    }
}