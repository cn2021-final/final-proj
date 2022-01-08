package web.http.request;

import java.util.NoSuchElementException;
import java.util.Scanner;

import web.http.HTTPRequest;

public class PostRequest extends HTTPRequest {
    public final long length;
    public PostRequest(String location, Float version, Scanner scanner) {
        super(RequestType.POST, location, version);
        long length = -1;
        String line = "whatever";
        while(!line.isEmpty()) {
            line = scanner.nextLine();
            String[] chunks = line.split(":| ");
            if(chunks[0].toLowerCase().equals("content-length")) length = Long.parseLong(chunks[chunks.length-1]);
        }
        if(length == -1) throw new NoSuchElementException("no content-length in the request");
        this.length = length;
    }
}
