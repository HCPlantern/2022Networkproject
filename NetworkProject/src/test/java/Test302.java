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

public class Test302 {
    private Client client;

    @Before
    public void setUp() {
        client = new Client();
    }

    private void sendRequest(String path) {
        RequestLine requestLine = new RequestLine(Method.GET, path);
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.putField(HeaderFields.Host, "localhost:5000");
        messageHeader.putField(HeaderFields.Connection, "keep-alive");
        MessageEntityBody body = new MessageEntityBody();
        HttpRequest httpRequest = new HttpRequest(requestLine, messageHeader, body);
        HttpResponse httpResponse = client.sendRequest(httpRequest);
        System.out.println(httpRequest);
        System.out.println(httpResponse);
    }

    @Test
    public void test1() {
        sendRequest("/movedIndex2.html");
        sendRequest("/movedIndex2.html");
    }

    @Test
    public void test2() {
        sendRequest("/movedPic2.png");
        sendRequest("/movedPic2.png");
    }

    @Test
    public void test3() {
        sendRequest("/movedPic2.jpg");
        sendRequest("/movedPic2.jpg");
    }
}
