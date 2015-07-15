import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.cache.HttpCacheContext;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.cache.CachingHttpClients;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CachingWebClient {
    public static void main(String[] args) throws Throwable {
        CloseableHttpClient cachingClient = CachingHttpClients.createMemoryBound();
        HttpCacheContext context = HttpCacheContext.create();

        while(true) {
            try {
                executeGet(cachingClient, context, new HttpGet("http://localhost:4567/api1"));
                executeGet(cachingClient, context, new HttpGet("http://localhost:4567/api2"));
            } finally {
                cachingClient.execute(new HttpGet("http://localhost:4567/api2"), context).close();
            }
            Thread.sleep(2000);
        }
    }

    private static void executeGet(CloseableHttpClient cachingClient, HttpCacheContext context, HttpGet getLocation) throws Throwable {
        String body = getBodyFromResponse(cachingClient.execute(getLocation, context));
        CacheResponseStatus responseStatus = context.getCacheResponseStatus();
        switch (responseStatus) {
            case CACHE_HIT:
                System.out.println("Using cached response: " + body);
                break;
            case CACHE_MISS:
                System.out.println("New response from server! : " + body);
                break;
            case VALIDATED:
                System.out.println("Validated response from server: " + body);
                break;
        }
    }

    private static String getBodyFromResponse(HttpResponse response) throws Throwable {
        HttpEntity entity = response.getEntity();
        if (entity.getContentLength() == 0)
            return "";

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()), 65728);
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

}
