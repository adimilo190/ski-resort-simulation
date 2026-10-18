package struktury_symulacji;

import symulacja.Zdarzenie;
import java.util.PriorityQueue;
// Implementacja kolejki zdarzeń oparta o biblioteczną kolejkę priorytetową java.util.PriorityQueue
// Zdarzenie jest Comparable (najpierw czas, a przy remisie kolejność wstawiania), więc kolejka jest stabilna bez dodatkowego opakowania
public class KolejkaZdarzenBiblioteczna implements KolejkaZdarzen {
    private final PriorityQueue<Zdarzenie> kolejka;

    public KolejkaZdarzenBiblioteczna() {
        kolejka = new PriorityQueue<>();
    }
    // Wstawia zdarzenie do kolejki
    @Override
    public void wstaw(Zdarzenie z) {
        kolejka.add(z);
    }
    // Pobiera (i usuwa) zdarzenie o najmniejszym czasie wystąpienia
    @Override
    public Zdarzenie pobierz() {
        if (czyPusta()) throw new IllegalStateException("Kolejka zdarzen jest pusta");
        return kolejka.poll();
    }
    // Sprawdza, czy kolejka jest pusta
    @Override
    public boolean czyPusta() {
        return kolejka.isEmpty();
    }
}
