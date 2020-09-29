import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.ddbrequest.definedvalues.qual.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

/**
 * People use many different ways to initialize maps; we should support as many as possible.
 */
class CrazyMaps {

    private static final Map<String, AttributeValue> DEFAULT_KEY =
        com.google.common.collect.ImmutableMap.of(":account_id", null,":type", null);

    void testGuava() {
        Map<String, AttributeValue> keys = DEFAULT_KEY;
        Map<String, String> names = com.google.common.collect.ImmutableMap.of("#type", "type");
        QueryRequest.Builder b = QueryRequest.builder()
                                             .tableName(null)
                                             .keyConditionExpression(String.format("%s = :account_id", null))
                                             .expressionAttributeValues(keys)
                                             .expressionAttributeNames(names)
                                             .filterExpression("#type = :type");

        QueryRequest.@DefinedValues({":account_id", ":type"}) Builder b2 = b;
    }


    void testGuavaBuilder() {
        Map<String, AttributeValue> keys = DEFAULT_KEY;
        Map<String, String> names =
            com.google.common.collect.ImmutableMap.<String, String>builder().put("#type", "type").build();

        QueryRequest.Builder b = QueryRequest.builder()
                                             .tableName(null)
                                             .keyConditionExpression(String.format("%s = :account_id", null))
                                             .expressionAttributeValues(keys)
                                             .expressionAttributeNames(names)
                                             .filterExpression("#type = :type");

        QueryRequest.@DefinedValues({":account_id", ":type"}) Builder b2 = b;
    }

    void testAws() {
        Map<String, AttributeValue> keys = DEFAULT_KEY;
        Map<String, String> names =
            software.amazon.awssdk.utils.ImmutableMap.<String, String>builder().put("#type", "type").build();

        QueryRequest.Builder b = QueryRequest.builder()
                                             .tableName(null)
                                             .keyConditionExpression(String.format("%s = :account_id", null))
                                             .expressionAttributeValues(keys)
                                             .expressionAttributeNames(names)
                                             .filterExpression("#type = :type");

        QueryRequest.@DefinedValues({":account_id", ":type"}) Builder b2 = b;
    }

    void testAws2() {
        Map<String, AttributeValue> keys = DEFAULT_KEY;
        Map<String, String> names = software.amazon.awssdk.utils.ImmutableMap.of("#type", "type");
        QueryRequest.Builder b = QueryRequest.builder()
                                             .tableName(null)
                                             .keyConditionExpression(String.format("%s = :account_id", null))
                                             .expressionAttributeValues(keys)
                                             .expressionAttributeNames(names)
                                             .filterExpression("#type = :type");

        QueryRequest.@DefinedValues({":account_id", ":type"}) Builder b2 = b;
    }

    void testJava() {
        Map<String, AttributeValue> keys = DEFAULT_KEY;

        Map<String, String> names = new HashMap<String, String>() {
            {
                put("#type", "type");
            }
        };

        QueryRequest.Builder b = QueryRequest.builder()
                                             .tableName(null)
                                             .keyConditionExpression(String.format("%s = :account_id", null))
                                             .expressionAttributeValues(keys)
                                             .expressionAttributeNames(names)
                                             .filterExpression("#type = :type");

        // QueryRequest.@DefinedValues({":account_id", ":type"}) Builder b2 = b;
    }

    void testSpecificMap() {
        LinkedHashMap<String, AttributeValue> keys = new LinkedHashMap<String, AttributeValue>() {
            {
                put(":account_id", null);
                put(":type", null);
            }
        };

        LinkedHashMap<String, String> names = new LinkedHashMap<>();
        names.put("#type", "type");

         QueryRequest.Builder b = QueryRequest.builder()
                                             .tableName(null)
                                             .keyConditionExpression(String.format("%s = :account_id", null))
                                             .expressionAttributeValues(keys)
                                             .expressionAttributeNames(names)
                                             .filterExpression("#type = :type");

        // QueryRequest.@DefinedValues({":account_id", ":type"}) Builder b2 = b;
    }

    void testCollections() {
        Map<String, AttributeValue> keys = DEFAULT_KEY;

        Map<String, String> names = Collections.singletonMap("#type", "type");

        QueryRequest.Builder b = QueryRequest.builder()
                                             .tableName(null)
                                             .keyConditionExpression(String.format("%s = :account_id", null))
                                             .expressionAttributeValues(keys)
                                             .expressionAttributeNames(names)
                                             .filterExpression("#type = :type");

        QueryRequest.@DefinedValues({":account_id", ":type"}) Builder b2 = b;
    }

    void testStream() {
        Map<String, AttributeValue> keys = DEFAULT_KEY;

        Map<String, String> names = Stream.of(new AbstractMap.SimpleEntry<>("#type", "type"))
                                          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        QueryRequest.Builder b = QueryRequest.builder()
                                             .tableName(null)
                                             .keyConditionExpression(String.format("%s = :account_id", null))
                                             .expressionAttributeValues(keys)
                                             .expressionAttributeNames(names)
                                             .filterExpression("#type = :type");

//        QueryRequest.@DefinedValues({":account_id", ":type"}) Builder b2 = b;
    }
}