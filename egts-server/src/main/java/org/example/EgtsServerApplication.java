package org.example;

import org.example.model.Package;

import java.io.*;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HexFormat;

public class EgtsServerApplication {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        var serverSocket = new ServerSocket(PORT);
        System.out.println("Listening on port " + PORT);
        while (true) {
            try (var socket = serverSocket.accept();
                 var inputStreamReader = new InputStreamReader(socket.getInputStream());
                 var bufferedReader = new BufferedReader(inputStreamReader);
                 var out = new PrintWriter(socket.getOutputStream(), true)
            ) {

                System.out.println("Connection accepted");
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    System.out.println("Server received: " + line);
                    var bytes = HexFormat.of().parseHex(line);
                    var result = new Package();

                    var state = result.decode(bytes);
                    System.out.println("State: " + state);
                    System.out.println("Package: " + result);

                    out.println("Answer from server: " + Arrays.toString(state.encode()));
                }
            }
        }
    }

}
