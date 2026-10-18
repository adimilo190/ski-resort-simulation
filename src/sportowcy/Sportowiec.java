package sportowcy;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import elementy_symulacji.Osrodek;
import elementy_symulacji.Wezel;
import elementy_symulacji.Trasa;
import elementy_symulacji.Wyciag;
import elementy_symulacji.Krawedz;
import symulacja.SilnikSymulacji;
import symulacja.ZdarzenieKoniecZjazduTrasa;
// Abstrakcyjna baza sportowca: wspólny stan, szablon podejmowania decyzji oraz mechanizm znudzenia trasami
// Konkretne typy (Lokalny, Zachłanny, Kolekcjoner) różnią się tylko sposobem przemyślanej decyzji i reakcją po przejeździe
public abstract class Sportowiec {
    // Wspólne atrybuty sportowca (widoczne dla podklas)
    protected final int id;
    protected final int poziomZaawansowania;
    protected final double spontanicznosc;          // epsilon - prawdopodobieństwo decyzji spontanicznej
    protected final double wspolczynnikZnudzenia;    // beta - szybkość narastania/zanikania znudzenia
    protected final double wagaTrudnosci;            // alfa_d
    protected final double wagaWyrownania;           // alfa_w
    protected final double wagaZnudzenia;            // alfa_z
    protected final boolean czySledzony;
    protected Wezel aktualnyWezel;
    protected final Random generator;
    protected final Osrodek osrodek;
    // Leniwa reprezentacja znudzenia: dla każdej trasy pamiętamy indeks ostatniego zjazdu nią (k) oraz wartość znudzenia w tym momencie (z_k)
    // Bieżące znudzenie po i kolejnych zjazdach innymi trasami wynosi z_k * (1 - beta)^i, więc nie trzeba przeliczać wszystkich tras co zjazd
    private int licznikZjazdow;
    private final int[] ostatniIndeksZjazdu;
    private final double[] ostatnieZnudzenie;
    // Historia przejazdów (tylko dla śledzonych) - kolejne krawędzie (trasy i wyciągi) we wspólnej numeracji według kolejności czasowej; numer przejazdu = indeks + 1
    private final List<Krawedz> historiaPrzejazdow;
    // Konstruktor inicjalizujący wspólny stan sportowca
    public Sportowiec(int id, int poziom, double spontanicznosc, double wspolczynnikZnudzenia,
                      double wagaTrudnosci, double wagaWyrownania, double wagaZnudzenia,
                      boolean sledzony, Wezel start, Osrodek osrodek) {
        this.id = id;
        this.poziomZaawansowania = poziom;
        this.spontanicznosc = spontanicznosc;
        this.wspolczynnikZnudzenia = wspolczynnikZnudzenia;
        this.wagaTrudnosci = wagaTrudnosci;
        this.wagaWyrownania = wagaWyrownania;
        this.wagaZnudzenia = wagaZnudzenia;
        this.czySledzony = sledzony;
        this.aktualnyWezel = start;
        this.osrodek = osrodek;
        this.generator = new Random(id);
        this.licznikZjazdow = 0;
        this.ostatniIndeksZjazdu = new int[osrodek.getLiczbaTras()];
        this.ostatnieZnudzenie = new double[osrodek.getLiczbaTras()];
        this.historiaPrzejazdow = new ArrayList<>();
    }
    // Metoda do raportowania zdarzeń, która wypisuje informacje o czasie i wiadomości, jeśli sportowiec jest śledzony
    public void raportSledzenia(int czas, String wiadomosc) {
        if (!czySledzony) return;
        int h = czas / 3600;
        int m = (czas % 3600) / 60;
        int s = czas % 60;
        System.out.printf("%02d:%02d:%02d: %s\n", h, m, s, wiadomosc);
    }
    // Szablon podejmowania decyzji: po godzinie zakończenia nic nie planujemy, z prawdopodobieństwem epsilon decyzja jest spontaniczna, w przeciwnym razie przemyślana
    protected void decyzja(int czas, SilnikSymulacji silnik) {
        if (czas >= SilnikSymulacji.KONIEC_SYMULACJI) {
            return;
        }
        if (generator.nextDouble(0.0, 1.0) < spontanicznosc) {
            spontanicznaDecyzja(czas, silnik);
        } else {
            decyzjaPrzemyslana(czas, silnik);
        }
    }
    // Przemyślana decyzja - inna dla każdego rodzaju sportowca (implementowana w podklasach)
    protected abstract void decyzjaPrzemyslana(int czas, SilnikSymulacji silnik);
    // Reakcja sportowca po pojawieniu się na stoku oraz po zakończeniu każdego zjazdu/wjazdu - co robić dalej (implementowana w podklasach)
    public abstract void kontynuuj(int czas, SilnikSymulacji silnik);
    // Decyzja spontaniczna: losuje jedną z tras lub wyciągów zaczynających się w obecnym węźle (każda opcja jednakowo prawdopodobna)
    protected void spontanicznaDecyzja(int czas, SilnikSymulacji silnik) {
        int losowaOpcja = generator.nextInt(0, aktualnyWezel.getLiczbaTras() + aktualnyWezel.getLiczbaWyciagow());
        if (losowaOpcja < aktualnyWezel.getLiczbaTras()) {
            Trasa wybranaTrasa = aktualnyWezel.getTrasa(losowaOpcja);
            raportSledzenia(czas, "Sportowiec" + id + " spontanicznie wybrał trasę nr " + wybranaTrasa.getId());
            rozpocznijZjazd(wybranaTrasa, czas, silnik);
        } else {
            Wyciag wybranyWyciag = aktualnyWezel.getWyciag(losowaOpcja - aktualnyWezel.getLiczbaTras());
            raportSledzenia(czas, "Sportowiec" + id + " spontanicznie wybrał wyciąg nr " + wybranyWyciag.getId());
            rozpocznijWjazd(wybranyWyciag, czas, silnik);
        }
    }
    // Pomocnicza metoda rozpoczynająca zjazd daną trasą - planuje w kolejce zdarzenie końca zjazdu
    protected void rozpocznijZjazd(Trasa trasa, int czas, SilnikSymulacji silnik) {
        silnik.getKolejka().wstaw(new ZdarzenieKoniecZjazduTrasa(czas + trasa.getCzasPrzejazdu(), this, trasa));
    }
    // Pomocnicza metoda ustawiająca sportowca w kolejce oczekujących do wyciągu
    protected void rozpocznijWjazd(Wyciag wyciag, int czas, SilnikSymulacji silnik) {
        wyciag.dodajDoKolejki(this, czas);
    }
    // Zwraca bieżący poziom znudzenia sportowca daną trasą, policzony leniwie ze wzoru z_k * (1 - beta)^i
    public double poziomZnudzenia(Trasa trasa) {
        int i = licznikZjazdow - ostatniIndeksZjazdu[trasa.getId()];
        return ostatnieZnudzenie[trasa.getId()] * Math.pow(1.0 - wspolczynnikZnudzenia, i);
    }
    // Rejestruje zjazd sportowca daną trasą - aktualizuje znudzenie oraz (dla śledzonych) dopisuje trasę do historii przejazdów
    public void zarejestrujZjazd(Trasa trasa, int czas) {
        aktualizujZnudzenie(trasa);
        if (czySledzony) {
            historiaPrzejazdow.add(trasa);
        }
    }
    // Rejestruje wjazd sportowca wyciągiem - dla śledzonych dopisuje wyciąg do historii przejazdów (wjazdy wyciągami nie wpływają na znudzenie)
    public void zarejestrujWjazd(Wyciag wyciag, int czas) {
        if (czySledzony) {
            historiaPrzejazdow.add(wyciag);
        }
    }
    // Aktualizuje znudzenie po zjeździe daną trasą według wygładzania wykładniczego z_t = beta * 1 + (1 - beta) * z_(t-1)
    private void aktualizujZnudzenie(Trasa trasa) {
        double znudzeniePrzed = poziomZnudzenia(trasa);
        licznikZjazdow++;
        ostatniIndeksZjazdu[trasa.getId()] = licznikZjazdow;
        ostatnieZnudzenie[trasa.getId()] = wspolczynnikZnudzenia + (1.0 - wspolczynnikZnudzenia) * znudzeniePrzed;
        assert ostatnieZnudzenie[trasa.getId()] >= -1e-9 && ostatnieZnudzenie[trasa.getId()] <= 1.0 + 1e-9;
    }
    // Gettery do pobierania atrybutów sportowca
    public int getId() { return id; }
    public int getPoziomZaawansowania() { return poziomZaawansowania; }
    public double getSpontanicznosc() { return spontanicznosc; }
    public double getWagaTrudnosci() { return wagaTrudnosci; }
    public double getWagaWyrownania() { return wagaWyrownania; }
    public double getWagaZnudzenia() { return wagaZnudzenia; }
    public boolean getSledzony() { return czySledzony; }
    public List<Krawedz> getHistoriaPrzejazdow() { return historiaPrzejazdow; }
    public Wezel getAktualnyWezel() { return aktualnyWezel; }
    // Setter do ustawienia aktualnego węzła sportowca
    public void setAktualnyWezel(Wezel wezel) { this.aktualnyWezel = wezel; }
}
