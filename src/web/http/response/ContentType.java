package web.http.response;

public enum ContentType {
    HTML("text/html"), DATA("application/octet-stream"), IMAGE("image/png"),
    JAVASCRIPT("text/javascript"), JSON("application/json"), TEXT("text/raw");

    public final String meaning;
    ContentType(String meaning) { this.meaning = meaning; }
}
