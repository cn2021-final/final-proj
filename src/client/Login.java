package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Login {
    DataInputStream input;
    DataOutputStream output;
    Scanner userInput;
    public Login(DataInputStream input, DataOutputStream output) {
        this.input = input;
        this.output = output;
        userInput = new Scanner(System.in);
    }

    public void run() throws IOException {
        System.out.print("username: ");
        String username = userInput.next();
        output.writeUTF(username);
    }
}
