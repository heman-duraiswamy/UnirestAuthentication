import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.SplittableRandom;

public class CrimsonHexagonApi {

    private static final String BASE_URL = "https://api.crimsonhexagon.com/api/";
    private static final String EMAIL_ID = "heman.duraiswamy@barclayscapital.com";
    private static final String PWD = "CBarcapH123";

    private static String authToken;

    public static void main(String[] args) {
        System.out.println("Hello Crimson Hexagon!!");

        try {
            authToken = doAuthentication();
            System.out.println("Crimson Hexagon auth token >> " + authToken);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*
     * Does a basic authentication on the email and password.
     * Returns auth token and id to be used in subsequent requests
     * @return Map with auth token and id
     */
    private static String doAuthentication() throws Exception {

        HttpResponse<JsonNode> response =
                Unirest.get(BASE_URL + "authenticate?username=" + EMAIL_ID + "&password=" + PWD).asJson();
        JSONObject responseBody = response.getBody().getObject();

        return (String) responseBody.get("auth");
    }
}