package testy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import elementy_symulacji.Osrodek;
import elementy_symulacji.Wezel;
import elementy_symulacji.Trasa;
import sportowcy.Sportowiec;
import sportowcy.SportowiecLokalny;
// Testy nowego wzoru na atrakcyjność trasy: a = alfa_d*d + alfa_w*w + alfa_z*(1-z)
class AtrakcyjnoscTrasyTest {
    private Osrodek osrodek;
    private Wezel start;
    private Trasa trasaDopasowana;   // poziom trudności równy poziomowi sportowca (d = 1)
    private Trasa trasaZaTrudna;     // poziom trudności o 5 wyższy (d = 0)
    @BeforeEach
    void zbudujOsrodek() {
        osrodek = new Osrodek();
        start = new Wezel(0, 100, 0, 0, true);
        Wezel dol = new Wezel(1, 50, 0, 1, false);
        osrodek.dodajWezel(start);
        osrodek.dodajWezel(dol);
        trasaDopasowana = new Trasa(0, start, dol, 5, 60, 0.3, 0.99);
        trasaZaTrudna = new Trasa(1, start, dol, 10, 60, 0.3, 0.99);
        osrodek.dodajTrase(trasaDopasowana);
        osrodek.dodajTrase(trasaZaTrudna);
    }
    // Tworzy sportowca o poziomie 5 z zadanymi wagami i współczynnikiem znudzenia
    private Sportowiec sportowiec(double wagaD, double wagaW, double wagaZ, double beta) {
        return new SportowiecLokalny(0, 5, 0.0, beta, wagaD, wagaW, wagaZ, false, start, osrodek);
    }
    // Świeża, idealnie dopasowana trasa (d=1, w=1 przy 0 przejazdów, z=0) ma atrakcyjność równą sumie wag, czyli 1
    @Test
    void swiezaDopasowanaTrasaMaAtrakcyjnoscJeden() {
        Sportowiec s = sportowiec(0.4, 0.2, 0.4, 0.5);
        assertEquals(1.0, trasaDopasowana.obliczAtrakcyjnosc(s), 1e-9);
    }
    // Znudzenie obniża atrakcyjność o alfa_z * z (tu d=1, w=1, więc a = alfa_d + alfa_w + alfa_z*(1-z))
    @Test
    void znudzenieObnizaAtrakcyjnosc() {
        Sportowiec s = sportowiec(0.4, 0.2, 0.4, 0.5);
        s.zarejestrujZjazd(trasaDopasowana, 100);   // z = 0.5
        // a = 0.4*1 + 0.2*1 + 0.4*(1-0.5) = 0.8
        assertEquals(0.8, trasaDopasowana.obliczAtrakcyjnosc(s), 1e-9);
    }
    // Zbyt trudna trasa (poziom trudności >= poziom sportowca + 5) ma dopasowanie d = 0
    @Test
    void zaTrudnaTrasaMaZeroweDopasowanie() {
        Sportowiec s = sportowiec(1.0, 0.0, 0.0, 0.0);
        assertEquals(0.0, trasaZaTrudna.obliczAtrakcyjnosc(s), 1e-9);
    }
}
