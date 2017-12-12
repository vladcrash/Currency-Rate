package ru.tinkoff.school.currencyrate.network;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import ru.tinkoff.school.currencyrate.models.Currency;


public class CurrencyDeserializer implements JsonDeserializer<Currency> {

    @Override
    public Currency deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Currency rate = null;
        if (json.isJsonObject()) {
            Set<Map.Entry<String, JsonElement>> entries = json.getAsJsonObject().entrySet();
            if (entries.size() > 0) {
                Map.Entry<String, JsonElement> entry = entries.iterator().next();
                rate = new Currency(entry.getKey(), entry.getValue().getAsDouble());
            }
        }
        return rate;
    }
}
