/**
 * @author : 孙留平
 * @since : 2019年2月26日 下午6:09:06
 * @see:
 */
package shell.common.jacoco;


import lombok.extern.slf4j.Slf4j;
import org.jacoco.core.tools.ExecDumpClient;
import org.jacoco.core.tools.ExecFileLoader;
import shell.common.CommonDefine;
import shell.common.base.CodeCoverageFilesAndFoldersDTO;
import shell.common.codebuild.entity.BuildType;
import shell.common.codebuild.impl.CodeBuildMaven;
import shell.common.codebuild.intf.CodeBuildIntf;
import shell.utils.CommonFileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Slf4j
public final class JacocoAgentClient {

	private JacocoTcpServer jacocoAgentTcpServer;

	public JacocoAgentClient(JacocoTcpServer jacocoAgentTcpServer) {
		this.jacocoAgentTcpServer = jacocoAgentTcpServer;
	}

	public JacocoAgentClient(String tcpServerIp, int tcpPort) {
		this.jacocoAgentTcpServer = new JacocoTcpServer(tcpServerIp,
		        tcpPort);
	}

	/**
	 * Dump Data: jacoco exec data
	 *
	 * @see :
	 * @param :
	 * @return : void
	 */
	public void dumpData(String folderPath, BuildType buildType) {
		CodeBuildIntf codeBuildIntf = new CodeBuildMaven();
		List<CodeCoverageFilesAndFoldersDTO> codeCoverageFilesAndFoldersDTOs = codeBuildIntf
		        .getCodeCoverageFilesAndFolders(folderPath);

		dumpExecData(folderPath, codeCoverageFilesAndFoldersDTOs);
	}

	/**
	 * Check Connectivity with Jacoco ServerIP and  TCP port
	 *
	 * @see :
	 * @param :
	 * @return : boolean
	 * @return
	 */
	public boolean checkTcpServerCanBeConnected() {
		try {
			ExecDumpClient dumpClient = new ExecDumpClient();
			dumpClient.dump(this.jacocoAgentTcpServer.getJacocoAgentIp(),
			        this.jacocoAgentTcpServer.getJacocoAgentPort());
			return true;
		} catch (Exception e) {
			log.error("can't connect to Jacoco tcpServer，please check config:{}", e.getMessage());
			String errorMessage = String.format("can't connect to Jacoco tcpServer，please check config",
			        this.jacocoAgentTcpServer.getJacocoAgentIp(),
			        this.jacocoAgentTcpServer.getJacocoAgentPort());
			throw new RuntimeException(errorMessage);
		}
	}


	/**
	 * Dump Data: jacoco exec data
	 *
	 * @see :
	 * @param :
	 * @return : void
	 */
	public void dumpExecData(String folderPath,
                             List<CodeCoverageFilesAndFoldersDTO> codeCoverageFilesAndFoldersDTOs) {
		log.debug("start to dump coverage exec data from :{} to :{} ", this.jacocoAgentTcpServer,
		        folderPath);

		File backUpExecFile = getBackUpExecFile();
		File parentFolderExecFile = new File(folderPath,
		        CommonDefine.JACOCO_EXEC_FILE_NAME_DEFAULT);

		// 如果当前不存在，且备份文件夹中有，则先从备份文件夹中取一份，复制到当前文件夹，保持有旧数据存在
		if (!parentFolderExecFile.exists() && backUpExecFile.exists()) {
			CommonFileUtils.copyFile(backUpExecFile, parentFolderExecFile);
		}
		backUpExecData();
		dumpExecDataToFile(parentFolderExecFile.getAbsolutePath());
		dumpExecData(codeCoverageFilesAndFoldersDTOs);
	}

	/**
	 * Backup execData
	 *
	 * @see :
	 * @param :
	 * @return : void
	 */
	public void backUpExecData() {
		log.debug("start to dump coverage exec data...");
		dumpExecDataToFile(getExecFilesBackUpFolder(),
		        CommonDefine.JACOCO_EXEC_FILE_NAME_DEFAULT);
		log.debug("finish dump coverage exec data");
	}

	/**
	 * Dump Exec Data And Save to Local File
	 *
	 * @see :
	 * @param :
	 * @return : void
	 */
	public void dumpExecDataToFile(String folderPath, String fileName) {
		dumpExecDataToFile(new File(folderPath, fileName).getAbsolutePath());
	}

	/**
	 * Dump Exec Data And Save to Local File
	 *
	 * @see :
	 * @param :
	 * @return : void
	 */
	public void dumpExecDataToFile(String filePath) {
		log.debug("start to dump coverage exec data from :{} to file:{}", this.jacocoAgentTcpServer,
		        filePath);
		ExecDumpClient dumpClient = new ExecDumpClient();
		dumpClient.setDump(true);
		ExecFileLoader execFileLoader = null;
		try {
			execFileLoader = dumpClient.dump(
			        this.jacocoAgentTcpServer.getJacocoAgentIp(),
			        this.jacocoAgentTcpServer.getJacocoAgentPort());

			execFileLoader.save(new File(filePath), true);
		} catch (IOException e2) {
			log.error("dump exec data failed:{}", e2.getMessage());
			throw new RuntimeException("tcp服务连接失败,请查看tcp配置");
		}
	}


	/**
	 * Dump Exec Data
	 *
	 * @see :
	 * @param :
	 * @return : void
	 */
	public void dumpExecData(
	        List<CodeCoverageFilesAndFoldersDTO> codeCoverageFilesAndFoldersDTOs) {

		File backUpExecFile = getBackUpExecFile();
		for (CodeCoverageFilesAndFoldersDTO codeCoverageFilesAndFoldersDTO : codeCoverageFilesAndFoldersDTOs) {
			File eachModuleExecFile = codeCoverageFilesAndFoldersDTO
			        .getExecutionDataFile();
			if (!eachModuleExecFile.exists() && backUpExecFile.exists()) {
				CommonFileUtils.copyFile(backUpExecFile, eachModuleExecFile);
			}

			backUpExecData();
			dumpExecDataToFile(eachModuleExecFile.getAbsolutePath());
		}
	}

	/**
	 * get local path of dump data file
	 *
	 * @see :
	 * @param :
	 * @return : String
	 * @return
	 */
	private String getExecFilesBackUpFolder() {
		return getExecFilesBackUpFolderFromJacocoAgentServer(
		        this.jacocoAgentTcpServer);
	}

	public static String getExecFilesBackUpFolderFromJacocoAgentServer(
	        JacocoTcpServer jacocoAgentTcpServer) {
		return JacocoOperationUtil.getJacocoCoverageExecBackupFolder()
		        + File.separator + jacocoAgentTcpServer.getJacocoAgentIp() + "_"
		        + jacocoAgentTcpServer.getJacocoAgentPort();
	}


	/**
	 *
	 * @see :
	 * @param :
	 * @return : String
	 * @return
	 */

	private File getBackUpExecFile() {
		return new File(getExecFilesBackUpFolder(),
		        CommonDefine.JACOCO_EXEC_FILE_NAME_DEFAULT);
	}
}
