package web.http.request;

import java.io.IOException;
import java.io.OutputStream;
import java.util.NoSuchElementException;

import web.http.HTTPRequest;
import web.http.HybridInputStream;

public class PostRequest extends HTTPRequest {
    public final long length;
    private final HybridInputStream input;
    public PostRequest(String location, Float version, HybridInputStream input) throws IOException {
        super(RequestType.POST, location, version);
        this.input = input;
        long length = -1;
        String line = "whatever";
        while(!line.isEmpty()) {
            line = input.getLine();
            String[] chunks = line.split(":| ");
            if(chunks[0].toLowerCase().equals("content-length")) length = Long.parseLong(chunks[chunks.length-1]);
        }
        if(length == -1) throw new NoSuchElementException("no content-length in the request");
        this.length = length;
    }

    // reads the data from the post request to `output`.
    // output can be a ByteArrayOutputStream, for reading into a memory buffer
    // or FileOutputStream, for reading into a file
    public void readData(OutputStream output) throws IOException {
        long progress = 0;
        byte[] buf = new byte[4096];
        while(progress < length) {
            if(length - progress < 4096) buf = new byte[(int)(length - progress)];
            input.read(buf);
            output.write(buf);
            progress += 4096;
        }
    }
}
