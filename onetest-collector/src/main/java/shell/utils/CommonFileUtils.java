package shell.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import shell.common.enums.FileSuffix;
import shell.common.jacoco.JacocoOperationUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class CommonFileUtils {

    /**
     * filesandfolders
     */
    public static List<File> filesAndFolders = new ArrayList<>();

    /**
     * only files
     */
    public static List<File> onlyFiles = new ArrayList<>();

    /**
     * only folders
     */
    public static List<File> onlyFolders = new ArrayList<>();

    private static final String CHINESE_CHARSET = "UTF-8";


    private static final int CACHE_SIZE = 2048;


    /**
     * get source code File
     *
     * @see :
     * @param :
     * @return : File
     * @return
     */
    public static File getSourceCodeFile() {
        String newerProjectFolderName="sourceCode";
        File newerFolder = JacocoOperationUtil
                .getCurrentSystemFileStorePath(newerProjectFolderName);
        log.debug("source code file path:{}", newerFolder.getAbsolutePath());
        return newerFolder;
    }


    /**
     * zip file
     *
     * @see :
     * @param :
     * @return : void
     * @param source
     * @param dest
     * @throws IOException
     */
    public static String zip(String source, String dest) throws IOException {
        log.debug("start to zip file");
        if (StringUtils.isEmpty(dest)) {
            dest = source + ".zip";
        }

        File destFile = new File(dest);

        if (destFile.exists()) {
            FileUtils.forceDelete(destFile);
        }

        try {
            zipFolderToFile(source, destFile.getAbsolutePath());
            log.debug("finish zip file");
            return destFile.getAbsolutePath();
        } catch (Exception e) {
            log.error("zip file error:{}", e.getMessage());
            throw new RuntimeException("file not exist!");
        }
    }

    /**
     * <p>
     * zip file
     * </p>
     *
     * @param sourceFolder
     *            source file
     * @param zipFilePath
     *            output file
     * @throws Exception
     */
    public static void zipFolderToFile(String sourceFolder, String zipFilePath)
            throws Exception {
        OutputStream out = new FileOutputStream(zipFilePath);
        BufferedOutputStream bos = new BufferedOutputStream(out);
        ZipOutputStream zos = new ZipOutputStream(bos);
        File file = new File(sourceFolder);
        String basePath = null;
        if (file.isDirectory()) {
            basePath = file.getPath();
        } else {
            basePath = file.getParent();
        }
        zipFile(file, basePath, zos);
        zos.closeEntry();
        zos.close();
        bos.close();
        out.close();
    }

    /**
     * <p>
     * zip file
     * </p>
     *
     * @param parentFile
     * @param basePath
     * @param zos
     * @throws Exception
     */
    private static void zipFile(File parentFile, String basePath,
                                ZipOutputStream zos) throws Exception {
        File[] files = new File[0];
        if (parentFile.isDirectory()) {
            files = parentFile.listFiles();
        } else {
            files = new File[1];
            files[0] = parentFile;
        }
        String pathName;
        InputStream is;
        BufferedInputStream bis;
        byte[] cache = new byte[CACHE_SIZE];
        for (File file : files) {
            if (file.isDirectory()) {
                zipFile(file, basePath, zos);
            } else {
                pathName = file.getPath().substring(basePath.length() + 1);
                is = new FileInputStream(file);
                bis = new BufferedInputStream(is);
                zos.putNextEntry(new ZipEntry(pathName));
                int nRead = 0;
                while ((nRead = bis.read(cache, 0, CACHE_SIZE)) != -1) {
                    zos.write(cache, 0, nRead);
                }
                bis.close();
                is.close();
            }
        }
    }


    /**
     * force delete file
     * @return : void
     * @param toDelteDir
     */
    public static void forceDeleteDirectory(File toDelteDir) {
        try {
            if (toDelteDir.exists()) {
                FileUtils.forceDelete(toDelteDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("delete file error:{}", toDelteDir.getAbsolutePath());
        }
    }




    public static File createFile(String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
            return file;
        } catch (IOException e) {
            log.error(ExceptionUtils.getMessage(e));
            return null;
        }
    }

    /**
     * Copy File
     *
     * @see :
     * @param :
     * @return : void
     * @param oldPath
     * @param newPath
     * @throws IOException
     */
    public static void copyFile(String oldPath, String newPath)
            throws IOException {
        File oldFile = new File(oldPath);
        File file = new File(newPath);

        try (FileInputStream in = new FileInputStream(oldFile);
             FileOutputStream out = new FileOutputStream(file);) {
            byte[] buffer = new byte[2097152];
            int readByte = 0;
            while ((readByte = in.read(buffer)) != -1) {
                out.write(buffer, 0, readByte);
            }
        }
    }

    /**
     * Copy File
     *
     * @see :
     * @param :
     * @return : void
     * @param fromFile
     * @param toFile
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void copyFile(File fromFile, File toFile) {

        try (FileInputStream fileInputStream = new FileInputStream(fromFile);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(
                     fileInputStream);
             FileOutputStream fileOutputStream = new FileOutputStream(
                     toFile);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                     fileOutputStream);) {
            IOUtils.copy(bufferedInputStream, bufferedOutputStream);
        } catch (Exception e) {
            log.error("copy file error:{}", e.getMessage());
        }
    }


    /**
     * get java file in given file path
     *
     * @see :
     * @param :
     * @return : void
     * @param fileFolder
     * @param fileSuffix
     */
    public static List<File> getFilesUnderFolder(String fileFolder,
                                                 FileSuffix fileSuffix) {
        File file = new File(fileFolder);

        if (!file.exists()) {
            throw new RuntimeException("file not exist！！！");
        }
        return getFilesUnderFolder(file, fileSuffix);
    }


    private static List<File> getFilesUnderFolder(File file,
                                                  FileSuffix fileSuffix) {

        if (!file.exists()) {
            throw new RuntimeException("file not exist！！！");
        }

        List<File> listFiles = new ArrayList<>();

        if (file.isFile()) {
            if (file.getName().endsWith(fileSuffix.getFileType())) {
                listFiles.add(file);
            }
        }
        if (file.isDirectory()) {
            File[] filesAndPaths = file.listFiles();
            for (File eachFile : filesAndPaths) {
                listFiles.addAll(getFilesUnderFolder(eachFile, fileSuffix));
            }
        }
        return listFiles;
    }

    /**
     * Copy file from sourcePath to newPath
     * @see :
     * @param :
     * @return : void
     * @param sourcePath
     * @param newPath
     * @throws IOException
     */
    public static void copyDir(String sourcePath, String newPath)
            throws IOException {
        log.debug("Copy file,from:{} to:{}", sourcePath, newPath);
        File file = new File(sourcePath);

        String fileSeperator = File.separator;
        String[] filePath = file.list();

        File newFile = new File(newPath);

        if (!newFile.getParentFile().exists()) {
            newFile.getParentFile().mkdirs();
        }

        if (!newFile.exists()) {
            newFile.mkdir();
        }

        for (int i = 0; i < filePath.length; i++) {
            if ((new File(sourcePath + fileSeperator + filePath[i]))
                    .isDirectory()) {
                copyDir(sourcePath + fileSeperator + filePath[i],
                        newPath + fileSeperator + filePath[i]);
            }

            if (new File(sourcePath + fileSeperator + filePath[i]).isFile()) {
                copyFile(sourcePath + fileSeperator + filePath[i],
                        newPath + fileSeperator + filePath[i]);
            }
        }
    }

    /**
     * Delete File
     *
     * @see :
     * @param :
     * @return : void
     * @param toDeleteFile
     */
    public static void deleteFile(File toDeleteFile) {
        if (toDeleteFile.exists()) {
            try {
                FileUtils.forceDelete(toDeleteFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 遍历文件
     *
     * @see :
     * @param :
     * @param topLevelFolder
     * @return
     */
    public static void walkFolder(File topLevelFolder) {

        if (!topLevelFolder.exists()) {
            return;
        }

        filesAndFolders.add(topLevelFolder);
        if (topLevelFolder.isDirectory()) {
            onlyFolders.add(topLevelFolder);
            File[] filesAndFoldersUnderThis = topLevelFolder.listFiles();
            for (File eachFileUnderThis : filesAndFoldersUnderThis) {
                walkFolder(eachFileUnderThis);
            }
        } else if (topLevelFolder.isFile()) {
            onlyFiles.add(topLevelFolder);
        }
    }

    /**
     * Print list
     *
     * @see :
     * @param :
     * @return : void
     */
    public static void displayListInfo(List<? extends Object> objectList) {
        if (objectList.isEmpty()) {
            log.info("objectList is empty");
        }
        for (Object object : objectList) {
            log.debug(object.toString());
        }
    }



}
