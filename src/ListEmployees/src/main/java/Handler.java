import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;

public class Handler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        Gson gson = new Gson();

        List<Employee> employees = new ArrayList<>();
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
        String tableName = System.getenv("TABLE_NAME");

        ScanRequest request = new ScanRequest(tableName);
        ScanResult result = dynamoDB.scan(request);
        result.getItems().forEach(item -> {
            Employee employee = new Employee(item.get("id").getS(), item.get("name").getS(),
                    item.get("department").getS(), Double.parseDouble(item.get("salary").getS()));
            employees.add(employee);
        });

        APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
        response.setStatusCode(200);
        response.setBody(gson.toJson(employees));

        return response;
    }

}
