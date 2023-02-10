package shell.coverage;

import lombok.Data;
import shell.common.codebuild.entity.BuildType;
import shell.common.codebuild.impl.CodeBuildMaven;
import shell.common.codebuild.intf.CodeBuildIntf;


@Data
public class CodeCoverage {

    /**
     * git projectName
     */
    private String projectName;

    /**
     * Jacoco Server IP
     */
    private String tcpServerIp;

    /**
     * Jacoco Server TCP Port
     */
    private int tcpServerPort;

    private String description;

    private BuildType buildType;

    private String jdkVersion;

    private String sourceCodePath;

    public CodeBuildIntf parseCodeBuildFromThisObject() {

        switch (this.getBuildType()) {
            case MAVEN:
                return new CodeBuildMaven();

            default:
                return new CodeBuildMaven();
        }
    }

}
