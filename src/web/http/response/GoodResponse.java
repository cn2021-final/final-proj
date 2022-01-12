package web.http.response;

import java.io.InputStream;

import web.http.HTTPResponse;

public class GoodResponse extends HTTPResponse {
    public GoodResponse(InputStream input, long contentLength) {
        // TODO: finish the response text
        super(ResponseType.OK, input, contentLength);
    }
}
