package web.http.request;

import web.http.HTTPRequest;

public class BadRequest extends HTTPRequest {
    public BadRequest() {
        super(RequestType.BAD, "", 0f);
    }
}
