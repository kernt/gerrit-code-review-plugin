package jenkins.plugins.gerrit.workflow;

import hudson.model.Label;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class GerritReviewStepTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void gerritReviewStepInvokeTest() throws Exception {
        WorkflowJob p = j.jenkins.createProject(WorkflowJob.class, "p");
        p.setDefinition(new CpsFlowDefinition(
                "node {\n" +
                        "  withEnv(['BRANCH=21/4321/1']) {\n" +
                        "    gerritReview label: 'Verified', score: -1, message: 'Does not work'\n" +
                        "  }\n" +
                        "}", true));
        WorkflowRun run = j.assertBuildStatusSuccess(p.scheduleBuild2(0));
        String log = JenkinsRule.getLog(run);
        System.out.println(log);
        assertTrue(log.contains("gerritReview"));
        assertTrue(log.contains("Verified"));
        assertTrue(log.contains("-1"));
        assertTrue(log.contains("Does not work"));
        assertTrue(log.contains("4321"));
    }
}
