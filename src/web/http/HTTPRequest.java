package web.http;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import web.http.request.BadRequest;
import web.http.request.GetRequest;
import web.http.request.PostRequest;
import web.http.request.RequestType;

public class HTTPRequest {
    public final String location;
    public final Float version;
    public final RequestType type;

    public HTTPRequest(RequestType type, String location, Float version) {
        this.location = location;
        this.version = version;
        this.type = type;
    }

    public static HTTPRequest parseRequest(DataInputStream input) throws IOException {
        Scanner scanner = new Scanner(input);
        HTTPRequest request = new BadRequest();
        try {
            String[] line = scanner.nextLine().split(" ");
            String type = line[0];
            String location = line[1];
            Float version = Float.parseFloat(line[2].split("/")[1]);
            switch(type) {
                case "GET":
                request = new GetRequest(location, version);
                break;
                case "POST":
                request = new PostRequest(location, version, scanner);
                break;
            }
        }
        catch(IndexOutOfBoundsException | NoSuchElementException e) {}
        scanner.close();
        return request;
    }
}