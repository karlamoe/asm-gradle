package buildlogic;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;

public class DependencyPublishingPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPluginManager().apply("java");
        project.getPluginManager().apply("maven-publish");

        var ext = project.getExtensions().getByType(PublishingExtension.class);
        ext.getPublications().register("main", MavenPublication.class, publication -> {
            publication.from(project.getComponents().getByName("java"));
        });

    }
}
