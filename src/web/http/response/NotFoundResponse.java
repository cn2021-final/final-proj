package web.http.response;

import java.io.ByteArrayInputStream;

import web.http.HTTPResponse;

public class NotFoundResponse extends HTTPResponse {
    private static final String defaultResponse =
    "<html><head><title>Not Found</title></head>"
    + "<body><h1>Not Found</h1><hr>seems like you've made a mistake</body></html>";
    public NotFoundResponse() {
        super(ResponseType.NOTFOUND, new ByteArrayInputStream(defaultResponse.getBytes()), defaultResponse.length());
    }
}
