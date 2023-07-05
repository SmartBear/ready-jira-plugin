package com.smartbear.ready.plugin.jira.parsers;

import com.atlassian.jira.rest.client.api.domain.CimFieldInfo;
import com.atlassian.jira.rest.client.api.domain.FieldSchema;
import com.atlassian.jira.rest.client.api.domain.StandardOperation;
import com.atlassian.jira.rest.client.internal.json.CimFieldsInfoJsonParser;
import com.atlassian.jira.rest.client.internal.json.FieldSchemaJsonParser;
import com.atlassian.jira.rest.client.internal.json.GenericJsonArrayParser;
import com.atlassian.jira.rest.client.internal.json.JsonObjectParser;
import com.atlassian.jira.rest.client.internal.json.JsonParseUtil;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.glassfish.jersey.internal.guava.Sets;

import javax.annotation.Nullable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CimFieldsInfoJsonParserExt extends CimFieldsInfoJsonParser {

    private final FieldSchemaJsonParser fieldSchemaJsonParser = new FieldSchemaJsonParser();

    public CimFieldsInfoJsonParserExt() {
    }

    public CimFieldInfo parse(JSONObject json) throws JSONException {
        String id = JsonParseUtil.getOptionalString(json, "id");
        return this.parse(json, id);
    }

    public CimFieldInfo parse(JSONObject json, String id) throws JSONException {
        boolean required = json.getBoolean("required");
        String name = JsonParseUtil.getOptionalString(json, "name");
        FieldSchema schema = this.fieldSchemaJsonParser.parse(json.getJSONObject("schema"));
        Set<StandardOperation> operations = this.parseOperations(json.getJSONArray("operations"));
        Iterable<Object> allowedValues = this.parseAllowedValues(json.optJSONArray("allowedValues"), schema);
        URI autoCompleteUri = JsonParseUtil.parseOptionalURI(json, "autoCompleteUrl");
        return new CimFieldInfo(id, required, name, schema, operations, allowedValues, autoCompleteUri);
    }

    private Iterable<Object> parseAllowedValues(@Nullable JSONArray allowedValues, FieldSchema fieldSchema) throws JSONException {
        if (allowedValues != null && !allowedValues.equals(JSONObject.NULL)) {
            if (allowedValues.length() == 0) {
                return Collections.emptyList();
            } else {
                JsonObjectParser<Object> allowedValuesJsonParser = this.getParserFor(fieldSchema);
                if (allowedValuesJsonParser != null) {
                    boolean isProjectCF = "project".equals(fieldSchema.getType()) && "com.atlassian.jira.plugin.system.customfieldtypes:project".equals(fieldSchema.getCustom());
                    boolean isVersionCF = "version".equals(fieldSchema.getType()) && "com.atlassian.jira.plugin.system.customfieldtypes:version".equals(fieldSchema.getCustom());
                    boolean isMultiVersionCF = "array".equals(fieldSchema.getType()) && "version".equals(fieldSchema.getItems()) && "com.atlassian.jira.plugin.system.customfieldtypes:multiversion".equals(fieldSchema.getCustom());
                    JSONArray valuesToParse;
                    if ((isProjectCF || isVersionCF || isMultiVersionCF) && allowedValues.get(0) instanceof JSONArray) {
                        valuesToParse = allowedValues.getJSONArray(0);
                    } else {
                        valuesToParse = allowedValues;
                    }

                    return GenericJsonArrayParser.create(allowedValuesJsonParser).parse(valuesToParse);
                } else {
                    int itemsLength = allowedValues.length();
                    List<Object> res = new ArrayList<>();

                    for (int i = 0; i < itemsLength; ++i) {
                        res.add(allowedValues.get(i));
                    }

                    return res;
                }
            }
        } else {
            return null;
        }
    }

    private Set<StandardOperation> parseOperations(JSONArray operations) throws JSONException {
        int operationsCount = operations.length();
        Set<StandardOperation> res = Sets.newHashSetWithExpectedSize(operationsCount);

        for (int i = 0; i < operationsCount; ++i) {
            String opName = operations.getString(i);
            try {
                StandardOperation op = StandardOperation.valueOf(opName.toUpperCase());
                res.add(op);
            } catch (Exception ignore) {}
        }

        return res;
    }

    private JsonObjectParser<Object> getParserFor(FieldSchema fieldSchema) {
        Set<String> customFieldsTypesWithFieldOption = Set.of("com.atlassian.jira.plugin.system.customfieldtypes:multicheckboxes", "com.atlassian.jira.plugin.system.customfieldtypes:radiobuttons", "com.atlassian.jira.plugin.system.customfieldtypes:select", "com.atlassian.jira.plugin.system.customfieldtypes:cascadingselect", "com.atlassian.jira.plugin.system.customfieldtypes:multiselect");
        String type = "array".equals(fieldSchema.getType()) ? fieldSchema.getItems() : fieldSchema.getType();
        String custom = fieldSchema.getCustom();
        if (custom != null && customFieldsTypesWithFieldOption.contains(custom)) {
            type = "customFieldOption";
        }

        JsonObjectParser<Object> jsonParser = (JsonObjectParser) this.registeredAllowedValueParsers.get(type);
        return jsonParser == null ? null : jsonParser;
    }
}
