package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Login {
    private DataInputStream input;
    private DataOutputStream output;
    private Scanner userInput;
    public Login(DataInputStream input, DataOutputStream output) {
        this.input = input;
        this.output = output;
        userInput = new Scanner(System.in);
    }

    public void run() throws IOException {
        System.out.print("username: ");
        String username = userInput.nextLine();
        output.writeUTF(username);
        new Lobby(input, output, userInput).run();
        userInput.close();
    }
}
