public class ABFAHRTSANZEIGE
{
    private BACKEND backend;
    private ABFAHRT[] abfahrten;

    public ABFAHRTSANZEIGE()
    {
        backend = new BACKEND();
    }

    public void Anzeigen()
    {
        // Lade die aktuellen Abfahrtsdaten vom MVG-Server...
        backend.loadData("de:09184:480");
        
        // Hole die geladene Liste der Abfahrten als Feld
        abfahrten = backend.abfahrtsListeGeben();

        // @TODO:
        // Zeige die nächsten Abfahrten auf dem Terminal an
        
    }
}
