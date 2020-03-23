// This test checks that the v1 sdk version of DDB is supported.

import java.util.Map;
import com.google.common.collect.ImmutableMap;

import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

class V1SdkExample {

    private final Map<String, AttributeValue> attributeValueMapGood = ImmutableMap.of(":account_id", null);
    private final Map<String, AttributeValue> attributeValueMapBad = ImmutableMap.of(":acount_id", null);

    private final String ACCOUNT_FILTER = ":account_id";

    private AmazonDynamoDB ddbClient;

    void test_good() {
        QueryRequest queryRequest = new QueryRequest()
                .withTableName(null)
                .withKeyConditionExpression("AccountId = " + ACCOUNT_FILTER)
                .withExpressionAttributeValues(attributeValueMapGood);

        QueryResult queryResult = ddbClient.query(queryRequest);
    }

    void test_bad() {
        QueryRequest queryRequest = new QueryRequest()
                .withTableName(null)
                .withKeyConditionExpression("AccountId = " + ACCOUNT_FILTER)
                .withExpressionAttributeValues(attributeValueMapBad);

        // :: error: ddb.expression.not.defined
        QueryResult queryResult = ddbClient.query(queryRequest);
    }
}
