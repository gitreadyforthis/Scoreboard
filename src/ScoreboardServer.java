import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;


//Must support mutlple clients at the same time  Some version of a Player Handler
//Questions are only specify to a game: Keep track of players  Player handler
//Authenticate players by name
//PLayer handler is how the player(Client) communicates with the server and the server communicates with the player(Client)

/**
 * Collect all the Scoreboard Server classes in this file. Note that this file
 * MUST BE called ScoreboardServer.java
 */
//middle man between client and game
class ScoreboardServer {
    private ArrayList<ChallengeResponseGame> games;
    private SSLContext sc;

    //Arraylist to hold the list of ChallengeResponseGame
    ScoreboardServer(ArrayList<ChallengeResponseGame> games) {
        this.games = games;
    }

    void startServer(int sslPort) {

        //Creates a server socket, bound to the specified port.
        SSLServerSocket server = null;
        try {
            server = (SSLServerSocket) sc.getServerSocketFactory().createServerSocket(sslPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean done = true;
        //listens for activity on the server
        while (true) {
            Socket client = null;
            try {
                if (server != null) {
                    client = server.accept();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader input = null;
            try {
                if (client != null) {
                    input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            PrintWriter output = null;
            try {
                if (client != null) {
                    output = new PrintWriter(client.getOutputStream(), true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            ScoreboardClient clientThread = new ScoreboardClient(input, output, games);
            Thread t = new Thread(clientThread);
            t.start();
            // register callback
            clientThread.registerCallback(clientThread);

        }

    }

    //setup for the ssl Connection
    //This will use the SSL with keystore and pasword
    //This will be ssl
    void setupSSL(String s, String password) {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        InputStream ksIs = null;
        try {
            ksIs = new FileInputStream(s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (ks != null) {
                ks.load(ksIs, password.toCharArray());
            }
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }
        KeyManagerFactory kmf = null;
        try {
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            if (kmf != null) {
                kmf.init(ks, password.toCharArray());
            }
        } catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            sc = SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            if (kmf != null && sc != null) {

                sc.init(kmf.getKeyManagers(), null, new SecureRandom());
            }
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }
    //Start the server itself
}
