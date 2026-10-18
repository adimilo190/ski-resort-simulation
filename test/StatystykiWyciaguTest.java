package testy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import elementy_symulacji.Osrodek;
import elementy_symulacji.Wezel;
import elementy_symulacji.Wyciag;
import sportowcy.Sportowiec;
import sportowcy.SportowiecLokalny;
import struktury_symulacji.KolejkaZdarzenBiblioteczna;
import symulacja.SilnikSymulacji;
import symulacja.ZdarzenieOdjazdWyciagu;
// Testy statystyk wyciągu: średnia długość kolejki (liczona po sekundach, z optymalizacją sumy) oraz liczba odjazdów wagonika
class StatystykiWyciaguTest {
    private Osrodek osrodek;
    private Wezel dol;
    private Wyciag wyciag;
    @BeforeEach
    void zbudujWyciag() {
        osrodek = new Osrodek();
        dol = new Wezel(0, 100, 0, 0, true);
        Wezel gora = new Wezel(1, 200, 0, 5, false);
        osrodek.dodajWezel(dol);
        osrodek.dodajWezel(gora);
        wyciag = new Wyciag(0, dol, gora, 10, 3, 240);
        osrodek.dodajWyciag(wyciag);
        dol.dodajWyciag(wyciag);
    }
    private Sportowiec sportowiec(int id) {
        return new SportowiecLokalny(id, 5, 0.0, 0.0, 0.5, 0.5, 0.0, false, dol, osrodek);
    }
    // Gdy w kolejce stale stoją 2 osoby przez całe okno, średnia długość wynosi 2
    @Test
    void sredniaDlugoscKolejkiStalej() {
        wyciag.dodajDoKolejki(sportowiec(0), 0);
        wyciag.dodajDoKolejki(sportowiec(1), 0);
        wyciag.zakonczStatystyki(10);
        assertEquals(2.0, wyciag.getSredniaDlugoscKolejki(0, 10), 1e-9);
    }
    // Dla zmiennej długości średnia jest ważona czasem: [0,5) długość 1, [5,10) długość 2 -> (5 + 10) / 10 = 1.5
    @Test
    void sredniaDlugoscKolejkiZmiennej() {
        wyciag.dodajDoKolejki(sportowiec(0), 0);
        wyciag.dodajDoKolejki(sportowiec(1), 5);
        wyciag.zakonczStatystyki(10);
        assertEquals(1.5, wyciag.getSredniaDlugoscKolejki(0, 10), 1e-9);
    }
    // Każdy odjazd wagonika zwiększa licznik odjazdów (potrzebny do procentu zajętości)
    @Test
    void liczbaOdjazdowRosnieZKazdymWagonikiem() {
        SilnikSymulacji silnik = new SilnikSymulacji(new KolejkaZdarzenBiblioteczna());
        new ZdarzenieOdjazdWyciagu(0, wyciag).wykonaj(silnik);
        new ZdarzenieOdjazdWyciagu(10, wyciag).wykonaj(silnik);
        assertEquals(2, wyciag.getLiczbaOdjazdow());
    }
}
