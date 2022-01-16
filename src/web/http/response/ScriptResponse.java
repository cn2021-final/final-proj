package web.http.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ScriptResponse extends GoodResponse {
    public ScriptResponse(String filename) throws FileNotFoundException {
        super(new FileInputStream(filename), new File(filename).length(), ContentType.JAVASCRIPT);
    }
}
