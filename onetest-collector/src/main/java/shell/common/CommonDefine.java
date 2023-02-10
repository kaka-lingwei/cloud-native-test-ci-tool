package shell.common;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CommonDefine {

    private CommonDefine() {

    }

    public static final List<String> COMPANY_MARK_LIST = new ArrayList<>();
    static {
        COMPANY_MARK_LIST.add("com");
        COMPANY_MARK_LIST.add("org");
    }
    /**
     * utf-8 encode
     */
    public static final String CHAR_SET_DEFAULT = Charset.defaultCharset()
            .toString();

    /**
     * base file
     */
    public static final String CURRENT_APP_BASE_FOLDER = "onetest";

    /**
     * full coverageReport Name
     */
    public static final String CODE_COVERAGE_DATA_FOLDER = "coverageReport";

    /**
     * Increment coverageReport Name
     */
    public static final String INCREMENT_CODE_COVERAGE_DATA_FOLDER = CODE_COVERAGE_DATA_FOLDER
            + "Increment";

    /**
     * maven source code
     */
    public static final String DEFAULT_SOURCE_FOLDER = "/src/main/java";

    /**
     * compiled class
     */
    public static final String DEFAULT_CLASSES_FOLDER = "/target/classes";

    /**
     *coverage file
     */
    public static final String JACOCO_COVERAGE_BASE_FOLDER = "jacoco_coverage";

    /**
     * 自定义的基础文件夹-linux
     */
    public static final String LINUX_SELFDEFINED_BASE_FOLDER = "/root/"
            + CURRENT_APP_BASE_FOLDER + "/";

    /**
     * jacoco file
     */
    public static final String LINUX_BASE_FOLDER = LINUX_SELFDEFINED_BASE_FOLDER
            + JACOCO_COVERAGE_BASE_FOLDER;

    /**
     * jacococlient.exec file name
     */
    public static final String JACOCO_EXEC_FILE_NAME_DEFAULT = "jacoco.exec";
    /**
     * pom
     */
    public static final String DEFAULT_POM_FILE_NAME = "pom.xml";

    /**
     * jacocoexec backup
     */
    public static final String DEFAULT_JACOCO_EXEC_BACKUP_FOLDER = "exec_data";



}
