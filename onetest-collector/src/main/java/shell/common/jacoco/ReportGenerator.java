
package shell.common.jacoco;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.*;
import org.jacoco.report.html.HTMLFormatter;
import org.jacoco.report.xml.XMLFormatter;
import shell.common.CommonDefine;
import shell.common.base.CodeCoverageFilesAndFoldersDTO;
import shell.common.codebuild.intf.CodeBuildIntf;
import shell.coverage.CodeCoverage;
import shell.utils.CommonFileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class ReportGenerator {
	private static final String JACOCO_VERSION_NOT_MATCH = "Cannot read execution data version";

	public ReportGenerator() {

	}


	/**
	 * generate report in target file
	 *
	 * @see :
	 * @param :
	 * @return : void
	 * @param reportDir
	 * @throws IOException
	 */
	private static void createReportWithMultiProjects(File reportDir,
                                                      List<CodeCoverageFilesAndFoldersDTO> codeCoverageFilesAndFoldersDTOs)
	        throws IOException {
		log.debug("start to generate coverage report in file:{}", reportDir);
		File coverageFolderFile = reportDir;
		if (coverageFolderFile.exists()) {
			CommonFileUtils.forceDeleteDirectory(coverageFolderFile);
		}

		HTMLFormatter htmlFormatter = new HTMLFormatter();
		XMLFormatter xmlFormatter=new XMLFormatter();
		IReportVisitor iReportVisitor = null;
		IReportVisitor iReportVisitor2 = null;


		boolean everCreatedReport = false;

		for (CodeCoverageFilesAndFoldersDTO codeCoverageFilesAndFoldersDTO : codeCoverageFilesAndFoldersDTOs) {
			// class文件为空或者不存在
			boolean classDirNotExists = (null == codeCoverageFilesAndFoldersDTO
			        .getClassesDirectory())
			        || (!(codeCoverageFilesAndFoldersDTO.getClassesDirectory()
			                .exists()));

			// class文件目录不存在
			boolean needNotToCreateReport = classDirNotExists;
			if (needNotToCreateReport) {
				log.debug("file path :{} can not find .class，not generate file",
				        codeCoverageFilesAndFoldersDTO.getProjectDir()
				                .getAbsolutePath());
				continue;
			}

			// 修改标志位
			everCreatedReport = true;
			log.debug("start to generate coverage report for:{}", codeCoverageFilesAndFoldersDTO
			        .getProjectDir().getAbsolutePath());
			IBundleCoverage bundleCoverage = analyzeStructureWithOutChangeMethods(
			        codeCoverageFilesAndFoldersDTO);
			ExecFileLoader execFileLoader = getExecFileLoader(
			        codeCoverageFilesAndFoldersDTO);
			iReportVisitor = htmlFormatter
			        .createVisitor(new FileMultiReportOutput(
			                new File(coverageFolderFile.getAbsolutePath(),
			                        codeCoverageFilesAndFoldersDTO
			                                .getProjectDir().getName())));

			if (null != execFileLoader) {
				iReportVisitor.visitInfo(
				        execFileLoader.getSessionInfoStore().getInfos(),
				        execFileLoader.getExecutionDataStore().getContents());
			}

			ISourceFileLocator iSourceFileLocator = getSourceFileLocatorsUnderThis(
			        codeCoverageFilesAndFoldersDTO.getSourceDirectory());
			iReportVisitor.visitBundle(bundleCoverage, iSourceFileLocator);
			iReportVisitor.visitEnd();

			String fileName=coverageFolderFile.getAbsolutePath()+"/"+
					codeCoverageFilesAndFoldersDTO
									.getProjectDir().getName()+"/"+"jacoco.xml";
			iReportVisitor2=xmlFormatter.createVisitor(new FileOutputStream(
					CommonFileUtils.createFile(fileName)));

			if (null != execFileLoader) {
				iReportVisitor2.visitInfo(
						execFileLoader.getSessionInfoStore().getInfos(),
						execFileLoader.getExecutionDataStore().getContents());
			}
			iReportVisitor2.visitBundle(bundleCoverage, iSourceFileLocator);
			iReportVisitor2.visitEnd();
		}

		if (!everCreatedReport) {
			throw new RuntimeException("can't generate coverage report,please check the the project is empty or uncompiled");
		}
	}

	/**
	 * get all files in target path
	 * @see :
	 * @param :
	 * @return : ISourceFileLocator
	 * @param topLevelSourceFileFolder
	 * @return
	 */
	private static ISourceFileLocator getSourceFileLocatorsUnderThis(
	        File topLevelSourceFileFolder) {
		MultiSourceFileLocator iSourceFileLocator = new MultiSourceFileLocator(
		        4);

		List<File> sourceFileFolders = getSourceFileFoldersUnderThis(
		        topLevelSourceFileFolder);

		for (File eachSourceFileFolder : sourceFileFolders) {
			iSourceFileLocator
			        .add(new DirectorySourceFileLocator(eachSourceFileFolder,
			                CommonDefine.CHAR_SET_DEFAULT, 4));
		}
		return iSourceFileLocator;
	}

	/**
	 * get all source code file in  root path of source code
	 * @see :
	 * @param :
	 * @return : String[]
	 * @param topLevelSourceFileFolder
	 * @return
	 */
	private static List<File> getSourceFileFoldersUnderThis(
	        File topLevelSourceFileFolder) {

		List<File> sourceFileFolders = new ArrayList<>();
		log.debug("get all source code file:{},root path of source code is:",
		        topLevelSourceFileFolder.getAbsolutePath());

		CommonFileUtils.onlyFolders = new ArrayList<>();
		CommonFileUtils.walkFolder(topLevelSourceFileFolder);

		List<File> foldersUnderThis = new ArrayList<>();
		foldersUnderThis.addAll(CommonFileUtils.onlyFolders);
		for (File eachFile : foldersUnderThis) {

			boolean isSourceFileFolder = isThisFolderSourceFileFolder(eachFile);
			if (isSourceFileFolder) {
				sourceFileFolders.add(eachFile.getParentFile());
			}

		}

		CommonFileUtils.displayListInfo(sourceFileFolders);
		return sourceFileFolders;
	}



	/**
	 * 
	 * 
	 * @see :check target file is source Code File
	 * @param :
	 * @return : boolean
	 * @param fileFolder
	 * @return
	 */
	private static boolean isThisFolderSourceFileFolder(File fileFolder) {
		if (!fileFolder.exists()) {
			return false;
		}

		// 如果是文件夹
		if (fileFolder.isDirectory()) {
			String name = fileFolder.getName();
			if (CommonDefine.COMPANY_MARK_LIST.contains(name)) {
				log.debug("Project:{},path: {} is Source Code File", name,
				        fileFolder.getAbsolutePath());
				return true;
			}
		}
		log.debug("Project:{},path: {} is not Source Code File", fileFolder.getName(),
		        fileFolder.getAbsolutePath());
		return false;
	}

	/**
	 * @see :
	 * @param :
	 * @return : ExecFileLoader
	 * @param codeCoverageFilesAndFoldersDTO
	 * @return
	 */
	public static ExecFileLoader getExecFileLoader(
	        CodeCoverageFilesAndFoldersDTO codeCoverageFilesAndFoldersDTO) {
		ExecFileLoader execFileLoaderTemp = new ExecFileLoader();
		try {
			execFileLoaderTemp.load(
			        codeCoverageFilesAndFoldersDTO.getExecutionDataFile());
			return execFileLoaderTemp;
		} catch (IOException e) {
			log.error("error,{}", e.getMessage());
			e.printStackTrace();

			if (StringUtils.isNoneBlank(e.getMessage())
			        && e.getMessage().contains(JACOCO_VERSION_NOT_MATCH)) {
				throw new RuntimeException(
				        "jacoco version not match:" + e.getMessage());
			}

			throw new RuntimeException(
			        "load jacoco coverage exec data error，jacoco version not match");
		}
	}


	private static IBundleCoverage analyzeStructureWithOutChangeMethods(
	        CodeCoverageFilesAndFoldersDTO codeCoverageFilesAndFoldersDTO)
	        throws IOException {
		final CoverageBuilder coverageBuilder = new CoverageBuilder();
		ExecFileLoader execFileLoaderTemp = getExecFileLoader(
		        codeCoverageFilesAndFoldersDTO);
		if (null == execFileLoaderTemp) {
			return coverageBuilder.getBundle(
			        codeCoverageFilesAndFoldersDTO.getProjectDir().getName());
		}
		final Analyzer analyzer = new Analyzer(
		        execFileLoaderTemp.getExecutionDataStore(), coverageBuilder);
		analyzer.analyzeAll(
		        codeCoverageFilesAndFoldersDTO.getClassesDirectory());
		return coverageBuilder.getBundle(
		        codeCoverageFilesAndFoldersDTO.getProjectDir().getName());
	}



	/**
	 * generage full coverage report
	 * 
	 * @see :
	 * @param :
	 * @return : void
	 * @param codeCoverage
	 */
	public static void createWholeCodeCoverageDataWithMulti(
	        CodeCoverage codeCoverage) {
		log.debug("start to generage full coverage report");
		File newerFolder = CommonFileUtils.getSourceCodeFile();


		List<CodeCoverageFilesAndFoldersDTO> codeCoverageFilesAndFoldersDTOs = null;


		CodeBuildIntf codeBuildIntf = codeCoverage.parseCodeBuildFromThisObject();
		codeCoverageFilesAndFoldersDTOs = codeBuildIntf
			        .getCodeCoverageFilesAndFolders(
			                newerFolder.getAbsolutePath());

		createWholeCodeCoverageDataWithMulti(newerFolder,
		        codeCoverageFilesAndFoldersDTOs);

		log.debug("finish generage full coverage report");
	}


	/**
	 * generage full coverage report
	 * 
	 * @see :
	 * @param :
	 * @return : void
	 * @param fileFolder
	 * @param codeCoverageFilesAndFoldersDTOs
	 */
	public static void createWholeCodeCoverageDataWithMulti(File fileFolder,
                                                            List<CodeCoverageFilesAndFoldersDTO> codeCoverageFilesAndFoldersDTOs) {
		try {

			File wholeCodeCoverageDataFolder = new File(
			        fileFolder.getAbsolutePath(),
			        CommonDefine.CODE_COVERAGE_DATA_FOLDER);
			createReportWithMultiProjects(wholeCodeCoverageDataFolder,
			        codeCoverageFilesAndFoldersDTOs);

			File wholeZipFile = new File(fileFolder.getAbsolutePath(),
			        CommonDefine.CODE_COVERAGE_DATA_FOLDER + ".zip");

			CommonFileUtils.zip(wholeCodeCoverageDataFolder.getAbsolutePath(),
			        wholeZipFile.getAbsolutePath());
		} catch (IOException e) {
			log.error("error:{}", e.getMessage());
			throw new RuntimeException("generage full coverage report error！");
		}

	}


	public static void main(String[] args) {

	}

}
