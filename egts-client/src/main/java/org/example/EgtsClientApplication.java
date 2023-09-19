package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class EgtsClientApplication {
    private final static String HOSTNAME = "server";
    private final static int PORT = 8080;

    public static void main(String[] args) throws IOException {
        try (Socket clientSocket = new Socket(HOSTNAME, PORT);
             InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
             BufferedReader in = new BufferedReader(isr);
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            System.out.println("Connected to " + HOSTNAME + " on port " + PORT);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Input line as bytes array (for example: 0100030B0003005900004A1538003359)");
                var userInput = scanner.nextLine();

                System.out.println("Sending to server:\n" + userInput);
                out.println(userInput);

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("Client received: " + line);
                }
            }
        }
    }
}
