package team.terrafirmgreg.fieldguide.localization;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class ExportLanguages {

    private static final String PROPERTY = "fieldguide.exportLanguages";

    private ExportLanguages() {
    }

    public static List<String> allKeys() {
        return Arrays.stream(Language.values()).map(Language::getKey).toList();
    }

    public static String toCsv(Iterable<String> codes) {
        StringBuilder out = new StringBuilder();
        for (String code : codes) {
            if (code == null || code.isBlank()) {
                continue;
            }
            if (out.length() > 0) {
                out.append(',');
            }
            out.append(code.trim().toLowerCase(Locale.ROOT));
        }
        return out.toString();
    }

    public static Set<String> resolveConfigured() {
        String raw = System.getProperty(PROPERTY, "").trim();
        if (raw.isEmpty()) {
            return new LinkedHashSet<>(allKeys());
        }
        if ("*".equals(raw)) {
            return null;
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static void main(String[] args) throws Exception {
        String csv = toCsv(allKeys());
        if (args.length > 0) {
            Path out = Path.of(args[0]);
            if (out.getParent() != null) {
                Files.createDirectories(out.getParent());
            }
            Files.writeString(out, csv, StandardCharsets.UTF_8);
            return;
        }
        System.out.print(csv);
    }
}
