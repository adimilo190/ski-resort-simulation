package symulacja;

import sportowcy.Sportowiec;
import elementy_symulacji.Wyciag;
// Klasa reprezentująca zdarzenie zakończenia wjazdu wyciągiem, które aktualizuje stan sportowca i generuje kolejne decyzje
public class ZdarzenieKoniecWjazduWyciagiem extends Zdarzenie {
    private final Sportowiec sportowiec;
    private final Wyciag wyciag;

    public ZdarzenieKoniecWjazduWyciagiem(int czasWystapienia, Sportowiec sportowiec, Wyciag wyciag) {
        super(czasWystapienia);
        this.sportowiec = sportowiec;
        this.wyciag = wyciag;
    }

    @Override
    public void wykonaj(SilnikSymulacji silnik) {
        sportowiec.setAktualnyWezel(wyciag.getKoniec());
        sportowiec.zarejestrujWjazd(wyciag, getCzasWystapienia());
        sportowiec.raportSledzenia(getCzasWystapienia(), "Sportowiec" + sportowiec.getId() + " zakończył wjazd wyciągiem nr " + wyciag.getId());
        sportowiec.kontynuuj(getCzasWystapienia(), silnik);
    }
}