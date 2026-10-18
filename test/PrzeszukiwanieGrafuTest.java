package testy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import elementy_symulacji.Wezel;
import elementy_symulacji.Trasa;
import elementy_symulacji.Wyciag;
import elementy_symulacji.Krawedz;
import przeszukiwanie.Sciezka;
import przeszukiwanie.WyszukiwarkaSciezek;
// Testy klasy WyszukiwarkaSciezek na grafie o strukturze jak na rysunku w treści (węzły 0-5, zmodyfikowany fragment ośrodka z drugiego pliku z przykładowymi danymi)
// Dokładne parametry tras i wyciągów nie mają znaczenia - liczy się kierunek krawędzi
class PrzeszukiwanieGrafuTest {
    private Wezel[] w;
    private WyszukiwarkaSciezek wyszukiwarka;
    // Buduje graf testowy przed każdym testem
    @BeforeEach
    void zbudujGraf() {
        w = new Wezel[6];
        for (int i = 0; i < 6; i++) {
            w[i] = new Wezel(i, 1000, i, i, false);
        }
        // Wyciągi (kierunek w górę stoku)
        dodajWyciag(0, 1);
        dodajWyciag(1, 3);
        dodajWyciag(2, 1);
        dodajWyciag(5, 3);
        // Trasy (kierunek w dół stoku); kolejność dodawania w obrębie węzła decyduje o wyborze przy remisie ścieżek
        dodajTrase(1, 0);
        dodajTrase(1, 2);
        dodajTrase(2, 4);
        dodajTrase(3, 1);
        dodajTrase(3, 4);
        dodajTrase(4, 0);
        dodajTrase(4, 1);
        dodajTrase(4, 5);
        wyszukiwarka = new WyszukiwarkaSciezek(6);
    }
    // Ścieżka z węzła 0 do 4 prowadzi przez 0, 1, 2, 4 i ma odległość 3
    @Test
    void sciezkaZ0Do4PrzebiegaPrzez0124() {
        Sciezka s = wyszukiwarka.znajdzNajkrotszaSciezke(w[0], w[4]);
        assertTrue(s.istnieje());
        assertEquals(3, s.odleglosc());
        assertEquals(List.of(0, 1, 2, 4), wezlyNaSciezce(w[0], s));
    }
    // Ścieżka z węzła 3 do 1 jest bezpośrednia (odległość 1)
    @Test
    void sciezkaZ3Do1JestBezposrednia() {
        Sciezka s = wyszukiwarka.znajdzNajkrotszaSciezke(w[3], w[1]);
        assertTrue(s.istnieje());
        assertEquals(1, s.odleglosc());
        assertEquals(List.of(3, 1), wezlyNaSciezce(w[3], s));
    }
    // Ścieżka z węzła 2 do samego siebie jest pusta (odległość 0)
    @Test
    void sciezkaZ2DoSamego2JestPusta() {
        Sciezka s = wyszukiwarka.znajdzNajkrotszaSciezke(w[2], w[2]);
        assertTrue(s.istnieje());
        assertEquals(0, s.odleglosc());
        assertTrue(s.getKrawedzie().isEmpty());
    }
    // Ścieżka z węzła 4 do 3 ma długość 2 - algorytm wybiera jedną z dwóch możliwych ścieżek (4-1-3 lub 4-5-3)
    @Test
    void sciezkaZ4Do3MaDlugosc2() {
        Sciezka s = wyszukiwarka.znajdzNajkrotszaSciezke(w[4], w[3]);
        assertTrue(s.istnieje());
        assertEquals(2, s.odleglosc());
        List<Integer> wezly = wezlyNaSciezce(w[4], s);
        assertEquals(4, wezly.get(0));
        assertEquals(3, wezly.get(2));
        assertTrue(wezly.get(1) == 1 || wezly.get(1) == 5);
    }
    // Pomocnicza metoda dodająca trasę między węzłami (parametry trasy nie mają znaczenia dla testu)
    private void dodajTrase(int od, int doWezla) {
        Trasa t = new Trasa(od * 10 + doWezla, w[od], w[doWezla], 1, 60, 0.3, 0.99);
        w[od].dodajTrase(t);
    }
    // Pomocnicza metoda dodająca wyciąg między węzłami (parametry wyciągu nie mają znaczenia dla testu)
    private void dodajWyciag(int od, int doWezla) {
        Wyciag wy = new Wyciag(od * 10 + doWezla, w[od], w[doWezla], 10, 4, 120);
        w[od].dodajWyciag(wy);
    }
    // Zwraca ciąg numerów węzłów na ścieżce: węzeł startowy oraz koniec każdej kolejnej krawędzi
    private List<Integer> wezlyNaSciezce(Wezel start, Sciezka s) {
        List<Integer> wezly = new ArrayList<>();
        wezly.add(start.getId());
        for (Krawedz k : s.getKrawedzie()) {
            wezly.add(k.getKoniec().getId());
        }
        return wezly;
    }
}
