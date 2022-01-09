package web.http;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HybridInputStream extends DataInputStream {

    public HybridInputStream(InputStream in) {
        super(in);
    }

    public String getLine() throws IOException {
        String s = "";
        char c;
        do {
            c = (char) readByte();
            if(c == '\n') break;
            s += c;
        } while(c != -1);
        return s.strip();
    }
    
}
