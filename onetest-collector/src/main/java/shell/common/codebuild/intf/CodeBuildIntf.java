/**
 * @author : 孙留平
 * @since : 2019年3月26日 上午10:56:58
 * @see:
 */
package shell.common.codebuild.intf;

import shell.common.base.CodeCoverageFilesAndFoldersDTO;

import java.util.List;

public interface CodeBuildIntf {


    /**
     * get all maven proect
     * 
     * @see :
     * @param :
     * @return : List<CodeCoverageFilesAndFoldersDTO>
     * @param projectPath
     * @return
     */
    List<CodeCoverageFilesAndFoldersDTO> getCodeCoverageFilesAndFolders(
            String projectPath);


    /**
     * check multi module
     * 
     * @see :
     * @param :
     * @return : boolean
     * @param projectPath
     * @return
     */
    boolean isThusProjectMulti(String projectPath);

    /**
     * init maven project
     * 
     * @see :
     * @param :
     * @return : CodeCoverageFilesAndFoldersDTO
     * @param projectDir
     * @return
     */
    CodeCoverageFilesAndFoldersDTO initDefaultCodeCoverageFilesAndFoldersDTO(
            String projectDir);


}
