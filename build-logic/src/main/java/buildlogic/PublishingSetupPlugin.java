package buildlogic;

import moe.karla.maven.publishing.MavenPublishingExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.publish.PublishingExtension;

public class PublishingSetupPlugin implements Plugin<Project> {
    private static final String TASK_NAME = "publishAllPublicationsToRootStageRepository";


    @Override
    public void apply(Project project) {
        project.getPluginManager().withPlugin("maven-publish", $ -> setup(project));

        if (project == project.getRootProject()) {
            project.getPluginManager().apply("moe.karla.maven-publishing");

            var ext = project.getExtensions().getByType(MavenPublishingExtension.class);

            ext.url = "https://github.com/karlamoe/asm-gradle";
            ext.developer("Karlatemp", "i@karla.moe");

            ext.license("MIT License", "https://github.com/karlamoe/asm-gradle/blob/main/LICENSE");
        }
    }

    private void setup(Project project) {
        var repoLocation = project.getRootProject().file("../build/maven-publishing-stage");
        project.getExtensions().getByType(PublishingExtension.class).repositories(repos -> {
            repos.maven(repo -> {
                repo.setName("RootStage");
                repo.setUrl(repoLocation.toURI());
            });
        });

        if (project != project.getRootProject()) {
            project.getRootProject().getPluginManager().apply(RootRepoPlugin.class);
            project.getRootProject().getTasks().named(TASK_NAME).configure(task -> {
                task.dependsOn(project.getTasks().named(TASK_NAME));
            });
        }
    }


    static class RootRepoPlugin implements Plugin<Project> {
        @Override
        public void apply(Project project) {
            project.getTasks().maybeCreate(TASK_NAME);
        }
    }
}
