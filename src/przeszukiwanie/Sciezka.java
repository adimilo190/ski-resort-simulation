package przeszukiwanie;
import java.util.List;
import elementy_symulacji.Krawedz;
// Wynik wyszukiwania najkrótszej ścieżki w grafie: informacja czy ścieżka istnieje oraz ciąg krawędzi do przejechania
// Odległość rozumiemy jako liczbę krawędzi (tras i wyciągów), którymi trzeba przejechać - czyli długość listy krawędzi
public class Sciezka {
    private final boolean istnieje;
    private final List<Krawedz> krawedzie;
    // Konstruktor - dla ścieżki z węzła do samego siebie lista krawędzi jest pusta (odległość 0), a istnieje = true
    public Sciezka(boolean istnieje, List<Krawedz> krawedzie) {
        this.istnieje = istnieje;
        this.krawedzie = krawedzie;
    }
    // Czy cel jest osiągalny ze startu (w silnie spójnym grafie ośrodka zawsze true)
    public boolean istnieje() {
        return istnieje;
    }
    // Odległość = liczba krawędzi na ścieżce
    public int odleglosc() {
        return krawedzie.size();
    }
    // Kolejne krawędzie od startu do celu (puste, gdy start == cel)
    public List<Krawedz> getKrawedzie() {
        return krawedzie;
    }
}
