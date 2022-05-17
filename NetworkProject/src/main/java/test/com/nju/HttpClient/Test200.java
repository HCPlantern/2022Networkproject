package test.com.nju.HttpClient;

import com.nju.HttpClient.Client.Client;
import com.nju.HttpClient.Components.Common.HeaderFields;
import com.nju.HttpClient.Components.Common.MessageEntityBody;
import com.nju.HttpClient.Components.Common.MessageHeader;
import com.nju.HttpClient.Components.Request.HttpRequest;
import com.nju.HttpClient.Components.Request.Method;
import com.nju.HttpClient.Components.Request.RequestLine;
import com.nju.HttpClient.Components.Response.HttpResponse;
import org.junit.Test;

public class Test200 {

    /**
     * 未设定 UA, 网站将禁止 Cache 缓存
     * 故该测试结果将保持状态码 200
     * @throws Exception
     */
    @Test
    public void testMain() {
        RequestLine requestLine = new RequestLine(Method.GET, "/");
        MessageHeader messageHeader = new MessageHeader();
        messageHeader.putField(HeaderFields.Host, "www.baidu.com");
        messageHeader.putField(HeaderFields.Accept, "*/*");
        messageHeader.putField(HeaderFields.Connection, "keep-alive");
//        messageHeader.putField(HeaderFields.Accept_Encoding, "gzip, deflate, br");
        MessageEntityBody messageBody = new MessageEntityBody();
        HttpRequest httpRequest = new HttpRequest(requestLine, messageHeader, messageBody);
        Client client = new Client();
        HttpResponse httpResponse = client.sendRequest(httpRequest);
        System.out.println(httpResponse.toString());
    }


} 
