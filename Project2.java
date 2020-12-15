import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;


public class Project2 {
    public static Integer port;
    public static int time;
    public static Timer timer = new Timer();
    public static int numberofpacketsRecieved = 0;

    public static int NUMOFSERVER = 2;
//    public static int SERVERID = 1;
        public static int SERVERID = 2;
//    public static int SERVERID = 3;
//    public static int SERVERID = 4;
    public static Topology server = new Topology(SERVERID, NUMOFSERVER);


    public static void main(String[] args) {

        // creates a topology object from a topology file defined by project

        if (args.length == 4) {
            String filename = args[1];
            String interval = args[3];
            initialize(filename);
        } else {
            System.out.println("Incorect call to program. Please use server -t <topology-file-name> -i <routing-update-interval>");
            functions.exit();
        }

        server.print();

        // Set listening port
        try {
            // Confirm user port meets requiremetns

            Integer test_port = Integer.parseInt(server.getIpsAndPorts().get(SERVERID-1).port);

            if ((test_port >= 1024) && (test_port <= 65535)) {
                // Call function that enables user to select from available commands
                port = test_port;
                time = Integer.parseInt(args[3]);
                program(server, time);
            }
            throw new IOException();
        } catch (IOException | NumberFormatException ex) {
            System.out.println("ERROR: Indicate a port number to start the program and initiate a connection.\n");
        }

    }

    private static void initialize(String filename) {
        ArrayList<String> filedata = new ArrayList<>();
        File toplogyfile = new File(filename);
        try {
            Scanner reader = new Scanner(toplogyfile);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                filedata.add(data);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // begin adding topology file data to topology object

        server.addToServerIDs(NUMOFSERVER);
        for (int i = 2; i < NUMOFSERVER+2; i++) {
            String[] topologyData = filedata.get(i).split(" ");
            server.addToIpPortMap(topologyData[1], topologyData[2], i - 1, false);

        }



        for (int i = NUMOFSERVER+2; i < (NUMOFSERVER*2)+1; i++) {
            String[] costData = filedata.get(i).split(" ");
            server.addCosts(Integer.parseInt(costData[0]), Integer.parseInt(costData[1]), costData[2]);
        }

    }

    public static void program(Topology server, int time) {
        String[] commands = {"help", "myip", "myport", "connect", "list", "terminate", "send", "exit", "update", "step", "packets", "display", "disable", "crash"};

        try {
            // Start the thread where program waits for connection
            Thread listen = new Thread(new Runnable() {
                public void run() {
                    functions.listening(port);
                }
            });

            listen.start();

            final Topology[] finalServer = {server};

            // TODO: I think this is supposed to check if the servers are conected, and if it
            //  cant connect after 3 times it will set the toplogy file to infinite and then
            //  reset the count. it will continue to do this untill connection every time the timer
            //  is called
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {

                        ArrayList<IpPortMap> ipmap = finalServer[0].getIpsAndPorts();



                        int i = 0;
                        for (IpPortMap ipPortMap : ipmap) {
                            if (ipPortMap.getServerId() != SERVERID) {
                                System.out.println("Sever with ip "+ ipPortMap.getIp() +" and server Id "+ ipPortMap.getServerId() + "is connected? "+ipPortMap.isConnected());
                                if (!ipPortMap.isConnected()) {
                                    finalServer[0] = functions.connect(ipPortMap.getIp(), Integer.parseInt(ipPortMap.getPort()), finalServer[0],i );
                                }
                            }
                            i++;
                        }
                        functions.step(finalServer[0]);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, time * 1000, time * 1000);
            server = finalServer[0];

        } catch (RuntimeException ex) {
            System.out.println("SAD!");
        }


        boolean status = true;

        while (status) {
            Scanner input = new Scanner(System.in);

            // Get user parameters and confirm it meets the expected command/parameter count
            String[] user_command = input.nextLine().split(" ");

            switch (user_command[0]) {
                case "help":
                    if (user_command.length == 1) {
                        functions.showHelp();
                    } else {
                        System.out.println("ERROR: Wrong # of parameters. Try again.\n");
                    }

                    break;
                case "myip":
                    if (user_command.length == 1) {
                        try {
                            System.out.println("IP Address: " + functions.getMyIP() + "\n");
                        } catch (Exception e) {
                            System.out.println("ERROR: Cannot get IP address. \n");
                        }
                    } else {
                        System.out.println("ERROR: Wrong # of parameters. Try again. \n");
                    }

                    break;
                case "myport":
                    if (user_command.length == 1) {
                        System.out.println("Listening port: " + port + "\n");
                    } else {
                        System.out.println("ERROR: Wrong # of parameters. Try again. \n");
                    }

                    break;
//                case "connect":
//                    if (user_command.length == 3) {
//                        try {
//                            functions.connect(user_command[1], Integer.parseInt(user_command[2]));
//                        } catch (Exception e) {
//                            System.out.println("ERROR: Could not connect to " + user_command[1]);
//                        }
//                    } else {
//                        System.out.println("ERROR: Wrong # of parameters. Try again.\n");
//                    }

//                    break;
                case "list":
                    if (user_command.length == 1) {
                        functions.listPeers();
                    } else {
                        System.out.println("ERROR: Wrong # of parameters. Try again.\n");
                    }

                    break;
                case "terminate":
                    if (user_command.length == 2) {
                        try {
                            functions.terminate(Integer.parseInt(user_command[1]));
                        } catch (Exception e) {
                            System.out.println("ERROR: Could not terminate connection with ID " + user_command[1] + "\n");
                        }
                    } else {
                        System.out.println("ERROR: Wrong # of parameters. Try again.\n");
                    }

                    break;
                case "send":
                    if (user_command.length >= 3) {
                        try {
                            Integer connection_id = Integer.parseInt(user_command[1]);
                            user_command[0] = "";
                            user_command[1] = "";

                            String user_command_str = String.join(" ", user_command);

                            functions.send(connection_id, user_command_str);
                        } catch (Exception e) {
                            System.out.println("ERROR: Could not connect to " + user_command[1]);
                        }
                    } else {
                        System.out.println("ERROR: Wrong # of parameters. Try again.\n");
                    }

                    break;
                case "exit":
                    if (user_command.length == 1) {
                        functions.exit();
                        status = false;
                        System.exit(0);
                    } else {
                        System.out.println("ERROR: Wrong # of parameters. Try again.\n");
                    }
                    break;
                case "update":
                    // TODO: Send signal to update other topology maps
                    //  curently only operates on topology map
                    if (user_command.length == 4) {
                        try {
                            server = functions.update(server.id, Integer.parseInt(user_command[1]), Integer.parseInt(user_command[2]), user_command[3], server);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("ERROR: Wrong # of parameters. Try again.\n");
                    }
                    break;
                case "step":
                    try {
                        functions.step(server);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "packets":
                    System.out.println("number of packets recieved since last call: " + numberofpacketsRecieved);
                    numberofpacketsRecieved = 0;
                    break;
                case "display":
                    try {
                        functions.display(server.costs);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "disable":
                    try {
                        server = functions.disable(server, Integer.parseInt(user_command[1]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "crash":
                    break;
                default:
                    System.out.println("ERROR: Command not in list. Try again.\n");
            }
        }
    }


}
