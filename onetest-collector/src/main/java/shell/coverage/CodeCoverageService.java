package shell.coverage;

import lombok.extern.slf4j.Slf4j;
import shell.common.jacoco.JacocoAgentClient;
import shell.common.jacoco.JacocoTcpServer;
import shell.common.jacoco.ReportGenerator;
import shell.utils.CommonFileUtils;

import java.io.File;
import java.util.List;

@Slf4j
public class CodeCoverageService {

    public CodeCoverage createAllCodeCoverageData(CodeCoverage codeCoverage) {

        log.info("Generage full CodeCoverage Data:{}", codeCoverage);
        /**
         *  Check Connectivity with Jacoco ServerIP and  TCP port
         */
        validateJacocoAgentTcpServer(codeCoverage);
        /**
         * Dump Data: jacoco exec data
         */
        dumpCoverageData(codeCoverage);
        /**
         * Generate covetage report by dump data (xml & html)
         */
        ReportGenerator.createWholeCodeCoverageDataWithMulti(codeCoverage);

        return codeCoverage;

    }


    /**
     * Dump Data: jacoco exec data
     *
     * @see :
     * @param :
     * @return : void
     * @param codeCoverage
     */
    private void dumpCoverageData(CodeCoverage codeCoverage) {

        File newerFileFolder = CommonFileUtils.getSourceCodeFile();

        List<JacocoTcpServer> jacocoAgentTcpServers = JacocoTcpServer
                    .parseJacocoAgentTcpServersFromCodeCoverage(codeCoverage);

        for (JacocoTcpServer jacocoAgentTcpServer : jacocoAgentTcpServers) {
                /**
                 * Dump Data: jacoco exec data
                 */
                JacocoAgentClient executionDataClient = new JacocoAgentClient(
                        jacocoAgentTcpServer);
                executionDataClient.dumpData(newerFileFolder.getAbsolutePath(),
                        codeCoverage.getBuildType());
        }
    }

    /**
     * Check Connectivity with Jacoco ServerIP and  TCP port
     * @see :
     * @param :
     * @return : void
     * @param codeCoverage
     */
    private void validateJacocoAgentTcpServer(CodeCoverage codeCoverage) {
            List<JacocoTcpServer> jacocoTcpServers = JacocoTcpServer
                    .parseJacocoAgentTcpServersFromCodeCoverage(codeCoverage);

            for (JacocoTcpServer jacocoTcpServer: jacocoTcpServers) {
                /**
                 * Check Connectivity with Jacoco ServerIP and  TCP port
                 */
                JacocoAgentClient executionDataClient = new JacocoAgentClient(
                        jacocoTcpServer);
                executionDataClient.checkTcpServerCanBeConnected();
            }
    }


    public static void main(String[] args) {

        CodeCoverage codeCoverage=new CodeCoverage();

    }


}
