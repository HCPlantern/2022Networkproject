import com.nju.HttpClient.Client.Client;
import com.nju.HttpClient.Components.Common.HeaderFields;
import com.nju.HttpClient.Components.Common.MessageEntityBody;
import com.nju.HttpClient.Components.Common.MessageHeader;
import com.nju.HttpClient.Components.Request.HttpRequest;
import com.nju.HttpClient.Components.Request.Method;
import com.nju.HttpClient.Components.Request.RequestLine;
import com.nju.HttpClient.Components.Response.HttpResponse;
import org.junit.Test;

public class TestKeepAlive {
    private Client client=new Client();
    private void request(String path,boolean enableAlive){
        RequestLine requestLine=new RequestLine(Method.GET,path);
        MessageHeader messageHeader=new MessageHeader();
        messageHeader.putField(HeaderFields.Host,"localhost:5000");
        if(enableAlive){
            messageHeader.putField(HeaderFields.Connection,"keep-alive");
        }
        messageHeader.putField(HeaderFields.Content_Length,"0");
        MessageEntityBody messageEntityBody=new MessageEntityBody();
        HttpRequest httpRequest=new HttpRequest(requestLine,messageHeader,messageEntityBody);
        try {
            HttpResponse response=client.sendRequest(httpRequest);
            response.saveBody(path);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testKeepAlive(){
        request("src/main/resources/StaticResources/favicon.ico",false);
        request("src/main/resources/StaticResources/favicon.ico",false);
        request("src/main/resources/StaticResources/favicon.ico",false);
        request("src/main/resources/StaticResources/favicon.ico",true);
        request("src/main/resources/StaticResources/favicon.ico",true);
    }
}
