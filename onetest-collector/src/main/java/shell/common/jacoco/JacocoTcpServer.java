package shell.common.jacoco;

import lombok.extern.slf4j.Slf4j;
import shell.coverage.CodeCoverage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JacocoTcpServer {
    private String jacocoAgentIp;
    private int jacocoAgentPort;
    private String jacocoExecFileName = "jacoco.exec";

    public JacocoTcpServer() {

    }

    public JacocoTcpServer(String jacocoAgentIp, int jacocoAgentPort) {
        super();
        this.jacocoAgentIp = jacocoAgentIp;
        this.jacocoAgentPort = jacocoAgentPort;
    }

    public String getJacocoAgentIp() {
        return jacocoAgentIp;
    }

    public void setJacocoAgentIp(String jacocoAgentIp) {
        this.jacocoAgentIp = jacocoAgentIp;
    }

    public int getJacocoAgentPort() {
        return jacocoAgentPort;
    }

    public void setJacocoAgentPort(int jacocoAgentPort) {
        this.jacocoAgentPort = jacocoAgentPort;
    }

    public String getJacocoExecFileName() {
        return jacocoExecFileName;
    }

    public void setJacocoExecFileName(String jacocoExecFileName) {
        this.jacocoExecFileName = jacocoExecFileName;
    }

    @Override
    public String toString() {
        return "JacocoAgentTcpServer [jacocoAgentIp=" + jacocoAgentIp
                + ", jacocoAgentPort=" + jacocoAgentPort
                + ", jacocoExecFileName=" + jacocoExecFileName + "]";
    }


    /**
     * Parse JacocoAgent TCPServer List
     *
     * @see :
     * @param :
     * @return : List<JacocoAgentTcpServer>
     * @param codeCoverage
     * @return
     */
    public static List<JacocoTcpServer> parseJacocoAgentTcpServersFromCodeCoverage(
            CodeCoverage codeCoverage) {
        String tcpServceIps = codeCoverage.getTcpServerIp();


        List<JacocoTcpServer> jacocoAgentTcpServers = new ArrayList<>();

        String tcpServerIpAndPortGroupsSep = ";";
        String tcpServerIpAndPortSep = ":";

        if (tcpServceIps.indexOf(tcpServerIpAndPortSep) == -1
                && tcpServceIps.indexOf(tcpServerIpAndPortGroupsSep) == -1) {

            jacocoAgentTcpServers
                    .add(new JacocoTcpServer(codeCoverage.getTcpServerIp(),
                            codeCoverage.getTcpServerPort()));

            if (codeCoverage.getTcpServerPort() == -1) {
                String errorMessage="Single JacocoAgent TCPServer:Invalid Port ";
                log.info(errorMessage);
                throw new RuntimeException(errorMessage);
            }
            return jacocoAgentTcpServers;
        }

        /**
         * Multi JacocoAgent TCPServer
         */
        String[] tcpServerIpAndPortGroups = tcpServceIps
                .split(tcpServerIpAndPortGroupsSep);

        for (String eachTcpServerIpAndPortGroup : tcpServerIpAndPortGroups) {
            String[] eachTcpServerIpAndPortGroupArr = eachTcpServerIpAndPortGroup
                    .split(tcpServerIpAndPortSep);

            if (eachTcpServerIpAndPortGroupArr.length != 2) {
                String errorMessage="Invalid TCPServer and Port Indo, correct formate:ip:port;ip:port";
                log.info(errorMessage);
                throw new RuntimeException(errorMessage);
            }

            JacocoTcpServer jacocoAgentTcpServer = new JacocoTcpServer(
                    eachTcpServerIpAndPortGroupArr[0],
                    Integer.parseInt(eachTcpServerIpAndPortGroupArr[1]));
            jacocoAgentTcpServers.add(jacocoAgentTcpServer);
        }

        return jacocoAgentTcpServers;
    }


}
