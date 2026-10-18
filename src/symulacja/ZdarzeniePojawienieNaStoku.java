package symulacja;

import sportowcy.Sportowiec;
// Klasa reprezentująca zdarzenie pojawienia się sportowca na stoku, które inicjuje jego obecność w symulacji i generuje pierwsze decyzje dotyczące wyboru trasy lub wyciągu
public class ZdarzeniePojawienieNaStoku extends Zdarzenie {
    private final Sportowiec sportowiec;

    public ZdarzeniePojawienieNaStoku(int czasWystapienia, Sportowiec sportowiec) {
        super(czasWystapienia);
        this.sportowiec = sportowiec;
    }

    @Override
    public void wykonaj(SilnikSymulacji silnik) {
        // Sportowiec domyślnie jest już umieszczony we właściwym węźle startowym przez konstruktor.
        sportowiec.raportSledzenia(getCzasWystapienia(), "Sportowiec" + sportowiec.getId() + " pojawił się na stoku w węźle nr " + sportowiec.getAktualnyWezel().getId());
        sportowiec.kontynuuj(getCzasWystapienia(), silnik);
    }
}