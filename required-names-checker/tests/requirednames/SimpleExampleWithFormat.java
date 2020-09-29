import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import org.checkerframework.ddbrequest.requirednames.qual.*;

class SimpleExampleWithFormat {

    private final static String ACCOUNT_ID_FIELD = "";

    void test() {
        QueryRequest.Builder b = QueryRequest.builder();
        b.keyConditionExpression(String.format("%s = :t0", ACCOUNT_ID_FIELD));
        b.filterExpression("#t1 = :t2");
        QueryRequest.@RequiredNames({"t1"}) Builder b2 = b;
    }

    void testFluent() {
        QueryRequest.Builder b = QueryRequest.builder()
                .keyConditionExpression(String.format("%s = :t0", ACCOUNT_ID_FIELD))
                .filterExpression("#t1 = :t2");
        QueryRequest.@RequiredNames({"t1"}) Builder b2 = b;
    }
}