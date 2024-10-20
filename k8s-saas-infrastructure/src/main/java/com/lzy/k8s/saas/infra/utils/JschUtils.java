package com.lzy.k8s.saas.infra.utils;

import com.jcraft.jsch.*;
import com.lzy.k8s.saas.client.result.ErrorCode;
import com.lzy.k8s.saas.infra.exception.SystemException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;
import java.util.Properties;


@Slf4j
public class JschUtils {

    private static Logger jschLogger = new Logger() {
        @Override
        public boolean isEnabled(int i) {
            return true;
        }

        @Override
        public void log(int i, String s) {
            if (i == 0) {
                log.debug(s);
            } else if (i == 1) {
                log.info(s);
            } else if (i == 2) {
                log.warn(s);
            } else if (i == 3) {
                log.error(s);
            } else if (i == 4) {
                // fatal
                log.error(s);
            } else {
                log.info(s);
            }
        }
    };

    public static void closeSshSession(Session session) {
        if (session != null) {
            session.disconnect();
        }
    }

    public static Session getSshSession(String username, String password, String host, Integer port,
                                        Integer sessionTimeout) {
        try {
            JSch jsch = new JSch();
            JSch.setLogger(jschLogger);
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            // timeout seconds
            session.setTimeout(sessionTimeout);

            if (!session.isConnected()) {
                session.connect();
            }
            return session;
        } catch (Throwable e){
            throw new SystemException(ErrorCode.CONNECT_LINUX_FAIL);
        }
    }

    public static String execCmdByShell(Session session, List<String> cmds) {
        String result = "";
        ChannelShell channelShell = null;
        try {
            channelShell = (ChannelShell) session.openChannel("shell");

            InputStream inputStream = channelShell.getInputStream();
            channelShell.setPty(true);
            channelShell.connect();

            OutputStream outputStream = channelShell.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);
            for (String cmd : cmds) {
                printWriter.println(cmd);
            }
            printWriter.flush();
            byte[] tmp = new byte[1024];
            while (true) {
                while (inputStream.available() > 0) {
                    int i = inputStream.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    String s = new String(tmp, 0, i);
                    if (s.contains("--More--")) {
                        outputStream.write((" ").getBytes());
                        outputStream.flush();
                    }
                    log.info(s);
                }
                if (channelShell.isClosed()) {
                    log.info("exit-status:" + channelShell.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            outputStream.close();
            inputStream.close();
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if (channelShell != null) {
                channelShell.disconnect();
            }
        }
        return result;
    }
}
