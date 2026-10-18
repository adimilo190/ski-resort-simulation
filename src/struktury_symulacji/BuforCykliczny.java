package struktury_symulacji;

import sportowcy.Sportowiec;
// Klasa implementująca bufor cykliczny do przechowywania sportowców oczekujących na wyciąg
public class BuforCykliczny {
    private Sportowiec[] bufor;
    private int poczatek;
    private int dlugosc;

    public BuforCykliczny(int poczatkowaPojemnosc) {
        bufor = new Sportowiec[poczatkowaPojemnosc];
        poczatek = 0;
        dlugosc = 0;
    }

    public void dodaj(Sportowiec s) {
        if (dlugosc == bufor.length) podwojRozmiar();
        int koniec = (poczatek + dlugosc) % bufor.length;
        bufor[koniec] = s;
        dlugosc++;
    }

    public Sportowiec pobierz() {
        if (czyPusty()) return null;
        Sportowiec s = bufor[poczatek];
        bufor[poczatek] = null;
        poczatek = (poczatek + 1) % bufor.length;
        dlugosc--;
        return s;
    }

    public boolean czyPusty() {
        return dlugosc == 0;
    }
    
    public int getDlugosc() { return dlugosc; }
    
    private void podwojRozmiar() {
        Sportowiec[] nowaTablica = new Sportowiec[bufor.length * 2];
        for (int i = 0; i < dlugosc; i++) {
            nowaTablica[i] = bufor[(poczatek + i) % bufor.length];
        }
        bufor = nowaTablica;
        poczatek = 0;
    }
}