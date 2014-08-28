package org.tool.faster.common.unix;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class LinuxActor {
	public static final int TIMEOUT = 2147483647;

	// This function is never used in prod
	public static List<String> command(String user, String passwd, String host, String command) throws Exception {
		List<String> output = new ArrayList<String>();

		String charset = "UTF-8";

		JSch jsch = new JSch();
		Session session = null;
		Channel channel = null;
		java.util.Properties config = null;

		InputStream in = null;
		BufferedReader reader = null;

		session = jsch.getSession(user, host, 22);
		session.setPassword(passwd);
		config = new java.util.Properties();
		config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();

		channel = session.openChannel("exec");
		((ChannelExec) channel).setCommand(command);
		channel.setInputStream(null);

		channel.connect();
		in = channel.getInputStream();
		reader = new BufferedReader(new InputStreamReader(in, Charset.forName(charset)));
		String buf = null;
		while ((buf = reader.readLine()) != null) {
			output.add(buf);
		}

		reader.close();
		channel.disconnect();
		session.disconnect();

		return output;
	}

	public static String commandStrRet(String user, String passwd, String host, String command) throws Exception {
		List<String> ret = command(user, passwd, host, command);

		String tmp = "";

		for (String str : ret) {
			tmp += str + "\n";
		}
		return tmp;
	}

	public static boolean fileExist(String user, String passwd, String host, String path) throws Exception {
		String command = "ls " + path;
		List<String> list;
		list = command(user, passwd, host, command);

		return list.size() != 0;
	}

	// ---USE SSH AUTHORIZED

	public static String getPrivateKey() {
		String userHome = "user.home";
		String path = System.getProperty(userHome);
		String privateKey = path + "/.ssh/id_rsa";

		return privateKey;
	}

	public static Session getSession(String user, String privateKey, String host) throws Exception {
		JSch jsch = new JSch();
		Session session = null;
		java.util.Properties config = null;

		jsch.addIdentity(privateKey);

		session = jsch.getSession(user, host, 22);

		config = new java.util.Properties();
		config.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect(TIMEOUT);
		session.setServerAliveInterval(TIMEOUT);

		return session;
	}



	public static Result sshCommand(String user, String privateKey, String host, String command) throws Exception {
		Result result = new Result();
		List<String> output = new ArrayList<String>();

		PipedInputStream pin = new PipedInputStream();
		Session session = getSession(user, privateKey, host);
		ChannelExec channel = (ChannelExec) session.openChannel("exec");
		channel.setPty(true);
		channel.setInputStream(null);

		channel.setCommand(command);
		PipedOutputStream pout = new PipedOutputStream(pin);
		channel.setOutputStream(pout);
		channel.setExtOutputStream(pout);
		channel.connect();

		BufferedReader br = new BufferedReader(new InputStreamReader(pin));
		while (true) {
			String line = null;
			while ((line = br.readLine()) != null) {
			output.add(line);
			}

			if (channel.isClosed()) {
				result.exitStatus = channel.getExitStatus();
				if (br != null) {
					try {
						br.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				break;
			}
		}

		
		if (channel.getExitStatus() == 0) {
			result.setSuccess(true);
		} else {
			result.setSuccess(false);
		}
		result.exitStatus = channel.getExitStatus();
		result.exlog=output;
		channel.disconnect();
		session.disconnect();
		return result;

	}

	public static String sshCommandStrRet(String user, String privateKey, String host, String command) throws Exception {
		return sshCommandStrRet(user, getPrivateKey(), host, command);
	}

	public static String sshCommandStrRet(String user, String host, String command) throws Exception {
		List<String> ret = sshCommand(user, host, command,null).exlog;

		String tmp = "";

		for (String str : ret) {
			tmp += str + "\n";
		}
		return tmp;
	}

	public static boolean sshFileExist(String user, String privateKey, String host, String path) throws Exception {
		return sshFileExist(user, getPrivateKey(), host, path);
	}

	public static boolean sshFileExist(String user, String host, String path) throws Exception {
		String command = "ls " + path;
		List<String> list;
		list = sshCommand(user, host, command, null).exlog;

		if (list.size() == 0 || list.get(0).contains("No such file or directory")) {
			return false;
		}
		return true;
	}
	
	public static boolean sshFileExist(String user, String[] hosts, String path,boolean allExist) throws Exception {
		if(allExist){
			for(String _host:hosts){
				if (!sshFileExist(user,_host,path)) {
					return false;
				}
			}	
			return true;
		} else {
			for(String _host:hosts){
				if (sshFileExist(user,_host,path)) {
					return true;
				}
			}
			return false;
		}
	}	

	public static void scp(String user, String privateKey, String host, String src, String dist) throws Exception {
		Session session = getSession(user, privateKey, host);
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftpChannel = (ChannelSftp) channel;

		sftpChannel.get(src, dist);
		sftpChannel.exit();
		channel.disconnect();
		session.disconnect();
	}

	public static void scp(String user, String host, String src, String dist) throws Exception {
		scp(user, getPrivateKey(), host, src, dist);
	}
}
