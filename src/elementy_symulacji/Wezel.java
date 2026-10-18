package elementy_symulacji;
// Klasa reprezentująca węzeł w ośrodku narciarskim, który może mieć wychodzące trasy i wyciągi
public class Wezel {
    private final int id;
    private final int wysokosc;
    private final int x;
    private final int y;
    private final boolean skomunikowany;
    private Trasa[] wychodzaceTrasy;
    private Wyciag[] wychodzaceWyciagi;
    private int liczbaTras = 0;
    private int liczbaWyciagow = 0;
    
    public Wezel(int id, int wysokosc, int x, int y, boolean skomunikowany) {
        this.id = id;
        this.wysokosc = wysokosc;
        this.x = x;
        this.y = y;
        this.skomunikowany = skomunikowany;
        this.wychodzaceTrasy = new Trasa[2];
        this.wychodzaceWyciagi = new Wyciag[2];
    }
    // Metody do dodawania tras i wyciągów, które automatycznie powiększają tablice, gdy są pełne
    public void dodajTrase(Trasa t) { 
        // Jeśli tablica jest pełna, powiększamy ją dwukrotnie
        if (liczbaTras == wychodzaceTrasy.length) {
            Trasa[] nowaTablica = new Trasa[wychodzaceTrasy.length * 2];
            for (int i = 0; i < wychodzaceTrasy.length; i++) {
                nowaTablica[i] = wychodzaceTrasy[i]; // Kopiujemy stare dane
            }
            wychodzaceTrasy = nowaTablica; // Zastępujemy starą tablicę nową
        }
        
        wychodzaceTrasy[liczbaTras] = t; 
        liczbaTras++;
    }

    public void dodajWyciag(Wyciag w) { 
        // Analogiczna logika powiększania tablicy dla wyciągów
        if (liczbaWyciagow == wychodzaceWyciagi.length) {
            Wyciag[] nowaTablica = new Wyciag[wychodzaceWyciagi.length * 2];
            for (int i = 0; i < wychodzaceWyciagi.length; i++) {
                nowaTablica[i] = wychodzaceWyciagi[i];
            }
            wychodzaceWyciagi = nowaTablica;
        }

        wychodzaceWyciagi[liczbaWyciagow] = w; 
        liczbaWyciagow++;
    }
    // Gettery do pobierania atrybutów węzła
    public int getId() { return id; }
    public int getWysokosc() { return wysokosc; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getLiczbaTras() { return liczbaTras; }
    public int getLiczbaWyciagow() { return liczbaWyciagow; }
    public boolean getSkomunikowany() { return skomunikowany; }
    public Trasa getTrasa(int id) { return wychodzaceTrasy[id]; }
    public Wyciag getWyciag(int id) { return wychodzaceWyciagi[id]; }
}
