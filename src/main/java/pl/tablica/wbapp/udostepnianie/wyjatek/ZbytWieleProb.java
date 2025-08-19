package pl.tablica.wbapp.udostepnianie.wyjatek;

public class ZbytWieleProb extends RuntimeException {
    public ZbytWieleProb() {
        super("Za dużo prób. Spróbuj ponownie później.");
    }
    public ZbytWieleProb(String message) {
        super(message);
    }
}