package web.http.response;

import java.io.InputStream;

import web.http.HTTPResponse;

public class GoodResponse extends HTTPResponse {
    public GoodResponse(InputStream input, long contentLength, ContentType contentType) {
        super(ResponseType.OK, input, contentLength, contentType);
    }
}
