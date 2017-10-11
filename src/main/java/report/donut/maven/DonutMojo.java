/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package report.donut.maven;

import io.magentys.donut.gherkin.Generator;
import io.magentys.donut.gherkin.model.ReportConsole;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import scala.collection.JavaConverters;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.VERIFY)
public class DonutMojo extends AbstractMojo {

    /**
     * Location of the json files.
     */
    @Parameter(property = "sourceDirectory", required = true, defaultValue = "${project.build.directory}/cucumber-reports")
    private File sourceDirectory;

    /**
     * Location for the report. Default is ${project.build.directory}/donut
     */
    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/donut")
    private File outputDirectory;

    /**
     * Generated file prefix. example "filePrefix-" would generate
     * filePrefix-donut-report.html
     */
    @Parameter(property = "prefix")
    private String prefix;

    /**
     * Execution timestamp.
     */
    @Parameter(property = "timestamp")
    private String timestamp;

    /**
     * Execution template.
     */
    @Parameter(property = "template", defaultValue = "default")
    private String template;

    @Parameter(property = "countSkippedAsFailure", defaultValue = "false")
    private boolean countSkippedAsFailure;

    @Parameter(property = "countPendingAsFailure", defaultValue = "false")
    private boolean countPendingAsFailure;

    @Parameter(property = "countUndefinedAsFailure", defaultValue = "false")
    private boolean countUndefinedAsFailure;

    @Parameter(property = "countMissingAsFailure", defaultValue = "false")
    private boolean countMissingAsFailure;

    @Parameter(property = "projectName", defaultValue = "")
    private String projectName;

    @Parameter(property = "projectVersion", defaultValue = "")
    private String projectVersion;

    @Parameter(property = "customAttributes")
    private List<CustomAttribute> customAttributes;

    @Parameter(property = "skip", defaultValue = "false")
    private boolean skip;

    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping generating reports...");
            return;
        }

        try {
            if (!sourceDirectory.exists()) {
                throw new MojoExecutionException("BUILD FAILED - The source directory does not exist");
            }

            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }

            getLog().info("Generating reports...");
            ReportConsole reportConsole = Generator
                    .apply(sourceDirectory.getAbsolutePath(), outputDirectory.getAbsolutePath(), prefix(), timestamp, template,
                            countSkippedAsFailure, countPendingAsFailure, countUndefinedAsFailure, countMissingAsFailure, projectName, projectVersion,
                            customAttributes());
            //TODO Remove once zip functionality has been added to donut
            zipDonutReport();

            if (reportConsole.buildFailed()) {
                int numberOfFailedScenarios = reportConsole.numberOfFailedScenarios();

                // The build could fail because of other reasons.
                if (numberOfFailedScenarios > 0)
                    throw new MojoExecutionException(
                            String.format("BUILD FAILED - There were %d test failures. - Check Report For Details)", numberOfFailedScenarios));

                throw new MojoExecutionException("BUILD FAILED - Check Report For Details");
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error Found:", e);
        }
    }

    private scala.collection.mutable.Map<String, String> customAttributes() {
        return JavaConverters.mapAsScalaMapConverter(customAttributes.stream().collect(Collectors.toMap(c -> c.getName(), c -> c.getValue())))
                .asScala();
    }

    private String prefix() {
        return prefix == null ? "" : prefix;
    }

    private void zipDonutReport() throws IOException, ArchiveException {
        Optional<File> file = FileUtils.listFiles(outputDirectory, new RegexFileFilter("^(.*)donut-report.html$"), TrueFileFilter.INSTANCE).stream().findFirst();
        if (!file.isPresent())
            throw new FileNotFoundException(String.format("Cannot find a donut report in folder: %s", outputDirectory.getAbsolutePath()));
        File zipFile = new File(outputDirectory, FilenameUtils.removeExtension(file.get().getName()) + ".zip");
        try (OutputStream os = new FileOutputStream(zipFile); ArchiveOutputStream aos = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, os); BufferedInputStream is = new BufferedInputStream(new FileInputStream(file.get()))) {
            aos.putArchiveEntry(new ZipArchiveEntry(file.get().getName()));
            IOUtils.copy(is, aos);
            aos.closeArchiveEntry();
            aos.finish();
        }
    }
}
