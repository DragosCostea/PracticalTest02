package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by student on 23.05.2017.
 */

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");
            String operation = bufferedReader.readLine();
            String date = bufferedReader.readLine();
            if (operation == null || operation.isEmpty() || date == null || date.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }
            HashMap<InetAddress, String> data = serverThread.getData();

            int operationInteger = Integer.parseInt(operation);
            InetAddress address = socket.getInetAddress();

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] operation and date " + operation + date);

            if (operationInteger == 0) {
                serverThread.setData(address, date);
                printWriter.println("added date");
                printWriter.flush();
            }
            else if (operationInteger == 1) {

                String existing = data.get(address);
                if (existing == null || !existing.equals(date)) {
                    printWriter.println("no date");
                    printWriter.flush();
                }
                else {
                    data.remove(address);
                    printWriter.println("removed date");
                    printWriter.flush();
                }
            }
            else if (operationInteger == 2) {
                String existing = data.get(address);
                if (existing == null || !existing.equals(date)) {
                    printWriter.println("no date to poll");
                    printWriter.flush();
                    Log.d(Constants.TAG, "no date to poll here ");
                }
                else {
                    Log.d(Constants.TAG, "date exists, checking: ");
                    try {

                        Socket socket = new Socket("utcnist.colorado.edu", 13);
                        Log.d(Constants.TAG, "created socket: ");
                        BufferedReader bufferedReaderNist = Utilities.getReader(socket);
                        bufferedReaderNist.readLine();
                        String dayTimeProtocol = bufferedReaderNist.readLine();
                        //dayTimeProtocol = bufferedReader.readLine();

                        Log.d(Constants.TAG, "The server returned: " + dayTimeProtocol);
                        socket.close();

                        String nistTrim = dayTimeProtocol.substring(15, 24);
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        Date dateNist = sdf.parse(nistTrim);

                        Log.d(Constants.TAG, "The server returned: " + dateNist);

                        SimpleDateFormat fromUser = new SimpleDateFormat("HH,mm,ss");

                        Date dateClient = fromUser.parse(existing);
                        Log.d(Constants.TAG, "The client returned: " + dateClient);

                        if (dateClient.after(dateNist)) {
                            printWriter.println("alarm ok");
                            printWriter.flush();
                        }
                        else {
                            printWriter.println("alarm not ok");
                            printWriter.flush();
                        }

                    } catch (UnknownHostException unknownHostException) {
                    Log.d(Constants.TAG, unknownHostException.getMessage());
                        if (Constants.DEBUG) {
                            Log.d(Constants.TAG, "Unknown host returned:");

                            unknownHostException.printStackTrace();
                        }
                    } catch (IOException ioException) {
                        Log.d(Constants.TAG, ioException.getMessage());
                        if (Constants.DEBUG) {
                            ioException.printStackTrace();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                ;
            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }
}
