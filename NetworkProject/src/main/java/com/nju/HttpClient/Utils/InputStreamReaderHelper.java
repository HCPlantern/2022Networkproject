package com.nju.HttpClient.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class InputStreamReaderHelper {
    //    读取一行数据，不包含\n 和 \r\n
//    也就是说没有换行符
//    注意单独一个\r表示回到这一行的行首
    public static String readLine(InputStream inputStream) throws IOException {
        ArrayList<Byte> byteArrayList = new ArrayList<>();
        int temp;
        while (true) {
            temp = inputStream.read();
            if ((char) temp == '\n') {
                return new String(toByteArray(byteArrayList));
            } else if ((char) temp == '\r') {
                temp = inputStream.read();
                if ((char) temp == '\n') {
                    return new String(toByteArray(byteArrayList));
                } else {
                    byteArrayList.add((byte) ('\r' & 0xff));
                    byteArrayList.add((byte) (temp & 0xff));
                    continue;
                }
            } else if (temp == -1) {
                return null;
            }
            byteArrayList.add((byte) (temp & 0xff));
        }
    }

    public static byte[] toByteArray(ArrayList<Byte> byteArrayList) {
        byte[] bytes = new byte[byteArrayList.size()];
        for (int i = 0; i < byteArrayList.size(); i++) {
            bytes[i] = byteArrayList.get(i);
        }
        return bytes;
    }
}
