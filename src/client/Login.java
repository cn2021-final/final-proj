package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Login {
    private final DataInputStream input;
    private final DataOutputStream output;
    private final Scanner userInput;
    public Login(DataInputStream input, DataOutputStream output, Scanner userInput) {
        this.input = input;
        this.output = output;
        this.userInput = userInput;
    }

    // for non-interactive purpose only!
    public Login(DataInputStream input, DataOutputStream output) {
        this.input = input;
        this.output = output;
        this.userInput = null;
    }

    // for interactive purpose only!
    public void run() throws IOException {
        System.out.print("username: ");
        String username = userInput.nextLine();
        output.writeUTF(username);
        new Lobby(input, output, userInput).run();
        userInput.close();
    }
}
