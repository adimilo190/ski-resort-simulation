package testy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import elementy_symulacji.Osrodek;
import elementy_symulacji.Wezel;
import elementy_symulacji.Trasa;
import sportowcy.Sportowiec;
import sportowcy.SportowiecLokalny;
// Testy mechanizmu znudzenia trasami (wygładzanie wykładnicze z_t = beta*x_t + (1-beta)*z_(t-1) oraz leniwe zanikanie z_(k+i) = z_k*(1-beta)^i)
class ZnudzenieTest {
    private Osrodek osrodek;
    private Trasa t0;
    private Trasa t1;
    private Wezel start;
    // Buduje minimalny ośrodek z dwiema trasami (o numerach 0 i 1)
    @BeforeEach
    void zbudujOsrodek() {
        osrodek = new Osrodek();
        start = new Wezel(0, 100, 0, 0, true);
        Wezel dol = new Wezel(1, 50, 0, 1, false);
        osrodek.dodajWezel(start);
        osrodek.dodajWezel(dol);
        t0 = new Trasa(0, start, dol, 5, 60, 0.3, 0.99);
        t1 = new Trasa(1, start, dol, 5, 60, 0.3, 0.99);
        osrodek.dodajTrase(t0);
        osrodek.dodajTrase(t1);
    }
    // Tworzy sportowca o zadanym współczynniku znudzenia beta
    private Sportowiec sportowiec(double beta) {
        return new SportowiecLokalny(0, 5, 0.0, beta, 0.5, 0.5, 0.0, false, start, osrodek);
    }
    // Na początku dnia znudzenie każdą trasą wynosi 0
    @Test
    void poczatkoweZnudzenieToZero() {
        Sportowiec s = sportowiec(0.5);
        assertEquals(0.0, s.poziomZnudzenia(t0), 1e-9);
        assertEquals(0.0, s.poziomZnudzenia(t1), 1e-9);
    }
    // Po jednym zjeździe daną trasą znudzenie nią rośnie do beta (z_1 = beta*1 + (1-beta)*0)
    @Test
    void znudzenieRosniePoZjezdzieTaSamaTrasa() {
        Sportowiec s = sportowiec(0.5);
        s.zarejestrujZjazd(t0, 100);
        assertEquals(0.5, s.poziomZnudzenia(t0), 1e-9);
    }
    // Dla beta = 0 sportowiec nigdy się nie nudzi
    @Test
    void betaZeroOznaczaBrakZnudzenia() {
        Sportowiec s = sportowiec(0.0);
        s.zarejestrujZjazd(t0, 100);
        s.zarejestrujZjazd(t0, 200);
        assertEquals(0.0, s.poziomZnudzenia(t0), 1e-9);
    }
    // Znudzenie trasą zanika wykładniczo wraz z kolejnymi zjazdami innymi trasami
    @Test
    void znudzenieZanikaPrzyZjazdachInnymiTrasami() {
        Sportowiec s = sportowiec(0.5);
        s.zarejestrujZjazd(t0, 100);   // z(t0) = 0.5 (indeks 1)
        s.zarejestrujZjazd(t1, 200);   // zjazd inną trasą (indeks 2)
        assertEquals(0.25, s.poziomZnudzenia(t0), 1e-9);   // 0.5 * (1-0.5)^1
        s.zarejestrujZjazd(t1, 300);   // kolejny zjazd inną trasą (indeks 3)
        assertEquals(0.125, s.poziomZnudzenia(t0), 1e-9);  // 0.5 * (1-0.5)^2
    }
    // Dla beta = 1 znudzenie trasą znika natychmiast po jednym zjeździe inną trasą (nie lubi powtarzać tej samej trasy dwa razy z rzędu)
    @Test
    void betaJedenZnudzenieZnikaPoInnymZjezdzie() {
        Sportowiec s = sportowiec(1.0);
        s.zarejestrujZjazd(t0, 100);
        assertEquals(1.0, s.poziomZnudzenia(t0), 1e-9);
        s.zarejestrujZjazd(t1, 200);
        assertEquals(0.0, s.poziomZnudzenia(t0), 1e-9);
    }
}
