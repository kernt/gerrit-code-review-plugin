// Copyright (C) 2018 GerritForge Ltd
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package jenkins.plugins.gerrit.workflow;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GerritReviewStep extends Step {
    private final String label;
    private int score;
    private String message;

    public class Execution extends SynchronousStepExecution<Void>
    {
        private final TaskListener listener;
        private final EnvVars envVars;

        protected Execution(@Nonnull StepContext context) throws IOException, InterruptedException {
            super(context);
            this.envVars = context.get(EnvVars.class);
            this.listener = getContext().get(TaskListener.class);
        }

        @Override
        protected Void run() throws Exception {
            String branch = envVars.get("BRANCH");
            Pattern changeBranchPattern = Pattern.compile("([0-9][0-9])/([0-9]+)/([0-9]+)");
            Matcher matcher = changeBranchPattern.matcher(branch);
            if(matcher.matches()) {
                int changeId = Integer.parseInt(matcher.group(2));
                echo("Gerrit review change %d label %s / %d (%s)", changeId, label, score, message);
            }
            return null;
        }

        private void echo(String fmt, Object... args) {
            String msg = String.format(fmt, args);
            listener.getLogger().println(msg);
        }
    }

    @DataBoundConstructor
    public GerritReviewStep(String label) {
        this.label = label;
    }

    public int getScore() {
        return score;
    }

    @DataBoundSetter
    public void setScore(int score) {
        this.score = score;
    }

    public String getMessage() {
        return message;
    }

    @DataBoundSetter
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<Class<?>> getRequiredContext() {
            return Collections.emptySet();
        }

        @Override
        public String getFunctionName() {
            return "gerritReview";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Gerrit Review Label";
        }
    }
}
