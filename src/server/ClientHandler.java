package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler {
    private Server server;
    private Socket socket;
    DataOutputStream out;
    DataInputStream in;
    String nick;


    public ClientHandler(Server server, Socket socket) {
        try {
            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    // цикл авторизации.
                    while (true) {
                        String str = in.readUTF();
                        if(str.startsWith("/auth")){
                            String[] token = str.split(" ");
                            String newNick =
                                    AuthService.getNickByLoginAndPass(token[1],token[2]);
                            System.out.println(newNick);
                            if(newNick != null & !server.equalsClient(newNick)){
//                                System.out.println("conteins " + server.conteinsClient(newNick));
//                                System.out.println("equals " + server.equalsClient(newNick));
                                sendMsg("/authok");
                                nick = newNick;
                                server.subscribe(this);

//                                System.out.println(server.getClass(this));
                                break;
                            }else {
                                sendMsg("Неверный логин / пароль. Or user online.");
                            }
                        }
                    }

                    //Цикл для работы
                    while (true) {
                        String str = in.readUTF();

//                        String[] strArr = str.split(" ");
//                        System.out.println(strArr[1]  + str);

                        if (str.equals("/end")) {
                            out.writeUTF("/end");
                            System.out.println("Клиент отключился");
                            break;
                        }
                        if(str.startsWith("/w"))
                        {
                            String[] strings = str.split(" ");
                            String to = strings[1];
                            String msg = str;
                            server.broadcastMsgClient(this, to, msg);
                        }else
                        {
                            System.out.println(str);
                            server.broadcastMsg(nick + ": " +str);
                        }


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.unsubscribe(this);
                    System.out.println("Клиент оключился");
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNick() {
        return  nick;
    }

    public void sendMsg(String str) {
        try {
            out.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
