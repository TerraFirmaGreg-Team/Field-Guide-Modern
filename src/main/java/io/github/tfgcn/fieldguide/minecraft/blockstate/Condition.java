package io.github.tfgcn.fieldguide.minecraft.blockstate;

import java.util.Map;

public interface Condition {
    boolean check(Map<String, String> properties);
}