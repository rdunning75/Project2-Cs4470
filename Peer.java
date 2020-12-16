import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Peer extends Object {
	private static int connectedByCounter = 1;
	private static int connectedToCounter = 1;
	Socket socket;
	int id;
	Integer port;

	public Peer(Socket socket) {
		this.id = connectedByCounter++;
		this.socket = socket;
		this.port = socket.getPort();
	}

	public Peer(Socket socket, Integer portNumber) {
		this.id = connectedToCounter++;
		this.socket = socket;
		this.port = portNumber;
	}

	public String getIp(){
		String ip = socket.getInetAddress().getHostAddress();
		return ip;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		String ip = socket.getInetAddress().getHostAddress();
		return id + ": " + ip + "\t" + port;
	}

	public void terminate() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		try {
			PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
			out.println(message);
			
			this.socket.shutdownOutput();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printMessage(){
		try {
			// Get data from input stream
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String response;

			// Checks to see if there is any input from remote peer
			while ((response = reader.readLine()) != null) {
				System.out.println("Message received from " + this.socket.getInetAddress());
				System.out.println("Sender's Port : " + port);
				System.out.println("Message:  " + response + "\n");
			}

			this.socket.shutdownInput();
			reader.close();

			throw new IOException();
		} catch (IOException ex) {
			System.out.println("\nConnection with " + this.socket.getInetAddress() + " closed.\n print");
		}
	}

	public String readMessage(){
		StringBuilder messageReturn= new StringBuilder();
		try {
			// Get data from input stream
			BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			String response;


			// Checks to see if there is any input from remote peer
			while ((response = reader.readLine()) != null) {
				messageReturn.append(response);
			}

			this.socket.shutdownInput();
			reader.close();

			throw new IOException();
		} catch (IOException ex) {
			// Does not require exception handling - expected to throw error to exit
		}

		return messageReturn.toString();
	}
}
