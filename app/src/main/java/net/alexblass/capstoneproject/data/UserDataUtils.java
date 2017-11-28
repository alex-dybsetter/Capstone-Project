package net.alexblass.capstoneproject.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import net.alexblass.capstoneproject.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;

import static net.alexblass.capstoneproject.data.Constants.ZIPCODE_REQUEST_BASE_URL;

/**
 * A class of helper methods to manipulate or evaluate user data.
 */

public class UserDataUtils {

    private static final String LOG_TAG = UserDataUtils.class.getSimpleName();

    public static int getGenderStringId(long id){
        switch((int)id){
            case 1:
                return R.string.gender_cismale;
            case 2:
                return R.string.gender_cisfemale;
            case 3:
                return R.string.gender_ftm;
            case 4:
                return R.string.gender_mtf;
            case 5:
                return R.string.gender_neutral;
            case 6:
                return R.string.gender_other;
            default:
                return R.string.default_selection;
        }
    }

    public static int getGenderAbbreviationStringId(long id){
        switch((int)id){
            case 1:
                return R.string.gender_cismale_abbreviation;
            case 2:
                return R.string.gender_cisfemale_abbreviation;
            case 3:
                return R.string.gender_ftm_abbreviation;
            case 4:
                return R.string.gender_mtf_abbreviation;
            case 5:
                return R.string.gender_neutral_abbreviation;
            case 6:
                return R.string.gender_other_abbreviation;
            default:
                return R.string.default_selection_abbreviation;
        }
    }

    public static int calculateAge(Calendar birthdayCalendar){
        final double daysInYear = 365.25; // To account for leap years
        long day = (1000 * 60 * 60 * 24); // 24 hours in milliseconds
        double currentAge;

        Calendar currentDate = Calendar.getInstance();

        currentDate.set(Calendar.MILLISECOND, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);

        birthdayCalendar.set(Calendar.MILLISECOND, 0);
        birthdayCalendar.set(Calendar.SECOND, 0);
        birthdayCalendar.set(Calendar.MINUTE, 0);
        birthdayCalendar.set(Calendar.HOUR_OF_DAY, 0);

        currentAge = (double) ((currentDate.getTimeInMillis() - birthdayCalendar.getTimeInMillis()) / day) / daysInYear;

        return (int) Math.floor(currentAge);
    }

    public static String getCityFromZip(String zipcode){

        String zipcodeUrl = ZIPCODE_REQUEST_BASE_URL + zipcode;

        URL url = createUrl(zipcodeUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request", e);
        }

        if (TextUtils.isEmpty(jsonResponse)){
            return null;
        }

        try {

            JSONObject result = new JSONObject(jsonResponse);
            return result.getString("city") + ", " + result.getString("state_abbrev");

        } catch (JSONException e){
            Log.e(LOG_TAG, "Problem parsing the JSON response.");
        }

        return null;
    }

    private static URL createUrl(String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(1000);
            urlConnection.setConnectTimeout(1500);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (inputStream != null){
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }

    public static class CityLoader extends AsyncTaskLoader<String> {

        private String mZipcode;

        public CityLoader(Context context, String zipcode){
            super(context);
            mZipcode = zipcode;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public String loadInBackground() {
            return getCityFromZip(mZipcode);
        }
    }
}
