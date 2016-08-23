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
package io.magentys.maven;

import io.magentys.donut.gherkin.Generator;
import io.magentys.donut.gherkin.model.ReportConsole;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

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
     * Generated file prefix. example "filePrefix-" would generate filePrefix-donut-report.html
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

    public void execute() throws MojoExecutionException {

        try {
            if (!sourceDirectory.exists()) {
                throw new MojoExecutionException("BUILD FAILED - as the source directory does not exist");
            }

            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }

            getLog().info("Generating reports...");
            ReportConsole reportConsole = Generator.apply(sourceDirectory.getAbsolutePath(), outputDirectory.getAbsolutePath(), getPrefix(), timestamp, template,
                    countSkippedAsFailure, countPendingAsFailure, countUndefinedAsFailure, countMissingAsFailure, projectName, projectVersion);

            if (reportConsole.buildFailed()) {
                int numberOfFailedScenarios = reportConsole.numberOfFailedScenarios();

                //Putting this condition as build could fail because of other reasons as well.
                if (numberOfFailedScenarios > 0) {
                    throw new MojoExecutionException(String.format("BUILD FAILED - There were %d test failures. - Check Report For Details)", numberOfFailedScenarios));
                } else {
                    throw new MojoExecutionException("BUILD FAILED - Check Report For Details");
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Error Found:", e);
        }

    }

    private String getPrefix() {
        return prefix == null ? "" : prefix;
    }

}
