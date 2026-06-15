package team.terrafirmgreg.fieldguide.data.minecraft.blockstate;

import lombok.Data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class PropertyCondition implements Condition {
    private Map<String, String> conditions;

    @Override
    public boolean check(Map<String, String> properties) {
        if (this.conditions == null) {
            return false;
        }

        for (Map.Entry<String, String> entry : this.conditions.entrySet()) {
            String propertyName = entry.getKey();
            String expectedValue = entry.getValue();
            String actualValue = properties.get(propertyName);

            if (!checkPropertyValue(propertyName, expectedValue, actualValue)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkPropertyValue(String propertyName, String expectedValue, String actualValue) {
        if (expectedValue.startsWith("!")) {
            
            String valueList = expectedValue.substring(1);
            Set<String> excludedValues = parseValueList(valueList);
            return !excludedValues.contains(actualValue);
        } else {
            
            Set<String> allowedValues = parseValueList(expectedValue);
            return allowedValues.contains(actualValue);
        }
    }

    private Set<String> parseValueList(String valueList) {
        Set<String> values = new HashSet<>();
        if (valueList != null && !valueList.trim().isEmpty()) {
            String[] parts = valueList.split("\\|");
            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    values.add(part.trim());
                }
            }
        }
        return values;
    }
}
