import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.google.gson.Gson;

public class Handler implements RequestHandler<APIGatewayV2HTTPEvent,APIGatewayV2HTTPResponse>{
    
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        Gson gson = new Gson();
        Employee employee = gson.fromJson(event.getBody(), Employee.class);
        employee.setId(UUID.randomUUID().toString());
        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.defaultClient();
        String tableName = System.getenv("TABLE_NAME");

        Map<String, AttributeValue> dbParams = new HashMap<>();
        dbParams.put("id", new AttributeValue(employee.getId()));
        dbParams.put("name", new AttributeValue(employee.getName()));
        dbParams.put("department", new AttributeValue(employee.getDepartment()));
        dbParams.put("salary", new AttributeValue(String.valueOf(employee.getSalary())));

        dynamoDB.putItem(tableName, dbParams);
        
        APIGatewayV2HTTPResponse response = new APIGatewayV2HTTPResponse();
        response.setStatusCode(200);
        response.setBody(gson.toJson(employee));

        return response;
    }
}
