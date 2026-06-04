package team.terrafirmgreg.fieldguide.export;

import team.terrafirmgreg.fieldguide.localization.ExportLanguages;

import java.util.List;
import java.util.Set;

/**
 * Forge export language resolution — delegates to {@link ExportLanguages} ({@link team.terrafirmgreg.fieldguide.localization.Language} enum).
 */
public final class FieldGuideExportLanguages {

    /** @deprecated use {@link ExportLanguages#allKeys()} */
    @Deprecated
    public static final List<String> SUPPORTED = ExportLanguages.allKeys();

    private FieldGuideExportLanguages() {
    }

    public static Set<String> resolve() {
        return ExportLanguages.resolveConfigured();
    }
}
