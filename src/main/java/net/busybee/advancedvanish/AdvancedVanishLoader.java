package net.busybee.advancedvanish;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.Exclusion;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.List;

/**
 * Downloads the cross-server sync libraries into the server's {@code libraries} folder on first
 * start, so the plugin jar stays small. Keep the versions in step with build.gradle.kts.
 */
@SuppressWarnings("UnstableApiUsage")
public final class AdvancedVanishLoader implements PluginLoader {

    private static final List<String> LIBRARIES = List.of(
            "redis.clients:jedis:7.5.3",
            "com.zaxxer:HikariCP:7.1.0");

    /** Paper asks plugins not to download from Maven Central directly and provides this mirror instead. */
    private static final String DEFAULT_MIRROR = "https://maven-central.storage-download.googleapis.com/maven2";

    @Override
    public void classloader(PluginClasspathBuilder builder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder("central", "default", centralUrl()).build());
        // Paper already provides SLF4J; a second copy would print "No SLF4J providers" warnings
        List<Exclusion> exclusions = List.of(new Exclusion("org.slf4j", "*", "*", "*"));
        for (String coordinates : LIBRARIES) {
            resolver.addDependency(new Dependency(new DefaultArtifact(coordinates), null, false, exclusions));
        }
        builder.addLibrary(resolver);
    }

    private static String centralUrl() {
        String configured = System.getenv("PAPER_DEFAULT_CENTRAL_REPOSITORY");
        return configured == null || configured.isBlank() ? DEFAULT_MIRROR : configured;
    }
}
