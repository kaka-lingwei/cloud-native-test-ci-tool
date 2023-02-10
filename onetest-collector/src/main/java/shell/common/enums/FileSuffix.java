
package shell.common.enums;


public enum FileSuffix {
	/**
	 * java
	 */
	JAVA_FILE(".java"),
	/**
	 * pom
	 */
	POM_XML_FILE("pom.xml");

	private String fileType;

	private FileSuffix() {
	}

	private FileSuffix(String fileType) {
		this.fileType = fileType;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
