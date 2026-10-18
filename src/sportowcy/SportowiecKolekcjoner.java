package sportowcy;
import elementy_symulacji.Osrodek;
import elementy_symulacji.Wezel;
import elementy_symulacji.Trasa;
// Sportowiec typu Kolekcjoner: stara się odwiedzić jak najwięcej tras - jako cel wybiera trasę, którą sam zjeżdżał najmniej razy
// Remisy rozstrzyga kolejno: mniejsza odległość (liczba tras i wyciągów do pokonania), potem większa atrakcyjność, a na końcu dowolnie
public class SportowiecKolekcjoner extends SportowiecZPlanem {
    // Osobisty licznik przejazdów każdej trasy przez tego sportowca (indeksowany numerem trasy)
    private final int[] osobisteLiczbyPrzejazdow;
    public SportowiecKolekcjoner(int id, int poziom, double spontanicznosc, double wspolczynnikZnudzenia,
                                 double wagaTrudnosci, double wagaWyrownania, double wagaZnudzenia,
                                 boolean sledzony, Wezel start, Osrodek osrodek) {
        super(id, poziom, spontanicznosc, wspolczynnikZnudzenia, wagaTrudnosci, wagaWyrownania, wagaZnudzenia, sledzony, start, osrodek);
        this.osobisteLiczbyPrzejazdow = new int[osrodek.getLiczbaTras()];
    }
    // Oprócz aktualizacji znudzenia (klasa bazowa) Kolekcjoner zlicza własne przejazdy danej trasy
    @Override
    public void zarejestrujZjazd(Trasa trasa, int czas) {
        super.zarejestrujZjazd(trasa, czas);
        osobisteLiczbyPrzejazdow[trasa.getId()]++;
    }
    // Cel kolekcjonera to trasa o najmniejszej liczbie własnych przejazdów; remisy: mniejsza odległość, potem większa atrakcyjność
    @Override
    protected Trasa wybierzCelowaTrase() {
        int[] odleglosci = getWyszukiwarka().odleglosciOd(aktualnyWezel);
        Trasa najlepsza = null;
        for (int i = 0; i < osrodek.getLiczbaTras(); i++) {
            Trasa t = osrodek.getTrasa(i);
            // Pomijamy trasy nieosiągalne (w silnie spójnym grafie nie powinny wystąpić)
            if (odleglosci[t.getPoczatek().getId()] < 0) {
                continue;
            }
            if (najlepsza == null || lepszaDlaKolekcjonera(t, najlepsza, odleglosci)) {
                najlepsza = t;
            }
        }
        return najlepsza;
    }
    // Czy trasa a jest dla kolekcjonera lepszym celem niż trasa b: mniej własnych przejazdów, dalej mniejsza odległość, dalej większa atrakcyjność
    private boolean lepszaDlaKolekcjonera(Trasa a, Trasa b, int[] odleglosci) {
        if (osobisteLiczbyPrzejazdow[a.getId()] != osobisteLiczbyPrzejazdow[b.getId()]) {
            return osobisteLiczbyPrzejazdow[a.getId()] < osobisteLiczbyPrzejazdow[b.getId()];
        }
        int odlegloscA = odleglosci[a.getPoczatek().getId()];
        int odlegloscB = odleglosci[b.getPoczatek().getId()];
        if (odlegloscA != odlegloscB) {
            return odlegloscA < odlegloscB;
        }
        return a.obliczAtrakcyjnosc(this) > b.obliczAtrakcyjnosc(this);
    }
}
