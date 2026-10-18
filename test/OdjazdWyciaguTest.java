package testy;
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
// Testy logiki odjazdu wyciągu: zabieranie pasażerów z kolejki zależnie od pojemności oraz wyliczanie maksymalnej długości kolejki
class OdjazdWyciaguTest {
    private Osrodek osrodek;
    private Wezel dol;
    // Tworzy minimalny ośrodek z jednym wyciągiem o zadanej pojemności i zapamiętuje węzeł dolny (startowy dla sportowców)
    private Wyciag utworzWyciag(int pojemnosc) {
        osrodek = new Osrodek();
        dol = new Wezel(0, 100, 0, 0, true);
        Wezel gora = new Wezel(1, 200, 0, 5, false);
        osrodek.dodajWezel(dol);
        osrodek.dodajWezel(gora);
        Wyciag wyciag = new Wyciag(0, dol, gora, 10, pojemnosc, 240);
        osrodek.dodajWyciag(wyciag);
        dol.dodajWyciag(wyciag);
        return wyciag;
    }
    // Tworzy prostego sportowca (typ Lokalny) startującego w węźle dolnym
    private Sportowiec utworzSportowca(int id) {
        return new SportowiecLokalny(id, 5, 0.0, 0.0, 0.5, 0.5, 0.0, false, dol, osrodek);
    }
    // Tworzy silnik symulacji z biblioteczną kolejką zdarzeń
    private SilnikSymulacji utworzSilnik() {
        return new SilnikSymulacji(new KolejkaZdarzenBiblioteczna());
    }
    // Scenariusz 1: wyciąg o pojemności 3 z pustą kolejką, ustawia się 4 sportowców, następuje odjazd - przejazd wykonuje 3 (jeden się nie mieści)
    @Test
    void odjazdZabieraTyluIluSieMiesci() {
        Wyciag wyciag = utworzWyciag(3);
        SilnikSymulacji silnik = utworzSilnik();
        for (int i = 0; i < 4; i++) {
            wyciag.dodajDoKolejki(utworzSportowca(i), SilnikSymulacji.POCZATEK_DNIA);
        }
        new ZdarzenieOdjazdWyciagu(SilnikSymulacji.POCZATEK_DNIA, wyciag).wykonaj(silnik);
        assertEquals(3, wyciag.getLiczbaPrzejazdow());
    }
    // Scenariusz 2: wyciąg o pojemności 3, ustawia się 2 sportowców, następuje odjazd - obaj odjeżdżają
    @Test
    void odjazdZabieraWszystkichGdyJestMiejsce() {
        Wyciag wyciag = utworzWyciag(3);
        SilnikSymulacji silnik = utworzSilnik();
        for (int i = 0; i < 2; i++) {
            wyciag.dodajDoKolejki(utworzSportowca(i), SilnikSymulacji.POCZATEK_DNIA);
        }
        new ZdarzenieOdjazdWyciagu(SilnikSymulacji.POCZATEK_DNIA, wyciag).wykonaj(silnik);
        assertEquals(2, wyciag.getLiczbaPrzejazdow());
    }
    // Scenariusz 3: ustawia się kolejno 4 sportowców, odjeżdża wagonik, potem ustawia się jeszcze jeden - maksymalna długość kolejki to 4
    @Test
    void maksymalnaDlugoscKolejkiJestPoprawna() {
        Wyciag wyciag = utworzWyciag(3);
        SilnikSymulacji silnik = utworzSilnik();
        for (int i = 0; i < 4; i++) {
            wyciag.dodajDoKolejki(utworzSportowca(i), SilnikSymulacji.POCZATEK_DNIA);
        }
        new ZdarzenieOdjazdWyciagu(SilnikSymulacji.POCZATEK_DNIA + 10, wyciag).wykonaj(silnik);
        wyciag.dodajDoKolejki(utworzSportowca(4), SilnikSymulacji.POCZATEK_DNIA + 20);
        assertEquals(4, wyciag.getMaksDlugoscKolejki());
    }
}
