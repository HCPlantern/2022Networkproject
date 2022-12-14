import com.nju.HttpClient.Client.Client;
import com.nju.HttpClient.Components.Common.HeaderFields;
import com.nju.HttpClient.Components.Common.MessageEntityBody;
import com.nju.HttpClient.Components.Common.MessageHeader;
import com.nju.HttpClient.Components.Request.HttpRequest;
import com.nju.HttpClient.Components.Request.Method;
import com.nju.HttpClient.Components.Request.RequestLine;
import com.nju.HttpClient.Components.Response.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TestKeepAlive {
    private Client client;

    private final String path = "src/main/resources/ResponseResources/";

    @Before
    public void setUp() {
        client = new Client();
        File file = new File(path);
        if (!file.isDirectory()) {
            file.mkdir();
        }
    }
    private void sendRequest(String path, boolean enableAlive, String savePath) {
        RequestLine requestLine = new RequestLine(Method.GET, path);
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.putField(HeaderFields.Host, "localhost:5000");
        if (enableAlive) {
            messageHeader.putField(HeaderFields.Connection, "keep-alive");
        }
        messageHeader.putField(HeaderFields.Content_Length, "0");
        MessageEntityBody messageEntityBody = new MessageEntityBody();
        HttpRequest httpRequest = new HttpRequest(requestLine, messageHeader, messageEntityBody);
        try {
            HttpResponse response = client.sendRequest(httpRequest);
            if (savePath != null) {
                try {
                    response.saveBody(savePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testKeepAlive() {
        sendRequest("/favicon.ico", false, path + "favicon.ico");
        sendRequest("/favicon.ico", false, path + "favicon.ico");
        sendRequest("/favicon.ico", false, path + "favicon.ico");
        sendRequest("/favicon.ico", true, path + "favicon.ico");
        sendRequest("/favicon.ico", true, path + "favicon.ico");
    }
}
