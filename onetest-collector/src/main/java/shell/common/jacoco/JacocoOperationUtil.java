/**
 * @author : 孙留平
 * @since : 2019年3月6日 下午9:34:33
 * @see:
 */
package shell.common.jacoco;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import shell.common.CommonDefine;

import java.io.File;


@Slf4j
public class JacocoOperationUtil {

    private JacocoOperationUtil() {

    }

    /**
     * get jacoco coverage path
     * 
     * @see :
     * @param :
     * @return : String
     * @return
     */
    public static String getJacocoCoverageBaseFolder() {

            return CommonDefine.LINUX_BASE_FOLDER;

    }

    /**
     * get jacoco coverage path
     * 
     * @see :
     * @param :
     * @return : String
     * @return
     */
    public static String getJacocoCoverageExecBackupFolder() {
        String jacocoCoverageBaseFolder = getJacocoCoverageBaseFolder();
        if (StringUtils.isEmpty(jacocoCoverageBaseFolder)) {
            return null;
        }

        return jacocoCoverageBaseFolder + File.separator
                + CommonDefine.DEFAULT_JACOCO_EXEC_BACKUP_FOLDER;
    }

    /**
     * get current file path
     * @see :
     * @param :
     * @return : File
     * @param filePath
     * @return
     */
    public static File getCurrentSystemFileStorePath(String filePath) {
        return new File(getJacocoCoverageBaseFolder(), filePath);
    }

}
