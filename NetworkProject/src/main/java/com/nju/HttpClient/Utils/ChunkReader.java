package com.nju.HttpClient.Utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ChunkReader {
//  对于响应报文的数据部分是分块编码时的读入
//  参数是一个输入流 注意此时输入流已经到了数据部分 前面的请求行和首部已经被读了
//  这里丢弃\r\n
    public static byte[] readChunk(InputStream inputStream) throws IOException {
//        有很多个块
//        每一个块对应一个字节数组
//        最后merge一下
        ArrayList<byte[]> buffer = new ArrayList<>();
        int length;
        String temp = InputStreamReaderHelper.readLine(inputStream);
        length = Integer.parseInt(temp,16);
        while(length!=0){
            byte[] tempBuffer = new byte[length];
            int count=0;
            while (count!=length){
                count+=inputStream.read(tempBuffer,count,length-count);
            }

            buffer.add(tempBuffer);
            inputStream.read();//CR \r
            inputStream.read();//LF \n
            temp = InputStreamReaderHelper.readLine(inputStream);

            length = Integer.parseInt(temp,16);

        }
        inputStream.read();//CR
        inputStream.read();//LF
        return mergeBytes(buffer);
    }
//    将各个分块的数据合并成完整的数据
    public static byte[] mergeBytes(ArrayList<byte[]> bytes) {
        int length = 0;
        int index = 0;
        for(int i=0;i<bytes.size();i++){
            length += bytes.get(i).length;
        }
        byte[] result = new byte[length];
        for(int i=0;i<bytes.size();i++){
            System.arraycopy(bytes.get(i), 0, result, index, bytes.get(i).length);
            index += bytes.get(i).length;
        }
        return result;
    }
}
