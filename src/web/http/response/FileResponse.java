package web.http.response;

import java.util.List;
import java.util.Arrays;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileResponse extends GoodResponse {
    public final static List<String> imageExtensions = Arrays.asList("jpg", "png");

    public FileResponse(String filename) throws FileNotFoundException {
        super(new FileInputStream(filename), new File(filename).length(), decideContentType(filename));
    }
    
    private static ContentType decideContentType(String filename) {
        String [] parts = filename.split("\\.");
        if (imageExtensions.contains(parts[parts.length - 1]))
            return ContentType.IMAGE;
        return ContentType.DATA;
    }
}
