import java.net.URL;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;
import java.util.*;
import org.json.*;

public class BACKEND
{
    final String HOST = "www.mvg.de";
    final String PATH = "/api/fahrinfo/departure/";
    final String URL_PARAMETERS = "?footway=0";

    final String SEARCH_PATH = "/api/fahrinfo/location/queryWeb";
    final String SEARCH_URL_PARAMETERS = "?q=";

    private ABFAHRT[] abfahrten;

    private boolean is_ready;

    public BACKEND()
    {
        is_ready = false;
    }

    public ABFAHRT[] abfahrtsListeGeben()
    {
        if (is_ready == false) {
            System.out.println("Fehler: Daten noch nicht vollstaendig geladen!");
            return null;
        } else {
            return abfahrten;
        }
    }

    /**
     * Der Code in dieser Methode muss nicht verstanden werden.
     * Die Methode kann als "Black Box" betrachtet werden.
     *   
     * With help of https://github.com/pc-coholic/PyMVGLive/issues/12#issuecomment-572468645
     * 
     * @param id ID der Abfahrtshaltestelle (z.B. "de:09162:1130" für Harras)
     */
    public void loadData(String id)
    {
        is_ready = false;
        try{
            ArrayList<ABFAHRT> abfahrtliste;
            String httpsURL = "https://" + HOST + PATH + id + URL_PARAMETERS;
            URL myUrl = new URL(httpsURL);
            HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();
            conn.setRequestProperty ("X-MVG-Authorization-Key", "Vv7L4Gbu2k");
            conn.setRequestProperty ("X-Requested-With", "XMLHttpRequest");
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String inputLine;

            int parseState = 0;

            int abfahrt_in = -1;
            String produkt = null;
            String linie = null;
            String ziel = null;

            abfahrtliste = new ArrayList<ABFAHRT>();

            while ((inputLine = br.readLine()) != null) {
                //System.out.println(inputLine);

                if (parseState == 0 && inputLine.contains("\"departures\" : [ {"))
                {
                    parseState = 1;
                }
                else if (parseState == 1)
                {
                    if (inputLine.contains("}, {"))
                    {
                        abfahrtliste.add(new ABFAHRT(abfahrt_in, produkt, linie, ziel));
                        abfahrt_in = -1;
                        produkt = null;
                        linie = null;
                        ziel = null;
                        parseState = 1;
                    }
                    else if (inputLine.contains("} ]"))
                    {
                        abfahrtliste.add(new ABFAHRT(abfahrt_in, produkt, linie, ziel));
                        parseState = 3;
                    }
                    else if (inputLine.contains("departureTime"))
                    {
                        StringBuilder sb = new StringBuilder();
                        for (char c : inputLine.toCharArray())
                        {
                            if (Character.isDigit(c))
                            {
                                sb.append(c);
                            }
                        }
                        Long temp = Long.parseLong(sb.toString()) - System.currentTimeMillis();
                        abfahrt_in = (int)(temp/1000);

                    }
                    else if (inputLine.contains("product"))
                    {
                        StringBuilder sb = new StringBuilder();
                        int substate = 0;
                        for (char c : inputLine.toCharArray())
                        {
                            if (c == '"')
                            {
                                substate++;
                            }
                            else if (substate == 3)
                            {
                                sb.append(c);
                            }
                        }
                        produkt = sb.toString();
                        if (produkt.equals("SBAHN"))
                        {
                            produkt = "S-Bahn";
                        }
                        else if (produkt.equals("REGIONAL_BUS"))
                        {
                            produkt = "Regionalbus";
                        }
                    }
                    else if (inputLine.contains("label"))
                    {
                        StringBuilder sb = new StringBuilder();
                        int substate = 0;
                        for (char c : inputLine.toCharArray())
                        {
                            if (c == '"')
                            {
                                substate++;
                            }
                            else if (substate == 3)
                            {
                                sb.append(c);
                            }
                        }
                        linie = sb.toString();
                    }
                    else if (inputLine.contains("destination"))
                    {
                        StringBuilder sb = new StringBuilder();
                        int substate = 0;
                        for (char c : inputLine.toCharArray())
                        {
                            if (c == '"')
                            {
                                substate++;
                            }
                            else if (substate == 3)
                            {
                                sb.append(c);
                            }
                        }
                        ziel = sb.toString();
                    }
                }
            }
            br.close();
            abfahrten = abfahrtliste.toArray(new ABFAHRT[0]);
            is_ready = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Fehler beim Laden der Daten!");
            is_ready = true;
        }
    }

    /**
     * Liefert Informationen zu Haltestellen, die mit dem übergebenen 
     *  Namen assoziiert werden und gibt diese auf der Konsole aus.
     * Insbesondere koennen so die IDs der Haltestellen ermittelt werden, 
     *  um Daten ueber Abfahrtszeiten abzufragen.
     *  
     * @param query Der Suchbegriff (z.B. "Marienplatz")
     */
    public void searchStation(String query)
    {
        try {
            // using string builder to handle white spaces in search string
            StringBuilder url = new StringBuilder();
            url.append("https://");
            url.append(HOST);
            url.append(SEARCH_PATH);
            url.append(SEARCH_URL_PARAMETERS);
            for (int i = 0; i < query.length(); i++)
            {
                if (query.charAt(i) == ' ')
                {
                    url.append("%20");
                }
                else
                {
                    url.append(query.charAt(i));
                }
            }
            String httpsURL = url.toString();
            URL myUrl = new URL(httpsURL);
            HttpsURLConnection conn = (HttpsURLConnection) myUrl.openConnection();
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String jsonData = "";
            String inputLine;
            
            while ((inputLine = br.readLine()) != null) {
                jsonData += inputLine;
            }
            
            JSONObject searchResult = new JSONObject(jsonData);
            JSONArray allLocations = searchResult.getJSONArray("locations");
            
            System.out.println("###################################");
            System.out.println("########## SEARCH RESULT ##########");
            
            System.out.println("Searching for station \"" + query + "\" returned the following results:");
            
            for (int i = 0; i < allLocations.length(); i++)
            {
                JSONObject location = allLocations.getJSONObject(i);
                if (location.getString("type").equals("station"))
                {
                    String id = location.getString("id");
                    String name = location.getString("name");
                    String place = location.getString("place");
                    System.out.println("name: " + name + " "
                        + "place: " + place + " "
                        + "id: " + id);
                }
            }
            
            System.out.println("############### END ###############");
            System.out.println("###################################");
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Fehler bei der Suche!");
        }
    }

}
