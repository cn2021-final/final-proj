package web.http.request;

import web.http.HTTPRequest;

public class GetRequest extends HTTPRequest {
    public GetRequest(String location, Float version) {
        super(RequestType.GET, location, version);
    }
}
