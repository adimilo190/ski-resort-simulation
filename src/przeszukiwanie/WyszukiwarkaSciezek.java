package przeszukiwanie;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import elementy_symulacji.Krawedz;
import elementy_symulacji.Wezel;
// Klasa wyznaczająca najkrótsze ścieżki w grafie ośrodka (krawędzie = trasy i wyciągi) algorytmem BFS
// Wydzielona z logiki sportowca, żeby można było ją niezależnie przetestować przez publiczny interfejs (kapsułkowanie - sportowiec używa jej wewnętrznie)
public class WyszukiwarkaSciezek {
    private final int liczbaWezlow;
    // Liczba węzłów grafu - potrzebna do tablic indeksowanych numerem węzła
    public WyszukiwarkaSciezek(int liczbaWezlow) {
        this.liczbaWezlow = liczbaWezlow;
    }
    // Znajduje najkrótszą ścieżkę (ciąg krawędzi) z węzła start do węzła cel; dla start == cel zwraca pustą ścieżkę o odległości 0
    public Sciezka znajdzNajkrotszaSciezke(Wezel start, Wezel cel) {
        // BFS: dla każdego węzła pamiętamy, czy był odwiedzony, oraz krawędź, którą został znaleziony po raz pierwszy (do odtworzenia ścieżki)
        boolean[] odwiedzony = new boolean[liczbaWezlow];
        Krawedz[] wchodzacaKrawedz = new Krawedz[liczbaWezlow];
        ArrayDeque<Wezel> kolejka = new ArrayDeque<>();
        odwiedzony[start.getId()] = true;
        kolejka.add(start);
        while (!kolejka.isEmpty()) {
            Wezel biezacy = kolejka.poll();
            if (biezacy.getId() == cel.getId()) {
                break;
            }
            // Rozważamy wszystkie krawędzie wychodzące: najpierw trasy, potem wyciągi
            for (int i = 0; i < biezacy.getLiczbaTras(); i++) {
                rozwazKrawedz(biezacy.getTrasa(i), odwiedzony, wchodzacaKrawedz, kolejka);
            }
            for (int i = 0; i < biezacy.getLiczbaWyciagow(); i++) {
                rozwazKrawedz(biezacy.getWyciag(i), odwiedzony, wchodzacaKrawedz, kolejka);
            }
        }
        if (!odwiedzony[cel.getId()]) {
            return new Sciezka(false, new ArrayList<>());
        }
        return new Sciezka(true, odtworzSciezke(start, cel, wchodzacaKrawedz));
    }
    // Rozważa pojedynczą krawędź w BFS: jeśli jej koniec nie był jeszcze odwiedzony, oznacza go i zapamiętuje, którą krawędzią został znaleziony
    private void rozwazKrawedz(Krawedz krawedz, boolean[] odwiedzony, Krawedz[] wchodzacaKrawedz, ArrayDeque<Wezel> kolejka) {
        Wezel sasiad = krawedz.getKoniec();
        if (!odwiedzony[sasiad.getId()]) {
            odwiedzony[sasiad.getId()] = true;
            wchodzacaKrawedz[sasiad.getId()] = krawedz;
            kolejka.add(sasiad);
        }
    }
    // Odtwarza ścieżkę od tyłu: od węzła docelowego cofamy się po zapamiętanych krawędziach wchodzących aż do startu, zbierając krawędzie w odwrotnej kolejności
    private List<Krawedz> odtworzSciezke(Wezel start, Wezel cel, Krawedz[] wchodzacaKrawedz) {
        LinkedList<Krawedz> sciezka = new LinkedList<>();
        Wezel biezacy = cel;
        while (biezacy.getId() != start.getId()) {
            Krawedz krawedz = wchodzacaKrawedz[biezacy.getId()];
            sciezka.addFirst(krawedz);
            biezacy = krawedz.getPoczatek();
        }
        return sciezka;
    }
    // Zwraca tablicę odległości (liczby krawędzi) od węzła start do każdego węzła grafu; wartość -1 oznacza węzeł nieosiągalny
    // Przydatne, gdy potrzebujemy odległości do wielu węzłów naraz (np. Kolekcjoner przy rozstrzyganiu remisów) - jedno przeszukiwanie zamiast wielu
    public int[] odleglosciOd(Wezel start) {
        int[] odleglosc = new int[liczbaWezlow];
        Arrays.fill(odleglosc, -1);
        ArrayDeque<Wezel> kolejka = new ArrayDeque<>();
        odleglosc[start.getId()] = 0;
        kolejka.add(start);
        while (!kolejka.isEmpty()) {
            Wezel biezacy = kolejka.poll();
            for (int i = 0; i < biezacy.getLiczbaTras(); i++) {
                rozwazOdleglosc(biezacy.getTrasa(i), biezacy, odleglosc, kolejka);
            }
            for (int i = 0; i < biezacy.getLiczbaWyciagow(); i++) {
                rozwazOdleglosc(biezacy.getWyciag(i), biezacy, odleglosc, kolejka);
            }
        }
        return odleglosc;
    }
    // Rozważa krawędź przy liczeniu odległości: jeśli jej koniec jest nieodwiedzony, ustawia jego odległość o 1 większą i dodaje do kolejki
    private void rozwazOdleglosc(Krawedz krawedz, Wezel od, int[] odleglosc, ArrayDeque<Wezel> kolejka) {
        Wezel sasiad = krawedz.getKoniec();
        if (odleglosc[sasiad.getId()] == -1) {
            odleglosc[sasiad.getId()] = odleglosc[od.getId()] + 1;
            kolejka.add(sasiad);
        }
    }
}
