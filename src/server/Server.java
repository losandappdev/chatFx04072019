package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server {
    Vector <ClientHandler> clients;

    public Server() throws SQLException {
        AuthService.connect();
//        System.out.println(AuthService.getNickByLoginAndPass("login1","pass1"));

        ServerSocket server = null;
        Socket socket = null;

        try {
            clients = new Vector<>();
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен");

            while(true){
                socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this,socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            AuthService.disconnect();
        }
    }

    public Vector<ClientHandler> getClients() {
        return clients;
    }
    public boolean conteinsClient(String nick){
        return clients.contains(nick);

    }
    public boolean equalsClient(String newNick){
//        return newNick.equals(clients.getNick);
        for (int i = 0; i <clients.size() ; i++) {
            if (newNick.equals(clients.get(i).getNick())){
                return true;
            }
        }
        return false;
    }

    public void broadcastMsg(String str){
        for (ClientHandler o: clients) {
            o.sendMsg(str);
        }
    }
    public void broadcastMsgClient(ClientHandler from, String to, String msg){
        for (ClientHandler client: clients) {
            if (client.getNick().equals(to)){
               client.sendMsg("/w From: " + from.getNick()+"  "+ msg);
               from.sendMsg("/w To: "+client.getNick() +" "+ msg);
               break;
            }
        }

//        (int i = 0; i <clients.size() ; i++) {
//            if (for clients.get(i).getNick().equals(to))
//                clients.elementAt(i).sendMsg("/w From: " + from.getNick()+"  "+ str);
////
//            break;
//        }
    }


    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
    }
}
