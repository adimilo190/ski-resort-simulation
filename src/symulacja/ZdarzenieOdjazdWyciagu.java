package symulacja;

import sportowcy.Sportowiec;
import elementy_symulacji.Wyciag;
// Klasa reprezentująca zdarzenie odjazdu wyciągu, które zabiera sportowców z kolejki oczekujących i generuje kolejne zdarzenia wjazdu oraz planuje kolejny odjazd
public class ZdarzenieOdjazdWyciagu extends Zdarzenie {
    private final Wyciag wyciag;

    public ZdarzenieOdjazdWyciagu(int czasWystapienia, Wyciag wyciag) {
        super(czasWystapienia);
        this.wyciag = wyciag;
    }

    @Override
    public void wykonaj(SilnikSymulacji silnik) {
        // Rejestrujemy odjazd (domyka statystyki kolejki sprzed zabrania pasażerów i zlicza odjazd wagonika)
        wyciag.zarejestrujOdjazd(getCzasWystapienia());
        int zabraniPasazerowie = 0;

        while (zabraniPasazerowie < wyciag.getMaksymalnaGrupa() && !wyciag.getKolejkaOczekujacych().czyPusty()) {
            Sportowiec pasazer = wyciag.getKolejkaOczekujacych().pobierz();
            if (pasazer != null) {
                pasazer.raportSledzenia(getCzasWystapienia(), "Sportowiec" + pasazer.getId() + " rozpoczął wjazd wyciągiem nr " + wyciag.getId());
                silnik.getKolejka().wstaw(new ZdarzenieKoniecWjazduWyciagiem(getCzasWystapienia() + wyciag.getCzasPrzejazdu(), pasazer, wyciag));
                zabraniPasazerowie++;
            }
        }
        wyciag.rejestrujPrzejazd(zabraniPasazerowie);
        silnik.getKolejka().wstaw(new ZdarzenieOdjazdWyciagu(getCzasWystapienia() + wyciag.getOdstepCzasowy(), wyciag));
    }
}