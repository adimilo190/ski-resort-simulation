package sportowcy;
import java.util.ArrayList;
import java.util.List;
import elementy_symulacji.Osrodek;
import elementy_symulacji.Wezel;
import elementy_symulacji.Trasa;
import elementy_symulacji.Wyciag;
import elementy_symulacji.Krawedz;
import symulacja.SilnikSymulacji;
import przeszukiwanie.Sciezka;
import przeszukiwanie.WyszukiwarkaSciezek;
// Abstrakcyjny sportowiec, który układa plan przejazdu (najkrótszy ciąg krawędzi wyznaczony BFS) do wybranej trasy docelowej i realizuje go krok po kroku
// Nową decyzję (lub spontaniczną) podejmuje dopiero po pełnym zrealizowaniu planu, czyli po zjechaniu wymarzoną trasą
public abstract class SportowiecZPlanem extends Sportowiec {
    private final WyszukiwarkaSciezek wyszukiwarka;
    private List<Krawedz> plan;   // kolejne krawędzie do przejechania; ostatnia z nich to trasa docelowa
    private int krokPlanu;        // indeks następnej krawędzi planu do wykonania
    public SportowiecZPlanem(int id, int poziom, double spontanicznosc, double wspolczynnikZnudzenia,
                             double wagaTrudnosci, double wagaWyrownania, double wagaZnudzenia,
                             boolean sledzony, Wezel start, Osrodek osrodek) {
        super(id, poziom, spontanicznosc, wspolczynnikZnudzenia, wagaTrudnosci, wagaWyrownania, wagaZnudzenia, sledzony, start, osrodek);
        this.wyszukiwarka = new WyszukiwarkaSciezek(osrodek.getLiczbaWezlow());
        this.plan = null;
        this.krokPlanu = 0;
    }
    // Wybór trasy docelowej - różny dla Zachłannego i Kolekcjonera
    protected abstract Trasa wybierzCelowaTrase();
    // Po pojawieniu się na stoku oraz po każdym przejeździe: jeśli plan ma jeszcze kroki, realizujemy następny; w przeciwnym razie podejmujemy nową decyzję
    @Override
    public void kontynuuj(int czas, SilnikSymulacji silnik) {
        if (czas >= SilnikSymulacji.KONIEC_SYMULACJI) {
            return;
        }
        if (plan != null && krokPlanu < plan.size()) {
            wykonajNastepnyKrokPlanu(czas, silnik);
        } else {
            decyzja(czas, silnik);
        }
    }
    // Przemyślana decyzja: wybiera trasę docelową, wyznacza BFS-em najkrótszą ścieżkę do jej początku, buduje plan (ścieżka + zjazd trasą docelową) i rusza
    @Override
    protected void decyzjaPrzemyslana(int czas, SilnikSymulacji silnik) {
        Trasa cel = wybierzCelowaTrase();
        Sciezka sciezka = wyszukiwarka.znajdzNajkrotszaSciezke(aktualnyWezel, cel.getPoczatek());
        plan = new ArrayList<>(sciezka.getKrawedzie());
        plan.add(cel);
        krokPlanu = 0;
        raportSledzenia(czas, "Sportowiec" + id + " zaplanował przejazd do trasy nr " + cel.getId() + " (kroków: " + plan.size() + ")");
        wykonajNastepnyKrokPlanu(czas, silnik);
    }
    // Wykonuje kolejny krok planu - rozpoczyna zjazd trasą albo ustawia się w kolejce do wyciągu
    private void wykonajNastepnyKrokPlanu(int czas, SilnikSymulacji silnik) {
        Krawedz krok = plan.get(krokPlanu);
        krokPlanu++;
        if (krok instanceof Trasa trasa) {
            raportSledzenia(czas, "Sportowiec" + id + " (plan) wybrał zjazd trasą nr " + trasa.getId());
            rozpocznijZjazd(trasa, czas, silnik);
        } else {
            Wyciag wyciag = (Wyciag) krok;
            raportSledzenia(czas, "Sportowiec" + id + " (plan) wybrał wjazd wyciągiem nr " + wyciag.getId());
            rozpocznijWjazd(wyciag, czas, silnik);
        }
    }
    // Udostępnia wyszukiwarkę ścieżek podklasom (np. Kolekcjoner używa odległości do rozstrzygania remisów)
    protected WyszukiwarkaSciezek getWyszukiwarka() {
        return wyszukiwarka;
    }
}
