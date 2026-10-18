package elementy_symulacji;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;
import sportowcy.Sportowiec;
// Klasa reprezentująca ośrodek narciarski, który zawiera trasy, wyciągi i węzły
public class Osrodek {
    private Trasa[] trasy;
    private Wyciag[] wyciagi;
    private Wezel[] wezly;
    private int liczbaTras = 0;
    private int liczbaWyciagow = 0;
    private int liczbaWezlow = 0;
    // Lista śledzonych sportowców - potrzebna na koniec dnia do wygenerowania mapek z historią ich przejazdów
    private final List<Sportowiec> sledzeni = new ArrayList<>();
    public Osrodek() {
        trasy = new Trasa[2];
        wyciagi = new Wyciag[2];
        wezly = new Wezel[2];
    }
    // Rejestruje śledzonego sportowca oraz zwraca listę wszystkich śledzonych
    public void dodajSledzonego(Sportowiec s) { sledzeni.add(s); }
    public List<Sportowiec> getSledzeni() { return sledzeni; }
    // Metody do dodawania tras, wyciągów i węzłów, które automatycznie powiększają tablice, gdy są pełne
    public void dodajWezel(Wezel w) {
        if (liczbaWezlow == wezly.length) {
            Wezel[] nowa = new Wezel[wezly.length * 2];
            for (int i = 0; i < wezly.length; i++) nowa[i] = wezly[i];
            wezly = nowa;
        }
        wezly[liczbaWezlow] = w; 
        liczbaWezlow++;
    }

    public void dodajTrase(Trasa t) {
        if (liczbaTras == trasy.length) {
            Trasa[] nowa = new Trasa[trasy.length * 2];
            for (int i = 0; i < trasy.length; i++) nowa[i] = trasy[i];
            trasy = nowa;
        }
        trasy[liczbaTras] = t;
        liczbaTras++;
    }

    public void dodajWyciag(Wyciag w) {
        if (liczbaWyciagow == wyciagi.length) {
            Wyciag[] nowa = new Wyciag[wyciagi.length * 2];
            for (int i = 0; i < wyciagi.length; i++) nowa[i] = wyciagi[i];
            wyciagi = nowa;
        }
        wyciagi[liczbaWyciagow] = w;
        liczbaWyciagow++;
    }
    // Gettery do pobierania węzłów, tras i wyciągów po ich ID
    public Wezel getWezel(int id) { return wezly[id]; }
    public Trasa getTrasa(int id) { return trasy[id]; }
    public Wyciag getWyciag(int id) { return wyciagi[id]; }
    public int getLiczbaTras() { return liczbaTras; }
    public int getLiczbaWyciagow() { return liczbaWyciagow; }
    public int getLiczbaWezlow() { return liczbaWezlow; }
    // Metoda do generowania raportu końcowego z wynikami symulacji; okno [poczatek, koniec) to zakres sekund symulacji (do średniej długości kolejki)
    public void generujRaportKoncowy(int poczatek, int koniec) {
        System.out.println("Statystyki tras:");
        for (int i = 0; i < liczbaTras; i++) {
            Trasa t = trasy[i];
            System.out.printf(Locale.ENGLISH, "Trasa %d: przejazdów: %d, wyrównanie: %.2f%n",
                    t.getId(), t.getLiczbaPrzejazdow(), t.getWyrownanie());
        }

        System.out.println("Statystyki wyciągów:");
        for (int i = 0; i < liczbaWyciagow; i++) {
            Wyciag w = wyciagi[i];
            w.zakonczStatystyki(koniec);
            long maksMozliwe = (long) w.getLiczbaOdjazdow() * w.getMaksymalnaGrupa();
            int procent = maksMozliwe == 0 ? 0 : (int) Math.round(100.0 * w.getLiczbaPrzejazdow() / maksMozliwe);
            System.out.printf(Locale.ENGLISH, "Wyciąg %d: kolejka śr: %d, maks: %d, przewiezieni: %d / %d (%d%%)%n",
                    w.getId(),
                    Math.round(w.getSredniaDlugoscKolejki(poczatek, koniec)),
                    w.getMaksDlugoscKolejki(),
                    w.getLiczbaPrzejazdow(),
                    maksMozliwe,
                    procent);
        }
    }
 
}
