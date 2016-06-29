package org.wzj.test;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

/**
 * Created by wens on 16-6-29.
 */
public class Server {

    public static void main(String[] args) throws Exception {

        int port  = 8080 ;

        ServerSocket serverSocket = getSSLServerSocket(port);

        while (true){

            final Socket socket = serverSocket.accept();
            //socket.setSoTimeout(2000);
            System.out.println("accept : " + socket.getRemoteSocketAddress() );
            new Thread(){
                @Override
                public void run() {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String line = null ;
                        while ((line = bufferedReader.readLine()) != null ){
                            if(line.equals("quit")){
                                System.out.println("close : " + socket.getRemoteSocketAddress());
                                socket.close();
                                break ;
                            }

                            System.out.println("Receive data from " + socket.getRemoteSocketAddress() + " : " + line );
                            socket.getOutputStream().write(("pong:" + line +"\n" ).getBytes());
                            socket.getOutputStream().flush();

                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } .start();



        }

    }

    private static ServerSocket getServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    private static ServerSocket getSSLServerSocket(int port) throws Exception {

        SSLContext sslContext = getSSLContext();
        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        //只是创建一个TCP连接，SSL handshake还没开始
        //客户端或服务端第一次试图获取socket输入流或输出流时，
        //SSL handshake才会开始
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket();
        String[] pwdsuits = sslServerSocket.getSupportedCipherSuites();
        sslServerSocket.setEnabledCipherSuites(pwdsuits);
        //默认是client mode，必须在握手开始之前调用
        sslServerSocket.setUseClientMode(false);
        //即使客户端不提供其证书，通信也将继续
        sslServerSocket.setWantClientAuth(true);
        sslServerSocket.bind(new InetSocketAddress(port));
        return sslServerSocket ;

    }


    public static SSLContext getSSLContext() throws Exception{

        //Trust Key Store
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(new FileInputStream("/home/wens/zy/test-main/server.jks"),
                "123456".toCharArray());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);
        TrustManager[] tms = trustManagerFactory.getTrustManagers();


        SSLContext sslContext  = SSLContext.getInstance("TLSV1");
        sslContext.init(null, tms, null);

        return sslContext;
    }
}
