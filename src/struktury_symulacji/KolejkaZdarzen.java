package struktury_symulacji;

import symulacja.Zdarzenie;
// Interfejs definiujący metody dla kolejki zdarzeń, która będzie używana w silniku symulacji do zarządzania zdarzeniami
public interface KolejkaZdarzen {
    void wstaw(Zdarzenie z);
    Zdarzenie pobierz();
    boolean czyPusta();
}