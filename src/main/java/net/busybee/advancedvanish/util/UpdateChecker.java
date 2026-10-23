package net.busybee.advancedvanish.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Compares the running version with the latest GitHub release, off the server threads. */
public final class UpdateChecker {

    private static final String LATEST_RELEASE = "https://api.github.com/repos/BusyBee-Development/AdvancedVanish/releases/latest";
    private static final String RELEASES_PAGE = "https://github.com/BusyBee-Development/AdvancedVanish/releases";

    private UpdateChecker() {
    }

    public static void check(JavaPlugin plugin) {
        String current = plugin.getPluginMeta().getVersion();
        Schedulers.async(plugin, () -> {
            try (HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
                HttpRequest request = HttpRequest.newBuilder(URI.create(LATEST_RELEASE))
                        .timeout(Duration.ofSeconds(10))
                        .header("Accept", "application/vnd.github+json")
                        .header("User-Agent", "AdvancedVanish/" + current)
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) return;

                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                if (!json.has("tag_name")) return;
                String latest = json.get("tag_name").getAsString().replaceFirst("^[vV]", "");
                if (isNewer(latest, current)) {
                    plugin.getLogger().info("AdvancedVanish v" + latest + " is available (running v" + current + "): " + RELEASES_PAGE);
                }
            } catch (Exception ignored) {
                // Offline or rate-limited; not worth a console line
            }
        });
    }

    /**
     * True when {@code candidate} is a higher dotted version than {@code current}. Non-numeric parts
     * count as 0, and a four-digit year (the old {@code 2026.5.0} scheme) is read as {@code 26.5.0}.
     */
    static boolean isNewer(String candidate, String current) {
        String[] a = candidate.split("[.-]");
        String[] b = current.split("[.-]");
        for (int i = 0; i < Math.max(a.length, b.length); i++) {
            int x = i < a.length ? parse(a[i], i) : 0;
            int y = i < b.length ? parse(b[i], i) : 0;
            if (x != y) return x > y;
        }
        return false;
    }

    private static int parse(String part, int index) {
        try {
            int value = Integer.parseInt(part);
            return index == 0 && value >= 2000 ? value - 2000 : value;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
