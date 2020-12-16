import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.net.InetSocketAddress;

public class functions {
    public static ArrayList<Peer> peerList = new ArrayList<>();
    public static Topology server = new Topology(0, 0);

    // Incoming connection
    public static void listening(int port) {
        try {
            ServerSocket ss_socket = new ServerSocket(port);
            distance_vector_routing.numberofpacketsRecieved++;

            while (true) {
                    Socket s_socket = ss_socket.accept();

                    Thread handle_socket = new Thread(new Runnable() {
                        public void run() {
                            Peer peer = new Peer(s_socket, s_socket.getPort());

                            String message = peer.readMessage();
                            String[] messageTokens = message.split(" ");
                            String function = messageTokens[0];

                            if(function.equals("update")){
                                int server1_update = Integer.parseInt(messageTokens[2]);
                                int server2_update = Integer.parseInt(messageTokens[1]);
                                String cost_update = messageTokens[3];

                                update(server1_update, server1_update, server2_update, cost_update, server);
                            } else if(function.equals("step")){

                            } else if(function.equals("disable")) {

                            }

                            try {
                                s_socket.close();
                                peer.terminate();
                            } catch (IOException ex) {
                                System.out.println("\n[!] listening COMMAND ERROR: Unable to listen to incoming connections.");
                            }
                        }
                    });
                handle_socket.start();
            }
        } catch (IOException e) {
            System.out.println("\n[!] listening COMMAND ERROR: Unable to listen to incoming connections.");
        }
    }

    public static void showHelp() {
        try (BufferedReader br = new BufferedReader(new FileReader("help.txt"))) {
            String helpline;
            while ((helpline = br.readLine()) != null) {
                System.out.println(helpline);
            }
        } catch (IOException e) {
            System.out.println("\n[!] help COMMAND ERROR: Unable to listen to incoming connections.");
        }
    }

    public static void setServer(Topology server2) {
        server = server2;
    }

    public static String getMyIP() throws UnknownHostException {
        InetAddress ip;
        ip = InetAddress.getLocalHost();

        return ip.getHostAddress();
    }

    public static Topology connect(String destinationIP, Integer portNumber, Topology server, int i, String message) {
        // TODO: Check if IP address is valid
        ArrayList<IpPortMap> ipmap = server.getIpsAndPorts();

        try {
            if (destinationIP.equals(getMyIP())) {
                // Not needed, will print error message when it runs through the list
            } else {
                Socket socket = new Socket(destinationIP, 2345);
                Peer peer = new Peer(socket, 2345);

                peer.sendMessage(message);

                peer.terminate();

                if (message.contains("update")) {
                    System.out.println("\n[!] update COMMAND SUCCESS: Updated cost for server with IP " + destinationIP);
                }
            }
        } catch (Exception e) {
            // If IP is not connected to ...
            if (ipmap.get(i).isConnected()) {
                // Increase connection try counter by one
                System.out.println("\n[!] connect COMMAND ERROR: Could not connect to " + ipmap.get(i).getIp());
                ipmap.get(i).setConnectTryCount(ipmap.get(i).getConnectTryCount() + 1);
                // If connection try count is = 3, set it to zero and set is connection to false
                if (ipmap.get(i).getConnectTryCount() == 3) {
                    System.out.println("\n[!] connect COMMAND INFO: Server id tp change : " + server.id + " & " + ipmap.get(i).getServerId());
                    functions.update(server.id, server.id, ipmap.get(i).getServerId(), "infinte", server);
                    ipmap.get(i).setConnectTryCount(0);
                    ipmap.get(i).setConnected(false);
                    
                    if (message.contains("update")) {
                        System.out.println("\n[!] update COMMAND ERROR: Unable to update cost for server with IP " + destinationIP);
                    }   
                }
            }
        }

        return server;
    }

    public static void send(int connectionID, String message) {
        boolean in_list = false;

        for (Peer peer : peerList) {
            if (peer.getId() == connectionID) {
                in_list = true;
                peer.sendMessage(message);
                System.out.println("\n[!] send message COMMAND SUCCESS: Message sent to " + peer.getId());
            }
        }

        if (!in_list) {
            System.out.println("\n[!] send message COMMAND ERROR: Connection ID " + connectionID + " not in list.");
        }
    }

    public static Topology update(int currentServerId, int serverID1, int serverID2, String newCost, Topology server) {
        if (currentServerId == serverID1) {
            CostMap currentCost = server.getCostMapByServerID2(serverID2);
            server.setCostMapByServerID2(serverID2, newCost);

            for (int i = 0; i < server.getIpsAndPorts().size(); i++) {
                if (server.getIpsAndPorts().get(i).getServerId() == serverID2) {

                    String message = "update " + serverID2 + " " + serverID1 + " " + newCost;

                    server = functions.connect(server.getIpsAndPorts().get(i).getIp(), Integer.parseInt(server.getIpsAndPorts().get(i).getPort()), server, i, message);
                }
            }

        } else {
            System.out.println("\n[!] update COMMAND ERROR: You may only update your connection costs with your own neighbors.");
        }

        return server;
    }

    public static void step(Topology server) throws IOException {
        ArrayList<IpPortMap> ipmap = server.getIpsAndPorts();
        System.out.println("\n[!] step COMMAND START: Sending cost to neighbors....");

        for (int i = 0; i < ipmap.size(); i++) {
            if (ipmap.get(i).getServerId() != server.getId()) {
                if (ipmap.get(i).isConnected()) {
                    String currentCost = server.getCostMapByServerID2(ipmap.get(i).getServerId()).getCost();

                    server = functions.update(server.id, server.id, ipmap.get(i).getServerId(), currentCost, server);

                    // System.out.println("\n[!] step COMMAND SUCCESS: Sent cost to neighbor with ID " + ipmap.get(i).getServerId());
                }
            }
        }
        
        return;
    }

    public static void display(ArrayList<CostMap> costMapList) {
        try {
            int j = 0;
            for (int i = 1; i < costMapList.size(); i++) {
            CostMap temp = costMapList.get(i);
            j = i;
            while (j > 0 && temp.id < costMapList.get(j - 1).id) {
                costMapList.set(j, costMapList.get(j - 1));
                j--;
            }
            costMapList.set(j, temp);
            }

            for(int i = 0; i < costMapList.size(); i++){
            costMapList.get(i).print();
            }

            System.out.println("\n[!] display COMMAND SUCCESS: View current routing table above.");
        } catch (Exception ex) {
            System.out.println("\n[!] display COMMAND ERROR: Cannot view current routing table.");
        }
    }

     public static void disable(Topology server, int serverID){
         if(serverID != server.id && !server.getCostMapByServerID2(serverID).cost.equals("infinite")){ 	  	
         server.getCostMapByServerID2(serverID).cost = "infinite";
         System.out.println("[!] disable COMMAND SUCCESS: \'Disabled\' connection with server " +serverID);
         try {
         	functions.step(server);
         	}
         catch (IOException e){
         	
         }    
         } else {
             System.out.println("[!] disable COMMAND ERROR: You can only disable connection with your neighbor!");
         }

     }

    public static String user_command(String[] user_input) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i=0; i < user_input.length; i++) {
            stringBuilder.append(user_input[i] + " ");
        }

        return stringBuilder.toString();

    }

    // public static void crash (Topology server) throws IOException {
    //     for (int i = 0; i < ipmap.size(); i++) {
    //         if (ipmap.get(i).getServerId() != server.getId()) {

    //             if (!ipmap.get(i).isConnected()) {
    //                 server = functions.connect(ipmap.get(i).getIp(), Integer.parseInt(ipmap.get(i).getPort()), server, i, "disconnect");
    //             } else {

    //             }
    //         }
    //     }
    // }
}
