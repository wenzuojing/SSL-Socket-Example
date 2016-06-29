package org.wzj.test;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyStore;

/**
 * Created by wens on 16-6-29.
 */
public class Client {

    public static void main(String[] args) throws Exception {

        String host = "localhost" ;
        int port = 8080 ;

        Socket socket = getSSLSocket(host, port);

        OutputStream outputStream = socket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        outputStream.write("hi\n".getBytes());
        outputStream.flush();

        String line = reader.readLine();
        System.out.println(line);


    }

    private static Socket getSocket(String host, int port) throws IOException {
        return new Socket(host, port);
    }

    private static Socket getSSLSocket(String host, int port) throws Exception {
        SSLContext sslContext = getSSLContext();
        SSLSocketFactory factory = sslContext.getSocketFactory();
        SSLSocket socket = (SSLSocket)factory.createSocket();
        String[] pwdsuits = socket.getSupportedCipherSuites();
        //socket可以使用所有支持的加密套件
        socket.setEnabledCipherSuites(pwdsuits);
        //默认就是true
        socket.setUseClientMode(true);

        SocketAddress address = new InetSocketAddress(host, port);
        socket.connect(address, 0);
        socket.addHandshakeCompletedListener(new HandshakeCompletedListener() {
            public void handshakeCompleted(HandshakeCompletedEvent handshakeCompletedEvent) {
                System.out.println("completed");
            }
        });
        return socket ;
    }


    public static SSLContext getSSLContext() throws Exception{

        //Trust Key Store
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("/home/wens/zy/test-main/clientTrust.jks"),
                "123456".toCharArray());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);
        TrustManager[] tms = trustManagerFactory.getTrustManagers();


        SSLContext sslContext  = SSLContext.getInstance("TLSV1");
        sslContext.init(null, tms, null);

        return sslContext;
    }

}
