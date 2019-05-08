package report.donut.maven;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class ResultSource {

    private String format;
    private File directory;

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public File getDirectory() {
        return directory;
    }

    @Override
    public String toString() {
        return (StringUtils.isEmpty(format) ? "cucumber" : format) + ":" + directory.getAbsolutePath();
    }
}
