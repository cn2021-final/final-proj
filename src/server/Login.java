package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class Login {
    DataInputStream input;
    DataOutputStream output;
    File rootDir;
    String username;
    public Login(DataInputStream input, DataOutputStream output, File rootDir) {
        this.input = input;
        this.output = output;
        this.rootDir = rootDir;
    }

    public void run() throws IOException {
        username = input.readUTF();
        File userDir = new File(rootDir, username);
        if(!userDir.exists()) {
            System.out.format("new user: %s\n", username);
            userDir.mkdir();
        }
        output.writeUTF("yay");
        System.out.format("login from %s\n", username);
    }
}
