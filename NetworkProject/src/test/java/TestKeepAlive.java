import com.nju.HttpClient.Client.Client;
import com.nju.HttpClient.Components.Common.HeaderFields;
import com.nju.HttpClient.Components.Common.MessageEntityBody;
import com.nju.HttpClient.Components.Common.MessageHeader;
import com.nju.HttpClient.Components.Request.HttpRequest;
import com.nju.HttpClient.Components.Request.Method;
import com.nju.HttpClient.Components.Request.RequestLine;
import com.nju.HttpClient.Components.Response.HttpResponse;
import org.junit.Test;

import java.io.IOException;

public class TestKeepAlive {
    private Client client=new Client();
    private void sendRequest(String path,boolean enableAlive,String savePath){
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
            if (savePath != null) {
                try {
                    response.saveBody(savePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println(response);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    public void testKeepAlive(){
        sendRequest("/favicon.ico",false,"favicon.ico");
        sendRequest("/favicon.ico",false,"favicon.ico");
        sendRequest("/favicon.ico",false,"favicon.ico");
        sendRequest("/favicon.ico",true,"favicon.ico");
        sendRequest("/favicon.ico",true,"favicon.ico");
    }
}
