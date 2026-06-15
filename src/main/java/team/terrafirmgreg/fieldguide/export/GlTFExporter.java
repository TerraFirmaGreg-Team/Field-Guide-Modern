package team.terrafirmgreg.fieldguide.export;

import team.terrafirmgreg.fieldguide.render3d.material.Material;
import team.terrafirmgreg.fieldguide.render3d.material.RenderState;
import team.terrafirmgreg.fieldguide.render3d.material.Texture;
import team.terrafirmgreg.fieldguide.render3d.math.Transform;
import team.terrafirmgreg.fieldguide.render3d.math.Vector2f;
import team.terrafirmgreg.fieldguide.render3d.math.Vector3f;
import team.terrafirmgreg.fieldguide.render3d.scene.Geometry;
import team.terrafirmgreg.fieldguide.render3d.scene.Mesh;
import team.terrafirmgreg.fieldguide.render3d.scene.Node;
import team.terrafirmgreg.fieldguide.render3d.scene.Vertex;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class GlTFExporter {

    private static final int GLB_MAGIC = 0x46546C67; 
    private static final int GLB_VERSION = 2;
    private static final int GLB_JSON_CHUNK_TYPE = 0x4E4F534A; 
    private static final int GLB_BIN_CHUNK_TYPE = 0x004E4942; 

    private static final String ACCESSOR_TYPE_SCALAR = "SCALAR";
    private static final String ACCESSOR_TYPE_VEC3 = "VEC3";
    private static final String ACCESSOR_TYPE_VEC2 = "VEC2";

    private static final int COMPONENT_TYPE_UNSIGNED_INT = 5125;
    private static final int COMPONENT_TYPE_FLOAT = 5126;

    private static final int TARGET_ARRAY_BUFFER = 34962;
    private static final int TARGET_ELEMENT_ARRAY_BUFFER = 34963;

    private static final String ALPHA_MODE_OPAQUE = "OPAQUE";
    private static final String ALPHA_MODE_BLEND = "BLEND";
    private static final String ALPHA_MODE_MASK = "MASK";
    
    private static final int MAG_FILTER_NEAREST = 9728;
    private static final int MIN_FILTER_NEAREST_MIPMAP_NEAREST = 9984;
    private static final int WRAP_CLAMP_TO_EDGE = 33071;

    private Map<String, Object> gltf;
    private List<Map<String, Object>> accessors;
    private List<Map<String, Object>> bufferViews;
    private List<Map<String, Object>> buffers;
    private List<Map<String, Object>> meshes;
    private List<Map<String, Object>> materials;
    private List<Map<String, Object>> textures;
    private List<Map<String, Object>> samplers;
    private List<Map<String, Object>> images;
    private List<Map<String, Object>> nodes;
    private List<Map<String, Object>> scenes;

    private ByteArrayOutputStream binaryData;
    private Map<Material, Integer> materialIndexMap;
    private Map<Texture, Integer> imageIndexMap;

    public void export(Node rootNode, String filePath) throws IOException {
        export(rootNode, filePath, "model");
    }

    public void export(Node rootNode, String filePath, String modelName) throws IOException {
        reset();

        List<Geometry> geometries = rootNode.getGeometryList(null);
        
        processGeometries(geometries);
        
        buildSceneStructure(geometries, modelName);
        
        writeGlbFile(filePath);

        log.debug("成功导出GLB文件: {}, 包含 {} 个几何体", filePath, geometries.size());
    }

    private void reset() {
        gltf = new LinkedHashMap<>();
        accessors = new ArrayList<>();
        bufferViews = new ArrayList<>();
        buffers = new ArrayList<>();
        meshes = new ArrayList<>();
        materials = new ArrayList<>();
        textures = new ArrayList<>();
        samplers = new ArrayList<>();
        images = new ArrayList<>();
        nodes = new ArrayList<>();
        scenes = new ArrayList<>();
        binaryData = new ByteArrayOutputStream();
        materialIndexMap = new HashMap<>();
        imageIndexMap = new HashMap<>();

    }

    private void processGeometries(List<Geometry> geometries) throws IOException {
        for (Geometry geometry : geometries) {
            processGeometry(geometry);
        }
    }

    private void processGeometry(Geometry geometry) throws IOException {
        Mesh mesh = geometry.getMesh();
        if (mesh == null) return;

        Vertex[] vertices = mesh.getVertexes();
        int[] indices = mesh.getIndexes();

        if (vertices == null || vertices.length == 0 || indices == null || indices.length == 0) {
            return;
        }

        Transform transform = geometry.getWorldTransform();

        List<Float> positions = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> texCoords = new ArrayList<>();

        for (Vertex vertex : vertices) {
            
            Vector3f pos = transform.transformVector(vertex.position, null);
            positions.add(pos.x);
            positions.add(pos.y);
            positions.add(pos.z);

            Vector3f norm = transform.transformNormal(vertex.normal, null);
            normals.add(norm.x);
            normals.add(norm.y);
            normals.add(norm.z);

            Vector2f uv = vertex.texCoord;
            texCoords.add(uv.x);
            texCoords.add(1.0f - uv.y); 
        }

        int positionAccessor = createFloatAccessor(positions, ACCESSOR_TYPE_VEC3);
        int normalAccessor = createFloatAccessor(normals, ACCESSOR_TYPE_VEC3);
        int texCoordAccessor = createFloatAccessor(texCoords, ACCESSOR_TYPE_VEC2);
        int indexAccessor = createUnsignedIntAccessor(indices);

        int materialIndex = processMaterial(geometry.getMaterial());

        Map<String, Object> gltfMesh = new LinkedHashMap<>();
        List<Map<String, Object>> primitives = new ArrayList<>();
        
        Map<String, Object> primitive = new LinkedHashMap<>();
        primitive.put("attributes", createAttributesMap(positionAccessor, normalAccessor, texCoordAccessor));
        primitive.put("indices", indexAccessor);
        primitive.put("material", materialIndex);
        primitive.put("mode", 4); 

        primitives.add(primitive);
        gltfMesh.put("primitives", primitives);
        meshes.add(gltfMesh);
    }

    private Map<String, Object> createAttributesMap(int positionAccessor, int normalAccessor, int texCoordAccessor) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("POSITION", positionAccessor);
        attributes.put("NORMAL", normalAccessor);
        attributes.put("TEXCOORD_0", texCoordAccessor);
        return attributes;
    }

    private int createFloatAccessor(List<Float> data, String type) throws IOException {
        
        int bufferView = createFloatBufferView(data);
        
        Map<String, Object> accessor = new LinkedHashMap<>();
        accessor.put("bufferView", bufferView);
        accessor.put("componentType", COMPONENT_TYPE_FLOAT);
        accessor.put("count", data.size() / getComponentCount(type));
        accessor.put("type", type);
        accessor.put("min", getMinValues(data, type));
        accessor.put("max", getMaxValues(data, type));

        int index = accessors.size();
        accessors.add(accessor);
        return index;
    }

    private int createUnsignedIntAccessor(int[] data) throws IOException {
        
        int bufferView = createIntBufferView(data);
        
        Map<String, Object> accessor = new LinkedHashMap<>();
        accessor.put("bufferView", bufferView);
        accessor.put("componentType", COMPONENT_TYPE_UNSIGNED_INT);
        accessor.put("count", data.length);
        accessor.put("type", ACCESSOR_TYPE_SCALAR);
        accessor.put("min", getMinValues(data));
        accessor.put("max", getMaxValues(data));

        int index = accessors.size();
        accessors.add(accessor);
        return index;
    }

    private int createFloatBufferView(List<Float> data) throws IOException {
        byte[] bytes = new byte[data.size() * 4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        for (Float f : data) {
            buffer.putFloat(f);
        }

        return createBufferView(bytes, TARGET_ARRAY_BUFFER);
    }

    private int createIntBufferView(int[] data) throws IOException {
        byte[] bytes = new byte[data.length * 4];
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        for (int i : data) {
            buffer.putInt(i);
        }

        return createBufferView(bytes, TARGET_ELEMENT_ARRAY_BUFFER);
    }

    private int createBufferView(byte[] data, int target) throws IOException {
        
        int currentOffset = binaryData.size();
        int padding = (4 - (currentOffset % 4)) % 4;
        
        for (int i = 0; i < padding; i++) {
            binaryData.write(0);
        }
        
        int offset = binaryData.size();
        binaryData.write(data);

        Map<String, Object> bufferView = new LinkedHashMap<>();
        bufferView.put("buffer", 0); 
        bufferView.put("byteOffset", offset);
        bufferView.put("byteLength", data.length);
        if (target != 0) {
            bufferView.put("target", target);
        }

        int index = bufferViews.size();
        bufferViews.add(bufferView);
        return index;
    }

    private int processMaterial(Material material) throws IOException {
        if (material == null) {
            
            return createDefaultMaterial();
        }

        Integer existingIndex = materialIndexMap.get(material);
        if (existingIndex != null) {
            return existingIndex;
        }

        Map<String, Object> gltfMaterial = createGltfMaterial(material);
        int index = materials.size();
        materials.add(gltfMaterial);
        materialIndexMap.put(material, index);

        return index;
    }
    
    private int addBufferView(byte[] data, String name) throws IOException {
        return createBufferView(data, 0); 
    }
    
    private int createDefaultMaterial() {
        Map<String, Object> material = new LinkedHashMap<>();
        material.put("name", "default");
        
        Map<String, Object> pbr = new LinkedHashMap<>();
        pbr.put("baseColorFactor", new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        pbr.put("metallicFactor", 0.0f);
        pbr.put("roughnessFactor", 1.0f);
        material.put("pbrMetallicRoughness", pbr);
        
        material.put("alphaMode", ALPHA_MODE_OPAQUE);

        int index = materials.size();
        materials.add(material);
        return index;
    }

    private Map<String, Object> createGltfMaterial(Material material) throws IOException {
        Map<String, Object> gltfMaterial = new LinkedHashMap<>();
        
        Texture diffuseTexture = material.getDiffuseMap();
        if (diffuseTexture != null) {
            int textureIndex = processTexture(diffuseTexture);
            
            Map<String, Object> pbr = new LinkedHashMap<>();
            Map<String, Object> baseColorTexture = new LinkedHashMap<>();
            baseColorTexture.put("index", textureIndex);
            baseColorTexture.put("texCoord", 0);
            pbr.put("baseColorTexture", baseColorTexture);
            
            String alphaMode = ALPHA_MODE_OPAQUE;
            float alpha = 1.0f;
            
            if (material.getRenderState() != null) {
                RenderState.BlendMode blendMode = material.getRenderState().getBlendMode();
                
                if (material.getRenderState().isAlphaTest()) {
                    alphaMode = ALPHA_MODE_MASK;
                    alpha = 0.8f;
                    gltfMaterial.put("alphaCutoff", material.getRenderState().getAlphaFalloff());
                } else if (blendMode == RenderState.BlendMode.ALPHA_BLEND || blendMode == RenderState.BlendMode.ADD) {
                    
                    alphaMode = ALPHA_MODE_BLEND;
                    alpha = 0.8f;
                }
            }

            pbr.put("baseColorFactor", new float[]{1.0f, 1.0f, 1.0f, alpha});
            gltfMaterial.put("alphaMode", alphaMode);
            
            if (material.getDiffuse() != null) {
                pbr.put("baseColorFactor", new float[]{
                    material.getDiffuse().x,
                    material.getDiffuse().y, 
                    material.getDiffuse().z,
                    alpha
                });
            } else {
                pbr.put("baseColorFactor", new float[]{1.0f, 1.0f, 1.0f, alpha});
            }
            
            float shininess = material.getShininess();
            float roughness = Math.max(0.0f, 1.0f - (shininess / 128.0f));
            pbr.put("roughnessFactor", roughness);
            pbr.put("metallicFactor", 0.0f); 
            
            gltfMaterial.put("pbrMetallicRoughness", pbr);
            
            if (material.getRenderState() != null) {
                RenderState.CullMode cullMode = material.getRenderState().getCullMode();
                if (cullMode == RenderState.CullMode.NEVER || cullMode == RenderState.CullMode.ALWAYS) {
                    gltfMaterial.put("doubleSided", true);
                }
            }
        } else {
            
            Map<String, Object> pbr = new LinkedHashMap<>();
            
            if (material.getDiffuse() != null) {
                pbr.put("baseColorFactor", new float[]{
                    material.getDiffuse().x,
                    material.getDiffuse().y, 
                    material.getDiffuse().z, 
                    material.getDiffuse().w
                });
            } else {
                pbr.put("baseColorFactor", new float[]{0.8f, 0.8f, 0.8f, 1.0f});
            }
            
            float shininess = material.getShininess();
            float roughness = Math.max(0.0f, 1.0f - (shininess / 128.0f));
            pbr.put("roughnessFactor", roughness);
            pbr.put("metallicFactor", 0.0f);
            
            gltfMaterial.put("pbrMetallicRoughness", pbr);
            gltfMaterial.put("alphaMode", ALPHA_MODE_OPAQUE);
        }

        return gltfMaterial;
    }

    private int processTexture(Texture texture) throws IOException {

        Integer existingIndex = imageIndexMap.get(texture);
        if (existingIndex != null) {
            
            Map<String, Object> gltfTexture = new LinkedHashMap<>();
            gltfTexture.put("source", existingIndex);
            gltfTexture.put("sampler", createNearestSampler());
            
            int index = textures.size();
            textures.add(gltfTexture);
            return index;
        }
        
        byte[] pngData = createPNGFromTexture(texture);

        int imageBufferView = createBufferView(pngData, 0); 
        
        Map<String, Object> image = new LinkedHashMap<>();
        image.put("name", texture.getName() != null ? texture.getName() : "texture_" + images.size());
        image.put("bufferView", imageBufferView);
        image.put("mimeType", "image/png");
        
        int imageIndex = images.size();
        images.add(image);
        imageIndexMap.put(texture, imageIndex);
        
        int textureIndex = textures.size();
        Map<String, Object> gltfTexture = new LinkedHashMap<>();
        gltfTexture.put("source", imageIndex);
        gltfTexture.put("sampler", createNearestSampler());
        textures.add(gltfTexture);
        
        return textureIndex;
    }
    
    private int createNearestSampler() {
        
        for (int i = 0; i < samplers.size(); i++) {
            Map<String, Object> sampler = samplers.get(i);
            if (MAG_FILTER_NEAREST == (Integer) sampler.get("magFilter") &&
                MIN_FILTER_NEAREST_MIPMAP_NEAREST == (Integer) sampler.get("minFilter")) {
                return i;
            }
        }
        
        Map<String, Object> sampler = new LinkedHashMap<>();
        sampler.put("magFilter", MAG_FILTER_NEAREST);
        sampler.put("minFilter", MIN_FILTER_NEAREST_MIPMAP_NEAREST);
        sampler.put("wrapS", WRAP_CLAMP_TO_EDGE);
        sampler.put("wrapT", WRAP_CLAMP_TO_EDGE);
        
        int index = samplers.size();
        samplers.add(sampler);
        return index;
    }
    
    private void buildSceneStructure(List<Geometry> geometries, String modelName) {
        
        Map<String, Object> buffer = new LinkedHashMap<>();
        buffer.put("byteLength", binaryData.size());
        buffers.add(buffer);

        for (int i = 0; i < geometries.size(); i++) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("name", modelName + "_" + i);
            node.put("mesh", i);
            nodes.add(node);
        }

        Map<String, Object> scene = new LinkedHashMap<>();
        List<Integer> sceneNodes = new ArrayList<>();
        for (int i = 0; i < geometries.size(); i++) {
            sceneNodes.add(i);
        }
        scene.put("nodes", sceneNodes);
        scenes.add(scene);

        gltf.put("asset", createAsset());
        gltf.put("accessors", accessors);
        gltf.put("bufferViews", bufferViews);
        gltf.put("buffers", buffers);
        gltf.put("meshes", meshes);
        gltf.put("materials", materials);
        gltf.put("textures", textures);
        gltf.put("samplers", samplers);
        gltf.put("images", images);
        gltf.put("nodes", nodes);
        gltf.put("scenes", scenes);
        gltf.put("scene", 0);
    }

    private Map<String, Object> createAsset() {
        Map<String, Object> asset = new LinkedHashMap<>();
        asset.put("version", "2.0");
        asset.put("generator", "FieldGuide");
        return asset;
    }

    private void writeGlbFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());

        String json = mapToJson(gltf);
        byte[] jsonData = json.getBytes(StandardCharsets.UTF_8);

        int jsonPadding = (4 - (jsonData.length % 4)) % 4;
        byte[] paddedJsonData = new byte[jsonData.length + jsonPadding];
        System.arraycopy(jsonData, 0, paddedJsonData, 0, jsonData.length);
        for (int i = 0; i < jsonPadding; i++) {
            paddedJsonData[jsonData.length + i] = 32; 
        }

        int binPadding = (4 - (binaryData.size() % 4)) % 4;
        for (int i = 0; i < binPadding; i++) {
            binaryData.write(0);
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            ByteBuffer header = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN);
            
            header.putInt(GLB_MAGIC);           
            header.putInt(GLB_VERSION);        
            header.putInt(12 + 8 + paddedJsonData.length + 8 + binaryData.size()); 
            
            fos.write(header.array());
            
            ByteBuffer jsonChunkHeader = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            jsonChunkHeader.putInt(paddedJsonData.length); 
            jsonChunkHeader.putInt(GLB_JSON_CHUNK_TYPE); 
            
            fos.write(jsonChunkHeader.array());
            fos.write(paddedJsonData);
            
            ByteBuffer binChunkHeader = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            binChunkHeader.putInt(binaryData.size()); 
            binChunkHeader.putInt(GLB_BIN_CHUNK_TYPE); 
            
            fos.write(binChunkHeader.array());
            fos.write(binaryData.toByteArray());
        }
    }

    private String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) json.append(",");
            first = false;
            
            json.append("\"").append(entry.getKey()).append("\":");
            json.append(valueToJson(entry.getValue()));
        }
        
        json.append("}");
        return json.toString();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private String valueToJson(Object value) {
        if (value == null) return "null";
        if (value instanceof Map) return mapToJson((Map<String, Object>) value);
        if (value instanceof List<?> list) return arrayToJson(list);
        if (value instanceof String) return "\"" + value + "\"";
        if (value instanceof Number) return value.toString();
        if (value instanceof float[] floats) return arrayToJson(floats);
        return "null";
    }

    private String arrayToJson(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) json.append(",");
            json.append(valueToJson(list.get(i)));
        }
        
        json.append("]");
        return json.toString();
    }

    private String arrayToJson(float[] array) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        for (int i = 0; i < array.length; i++) {
            if (i > 0) json.append(",");
            json.append(String.format("%.6f", array[i]));
        }
        
        json.append("]");
        return json.toString();
    }

    private int getComponentCount(String type) {
        switch (type) {
            case ACCESSOR_TYPE_SCALAR -> { return 1; }
            case ACCESSOR_TYPE_VEC2 -> { return 2; }
            case ACCESSOR_TYPE_VEC3 -> { return 3; }
            default -> throw new IllegalArgumentException("Unknown type: " + type);
        }
    }

    private List<Float> getMinValues(List<Float> data, String type) {
        int components = getComponentCount(type);
        List<Float> min = new ArrayList<>();
        
        for (int i = 0; i < components; i++) {
            min.add(data.get(i));
        }
        
        for (int i = components; i < data.size(); i += components) {
            for (int j = 0; j < components; j++) {
                int idx = i + j;
                if (idx < data.size()) {
                    min.set(j, Math.min(min.get(j), data.get(idx)));
                }
            }
        }
        
        return min;
    }

    private List<Float> getMaxValues(List<Float> data, String type) {
        int components = getComponentCount(type);
        List<Float> max = new ArrayList<>();
        
        for (int i = 0; i < components; i++) {
            max.add(data.get(i));
        }
        
        for (int i = components; i < data.size(); i += components) {
            for (int j = 0; j < components; j++) {
                int idx = i + j;
                if (idx < data.size()) {
                    max.set(j, Math.max(max.get(j), data.get(idx)));
                }
            }
        }
        
        return max;
    }

    private List<Integer> getMinValues(int[] data) {
        int min = data[0];
        for (int value : data) {
            min = Math.min(min, value);
        }
        return Collections.singletonList(min);
    }

    private List<Integer> getMaxValues(int[] data) {
        int max = data[0];
        for (int value : data) {
            max = Math.max(max, value);
        }
        return Collections.singletonList(max);
    }
    
    private byte[] createPNGFromTexture(Texture texture) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(texture.getImage(), "png", baos);
        return baos.toByteArray();
    }
}