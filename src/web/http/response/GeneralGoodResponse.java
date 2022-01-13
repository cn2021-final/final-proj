package web.http.response;

import java.io.ByteArrayInputStream;

public class GeneralGoodResponse extends GoodResponse {
    private static String response = "success";
    public GeneralGoodResponse() {
        super(new ByteArrayInputStream(response.getBytes()), response.length(), ContentType.TEXT);
    }
}
