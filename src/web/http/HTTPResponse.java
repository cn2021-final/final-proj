package web.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import web.http.response.ResponseType;
import web.http.response.ContentType;

public class HTTPResponse {
    public final ResponseType type;
    public final InputStream data;
    public final long contentLength;
    public final ContentType contentType;
    protected HTTPResponse(ResponseType type, InputStream data, long contentLength, ContentType contentType) {
        this.data = data;
        this.type = type;
        this.contentLength = contentLength;
        this.contentType = contentType;
    }

    public void WriteResponse(DataOutputStream output) throws IOException {
        output.writeBytes(generateHeader());
        byte[] buf = new byte[4096];
        long progress = 0;
        while(progress < contentLength) {
            if(contentLength - progress < 4096) buf = new byte[(int)(contentLength + progress)];
            data.read(buf);
            output.write(buf);
            progress += 4096;
        }
    }

    protected String generateHeader() throws IOException {
        return "HTTP/1.1 " + type.code + " " + type.meaning + "\r\n"
        + "content-length: " + contentLength + "\r\n"
        + "Content-Type: " + contentType.meaning + "\r\n"
        + "\r\n";
    }
}
