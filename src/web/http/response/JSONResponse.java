package web.http.response;

import java.io.ByteArrayInputStream;

public class JSONResponse extends GoodResponse {
    public JSONResponse(String jsonString) {
        super(new ByteArrayInputStream(jsonString.getBytes()), jsonString.length(), ContentType.JSON);
    }
}
