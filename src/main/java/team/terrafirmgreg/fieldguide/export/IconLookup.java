package team.terrafirmgreg.fieldguide.export;

import java.util.Optional;

public interface IconLookup {

    Optional<IconRef> resolveItem(String itemId);

    Optional<IconRef> resolveBlockItem(String itemId);

    Optional<IconRef> resolveFluid(String fluidId);
}
