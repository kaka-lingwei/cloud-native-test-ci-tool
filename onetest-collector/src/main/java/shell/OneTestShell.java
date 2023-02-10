package shell;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import shell.common.codebuild.entity.BuildType;
import shell.coverage.CodeCoverage;
import shell.coverage.CodeCoverageService;

@Slf4j
public class OneTestShell {


    public static void main(String[] args) throws Exception {

        CodeCoverage codeCoverage=new CodeCoverage();
        codeCoverage.setBuildType(BuildType.MAVEN);
        String ips=System.getProperty("ALL_IP");;
        String port=System.getProperty("PORT");
        System.out.println(ips);
        System.out.println(port);
        String[] ipInfos=ips.split(",");
        String serverIps="";
        for(String ip:ipInfos)
        {
            if(StringUtils.isNotBlank(ip)) {
                String[] temp = ip.split(":");
                String serverIp = temp[1];
                serverIp = serverIp + ":" + port;
                serverIps = serverIps + serverIp + ";";
            }
        }
        codeCoverage.setTcpServerIp(serverIps);
        codeCoverage.setProjectName("rocketmq");
        codeCoverage.setJdkVersion("1.8");
        codeCoverage.setTcpServerPort(Integer.valueOf(port));

        CodeCoverageService codeCoverageService=new CodeCoverageService();
        codeCoverageService.createAllCodeCoverageData(codeCoverage);


    }
}
