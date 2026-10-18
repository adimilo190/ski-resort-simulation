package testy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import struktury_symulacji.KolejkaZdarzen;
import struktury_symulacji.KolejkaZdarzenBiblioteczna;
import symulacja.Zdarzenie;
import symulacja.SilnikSymulacji;
// Testy bibliotecznej kolejki zdarzeń: wydawanie według czasu oraz stabilność (przy równym czasie kolejność wstawiania)
class KolejkaZdarzenTest {
    // Tworzy proste zdarzenie o zadanym czasie - akcja pusta, bo testujemy tylko zachowanie kolejki
    private Zdarzenie zdarzenie(int czas) {
        return new Zdarzenie(czas) {
            @Override
            public void wykonaj(SilnikSymulacji silnik) {
            }
        };
    }
    // Zdarzenia są wydawane od najwcześniejszego czasu, niezależnie od kolejności wstawiania
    @Test
    void wydajeZdarzeniaWKolejnosciCzasu() {
        KolejkaZdarzen kolejka = new KolejkaZdarzenBiblioteczna();
        Zdarzenie pozne = zdarzenie(30);
        Zdarzenie wczesne = zdarzenie(10);
        Zdarzenie srednie = zdarzenie(20);
        kolejka.wstaw(pozne);
        kolejka.wstaw(wczesne);
        kolejka.wstaw(srednie);
        assertSame(wczesne, kolejka.pobierz());
        assertSame(srednie, kolejka.pobierz());
        assertSame(pozne, kolejka.pobierz());
    }
    // Przy równym czasie zdarzenia wydawane są w kolejności wstawiania (stabilność)
    @Test
    void przyRownymCzasieZachowujeKolejnoscWstawiania() {
        KolejkaZdarzen kolejka = new KolejkaZdarzenBiblioteczna();
        Zdarzenie pierwsze = zdarzenie(50);
        Zdarzenie drugie = zdarzenie(50);
        Zdarzenie trzecie = zdarzenie(50);
        kolejka.wstaw(pierwsze);
        kolejka.wstaw(drugie);
        kolejka.wstaw(trzecie);
        assertSame(pierwsze, kolejka.pobierz());
        assertSame(drugie, kolejka.pobierz());
        assertSame(trzecie, kolejka.pobierz());
    }
    // Próba pobrania z pustej kolejki zgłasza wyjątek
    @Test
    void pustaKolejkaZglaszaWyjatek() {
        KolejkaZdarzen kolejka = new KolejkaZdarzenBiblioteczna();
        assertThrows(IllegalStateException.class, kolejka::pobierz);
    }
    // czyPusta poprawnie raportuje stan kolejki
    @Test
    void czyPustaPoprawnieRaportujeStan() {
        KolejkaZdarzen kolejka = new KolejkaZdarzenBiblioteczna();
        assertTrue(kolejka.czyPusta());
        kolejka.wstaw(zdarzenie(5));
        assertFalse(kolejka.czyPusta());
        kolejka.pobierz();
        assertTrue(kolejka.czyPusta());
    }
}
