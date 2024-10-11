package com.thizthizzydizzy.vrmanager.special.pimax.piSvc;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.thizthizzydizzy.vrmanager.special.pimax.piSvc.piSvcDesc.piVector3f;
import java.lang.reflect.Type;
public class piVector3fAdapter implements JsonSerializer<piVector3f>, JsonDeserializer<piVector3f>{
    @Override
    public JsonElement serialize(piVector3f src, Type typeOfSrc, JsonSerializationContext context){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("x", src.x);
        jsonObject.addProperty("y", src.y);
        jsonObject.addProperty("z", src.z);
        return jsonObject;
    }
    @Override
    public piVector3f deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException{
        JsonObject jsonObject = json.getAsJsonObject();
        float x = jsonObject.get("x").getAsFloat();
        float y = jsonObject.get("y").getAsFloat();
        float z = jsonObject.get("z").getAsFloat();
        return new piVector3f(x, y, z);
    }
}
