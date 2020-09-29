import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import org.checkerframework.ddbrequest.requirednames.qual.*;

class SimpleExample {

    void test() {
        QueryRequest.Builder b = QueryRequest.builder();
        b.keyConditionExpression(":t0");
        b.filterExpression("#t1 = :t2");
        QueryRequest.@RequiredNames({"t1"}) Builder b2 = b;
    }

    void testFluent() {
        QueryRequest.Builder b = QueryRequest.builder()
                .keyConditionExpression(":t0")
                .filterExpression("#t1 = :t2");
        QueryRequest.@RequiredNames({"t1"}) Builder b2 = b;
    }
}