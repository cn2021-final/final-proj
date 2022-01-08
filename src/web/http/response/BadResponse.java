package web.http.response;

import java.io.ByteArrayInputStream;

import web.http.HTTPResponse;

public class BadResponse extends HTTPResponse {
    private static final String defaultResponse =
    "<html><head><title>Bad Request</title></head>"
    + "<body><h1>Bad Request</h1><hr>seems like you've made a mistake</body></html>";
    public BadResponse() {
        super(ResponseType.BAD, new ByteArrayInputStream(defaultResponse.getBytes()), defaultResponse.length());
    }
}
