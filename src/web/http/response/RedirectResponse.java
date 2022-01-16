package web.http.response;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import web.http.HTTPResponse;

public class RedirectResponse extends HTTPResponse {
    private static final byte[] responseText = "You're being redirected...".getBytes();
    private final String location;
    public RedirectResponse(String location) {
        super(ResponseType.FOUND, new ByteArrayInputStream(responseText), responseText.length, ContentType.TEXT);
        this.location = location;
    }

    @Override
    protected String generateHeader() throws IOException {
        return super.generateHeader().strip() + "\r\n"
        + "Location: " + location + "\r\n"
        + "\r\n";
    }
}
