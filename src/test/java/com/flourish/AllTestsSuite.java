/**
 * A central test suite class that aggregates tests from multiple packages.
 *
 * <p>Usage:
 * <ul>
 *   <li>Use this class in an IDE to run all included packages.</li>
 *   <li>Or use it in specialized CI pipelines that specifically target suite classes.</li>
 * </ul>
 * </p>
 *
 * @author
 *   Joar Eliasson
 * @version
 *   1.1.0
 * @since
 *   2025-02-24
 */
package com.flourish;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

/**
 * JUnit 5 suite that runs all tests in the specified packages.
 */
@Suite
@SelectPackages({
        "com.flourish.service",
        "com.flourish.repository",
        "com.flourish.domain",
        "com.flourish.integration.plantdata",
        "com.flourish.security"
})
@ConfigurationParameter(key = "junit.jupiter.extensions.autodetection.enabled", value = "true")
public class AllTestsSuite {
}
