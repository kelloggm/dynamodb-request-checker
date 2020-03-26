## DynamoDB Request Checker

An accumulation analysis for checking that requests to AWS DynamoDB are well-formed.

When creating a `QueryRequest` to send to DynamoDB, a programmer specifies
a ["Key Condition Expression"](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Query.html#Query.KeyConditionExpressions), 
a ["Filter Expression"](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Query.html#Query.FilterExpression), or both.
These expression can include variables and meta-variables: ["Expression Attribute Values"](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.ExpressionAttributeValues.html) and
["Expression Attribute Names"](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.ExpressionAttributeNames.html).
If the attribute values and attribute names used in the key condition or filter expression
are not actually defined in the query, then the query will fail at run time when
attempting to connect to the DynamoDB table.

This checker detects mismatches between the key condition or filter expression supplied
to a `QueryRequest` and the actual attribute values and attribute names supplied to that
request. This checker is (for now) an unsound bug-finding tool: it is not guaranteed
to find such errors. The checker handles both the 1.x and 2.x AWS DynamoDB SDK versions.

For example, consider the following `QueryRequest`:

```
        Map<String, AttributeValue> keys = ImmutableMap.of(
                ":account_id", account_id,
                ":type", type
        );
        QueryRequest request = QueryRequest.builder()
                .tableName(this.perimetersTableName)
                .keyConditionExpression(String.format("%s = :account_id", ""))
                .expressionAttributeValues(keys)
                .expressionAttributeNames(ImmutableMap.of("#type", "type"))
                .filterExpression("#type = :type")
                .build();
```

The checker will verify that this example is correct: the used values are ":account_id"
and ":type", which are both defined in `keys`; the used name is "#type" which is defined
in the `ImmutableMap.of` expression. Neglecting to include any of these will result in an
error.

### Guarantees

This checker will always issue an error for a malformed DynamoDB `QueryRequest` *if* the arguments
to both `filterExpression` and `keyConditionExpression` are compile-time constants.

### Using the checker

This checker is in an experimental state, and is therefore not available on Maven Central.

To use it, clone this repository and build from source by invoking `./gradlew assemble`.

Next, run `./gradlew printClasspath` and put the result on the classpath when compiling
the target Java code, and add the option
`-processor org.checkerframework.ddbrequest.enforcechecks.EnforceChecksChecker` to the
target `javac` invocation.

## License

This library is licensed under the MIT-0 License. See the LICENSE file.

