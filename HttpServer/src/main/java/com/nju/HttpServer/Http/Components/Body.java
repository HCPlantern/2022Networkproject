package com.nju.HttpServer.Http.Components;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
@AllArgsConstructor
public class Body implements Component {

    byte[] data;

    public Body() {
        this.data = new byte[0];
    }

    public Body(String data) {
        this.data = data.getBytes(StandardCharsets.UTF_8);
    }

    public void append(String s) {
        String tmp = new String(this.data);
        this.data = (tmp + s).getBytes();
    }

    @Override
    public String ToString() {
        return new String(data);
    }

    @Override
    public byte[] ToBytes() {
        return data;
    }

}
