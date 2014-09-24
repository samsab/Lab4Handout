package edu.miamioh.cse283.lab4;

import java.io.*;
import java.net.*;
import java.util.*;

import edu.miamioh.cse283.lab4.MathProblem;

public class Lab4Server {
	public static final String GET_WORK = "GET WORK";
	public static final String PUT_ANSWER = "PUT ANSWER";
	public static final String AMP_WORK = "AMP WORK";
	public static final String AMP_NONE = "AMP NONE";
	public static final String AMP_OK = "AMP OK";
	public static final String AMP_ERROR = "AMP ERROR";
	public static final String GET_STATUS = "GET STATUS";
	public static final String AMP_STATUS = "AMP STATUS";
	public static final String END_SESSION = "END SESSION";
	
	public static void main(String[] args) throws IOException {
		public int threads=0;
		
		if (args.length != 2) {
			System.out.println("Usage: java Lab4Server <port> <n items>");
			return;
		}
		
		ServerSocket server=null;
		Socket client=null;
		int nwork = Integer.parseInt(args[1]);
		
		try {
			server = new ServerSocket(Integer.parseInt(args[0]));
		} catch (SocketException e) {
			System.out.println(e.toString());
			return;
		}
		
		try {
			System.out.println("Lab4Server listening on: " + server.getLocalSocketAddress());
			System.out.println("Lab4Server listening on:    " + InetAddress.getLocalHost().getHostAddress() + ":" + server.getLocalPort());

			while(true) {
				client = server.accept();
				threads++;
				
				PrintWriter out = new PrintWriter(client.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

				String line;
				Double expectedAns=0.0;
				int count=0;
				int correct=0;
				int incorrect=0;
				
				while((line = in.readLine()) != null) {
					System.out.println("CLIENT REQUEST: " + line);
					
					if(line.startsWith(GET_WORK)) {
						if(count < nwork) {
							out.println(AMP_WORK);
							String amp = MathProblem.generate();
							out.println(amp);
							
							expectedAns = MathProblem.solve(amp);
							
							System.out.println("  RESPONSE: " + amp);
							++count;
						} else {
							System.out.println("  RESPONSE: NONE");
							out.println(AMP_NONE);
							break;
						}
					} else if(line.startsWith(PUT_ANSWER)) { // client is sending an answer:
						String answer = in.readLine();
						out.println(AMP_OK);
						
						if(expectedAns == Double.parseDouble(answer)) {
							System.out.println("  CORRECT");
							++correct;
						} else {
							System.out.println("  INCORRECT");
							++incorrect;
						}
						System.out.println("  RESPONSE: OK");
					} else if(line.startsWith(GET_STATUS)) {
						out.println(AMP_STATUS);
						
						
					} else {
						out.println(AMP_ERROR);
						System.out.println("  RESPONSE: ERROR");
					}
				}
				
				client.close();
				System.out.println("---- END: " + correct + " OF " + nwork + " CORRECT RESPONSES ----");
			}
		} catch(SocketException ex) {
			// only get here if something went wrong
			System.out.println("EXCEPTION: " + ex.getMessage());
		} finally {
			if(server != null) {
				server.close();
			}
			if( client != null) {
				client.close();
			}
		}
	}
}