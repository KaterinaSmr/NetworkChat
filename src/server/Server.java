package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private final int PORT = 8189;
    private ServerSocket server = null;
    private Socket socket = null;
    private CopyOnWriteArrayList<ClientHandler> clients;
    private BaseAuthService baseAuthService;

    public Server() {

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен, ждем подключений");
            clients = new CopyOnWriteArrayList<>();
            baseAuthService = new BaseAuthService();
            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");
                clients.add(new ClientHandler(this, socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BaseAuthService getBaseAuthService() {
        return baseAuthService;
    }

    public boolean isNameAvaliable (String name){
        for (ClientHandler c:clients) {
            if (c.getName().equals(name)){
                return false;
            }
        }
        return true;
    }

    public ClientHandler getClientHandlerByName (String name){
        for (ClientHandler c:clients) {
            if (c.getName().equals(name)) return c;
        }
        return null;
    }

    public void broadcast(String msg){
        for (ClientHandler c:clients) {
            if (c.isAuthorized()) {
                c.sendMessage(msg);
            }
        }
    }

    public void broadcastUserList (){
        StringBuffer userList = new StringBuffer("/userlist ");
        for (ClientHandler c:clients) {
            if (c.isAuthorized()) {
                userList.append(c.getName()).append(" ");
            }
        }
        System.out.println(userList);
        for (ClientHandler c:clients) {
            if (c.isAuthorized()) {
                c.sendMessage(userList.toString());
            }
        }
    }

    public void unsubscribeMe (ClientHandler c){
        clients.remove(c);
        broadcastUserList();
    }
}
