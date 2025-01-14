
package shell.common.codebuild;


public class CodeBuildDefaultDefines {
    private CodeBuildDefaultDefines() {
    }

    public static final String TARGET_COMPILE = "compile";

    public static final String CODE_BUILD_TARGET = "target";

    public static final String CODE_BUILD_PROPERTY = "property";

    public static final String CODE_BUILD_PROPERTY_ATTRIBUTE = "file";

    public static final String CODE_BUILD_COMPILE_COMMAND = "javac";

    public static final String CODE_BUILD_COMPILE_DESTDIR = "destdir";

    public static final String CODE_BUILD_COMPILE_SRCDIR = "srcdir";

    public static final String DEFAULT_DELIMETER = ":";

    public static final String CODE_BUILD_COMPILE_COMMAND_START_TAG = "<"
            + CODE_BUILD_COMPILE_COMMAND;
}
