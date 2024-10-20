package com.lzy.k8s.saas.core.test;

import com.google.common.collect.Lists;
import com.jcraft.jsch.Session;
import com.lzy.k8s.saas.infra.utils.JschUtils;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.List;

public class LinuxShellTest extends BaseCoreTest {

    @Test
    public void execShell() {
        Session sshSession = JschUtils.getSshSession("C:\\Users\\hzsdl\\Downloads\\k8s-ubuntu-master-node.pem", "ubuntu", "can",
                "3.107.41.143", 22, 600);
        List<String> cmds = Lists.newArrayList();
        cmds.add("ls /");
//        cmds.add("exit");
        String line = JschUtils.execCmdByShell(sshSession, cmds);

        System.out.println(line);
        JschUtils.closeAll(sshSession, "");
    }

    @Test
    public void execShellFromFile() throws FileNotFoundException {
        Session sshSession = JschUtils.getSshSession("C:\\Users\\hzsdl\\Downloads\\k8s-ubuntu-master-node.pem", "ubuntu", "can",
                "3.107.41.143", 22, 600);
        String line = JschUtils.execShellFromFile(sshSession, ResourceUtils.getFile("classpath:shell/test.sh"));

        System.out.println(line);
        JschUtils.closeAll(sshSession, "");
    }


}
