/**
 * Repräsentiert einen Datensatz für die Abfahrt eines Transportmittels
 *  von der gewünschten Starthaltestelle zu einem bestimmten Zeitpunkt 
 *  zu einer bestimmten Zielhaltestelle.
 */
public class ABFAHRT
{
    private int abfahrt_in;
    private String produkt;
    private String linie;
    private String ziel;

    /**
     * Erzeugt ein Objekt Abfahrt mit den übergebenen Daten.
     * 
     * @param abfahrt_in    Zeit bis zur Abfahrt in Sekunden
     * @param produkt       Art des Transportmittels (Bus, Tram, U-Bahn, S-Bahn)
     * @param linie         Bezeichnung der Linie
     * @param ziel          Name der Zielhaltestelle
     */
    public ABFAHRT(int abfahrt_inNeu, String produktNeu, String linieNeu, String zielNeu)
    {
        abfahrt_in = abfahrt_inNeu;
        produkt = produktNeu;
        linie = linieNeu;
        ziel = zielNeu;
    }

    /**
     * Gibt die Abfahrt in Sekunden ab dem Zeitpunkt der Abfrage zurück.
     */
    private int AbfahrtInSekundenGeben()
    {
        return abfahrt_in;
    }

    /**
     * Gibt die Art des Transportmittels zurück.
     * Ist dafür kein Wert festgelegt, wird "Error!" zurückgegeben.
     */
    private String ProduktGeben()
    {
        if (produkt == null)
        {
            return "Error!";
        }
        else
        {
            return produkt;
        }
    }

    /**
     * Gibt die Bezeichnung der Linie zurück.
     * Ist dafür kein Wert festgelegt, wird "Error!" zurückgegeben.
     */
    private String LinieGeben()
    {
        if (linie == null)
        {
            return "Error!";
        }
        else
        {
            return linie;
        }
    }

    /**
     * Gibt die Bezeichnung der Zielhaltestelle zurück.
     * Ist dafür kein Wert festgelegt, wird "Error!" zurückgegeben.
     */
    private String ZielGeben()
    {
        if (ziel == null)
        {
            return "Error!";
        }
        else
        {
            return ziel;
        }
    }


}
