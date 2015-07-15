import spark.Response;

import static spark.Spark.*;

public class SparkJavaServer {
    public static void main(String[] args) {
        final int[] apiCallCounts = {0,0};

        get("/api1", (req, res) -> generateAPIResponse(1, ++apiCallCounts[0], res));
        get("/api2", (req, res) -> {
            res.header("cache-control", "public, max-age=10");
            return generateAPIResponse(2, ++apiCallCounts[1], res);
        });
    }

    private static Object generateAPIResponse(int apiId, int callCount, Response res) {
        String response = "API " + apiId + " called " + callCount + " times";
        System.out.println("Returning: " + response);
        return response;
    }
}
