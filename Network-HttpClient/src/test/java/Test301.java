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

import java.io.IOException;

public class Test301 {
    private Client client;

    private final String path = "src/main/resources/ResponseResources/";

    @Before
    public void setUp() {
        client = new Client();
    }

    private void sendRequest(String path, String savePath) {
        RequestLine requestLine = new RequestLine(Method.GET, path);
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.putField(HeaderFields.Host, "localhost:5000");
        messageHeader.putField(HeaderFields.Connection, "keep-alive");
        MessageEntityBody body = new MessageEntityBody();
        HttpRequest httpRequest = new HttpRequest(requestLine, messageHeader, body);
        HttpResponse httpResponse = client.sendRequest(httpRequest);
        if (savePath != null) {
            try {
                httpRequest.getRequestEntityBody().save(savePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void test1() {
        sendRequest("/movedIndex.html", path + "movedIndex.html");
        sendRequest("/movedIndex.html", path + "movedIndex.html");
    }

    @Test
    public void test2() {
        sendRequest("/movedPic.png", path + "movedPic.png");
        sendRequest("/movedPic.png", path + "movedPic.png");
    }
}
