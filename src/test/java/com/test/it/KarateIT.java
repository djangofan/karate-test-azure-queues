package com.test.it;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import lombok.extern.slf4j.Slf4j;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class KarateIT extends KarateTestBase {

    private static final String APP_NAME = System.getProperty("functionAppName");
    private static final String REPORT_TITLE = APP_NAME + " (karate) ENV: " + System.getProperty("karate.env");
    private static final String TAG_PARAM = "karate.tag";

    public static void main(String[] args) {
        new KarateIT().runAPITests();
    }

    @Override
    @Test
    public void runAPITests() {
        String tag = StringUtils.isBlank(System.getProperty(TAG_PARAM)) ? "@regression" : System.getProperty(TAG_PARAM);
        Results results = Runner.path("classpath:features")
                .tags(tag)
                .reportDir("target/karate-reports/xml")
                .outputCucumberJson(true)
                .outputJunitXml(true)
                .parallel(2);

        Assertions.assertTrue(results.getFeaturesTotal() > 0,
                "Did not find any cucumber tests to execute.");

        generateReport(results.getReportDir());

        assertEquals(0, results.getFailCount(), "Had at least one test failure.");
    }

    static void generateReport(String karateOutputPath) {
        Collection<File> jsonFiles = FileUtils.listFiles(new File(karateOutputPath), new String[] {"json"}, true);

        List<String> jsonPaths = jsonFiles.stream().map(File::getAbsolutePath).collect(Collectors.toList());

        Configuration config = new Configuration(new File("target"), REPORT_TITLE);
        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, config);

        reportBuilder.generateReports();
    }

}
