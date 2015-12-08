import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class IncidentCreator {

    private static final String BASE_URL = "https://app.resilientsystems.com/rest";
    private static final String CSRF_TOKEN = "csrf_token";
    private static final String ORG_ID = "org_id";

    public static void main(String[] args) {

        try {
            // get the list of all incidents
            JSONArray incidents = getAllIncidents();
            for (int i = 0; i < incidents.length(); i++) {
                System.out.println("Obj " + i + ": " + incidents.getJSONObject(i).toString());
            }

            // get a specific incident
            JSONObject incident = getAnIncident("26314");
            System.out.println("Incident #26314: " + incident.toString());

            // Create an incident
            JSONObject createdIncident = createIncident();
            System.out.println("New Incident: " + createdIncident.toString());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();

        }
    }

    /*
    Creates an incident in CO3 system
    @return JSONObject of the created incident
     */
    private static JSONObject createIncident() throws Exception {

        // Do basic authentication
        Map<String, String> authMap = doAuthentication();

        JSONObject incidentObject = new JSONObject();
        incidentObject.put("name", "HemanApiTest5")
                .put("description", "Test incident creation through API - Unirest")
                .put("incident_type_ids", new int[]{19})
                .put("discovered_date", System.currentTimeMillis());

        HttpResponse<JsonNode> incidentCreateResponse =
                Unirest.post(BASE_URL + "/orgs/" + authMap.get(ORG_ID) + "/incidents")
                    .header("Content-Type", "application/json")
                    .header("X-sess-id", authMap.get(CSRF_TOKEN))
                    .body(new JsonNode(incidentObject.toString()))
                    .asJson();

        return incidentCreateResponse.getBody().getObject();
    }

    /*
    Get list of all incidents. This uses the csrf token and org id obtained from the authentication request
    @return JSONArray of all incidents
     */
    private static JSONArray getAllIncidents() throws Exception {

        // Do basic authentication
        Map<String, String> authMap = doAuthentication();

        HttpResponse<JsonNode> getIncidentsResponse =
                Unirest.get(BASE_URL + "/orgs/" + authMap.get(ORG_ID) + "/incidents")
                        .header("X-sess-id", authMap.get(CSRF_TOKEN))
                    .asJson();

        return getIncidentsResponse.getBody().getArray();
    }

    /*
    Gets a specific incident for the given incident id
    @param String incidentId
     */
    private static JSONObject getAnIncident(String incidentId) throws Exception {

        // Do basic authentication
        Map<String, String> authMap = doAuthentication();

        HttpResponse<JsonNode> getIncidentResponse =
                Unirest.get(BASE_URL + "/orgs/" + authMap.get(ORG_ID) + "/incidents/" + incidentId)
                        .header("X-sess-id", authMap.get(CSRF_TOKEN))
                    .asJson();

        return getIncidentResponse.getBody().getObject();
    }

    /*
    Does a basic authentication on the email and password. Returns an org od and csrf token
    will be passed as part of headers for subsequent requests.
    @return Map with csrf token and org id
     */
    private static Map<String, String> doAuthentication() throws Exception {

        Map<String,String> authMap = new HashMap<String, String>();

        HttpResponse<JsonNode> response =
                Unirest.post(BASE_URL + "/session")
                        .header("Content-Type", "application/json")
                        .body(new JsonNode(readLoginCredentials().toString()))
                    .asJson();

        JSONObject responseBody = response.getBody().getObject();
        JSONArray orgs = (JSONArray) responseBody.get("orgs");

        authMap.put(CSRF_TOKEN, (String) responseBody.get("csrf_token"));
        authMap.put(ORG_ID, String.valueOf(((JSONObject) orgs.get(0)).get("id")));

        System.out.println("AuthMap: " + authMap);
        return authMap;
    }

    /*
    Reads login credentials email and password from the property file.
    The password will be in an encrypted format in the property file, which will be read and then decrypted here
    @return JSONObject of authenticated response
     */
    private static JSONObject readLoginCredentials() {

        JSONObject loginRequest = new JSONObject();
        loginRequest.put("email", "user@somedomain.com")
                    .put("password", "some_password");

        return loginRequest;
    }

}
