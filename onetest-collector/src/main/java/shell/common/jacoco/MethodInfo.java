package shell.common.jacoco;

import lombok.Data;

@Data
public class MethodInfo {
    /**
     * java file
     */
    public String classFile;
    /**
     * class name
     */
    public String className;
    /**
     * package name
     */
    public String packages;
    /**
     * method  md5
     */
    public String md5;
    /**
     * method
     */
    public String methodName;
    /**
     * method param
     */
    public String parameters;

}
