package web.http.response;

public enum ContentType {
    HTML("text/html"), DATA("application/octet-stream"), IMAGE("image/png");

    public final String meaning;
    ContentType(String meaning) { this.meaning = meaning; }
}
