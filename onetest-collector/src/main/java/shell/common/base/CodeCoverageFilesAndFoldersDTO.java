
package shell.common.base;
import java.io.File;


public class CodeCoverageFilesAndFoldersDTO {
    private File projectDir;

    /**
     * jacoco exec data file
     */
    private File executionDataFile;

    /**
     * complied source class file
     */
    private File classesDirectory;

    /**
     * source code file
     */
    private File sourceDirectory;
    private File reportDirectory;
    private File incrementReportDirectory;

    public File getProjectDir() {
        return projectDir;
    }

    public void setProjectDir(File projectDir) {
        this.projectDir = projectDir;
    }

    public File getExecutionDataFile() {
        return executionDataFile;
    }

    public void setExecutionDataFile(File executionDataFile) {
        this.executionDataFile = executionDataFile;
    }

    public File getClassesDirectory() {
        return classesDirectory;
    }

    public void setClassesDirectory(File classesDirectory) {
        this.classesDirectory = classesDirectory;
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public File getReportDirectory() {
        return reportDirectory;
    }

    public void setReportDirectory(File reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    public File getIncrementReportDirectory() {
        return incrementReportDirectory;
    }

    public void setIncrementReportDirectory(File incrementReportDirectory) {
        this.incrementReportDirectory = incrementReportDirectory;
    }

    @Override
    public String toString() {
        return "CodeCoverageFilesAndFoldersDTO [projectDir=" + projectDir
                + ", executionDataFile=" + executionDataFile
                + ", classesDirectory=" + classesDirectory
                + ", sourceDirectory=" + sourceDirectory + ", reportDirectory="
                + reportDirectory + ", incrementReportDirectory="
                + incrementReportDirectory + "]";
    }
}
