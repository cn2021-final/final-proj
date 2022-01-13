package web.http.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import web.http.HTTPResponse;

public class HTMLResponse extends GoodResponse {
    public HTMLResponse(String filename) throws FileNotFoundException {
        super(new FileInputStream(filename), new File(filename).length(), ContentType.HTML);
    }
}
