import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.Map;
import com.google.common.collect.ImmutableMap;

class FullExample {

    final String perimetersTableName = "myTable";

    void example() {
        Map<String, AttributeValue> keys = ImmutableMap.of(
                ":account_id", null,
                // stick to ACCOUNT perimeters for now
                ":type", null
        );
        QueryRequest request = QueryRequest.builder()
                .tableName(this.perimetersTableName)
                .keyConditionExpression(String.format("%s = :account_id", ""))
                .expressionAttributeValues(keys)
                .expressionAttributeNames(ImmutableMap.of("#type", "type"))
                .filterExpression("#type = :type")
                .build();
    }

    void bad_example_1() {
        Map<String, AttributeValue> keys = ImmutableMap.of(
                // ":account_id" not defined!
                //":account_id", AttributeValue.builder().s(account).build(),
                // stick to ACCOUNT perimeters for now
                ":type", null
        );
        QueryRequest request = QueryRequest.builder()
                .tableName(this.perimetersTableName)
                .keyConditionExpression(String.format("%s = :account_id", ""))
                .expressionAttributeValues(keys)
                .expressionAttributeNames(ImmutableMap.of("#type", "type"))
                .filterExpression("#type = :type")
                // :: error: (ddb.expression.not.defined)
                .build();
    }

    // #s and :s are mixed up in the definitions!
    void bad_example_2() {
        Map<String, AttributeValue> keys = ImmutableMap.of(
                "#account_id", null,
                // stick to ACCOUNT perimeters for now
                "#type", null
        );
        QueryRequest request = QueryRequest.builder()
                .tableName(this.perimetersTableName)
                .keyConditionExpression(String.format("%s = :account_id", ""))
                .expressionAttributeValues(keys)
                .expressionAttributeNames(ImmutableMap.of(":type", "type"))
                .filterExpression("#type = :type")
                // :: error: (ddb.names.and.values.confused) :: error: (ddb.expression.not.defined)
                .build();
    }
}