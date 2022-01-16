package web.http.response;

public enum ResponseType {
    OK(200, "OK"), NOTFOUND(404, "Not Found"), BAD(400, "Bad Requset"), FOUND(302, "Found");

    public final int code;
    public final String meaning;
    ResponseType(int code, String meaning) { 
        this.code = code;
        this.meaning = meaning;
    }
}
