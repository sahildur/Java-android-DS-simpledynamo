package edu.buffalo.cse.cse486586.simpledynamo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SimpleDynamoProvider extends ContentProvider {

    ArrayList<String> keysorginserted = new ArrayList<String>();
    static final int SERVER_PORT = 10000;
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static int flagfirst = 0;
    static int querycounter = 0;

    public Uri mUri;

    ArrayList<String> porthashlish = new ArrayList<String>();

    ArrayList<String> activelist = new ArrayList<String>();
    ArrayList<String> activelistnonhash = new ArrayList<String>();
    HashMap<String, String> porthashwithorg = new HashMap<String, String>();
    HashMap<String, String> tempstore = new HashMap<String, String>();

    HashMap<String, String> missedby5554 = new HashMap<String, String>();
    HashMap<String, String> missedby5556 = new HashMap<String, String>();
    HashMap<String, String> missedby5558 = new HashMap<String, String>();
    HashMap<String, String> missedby5560 = new HashMap<String, String>();
    HashMap<String, String> missedby5562 = new HashMap<String, String>();

    MatrixCursor curglobal;
    MatrixCursor starcursor;

    static boolean waitforstar = false;
    static boolean waitingforservertask = false;
    static boolean waitforpreviousquery = false;
    static int waitforstarcount;
    static int someoneisfailed;
    static int deletecalled;
    static int deleteprocess;

    static String myPort;
    static String myhalfport;

    static String myhash;
    static String myprehash;
    static String mysuccesshash;
    static String mysuccessnexthash;

    HashMap<String, Integer> waitsingle = new HashMap<String, Integer>();
    HashMap<String, MatrixCursor> globalmapcur = new HashMap<String, MatrixCursor>();

    static String mynextsuccesshash;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        try {
            deletecalled = 1;
            deleteprocess++;
            if (deleteprocess == 1) {
                //let all know delete has begun
                ArrayList<String> portremaining = new ArrayList<String>();
                portremaining.add("11108");
                portremaining.add("11112");
                portremaining.add("11116");
                portremaining.add("11120");
                portremaining.add("11124");
                portremaining.remove(myPort);

                message_obj msg = new message_obj(myPort, "", "", "deleteprocessbegin");
                Log.v("here57a", "here57");
                for (String p : portremaining) {
                    try {
                        Socket socketf = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(p));
                        Log.v("here58a", "here58a");

                        OutputStream outf = socketf.getOutputStream();
                        ObjectOutputStream ooutf = new ObjectOutputStream(outf);

                        ooutf.writeObject(msg);

                        Log.v("outf17a", outf.toString());
                        /*
                         * TODO: Fill in your client code that sends out a message.
                         */

                        socketf.close();
                    } catch (Exception e) {
                        Log.v("Exception 17a", "couldnotinforma");

                    }
                }

            }
            keysorginserted.remove(selection);
            String hashedstring = genHash(selection);
            selection = hashedstring;
            Log.v("deletekeyhash", selection);
        } catch (NoSuchAlgorithmException e) {
            Log.v("exception5", "exception5");
        }

        getContext().deleteFile(selection);

        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

		// TODO Auto-generated method stub
        //return null;
        if (deletecalled == 1) {
            return null;
        }

        try {
            String vall = (String) values.get("value");
            Log.v("insertval", vall);
            //vall="55";
            String keyyyy = (String) values.get("key");

			///
            String findkeyhash = "";
            try {
                findkeyhash = genHash(keyyyy);
            } catch (NoSuchAlgorithmException e) {
                Log.v("exception4", "exception4");
            }

            Log.v("findkeyhash", findkeyhash);

            ArrayList<String> porthashlishtesting = new ArrayList<String>(porthashwithorg.keySet());
            //porthashlishtesting=;
            porthashlishtesting.add(findkeyhash);

            for (String s : porthashlishtesting) {
                Log.v("printing12s", s);
            }

            Collections.sort(porthashlishtesting);
            Log.v("printing13", "printing");
            for (String s : porthashlishtesting) {
                Log.v("printing13s", s);
            }

            int rightindex = porthashlishtesting.indexOf(findkeyhash);
            String rightone = "";
            String successor1 = "";
            String successor2 = "";

            Log.v("indexofkey", porthashlishtesting.indexOf(findkeyhash) + "");
            porthashlishtesting.remove(findkeyhash);
            int flag = 0;

            if (rightindex <= 2) {
                rightone = porthashwithorg.get(porthashlishtesting.get(rightindex));
                successor1 = porthashwithorg.get(porthashlishtesting.get(rightindex + 1));
                successor2 = porthashwithorg.get(porthashlishtesting.get(rightindex + 2));
            } else if (rightindex <= 3) {
                rightone = porthashwithorg.get(porthashlishtesting.get(rightindex));
                successor1 = porthashwithorg.get(porthashlishtesting.get(rightindex + 1));
                successor2 = porthashwithorg.get(porthashlishtesting.get(0));
            } else if (rightindex <= 4) {
                rightone = porthashwithorg.get(porthashlishtesting.get(4));
                successor1 = porthashwithorg.get(porthashlishtesting.get(0));
                successor2 = porthashwithorg.get(porthashlishtesting.get(1));
            } else {
                rightone = porthashwithorg.get(porthashlishtesting.get(0));
                successor1 = porthashwithorg.get(porthashlishtesting.get(1));
                successor2 = porthashwithorg.get(porthashlishtesting.get(2));
            }
            Log.v("rightone", rightone);
            Log.v("successor1", successor1);
            Log.v("successor2", successor2);
            int savehere = 0;
            String nodetosendnext = "";
            String nodetodendiffailed = "";
            if (myhash.equals(genHash(rightone))) {//send to success1
                savehere = 1;
                nodetosendnext = successor1;
                nodetodendiffailed = successor2;
                if (keysorginserted.contains(keyyyy)) {
                    Log.v("gayab0", "gayab0");
                    return uri;
                }

            } else if (myhash.equals(genHash(successor1))) {//send to success2
                savehere = 1;
                nodetosendnext = successor2;
                nodetodendiffailed = rightone;
                if (keysorginserted.contains(keyyyy)) {
                    Log.v("gayab1", "gayab1");

                    return uri;
                }

            } else if (myhash.equals(genHash(successor2))) {//send to first
                savehere = 1;
                nodetosendnext = rightone;
                nodetodendiffailed = successor1;
                if (keysorginserted.contains(keyyyy)) {
                    Log.v("gayab2", "gayab2");
                    return uri;
                }

            } else {
                //send to first
                nodetosendnext = rightone;
                nodetodendiffailed = successor1;
                Log.v("gayab3", "gayab3");
            }

            Log.v("savehere", savehere + "");
            Log.v("nodetosendnext", nodetosendnext);
            Log.v("nodetodendiffailed", nodetodendiffailed);

            if (savehere == 1) {
                Log.v("savehereye", "savehereye");
                //keysorginserted.remove(keyyyy);
                keysorginserted.add(keyyyy);
                String filename = findkeyhash;//"SimpleMessengerOutput";
                //String string = strReceived + "\n";
                FileOutputStream outputStream;

                outputStream = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(vall.getBytes());
                outputStream.close();
                Log.v("savehereye2", "savehereye2");
            }
//////////////

            try {

                if (nodetosendnext.equals("5554")) {
                    missedby5554.put(keyyyy, vall);
                } else if (nodetosendnext.equals("5556")) {
                    missedby5556.put(keyyyy, vall);
                } else if (nodetosendnext.equals("5558")) {
                    missedby5558.put(keyyyy, vall);
                } else if (nodetosendnext.equals("5560")) {
                    missedby5560.put(keyyyy, vall);
                } else {
                    missedby5562.put(keyyyy, vall);
                }

                Log.v("sendnext", "sendnext");
                int doubletheport = 2 * Integer.parseInt(nodetosendnext);
                Log.v("doubletheport", doubletheport + "");
                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(doubletheport + ""));

//		tstat = msgs[2];
                message_obj msg_obj = new message_obj(myPort, keyyyy, vall, "justinsert");

                OutputStream out = socket.getOutputStream();
                ObjectOutputStream oout = new ObjectOutputStream(out);

                oout.writeObject(msg_obj);

                Log.v("out", out.toString());
                /*
                 * TODO: Fill in your client code that sends out a message.
                 */

                socket.close();

                if (nodetodendiffailed.equals("5554")) {
                    missedby5554.put(keyyyy, vall);
                } else if (nodetodendiffailed.equals("5556")) {
                    missedby5556.put(keyyyy, vall);
                } else if (nodetodendiffailed.equals("5558")) {
                    missedby5558.put(keyyyy, vall);
                } else if (nodetodendiffailed.equals("5560")) {
                    missedby5560.put(keyyyy, vall);
                } else {
                    missedby5562.put(keyyyy, vall);
                }

            } catch (Exception e) {
                Log.v("Failuretosend", "Failuretosend");
                //
                if (nodetodendiffailed.equals("5554")) {
                    missedby5554.put(keyyyy, vall);
                } else if (nodetodendiffailed.equals("5556")) {
                    missedby5556.put(keyyyy, vall);
                } else if (nodetodendiffailed.equals("5558")) {
                    missedby5558.put(keyyyy, vall);
                } else if (nodetodendiffailed.equals("5560")) {
                    missedby5560.put(keyyyy, vall);
                } else {
                    missedby5562.put(keyyyy, vall);
                }

	//
                Log.v("nodetodendiffailed", "nodetodendiffailed");
                int doubletheport2 = 2 * Integer.parseInt(nodetodendiffailed);
                Log.v("doubletheport", doubletheport2 + "");
                Socket socket2 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(doubletheport2 + ""));

                //		tstat = msgs[2];
                message_obj msg_obj2 = new message_obj(myPort, keyyyy, vall, "justinsert");

                OutputStream out2 = socket2.getOutputStream();
                ObjectOutputStream oout2 = new ObjectOutputStream(out2);

                oout2.writeObject(msg_obj2);

                Log.v("out2", out2.toString());
                /*
                 * TODO: Fill in your client code that sends out a message.
                 */

                socket2.close();
            }

        } catch (Exception e) {
            Log.e("Failed57", "Failed57");
        }
        //Log.v("insert123", values.toString());
        return uri;

    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub

        TelephonyManager tel = (TelephonyManager) getContext().getSystemService(
                Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        Log.v("portStr_c", portStr);
        Log.v("myPort_c", myPort);

        mUri = buildUri("content", "edu.buffalo.cse.cse486586.simpledynamo.provider");

        try {
            porthashwithorg.put(genHash("5554"), "5554");
            porthashwithorg.put(genHash("5556"), "5556");
            porthashwithorg.put(genHash("5558"), "5558");
            porthashwithorg.put(genHash("5560"), "5560");
            porthashwithorg.put(genHash("5562"), "5562");

			// porthashlish.add(genHash("key1"));
            myhash = genHash(portStr);
            myprehash = myhash;
            mysuccesshash = myhash;

            Log.v("myhash01", myhash);
            Log.v("myprehash01", myprehash);
            Log.v("mysuccesshash01", mysuccesshash);
            try {

                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);
                //return true;
            } catch (IOException e) {
                Log.e("Create Server", "Can't create a ServerSocket");
                Log.e("Create Server", e.getMessage());
                //return false;
            }
            Log.v("flow5", "flow5");
        } catch (NoSuchAlgorithmException e) {
            Log.v("exception6", "exception7");
        }

        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        querycounter++;
        Log.v("querycounter", querycounter + "");

		// TODO Auto-generated method stub
        //return null;
        Log.v("queryselection", selection);

        String orgkey = selection;
        String gloqhashedstring = "";
        try {
            String hashedstring = genHash(selection);
            gloqhashedstring = genHash(selection);
            selection = hashedstring;
            Log.v("queryhash", selection);
        } catch (NoSuchAlgorithmException e) {
            Log.v("exception3", "exception3");
        }

//@//9a78211436f6d425ec38f5c4e02270801f3524f8
        //*//df58248c414f342c81e056b40bee12d17a08bf61
        ////all files list
        if (selection.equals("df58248c414f342c81e056b40bee12d17a08bf61")) {
            Log.v("hereiam1", "hereiam1");

				/// check what all ports are active
				////
            //waitforstarcount=activelistnonhash.size();
            waitforstarcount = 5;
            for (String starsend : activelistnonhash) {
                try {
                    Socket socketj = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(starsend));

                    Log.v("myPortquery0j", "yae");
                    Log.v("myPortqueryj", this.myPort);
                    message_obj msg_obj = new message_obj(myPort, "", "", "querystar");

                    OutputStream outj = socketj.getOutputStream();
                    ObjectOutputStream ooutj = new ObjectOutputStream(outj);

                    ooutj.writeObject(msg_obj);

                    Log.v("out2", outj.toString());
                    /*
                     * TODO: Fill in your client code that sends out a message.
                     */

                    socketj.close();
                } catch (Exception e) {
                    waitforstarcount--;
                    someoneisfailed = 1;
                    Log.v("Exception22", "exception22");
                }
            }

            waitforstar = true;

            Log.v("activelistnonhashsize", waitforstarcount + "");
            while (waitforstar) {

            }
            Log.v("starcursorreturn", "yes");
            return starcursor;

        }

        int star = 1;
        if (selection.equals("9a78211436f6d425ec38f5c4e02270801f3524f8")) {
            Log.v("allfilesname1", "hey");
            //all files in dir
//        File mydir = getContext().getFilesDir();
//        File lister = mydir.getAbsoluteFile();
            String[] s = new String[]{"key", "value"};
            MatrixCursor cur = new MatrixCursor(s);
            for (String list : keysorginserted) {
                //

                try {
                    String hashedstring = genHash(list);
                    selection = hashedstring;
                    Log.v("queryhash6", selection);
                } catch (NoSuchAlgorithmException e) {
                    Log.v("exception6", "exception3");
                }

                File file = new File(getContext().getFilesDir() + "/" + selection);

                Log.v("querycheck11", file.toString());

				//File file = "";
                InputStream in = null;
                try {
                    // in = new BufferedInputStream(new FileInputStream(file));
                    InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
                    BufferedReader bufferedReader = new BufferedReader(isr);

                    String temp = bufferedReader.readLine();
                    Log.v("fileeadcheck11", temp);

                    cur.addRow(new String[]{list, temp});

					//finally {
                    //  if (in != null) {
                    //     in.close();
                    // }
                    //}
                } catch (Exception e) {
                    Log.v("query exception", "exception1");
                }

                ///
                Log.v("allfilesname", list);
            }
            Log.v("allfilesname2", "heya");
            return cur;

        }
        //global query dynamic

        if (!keysorginserted.contains(orgkey)) {
            try {

//				while(waitforpreviousquery)
//				{
//
//				}
//				waitforpreviousquery=true;
//////
                //whom to ask key not with me
                ArrayList<String> porthashlishtesting = new ArrayList<String>(porthashwithorg.keySet());
                //porthashlishtesting=;
                porthashlishtesting.add(gloqhashedstring);

                Collections.sort(porthashlishtesting);

                int rightindex = porthashlishtesting.indexOf(gloqhashedstring);
                String rightone = "";
                String successor1 = "";
                String successor2 = "";

                Log.v("indexofkey", porthashlishtesting.indexOf(gloqhashedstring) + "");
                porthashlishtesting.remove(gloqhashedstring);
                int flag = 0;

                if (rightindex <= 2) {
                    rightone = porthashwithorg.get(porthashlishtesting.get(rightindex));
                    successor1 = porthashwithorg.get(porthashlishtesting.get(rightindex + 1));
                    successor2 = porthashwithorg.get(porthashlishtesting.get(rightindex + 2));
                } else if (rightindex <= 3) {
                    rightone = porthashwithorg.get(porthashlishtesting.get(rightindex));
                    successor1 = porthashwithorg.get(porthashlishtesting.get(rightindex + 1));
                    successor2 = porthashwithorg.get(porthashlishtesting.get(0));
                } else if (rightindex <= 4) {
                    rightone = porthashwithorg.get(porthashlishtesting.get(4));
                    successor1 = porthashwithorg.get(porthashlishtesting.get(0));
                    successor2 = porthashwithorg.get(porthashlishtesting.get(1));
                } else {
                    rightone = porthashwithorg.get(porthashlishtesting.get(0));
                    successor1 = porthashwithorg.get(porthashlishtesting.get(1));
                    successor2 = porthashwithorg.get(porthashlishtesting.get(2));
                }
                Log.v("rightone127", rightone);
                Log.v("successor1127", successor1);
                Log.v("successor2127", successor2);

                Log.v("maybesame127", rightone + " " + myhalfport);
                Log.v("maybesame127", successor1 + " " + myhalfport);

                ArrayList<String> querytoask = new ArrayList<String>();
                querytoask.add(rightone);
                querytoask.add(successor1);
                querytoask.add(successor2);

				/////////
                for (String sta : querytoask) {
                    try {
						//String psuccesstosend = porthashwithorg.get(mysuccesshash);

                        String psuccesstosend = sta;

                        int tempi = Integer.parseInt(psuccesstosend);
                        tempi = tempi * 2;
                        String mysuccesshashport = "" + tempi;
                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(mysuccesshashport));

                        Log.v("myPortquery0", "yae");
                        Log.v("myPortquery", this.myPort);
                        message_obj msg_obj = new message_obj(myPort, orgkey, "", "query");

                        OutputStream out = socket.getOutputStream();
                        ObjectOutputStream oout = new ObjectOutputStream(out);

                        oout.writeObject(msg_obj);

                        Log.v("out2", out.toString());
                        /*
                         * TODO: Fill in your client code that sends out a message.
                         */

                        socket.close();
                        Log.v("close12", "close12");

                    } catch (Exception e) {
                        Log.v("querytonextfailed", "querytonextfailed");
                        ;

                    }

                }

//////
            } catch (Exception e) {
                Log.v("Exception12", "exception12");
            }

            waitsingle.put(orgkey, 1);
            //waitingforservertask=true;
            while (waitsingle.get(orgkey) == 1) {

            }
            waitsingle.remove(orgkey);
            //waitforpreviousquery=false;
            querycounter--;
            return globalmapcur.get(orgkey);
            //return curglobal;

        }

//
        File file = new File(getContext().getFilesDir() + "/" + selection);

        Log.v("querycheck1", file.toString());

		//File file = "";
        InputStream in = null;
        try {
            // in = new BufferedInputStream(new FileInputStream(file));
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(isr);

            String temp = bufferedReader.readLine();
            Log.v("fileeadcheck1", temp);
            String[] s = new String[]{"key", "value"};
            MatrixCursor cur = new MatrixCursor(s);
            cur.addRow(new String[]{orgkey, temp});
            querycounter--;
            return cur;
			//finally {
            //  if (in != null) {
            //     in.close();
            // }
            //}
        } catch (Exception e) {
            Log.v("query exception", "exception1");
        }

        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         *
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         *
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
        Log.v("query", selection);
        return null;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    private String genHash(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] sha1Hash = sha1.digest(input.getBytes());
        Formatter formatter = new Formatter();
        for (byte b : sha1Hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

    class ServerTask extends AsyncTask<ServerSocket, String, Void> {

        private static final String TAG = "oye";

		//private final ContentResolver serverContentResolver=SimpleDhtActivity.getContentResolver();
        private int count = 0;

        @Override

        protected Void doInBackground(ServerSocket... sockets) {
            ServerSocket serverSocket = sockets[0];

            /*
             * TODO: Fill in your server code that receives messages and passes them
             * to onProgressUpdate().
             */
            Log.v("server", "Hiii");
            //publishProgress("msg");
            Socket socket = null;
            //count=-1;
            while (!serverSocket.isClosed()) {
                try {
                    //firsttime
                    if (flagfirst == 0) {
                        flagfirst = 1;
						////////////
                        ////inform all that I have joined and give me my missed messages
                        message_obj msg = new message_obj(myPort, "", "", "joinedag");

                        ArrayList<String> portremaining = new ArrayList<String>();
                        portremaining.add("11108");
                        portremaining.add("11112");
                        portremaining.add("11116");
                        portremaining.add("11120");
                        portremaining.add("11124");
                        portremaining.remove(myPort);

                        Log.v("here57", "here57");
                        for (String p : portremaining) {
                            try {
                                Socket socketf = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                        Integer.parseInt(p));
                                Log.v("here58", "here58");

                                OutputStream outf = socketf.getOutputStream();
                                ObjectOutputStream ooutf = new ObjectOutputStream(outf);

                                ooutf.writeObject(msg);

                                Log.v("outf17", outf.toString());
                                /*
                                 * TODO: Fill in your client code that sends out a message.
                                 */

                                socketf.close();
                            } catch (Exception e) {
                                Log.v("Exception 17", "couldnotinform");

                            }
                        }

						////////////////
                        //try {
                        int halft = Integer.parseInt(myPort) / 2;
                        //activelist.add(genHash(halft+""));
                        activelistnonhash.add("11108");
                        activelistnonhash.add("11112");
                        activelistnonhash.add("11116");
                        activelistnonhash.add("11120");
                        activelistnonhash.add("11124");
						//}catch(NoSuchAlgorithmException e)
//						{
//							Log.v("Exception19","exception18");
//						}
                        ///// hardcode ports
                        try {
                            activelist.add(genHash("5554"));
                            activelist.add(genHash("5556"));
                            activelist.add(genHash("5558"));
                            activelist.add(genHash("5560"));
                            activelist.add(genHash("5562"));
                        } catch (NoSuchAlgorithmException e) {
                            Log.v("Exception19", "exception18");
                        }
                        ArrayList<String> freshactive = new ArrayList<String>();
                        for (String yup : activelist) {
                            freshactive.add(yup);
                        }

                        Collections.sort(freshactive);

                        for (String sts : freshactive) {
                            Log.v("sortedv", sts);
                        }

                        int index = freshactive.indexOf(myhash);
                        Log.v("myindex", index + "");
                        Log.v("myhash", myhash + "");

                        int lasttind = freshactive.size() - 1;
                        int lasttindbef = freshactive.size() - 2;
                        if (index == 0) {
                            myprehash = freshactive.get(lasttind);
                            mysuccesshash = freshactive.get(1);
                            mysuccessnexthash = freshactive.get(2);
                        } else if (index == lasttind) {
                            myprehash = freshactive.get(lasttind - 1);
                            mysuccesshash = freshactive.get(0);
                            mysuccessnexthash = freshactive.get(1);
                        } else if (index == lasttindbef) {
                            myprehash = freshactive.get(lasttindbef - 1);
                            mysuccesshash = freshactive.get(lasttind);
                            mysuccessnexthash = freshactive.get(0);
                        } else {
                            myprehash = freshactive.get(index - 1);
                            mysuccesshash = freshactive.get(index + 1);
                            mysuccessnexthash = freshactive.get(index + 2);
                        }

                        Log.v("myprehash", myprehash + "");
                        Log.v("mysuccesshash", mysuccesshash + "");
                        Log.v("mysuccessnexthash", mysuccessnexthash + "");

						/////
//						if (!myPort.equals("11108")) {
//							//inform 11108 that I have joined
//							//activelist.add(myPort);
//							message_obj msg = new message_obj(myPort, "", "", "joined");
//
//							///
//							try {
//
//								Log.v("here57", "here57");
//								Socket socketf= new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
//										Integer.parseInt("11108"));
//								Log.v("here58", "here58");
//
//								OutputStream outf = socketf.getOutputStream();
//								ObjectOutputStream ooutf = new ObjectOutputStream(outf);
//
//								ooutf.writeObject(msg);
//
//								Log.v("outf17", outf.toString());
//                /*
//                 * TODO: Fill in your client code that sends out a message.
//                 */
//
//								socketf.close();
//							} catch (Exception e) {
//								Log.v("Exception 17", "couldnotinform");
//
//							}
//
//							////
//						}
                    }

                    //
                    Log.v("BEFOREACCCEPT", "BEFOREACCCET");
                    socket = serverSocket.accept();
                    ObjectInputStream inp = new ObjectInputStream(socket.getInputStream());
                    //try {
                    message_obj msg_received = (message_obj) inp.readObject();

                    Log.v("msg_stat", msg_received.status);

                    if (msg_received.status.equals("deleteprocessbegin")) {
                        deletecalled = 1;
                    }
                    if (msg_received.status.equals("karoaddyerahgaya")) {
                        Log.v("fromothers-", "fromothers-");

                        for (String ske : msg_received.resultquery.keySet()) {
                            if (!keysorginserted.contains(ske)) {

                                Log.v("fromothers0", "fromothers0");
                                Log.v("fromothers0key", ske);
                                ContentValues cvtest = new ContentValues();
                                cvtest.put("key", "" + ske);
                                cvtest.put("value", "" + msg_received.resultquery.get(ske));

                                getContext().getContentResolver().insert(mUri, cvtest);
                            }
                        }

                    }

                    if (msg_received.status.equals("joinedag") && deletecalled == 0) {

                        HashMap<String, String> toiterate = new HashMap<String, String>();
                        String oport = msg_received.origin_port;
                        if (oport.equals("11108")) {
                            toiterate = missedby5554;
                        } else if (oport.equals("11112")) {
                            toiterate = missedby5556;
                        } else if (oport.equals("11116")) {
                            toiterate = missedby5558;
                        } else if (oport.equals("11120")) {
                            toiterate = missedby5560;
                        } else {
                            toiterate = missedby5562;
                        }
                        message_obj msg222 = new message_obj(myPort, "", "", "karoaddyerahgaya");
                        msg222.resultquery = toiterate;

                        try {
                            Socket socketf2 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                    Integer.parseInt(oport));
                            Log.v("here582", "here582");

                            OutputStream outf2 = socketf2.getOutputStream();
                            ObjectOutputStream ooutf2 = new ObjectOutputStream(outf2);

                            ooutf2.writeObject(msg222);

                            Log.v("outf172", "sentrahgaya");
                            /*
                             * TODO: Fill in your client code that sends out a message.
                             */

                            socketf2.close();
                        } catch (Exception e) {
                            Log.v("Exception 172", "couldnotinform2");

                        }

                    }
                    if (msg_received.status.equals("querystarcomplete")) {
                        Log.v("qcompletereceived2", "yo");
                        Log.v("msg_received_org2", msg_received.origin_port + "");
                        if (waitforstarcount == 5 || (someoneisfailed == 1 && waitforstarcount == 4)) {
                            String[] sglo = new String[]{"key", "value"};
                            starcursor = new MatrixCursor(sglo);
                        }
                        waitforstarcount--;
                        for (String ststs : msg_received.resultquery.keySet()) {

                            Log.v("here27", "here27");
                            Log.v("qcompletekey", ststs + "");
                            Log.v("qcompleteval", msg_received.resultquery.get(ststs) + "");

                            starcursor.addRow(new String[]{ststs, msg_received.resultquery.get(ststs)});

                        }
                        Log.v("waitforstarcountans", "" + waitforstarcount);
                        if (waitforstarcount == 0) {
                            Log.v("waitforstarcount0", "waitforstarcount0");
                            waitforstar = false;
                        }

                    }

                    if (msg_received.status.equals("querystar")) {
                        try {
                            Log.v("yahanq1", "yahanq1");
                            Cursor resultCursor = getContext().getContentResolver().query(mUri, null, "@", null, null);
                            message_obj msgobj_n = new message_obj(myPort, "", "", "querystarcomplete");
                            while (resultCursor.moveToNext()) {
                                int keyIndex = resultCursor.getColumnIndex("key");
                                int valueIndex = resultCursor.getColumnIndex("value");
                                String listkeyy = resultCursor.getString(keyIndex);
                                String listvaly = resultCursor.getString(valueIndex);

                                msgobj_n.resultquery.put(listkeyy, listvaly);
                                Log.v("@@@@@listkey", listkeyy + "");
                                Log.v("@@@@@listval", listvaly + "");

                            }

                            //msg_received.status="querystarcomplete";
                            Log.v("yahanq22", "yahanq22");
                            Log.v("yahanq22org", msg_received.origin_port);
                            Socket sockett = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                    Integer.parseInt(msg_received.origin_port));

                            OutputStream outt = sockett.getOutputStream();
                            Log.v("yahanq33", "yahanq33");
                            ObjectOutputStream ooutt = new ObjectOutputStream(outt);

                            ooutt.writeObject(msgobj_n);

                            Log.v("out3", outt.toString());
                            /*
                             * TODO: Fill in your client code that sends out a message.
                             */

                            sockett.close();
                            Log.v("close1234", "close12");

                        } catch (Exception e) {
                            Log.v("Exception23", "exception23");
                        }
                    }

                    if (msg_received.status.equals("updatejoin")) {

						//
                        activelist = msg_received.globalactive;
                        activelistnonhash = msg_received.globalactivewiithouthash;
                        ArrayList<String> freshactive = new ArrayList<String>();
                        for (String yup : activelist) {
                            freshactive.add(yup);
                        }

                        Collections.sort(freshactive);

                        for (String sts : freshactive) {
                            Log.v("sortedv", sts);
                        }

                        int index = freshactive.indexOf(myhash);
                        Log.v("myindex", index + "");
                        Log.v("myhash", myhash + "");

                        int lasttind = freshactive.size() - 1;
                        int secondlasttind = freshactive.size() - 2;
                        if (index == 0) {
                            myprehash = freshactive.get(lasttind);
                            mysuccesshash = freshactive.get(1);
                            mynextsuccesshash = freshactive.get(2);
                        } else if (index == lasttind) {
                            myprehash = freshactive.get(lasttind - 1);
                            mysuccesshash = freshactive.get(0);
                            mynextsuccesshash = freshactive.get(1);
                        } else if (index == secondlasttind) {
                            myprehash = freshactive.get(index - 1);
                            mysuccesshash = freshactive.get(index + 1);
                            mynextsuccesshash = freshactive.get(0);
                        } else {
                            myprehash = freshactive.get(index - 1);
                            mysuccesshash = freshactive.get(index + 1);
                            mynextsuccesshash = freshactive.get(index + 2);
                        }

                        Log.v("myprehash", myprehash + "");
                        Log.v("mysuccesshash", mysuccesshash + "");

                        ////
                    }
                    Log.v("myPortagain", myPort);
                    if (msg_received.status.equals("joined") && myPort.equals("11108")) {

                        activelistnonhash.add(msg_received.origin_port);
                        Log.v("myPortagainenter", myPort);
                        try {

                            int temphalfport = Integer.parseInt(msg_received.origin_port) / 2;
                            activelist.add(genHash(temphalfport + ""));
                            Log.v("printactive", "yee");
                            for (String sa : activelist) {
                                Log.v("pprint1", sa);
                            }
                        } catch (NoSuchAlgorithmException e) {
                            Log.v("Exception 18", "exception 18");
                        }
                        for (String eachporta : activelistnonhash) {
                            Log.v("eachporta", eachporta);
                            Socket socketa = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                    Integer.parseInt(eachporta));

                            message_obj msg_obja = new message_obj(msg_received.origin_port, "", "", "updatejoin");
                            msg_obja.globalactive = activelist;
                            msg_obja.globalactivewiithouthash = activelistnonhash;

                            OutputStream outa = socketa.getOutputStream();
                            ObjectOutputStream oouta = new ObjectOutputStream(outa);

                            oouta.writeObject(msg_obja);

                            Log.v("out", outa.toString());
                            /*
                             * TODO: Fill in your client code that sends out a message.
                             */

                            socketa.close();

                        }
                    }

                    if (msg_received.status.equals("querycomplete")) {
                        Log.v("qcompletereceived", "yo");
                        Log.v("msg_received_org", msg_received.origin_port + "");
                        Log.v("msg_received_key", msg_received.key + "");

						////
                        ////
                        String[] sglo = new String[]{"key", "value"};
                        MatrixCursor curlocal = new MatrixCursor(sglo);
                        for (String ststs : msg_received.resultquery.keySet()) {

                            Log.v("qcompletekey", ststs + "");
                            Log.v("qcompleteval", msg_received.resultquery.get(ststs) + "");

                            curlocal.addRow(new String[]{ststs, msg_received.resultquery.get(ststs)});

                        }
                        if (!globalmapcur.containsKey(msg_received.key)) {
                            globalmapcur.put(msg_received.key, curlocal);
                        }
                        //waitingforservertask=false;
                        waitsingle.put(msg_received.key, 0);
                    }

                    if (msg_received.status.equals("justinsert")) {
                        Log.v("justkey", msg_received.key);
                        Log.v("justvalue", msg_received.value);
                        ContentValues cvtest = new ContentValues();

                        cvtest.put("key", "" + msg_received.key);
                        cvtest.put("value", "" + msg_received.value);

                        getContext().getContentResolver().insert(mUri, cvtest);

                    } else if (msg_received.status.equals("query")) {
                        if (!keysorginserted.contains(msg_received.key)) {
                            try {
                                String psuccesstosend = porthashwithorg.get(mysuccesshash);
                                int tempi = Integer.parseInt(psuccesstosend);
                                tempi = tempi * 2;
                                String mysuccesshashport = "" + tempi;
                                Socket sockett = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                        Integer.parseInt(mysuccesshashport));

                                Log.v("oya22", "oya22");
                                Log.v("queryport", msg_received.origin_port);
                                message_obj msg_obj = new message_obj(msg_received.origin_port, msg_received.key, "", "query");

                                OutputStream outt = sockett.getOutputStream();
                                ObjectOutputStream ooutt = new ObjectOutputStream(outt);

                                ooutt.writeObject(msg_obj);

                                Log.v("out", outt.toString());
                                /*
                                 * TODO: Fill in your client code that sends out a message.
                                 */

                                sockett.close();
                                Log.v("close123", "close12");
                            } catch (Exception e) {
                                Log.v("Exception14", "exception14");

                                ////query to next failed
                                String psuccesstosend88 = porthashwithorg.get(mysuccessnexthash);
                                int tempi88 = Integer.parseInt(psuccesstosend88);
                                tempi88 = tempi88 * 2;
                                String mysuccesshashport88 = "" + tempi88;
                                Socket sockett88 = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                        Integer.parseInt(mysuccesshashport88));

                                Log.v("oya2288", "oya2288");
                                Log.v("queryport88", msg_received.origin_port);
                                message_obj msg_obj88 = new message_obj(msg_received.origin_port, msg_received.key, "", "query");

                                OutputStream outt88 = sockett88.getOutputStream();
                                ObjectOutputStream ooutt88 = new ObjectOutputStream(outt88);

                                ooutt88.writeObject(msg_obj88);

                                Log.v("out88", outt88.toString());
                                /*
                                 * TODO: Fill in your client code that sends out a message.
                                 */

                                sockett88.close();
                                Log.v("close12388", "close1288");

								///
                            }

                        } else {
                            try {
                                Log.v("yahan1", "yahan1");
                                Cursor resultCursor = getContext().getContentResolver().query(mUri, null, msg_received.key, null, null);
                                while (resultCursor.moveToNext()) {
                                    int keyIndex = resultCursor.getColumnIndex("key");
                                    int valueIndex = resultCursor.getColumnIndex("value");
                                    String listkeyy = resultCursor.getString(keyIndex);
                                    String listvaly = resultCursor.getString(valueIndex);
                                    msg_received.resultquery.put(listkeyy, listvaly);
                                    Log.v("@@@@listkey", listkeyy + "");
                                    Log.v("@@@@listval", listvaly + "");

                                }

                                msg_received.status = "querycomplete";
                                Log.v("yahan2", "yahan2");
                                Log.v("yahan2org", msg_received.origin_port);
                                Socket sockett = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                        Integer.parseInt(msg_received.origin_port));

                                OutputStream outt = sockett.getOutputStream();
                                Log.v("yahan3", "yahan3");
                                ObjectOutputStream ooutt = new ObjectOutputStream(outt);

                                ooutt.writeObject(msg_received);

                                Log.v("out", outt.toString());
                                /*
                                 * TODO: Fill in your client code that sends out a message.
                                 */

                                sockett.close();
                                Log.v("close1234", "close12");

                            } catch (Exception e) {
                                Log.v("Exception15", "exception15");
                            }
                        }
                    }

					//publishProgress(temps);
                } catch (Exception e) {
                    Log.e("Sah", "Exceptionmsg");
                }
            }
            Log.v("CHECKreturnv", "CHECKreturnv");
            return null;
        }

        protected void onProgressUpdate(String... strings) {
            /*
             * The following code displays what is received in doInBackground().
             */

            Log.v("ser_check_prog-1", "tep");

            try {
				//Log.v("ser_check_prog1","trystatt");

            } catch (Exception e) {
                Log.e("serveron progress", "sometemp");
                Log.e("serveron progress", e.getMessage());
            }
        }

        private Uri buildUri(String scheme, String authority) {
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.authority(authority);
            uriBuilder.scheme(scheme);
            return uriBuilder.build();
        }
    }

    class ClientTask extends AsyncTask<String, Void, Void> {

        static final String REMOTE_PORT0 = "11108";
        static final String REMOTE_PORT1 = "11112";
        static final String REMOTE_PORT2 = "11116";
        static final String REMOTE_PORT3 = "11120";
        static final String REMOTE_PORT4 = "11124";

        @Override
        protected Void doInBackground(String... msgs) {
            try {
                Log.v("clienttask", "clientask");
                String remotePort = REMOTE_PORT0;
				//     if (msgs[1].equals(REMOTE_PORT0))
                //       remotePort = REMOTE_PORT1;

                String psuccesstosend = porthashwithorg.get(mysuccesshash);
                int tempi = Integer.parseInt(psuccesstosend);
                tempi = tempi * 2;

                String tstat = msgs[2];
                Log.v("tstat", tstat);
                if (tstat.equals("insert")) {
                    Log.v("tstat1", tstat);
                    String successorport = tempi + "";
                    String[] portnos = new String[]{successorport};
                    for (String eachport : portnos) {
                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                Integer.parseInt(eachport));

                        String msgToSend = msgs[0];
                        String tkey = msgs[0];
                        String tval = msgs[1];

                        Log.v("tkey", tkey);
                        Log.v("tval", tval);

                        tstat = msgs[2];
                        message_obj msg_obj = new message_obj(myPort, tkey, tval, tstat);

                        OutputStream out = socket.getOutputStream();
                        ObjectOutputStream oout = new ObjectOutputStream(out);

                        oout.writeObject(msg_obj);

                        Log.v("out", out.toString());
                        /*
                         * TODO: Fill in your client code that sends out a message.
                         */

                        socket.close();

                    }
                }

                Log.v("ClientTask", "Loop end send to all including self");
            } catch (UnknownHostException e) {
                Log.e("ClientTask", "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e("ClientTASK", "ClientTask socket IOException");
                Log.e("ClientTASK", e.getMessage());
            }

            return null;
        }
    }

}
