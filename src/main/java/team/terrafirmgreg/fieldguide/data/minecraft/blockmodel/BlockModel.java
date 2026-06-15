package team.terrafirmgreg.fieldguide.data.minecraft.blockmodel;

import lombok.Data;

import java.util.*;

@Data
public class BlockModel {

    private String credit;
    private String parent;
    private Boolean ambientOcclusion;
    private Map<String, String> textures = new HashMap<>();
    private List<ModelElement> elements;
    private Map<String, DisplayTransform> display;
    private String guiLight;
    private List<ModelOverride> overrides;

    private String loader;
    private Map<String, BlockModel> perspectives;
    private String fluid;

    private transient BlockModel parentModel;
    private transient Set<String> inherits = new TreeSet<>();

    public boolean hasElements() {
        return elements != null && !elements.isEmpty();
    }

    public void mergeWithParent() {
        if (this.parentModel == null) {
            initRootModel();
            return;
        }

        if (this.ambientOcclusion == null) {
            this.ambientOcclusion = this.parentModel.ambientOcclusion;
        }

        if (this.textures == null) {
            if (parentModel.textures != null) {
                this.textures = new HashMap<>(this.parentModel.textures);
            }
        } else if (this.parentModel.textures != null) {
            Map<String, String> mergedTextures = new HashMap<>(this.parentModel.textures);
            mergedTextures.putAll(this.textures);
            this.textures = mergedTextures;
        }

        if (this.elements == null) {
            this.elements = this.parentModel.elements != null ? new ArrayList<>(this.parentModel.elements) : null;
        }

        if (this.display == null) {
            if (parentModel.display != null) {
                this.display = new HashMap<>(this.parentModel.display);
            }
        } else if (this.parentModel.display != null) {
            Map<String, DisplayTransform> mergedDisplay = new HashMap<>(this.parentModel.display);
            mergedDisplay.putAll(this.display);
            this.display = mergedDisplay;
        }

        if (this.guiLight == null) {
            this.guiLight = this.parentModel.guiLight;
        }

        if (this.overrides == null) {
            if (this.parentModel.getOverrides() != null) {
                this.overrides = new ArrayList<>(this.parentModel.overrides);
            }
        }

        if (this.loader == null) {
            this.loader = this.parentModel.loader;
        }

        this.inherits.addAll(parentModel.inherits);
    }

    private void initRootModel() {
        if (ambientOcclusion == null) {
            ambientOcclusion = true;
        }

        if (guiLight == null) {
            guiLight = "side";
        }
    }

    public boolean instanceOf(String modelId) {
        return inherits.contains(modelId);
    }

    @Override
    public String toString() {
        return "BlockModel{" +
                "parent='" + parent + '\'' +
                ", ambientOcclusion=" + ambientOcclusion +
                ", textures=" + textures +
                ", elements=" + elements +
                ", display=" + display +
                ", guiLight='" + guiLight + '\'' +
                ", overrides=" + overrides +
                ", loader='" + loader + '\'' +
                '}';
    }
}
