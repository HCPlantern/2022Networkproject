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

public class TestCode405 {
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

    private void sendRequest(String path, String savePath) throws IOException {
        RequestLine requestLine = new RequestLine(Method.POST, path);
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.putField(HeaderFields.Host, "localhost:5000");
        messageHeader.putField(HeaderFields.Connection, "keep-alive");
        MessageEntityBody body = new MessageEntityBody();
        HttpRequest httpRequest = new HttpRequest(requestLine, messageHeader, body);
        HttpResponse httpResponse = client.sendRequest(httpRequest);

        if (savePath != null) {
            try {
                httpResponse.saveBody(savePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println(httpResponse);
        }
    }

    @Test
    public void test1() {
        try {
            sendRequest("/style.css", path + "style.css");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
