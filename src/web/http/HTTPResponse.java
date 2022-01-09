package web.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import web.http.response.ResponseType;

public class HTTPResponse {
    public final ResponseType type;
    public final InputStream data;
    public final long contentLength;
    protected HTTPResponse(ResponseType type, InputStream data, long contentLength) {
        this.data = data;
        this.type = type;
        this.contentLength = contentLength;
    }

    public void WriteResponse(DataOutputStream output) throws IOException {
        output.writeBytes(generateHeader());
        byte[] buf = new byte[4096];
        while(data.available() > 0) {
            if(data.available() < 4096) buf = new byte[data.available()];
            data.read(buf);
            output.write(buf);
        }
    }

    private String generateHeader() throws IOException {
        return "HTTP/1.1 " + type.code + " " + type.meaning + "\r\n"
        + "content-length: " + contentLength + "\r\n"
        + "\r\n";
    }
}