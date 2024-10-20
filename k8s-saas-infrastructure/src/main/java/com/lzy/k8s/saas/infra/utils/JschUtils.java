package com.lzy.k8s.saas.infra.utils;

import com.google.common.collect.Lists;
import com.jcraft.jsch.*;
import com.lzy.k8s.saas.client.result.ErrorCode;
import com.lzy.k8s.saas.infra.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;


@Slf4j
public class JschUtils {

    private static Logger jschLogger = new Logger() {
        @Override
        public boolean isEnabled(int i) {
            // only show warn error fatal
            return i > 1;
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
            }
        }
    };

    public static void closeAll(Session session, String privateKeyFile) {
        if (session != null) {
            session.disconnect();
        }
        File file = new File(privateKeyFile);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                log.info("delete the local pem file fail.");
            }
        }
    }

    public static Session getSshSession(String privateKey, String username, String password, String host, Integer port,
                                        Integer sessionTimeout) {
        try {
            JSch jsch = new JSch();
            File file = new File(privateKey);
            if (file.exists()) {
                jsch.addIdentity(privateKey);
            }
            JSch.setLogger(jschLogger);
            Session session = jsch.getSession(username, host, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("PreferredAuthentications", "publickey,password");
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

    public static String execShellFromFile(Session session, File file) {
        List<String> cmds = extractCmdsFromFile(file);
        return execCmdByShell(session, cmds);
    }

    public static List<String> extractCmdsFromFile(File file) {
        try {
            if (file.exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line;
                List<String> cmds = Lists.newArrayList();
                while ((line = bufferedReader.readLine()) != null) {
                    if (StringUtils.isNotBlank(line)) {
                        cmds.add(line);
                    }
                }
                return cmds;
            }
            return Lists.newArrayList();
        } catch (IOException e) {
            log.error("extractCmdsFromFile fail, err: ", e);
            return Lists.newArrayList();
        }
    }

    public static String execCmdByShell(Session session, List<String> cmds) {
        StringBuilder sb = new StringBuilder();
        ChannelShell channelShell = null;
        try {
            channelShell = (ChannelShell) session.openChannel("shell");
            channelShell.setPty(true);

            InputStream fromServer = channelShell.getInputStream();
            channelShell.connect();
            OutputStream outputStream = channelShell.getOutputStream();
            PrintWriter toServer = new PrintWriter(outputStream);
            for (String cmd : cmds) {
                toServer.println(cmd);
            }
            toServer.flush();

            // read the shell response
            byte[] tmp = new byte[1024];
            while (true) {
                while (fromServer.available() > 0) {
                    int i = fromServer.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    String s = new String(tmp, 0, i);
                    sb.append(s);
                }
                if (channelShell.isClosed()) {
                    log.info("exit-status:" + channelShell.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            outputStream.close();
            fromServer.close();
        } catch(Throwable e){
            log.error("execShellFromFile fail, err: ", e);
        } finally {
            if (channelShell != null) {
                channelShell.disconnect();
            }
        }
        return sb.toString();
    }
}
