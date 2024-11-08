package de.tomalbrc.sandstorm.component.emitter;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import de.tomalbrc.sandstorm.component.ParticleComponent;
import de.tomalbrc.sandstorm.util.EmitterDirection;
import gg.moonflower.molangcompiler.api.MolangExpression;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.lang.reflect.Type;
import java.util.List;

public class EmitterShapeBox implements ParticleComponent<EmitterShapeBox> {
    @SerializedName("offset")
    public MolangExpression[] offset = new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO}; // default: [0, 0, 0]

    @SerializedName("half_dimensions")
    public MolangExpression[] halfDimensions = new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO}; // default: [0, 0, 0]

    @SerializedName("surface_only")
    public boolean surfaceOnly = false; // default: false

    @SerializedName("direction")
    public EmitterDirection direction = EmitterDirection.OUTWARDS; // default: "outwards"

    @SerializedName("direction")
    public List<MolangExpression> directionList;

    public static class Deserializer implements JsonDeserializer<EmitterShapeBox> {
        @Override
        public EmitterShapeBox deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            EmitterShapeBox box = new EmitterShapeBox();

            if (jsonObject.has("offset")) {
                box.offset = context.deserialize(jsonObject.get("offset"), MolangExpression[].class);
            }

            if (jsonObject.has("half_dimensions")) {
                box.halfDimensions = context.deserialize(jsonObject.get("half_dimensions"), MolangExpression[].class);
            }

            if (jsonObject.has("surface_only")) {
                box.surfaceOnly = jsonObject.get("surface_only").getAsBoolean();
            }

            if (jsonObject.has("direction")) {
                JsonElement directionElement = jsonObject.get("direction");
                if (directionElement.isJsonPrimitive()) {
                    try {
                        box.direction = EmitterDirection.valueOf(directionElement.getAsString().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new JsonParseException("Invalid value for EmitterDirection: " + directionElement.getAsString());
                    }
                } else if (directionElement.isJsonArray()) {
                    box.directionList = new ObjectArrayList<>();
                    for (JsonElement element : directionElement.getAsJsonArray()) {
                        box.directionList.add(context.deserialize(element, MolangExpression.class));
                    }
                }
            }

            return box;
        }
    }
}
