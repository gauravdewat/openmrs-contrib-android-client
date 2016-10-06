package org.openmrs.mobile.utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.openmrs.mobile.models.retrofit.Observation;

import java.lang.reflect.Type;

public class ObservationDeserializer implements JsonDeserializer<Observation> {

    private static final String UUID_KEY = "uuid";
    private static final String DISPLAY_KEY = "display";
    private static final String VALUE_KEY = "value";

    @Override
    public Observation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        Observation observation = new Observation();
        observation.setUuid(jsonObject.get(UUID_KEY).getAsString());
        observation.setDisplay(jsonObject.get(DISPLAY_KEY).getAsString());

        if (jsonObject.get("concept") != null && "Visit Diagnoses".equals(jsonObject.get("concept").getAsJsonObject().get(DISPLAY_KEY).getAsString())) {
            JsonArray diagnosisDetailJSONArray = jsonObject.get("groupMembers").getAsJsonArray();
            for (int i = 0; i < diagnosisDetailJSONArray.size(); i++) {

                JsonObject diagnosisDetails = diagnosisDetailJSONArray.get(i).getAsJsonObject();
                String diagnosisDetail = diagnosisDetails.get("concept").getAsJsonObject().get(DISPLAY_KEY).getAsString();

                if ("Diagnosis order".equals(diagnosisDetail)) {
                    observation.setDiagnosisOrder(
                            diagnosisDetails.getAsJsonObject().get(VALUE_KEY).getAsJsonObject().get(DISPLAY_KEY).getAsString());
                } else if ("Diagnosis certainty".equals(diagnosisDetail)) {
                    observation.setDiagnosisCertanity(
                            diagnosisDetails.getAsJsonObject().get(VALUE_KEY).getAsJsonObject().get(DISPLAY_KEY).getAsString());
                } else {
                    try {
                        observation.setDiagnosisList(diagnosisDetails.getAsJsonObject().get(VALUE_KEY).getAsJsonObject().get(DISPLAY_KEY).getAsString());
                    }
                    catch (IllegalStateException e) {
                        observation.setDiagnosisList(diagnosisDetails.getAsJsonObject().get(VALUE_KEY).getAsString());
                    }
                }
            }
        } else if (jsonObject.get("concept") != null && "Text of encounter note".equals(jsonObject.get("concept").getAsJsonObject().get(DISPLAY_KEY).getAsString())) {
            observation.setDiagnosisNote(jsonObject.getAsJsonObject().get(VALUE_KEY).getAsString());
        }
        return observation;
    }

}
