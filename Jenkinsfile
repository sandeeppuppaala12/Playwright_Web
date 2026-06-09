/**
 * Jenkins Pipeline for Playwright Web Automation
 * Configured for Ubuntu Linux environment with parallel browser execution
 * 
 * Prerequisites:
 * - Jenkins with Pipeline plugin
 * - Java 17+ installed
 * - Maven installed
 * - Playwright dependencies installed on agent
 */

pipeline {
    agent any
    tools{
        maven 'Playwright_Maven'
    }
    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        JAVA_TOOL_OPTIONS = '-XX:+UnlockExperimentalVMOptions -XX:+UseG1GC'
        PATH = "${JAVA_HOME}/bin:${PATH}"
    }

    stages {
        stage('Setup Environment') {
            steps {
                script {
                    echo "========== Verifying Environment =========="
                    sh '''
                        echo "Java Version:"
                        java -version
                        echo ""
                        echo "Maven Version:"
                        mvn -version
                        echo ""
                        echo "OS Information:"
                        uname -a
                    '''
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    echo "========== Building Maven Project =========="
                    sh 'mvn clean compile -DskipTests -q'
                }
            }
        }

        stage('Test - Parallel Execution') {
            steps {
                script {
                    echo "========== Executing Tests in Parallel (Chrome, Firefox, Edge) =========="
                    /*sh '''
                        # Run tests with parallel execution
                        mvn test \
                            -Dgroups="ui" \
                            -Dorg.slf4j.simpleLogger.defaultLogLevel=INFO \
                            --no-transfer-progress
                    '''*/
                   sh 'mvn clean test -DsuiteXmlFile=testng.xml'
                }
            }
        }

        stage('Generate Reports') {
            steps {
                script {
                    echo "========== Processing Test Results =========="
                    sh '''
                        if [ -d "Reports" ]; then
                            echo "Test report generated at: $WORKSPACE/Reports/Report_*.html"
                            ls -la Reports/ || true
                            ls -la Reports/Screenshots/ || true
                        fi
                    '''
                }
            }
        }
    }

    post {
        always {
            script {
                echo "========== Archiving Artifacts =========="
                
                // Archive HTML Reports
                archiveArtifacts artifacts: 'Reports/**/*.html', 
                                 allowEmptyArchive: true, 
                                 onlyIfSuccessful: false
                
                // Archive Screenshots
                archiveArtifacts artifacts: 'Reports/Screenshots/**/*.png', 
                                 allowEmptyArchive: true, 
                                 onlyIfSuccessful: false
                
                // Archive Logs
                archiveArtifacts artifacts: 'logs/**/*.log*', 
                                 allowEmptyArchive: true, 
                                 onlyIfSuccessful: false
                
                // Archive TestNG Reports
                archiveArtifacts artifacts: 'test-output/**', 
                                 allowEmptyArchive: true, 
                                 onlyIfSuccessful: false
            }
        }

        success {
            script {
                echo "========== BUILD SUCCESSFUL =========="
                // Send success notification
                emailext(
                    subject: "✓ Playwright Web Automation Tests - PASSED [${env.BUILD_NUMBER}]",
                    body: '''
                        The automated tests have completed successfully.
                        
                        Build: ${BUILD_NUMBER}
                        Status: SUCCESS
                        Duration: ${BUILD_DURATION}
                        
                        Log: ${BUILD_LOG}
                        
                        Artifacts: ${BUILD_URL}artifact/
                    ''',
                    to: '${DEFAULT_RECIPIENTS}',
                    mimeType: 'text/html'
                )
            }
        }

        failure {
            script {
                echo "========== BUILD FAILED =========="
                // Send failure notification
                emailext(
                    subject: "✗ Playwright Web Automation Tests - FAILED [${env.BUILD_NUMBER}]",
                    body: '''
                        The automated tests have FAILED.
                        
                        Build: ${BUILD_NUMBER}
                        Status: FAILURE
                        Duration: ${BUILD_DURATION}
                        
                        Please review the logs and artifacts.
                        
                        Log: ${BUILD_LOG}
                        Artifacts: ${BUILD_URL}artifact/
                    ''',
                    to: '${DEFAULT_RECIPIENTS}',
                    mimeType: 'text/html'
                )
            }
        }

        unstable {
            script {
                echo "========== BUILD UNSTABLE =========="
                echo "Some tests may have been skipped or had warnings"
            }
        }

        cleanup {
            script {
                echo "========== Cleaning Up =========="
                deleteDir()
            }
        }
    }
}
