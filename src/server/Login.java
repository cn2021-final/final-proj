package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class Login {
    private DataInputStream input;
    private DataOutputStream output;
    private UserManager userManager;
    private String username;
    public Login(DataInputStream input, DataOutputStream output, UserManager userManager) {
        this.input = input;
        this.output = output;
        this.userManager = userManager;
    }

    public void run() throws IOException {
        username = input.readUTF();
        userManager.login(username);
        new Lobby(input, output, username, userManager).run();
        System.out.format("logout from %s\n", username);
    }
}
