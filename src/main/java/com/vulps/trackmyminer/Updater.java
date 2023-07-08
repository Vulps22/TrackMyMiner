package com.vulps.trackmyminer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {

    private static final String url = "https://raw.githubusercontent.com/Vulps22/TrackMyMiner/master/src/main/resources/version.txt";

    public static boolean shouldUpdate() throws IOException {

        String current = getVersion();
        String latest = getRemoteVersion();

        if (current == null || latest.isEmpty()) {
            return false; // Unable to retrieve version information, skip update check
        }

        // Remove any suffixes (e.g., -SNAPSHOT, -ALPHA, -BETA) from the versions
        String currentVersion = removeSuffix(current);
        String latestVersion = removeSuffix(latest);

        // Compare the versions
        int result = compareVersions(currentVersion, latestVersion);

        if (result < 0) {
            // Update is available, check the suffixes to determine if it's an important update
            boolean isSnapshot = hasSnapshotSuffix(current);
            boolean isAlpha = hasAlphaSuffix(current);
            boolean isBeta = hasBetaSuffix(current);

            if (isSnapshot) {
                // Inform snapshot users of an important update
                return true;
            } else if (isAlpha && !hasSnapshotSuffix(latest)) {
                // Inform alpha users if the latest version is not a snapshot
                return true;
            } else if (isBeta && !hasSnapshotSuffix(latest) && !hasAlphaSuffix(latest)) {
                // Inform beta users if the latest version is not a snapshot or alpha
                return true;
            }

            return false; // Other cases (e.g., stable release, non-important update)
        } else if (result == 0) {
            // Versions are the same, check if the latest version has a suffix
            return !hasSuffix(latest) && hasSuffix(current);
        } else {
            return false; // Current version is ahead of the latest version
        }
    }

    private static boolean hasSnapshotSuffix(String version) {
        return version.endsWith("-SNAPSHOT");
    }

    private static boolean hasAlphaSuffix(String version) {
        return version.endsWith("-ALPHA");
    }

    private static boolean hasBetaSuffix(String version) {
        return version.endsWith("-BETA");
    }

    private static boolean hasSuffix(String version) {
        int index = version.indexOf("-");
        return index != -1 && index < version.length() - 1;
    }

    private static String removeSuffix(String version) {
        int index = version.indexOf("-");
        if (index != -1) {
            return version.substring(0, index);
        }
        return version;
    }

    private static int compareVersions(String version1, String version2) {
        String[] v1Tokens = version1.split("\\.");
        String[] v2Tokens = version2.split("\\.");

        int length = Math.max(v1Tokens.length, v2Tokens.length);
        for (int i = 0; i < length; i++) {
            int v1Token = (i < v1Tokens.length) ? Integer.parseInt(v1Tokens[i]) : 0;
            int v2Token = (i < v2Tokens.length) ? Integer.parseInt(v2Tokens[i]) : 0;

            if (v1Token < v2Token) {
                return -1;
            } else if (v1Token > v2Token) {
                return 1;
            }
        }

        return 0; // Versions are equal
    }

    public static String getRemoteVersion() throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            // Maybe change to OkHttp?
            URL versionUrl = new URL(url);
            connection = (HttpURLConnection) versionUrl.openConnection();
            connection.setRequestMethod("GET");

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }

            return content.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String getVersion() {
        ClassLoader classLoader = Updater.class.getClassLoader();
        try {
            InputStream inputStream = classLoader.getResourceAsStream("version.txt");
            if (inputStream == null) return null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader.readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
