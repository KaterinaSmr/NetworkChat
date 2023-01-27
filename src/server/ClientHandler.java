package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private final Server server;
    private final Socket socket;
    private String name;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean isAuthorized;

    public ClientHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        name = "unknown";
        isAuthorized = false;
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(()->{
            try {
                Thread.sleep(120000);
                if (!isAuthorized) {
                sendMessage("Время ожидания сервера истекло. Для продолжения необходимо переподключиться");
                sendMessage("/end");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
                ).start();

        new Thread(() -> {
            try {
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/auth")) {
                        String[] elements = msg.split(" ");
                        String checkName = server.getBaseAuthService().getNameByLoginPass(elements[1], elements[2]);
                        if (checkName != null) {
                            if (server.isNameAvaliable(checkName)) {
                                isAuthorized = true;
                                sendMessage("/authok");
                                name = checkName;
                                server.broadcast(name + " присоединился");
                                server.broadcastUserList();
                                break;
                            } else {
                                sendMessage("Пользователь уже залогинен");
                            }
                        } else {
                            sendMessage("Неверный логин/пароль");
                        }
                    } else if (msg.startsWith("/end")) {
                        server.broadcast(name + " покидает чат");
                        server.unsubscribeMe(this);
                        break;
                    } else {
                        sendMessage("Сначала нужно залогиниться");
                    }
                }
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        if (msg.startsWith("/end")) {
                            server.broadcast(name + " покидает чат");
                            server.unsubscribeMe(this);
                            break;
                        } else if (msg.startsWith("/w")) {
                            String nameTo = msg.split(" ")[1];
                            String message = msg.substring(4+nameTo.length());
                            ClientHandler to = server.getClientHandlerByName(nameTo);
                            if (to != null){
                                to.sendMessage("(private from " + this.name + "): " + message);
                                this.sendMessage("(private to " + to.getName() + "): " + message);
                            } else {
                                this.sendMessage("Пользователь " + nameTo + " не залогинен");
                            }
                        }
                    } else {
                        server.broadcast(name + ": " + msg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    server.unsubscribeMe(this);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendMessage (String msg){
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }
}
