package symulacja;

import sportowcy.Sportowiec;
import elementy_symulacji.Trasa;
// Klasa reprezentująca zdarzenie zakończenia zjazdu trasą, które aktualizuje stan sportowca i generuje kolejne decyzje
public class ZdarzenieKoniecZjazduTrasa extends Zdarzenie {
    private final Sportowiec sportowiec;
    private final Trasa trasa;

    public ZdarzenieKoniecZjazduTrasa(int czasWystapienia, Sportowiec sportowiec, Trasa trasa) {
        super(czasWystapienia);
        this.sportowiec = sportowiec;
        this.trasa = trasa;
    }

    @Override
    public void wykonaj(SilnikSymulacji silnik) {
        trasa.rejestrujPrzejazd();
        sportowiec.setAktualnyWezel(trasa.getKoniec());
        sportowiec.zarejestrujZjazd(trasa, getCzasWystapienia());
        sportowiec.raportSledzenia(getCzasWystapienia(), "Sportowiec" + sportowiec.getId() + " zakończył zjazd trasą nr " + trasa.getId());
        sportowiec.kontynuuj(getCzasWystapienia(), silnik);
    }
}