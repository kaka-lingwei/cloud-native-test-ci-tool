
package shell.common.codebuild.impl;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shell.common.CommonDefine;
import shell.common.base.CodeCoverageFilesAndFoldersDTO;
import shell.common.codebuild.intf.CodeBuildIntf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CodeBuildMaven implements CodeBuildIntf {
    private static final Logger logger = LoggerFactory
            .getLogger(CodeBuildMaven.class);

    public CodeBuildMaven() {

    }
    /**
     * init maven project
     * 
     * @see :
     * @param :
     * @return : CodeCoverageFilesAndFoldersDTO
     * @param projectDir
     * @return
     */
    @Override
    public CodeCoverageFilesAndFoldersDTO initDefaultCodeCoverageFilesAndFoldersDTO(
            String projectDir) {
        CodeCoverageFilesAndFoldersDTO codeCoverageFilesAndFoldersDTO = new CodeCoverageFilesAndFoldersDTO();
        codeCoverageFilesAndFoldersDTO.setProjectDir(new File(projectDir));
        codeCoverageFilesAndFoldersDTO.setSourceDirectory(
                new File(projectDir, CommonDefine.DEFAULT_SOURCE_FOLDER));

        codeCoverageFilesAndFoldersDTO.setClassesDirectory(
                new File(projectDir, CommonDefine.DEFAULT_CLASSES_FOLDER));

        codeCoverageFilesAndFoldersDTO.setExecutionDataFile(new File(projectDir,
                CommonDefine.JACOCO_EXEC_FILE_NAME_DEFAULT));

        codeCoverageFilesAndFoldersDTO.setReportDirectory(
                new File(projectDir, CommonDefine.CODE_COVERAGE_DATA_FOLDER));

        codeCoverageFilesAndFoldersDTO.setIncrementReportDirectory(new File(
                projectDir, CommonDefine.INCREMENT_CODE_COVERAGE_DATA_FOLDER));

        return codeCoverageFilesAndFoldersDTO;
    }

    /**
     * parse pom.xml
     * 
     * @see :
     * @param :
     * @return : void
     * @param pomFilePath
     */
    public static Model parsePom(String pomFilePath) {
        String finalPomFilePath = null;
        // 如果路径以pom.xml结尾，则代表是个文件
        if (pomFilePath.endsWith(CommonDefine.DEFAULT_POM_FILE_NAME)) {
            finalPomFilePath = pomFilePath;
        } else {
            // 如果不是以pom.xml结尾
            File currentPath = new File(pomFilePath);
            // 不是以pom.xml结尾，又是个文件，则报错
            if (currentPath.isFile()) {
                throw new RuntimeException("not pom file，can not parse");
            }

            if (!checkProjectMaven(pomFilePath)) {
                finalPomFilePath = null;
            } else {
                finalPomFilePath = pomFilePath + File.separator
                        + CommonDefine.DEFAULT_POM_FILE_NAME;
            }
        }

        if (null == finalPomFilePath) {
            throw new RuntimeException(
                    pomFilePath + "can't find pom.xml");
        }

        try (FileInputStream fileInputStream = new FileInputStream(
                new File(finalPomFilePath));) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            return reader.read(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("file operation error FileNotFoundException:{}", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("file operation error :{}", e.getMessage());
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            logger.error("file operation error XmlPullParserException:{}", e.getMessage());
        }
        return null;
    }

    /**
     * Get SubModule from module
     * 
     * @see :
     * @param :
     * @return : List<String>
     * @param model
     * @return
     */
    public static List<String> getModulesFromModel(Model model) {
        if (null == model) {
            return new ArrayList<>();
        }
        List<String> moduleList = model.getModules();

        List<String> finalModuleList = new ArrayList<>();
        for (String module : moduleList) {
            if (!module.startsWith("..")) {
                finalModuleList.add(module);
            }
        }
        return finalModuleList;
    }

    /**
     * parse module info from pom.xml
     * 
     * @see :
     * @param :
     * @return : List<String>
     * @param pomFile
     * @return
     */
    public static List<String> getModulesFromPomFile(String pomFile) {
        Model model = parsePom(pomFile);
        if (null == model) {
            return new ArrayList<>();
        }
        return getModulesFromModel(model);
    }

    /**
     * @see :
     * @param :
     * @return : void
     */
    public static boolean checkProjectMaven(String projectDir) {

        File projectFile = new File(projectDir);

        if (!projectFile.exists()) {
            return false;
        }

        if (!projectFile.isDirectory()) {
            return false;
        }
        File pomFileTemp = new File(projectDir,
                CommonDefine.DEFAULT_POM_FILE_NAME);
        // 如果pom文件不存在
        return pomFileTemp.exists();
    }


    /**
     * get all maven proect
     *
     */
    @Override
    public List<CodeCoverageFilesAndFoldersDTO> getCodeCoverageFilesAndFolders(
            String projectPath) {

        List<CodeCoverageFilesAndFoldersDTO> codeCoverageFilesAndFoldersDTOs = new ArrayList<>();

        if (!isThusProjectMulti(projectPath)) {
            CodeCoverageFilesAndFoldersDTO codeCoverageFilesAndFoldersDTO = initDefaultCodeCoverageFilesAndFoldersDTO(
                    projectPath);
            codeCoverageFilesAndFoldersDTOs.add(codeCoverageFilesAndFoldersDTO);
            return codeCoverageFilesAndFoldersDTOs;
        }

        List<String> modules = CodeBuildMaven
                .getModulesFromPomFile(projectPath);
        for (String eachModuleName : modules) {
            File currentModuleProject = new File(projectPath, eachModuleName);
            CodeCoverageFilesAndFoldersDTO codeCoverageFilesAndFoldersDTO = initDefaultCodeCoverageFilesAndFoldersDTO(
                    currentModuleProject.getAbsolutePath());
            codeCoverageFilesAndFoldersDTOs.add(codeCoverageFilesAndFoldersDTO);
        }
        return codeCoverageFilesAndFoldersDTOs;
    }


    /**
     * check multi module
     * 
     * @see : parsee parent pom.xml
     * @author Administrator
     */
    @Override
    public boolean isThusProjectMulti(String projectPath) {
        boolean doesNotHaveSubModules = getModulesFromPomFile(projectPath)
                .isEmpty();
        return !doesNotHaveSubModules;
    }



}
