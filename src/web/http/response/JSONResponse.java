package web.http.response;

import java.io.ByteArrayInputStream;

import web.http.HTTPResponse;

public class JSONResponse extends GoodResponse {
    public JSONResponse(String jsonString) {
        super(new ByteArrayInputStream(jsonString.getBytes()), jsonString.length(), ContentType.JSON);
    }
}
