import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import org.checkerframework.ddbrequest.ddbdefinitions.qual.*;
import java.util.Map;
import com.google.common.collect.ImmutableMap;

class SimpleExample {

    void test() {
        Map<String, AttributeValue> keys = ImmutableMap.of(
                ":account_id", null,
                // stick to ACCOUNT perimeters for now
                ":type", null
        );
        QueryRequest.Builder b = QueryRequest.builder()
                .tableName(null)
                .keyConditionExpression(String.format("%s = :account_id", null))
                .expressionAttributeValues(keys)
                .expressionAttributeNames(ImmutableMap.of("#type", "type"))
                .filterExpression("#type = :type");

        QueryRequest.@DDBDefinitions(names={"#type"}, values={":account_id", ":type"}) Builder b2 = b;
    }
}