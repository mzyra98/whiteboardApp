package pl.tablica.wbapp.wyjatek;

public class KolizjaWartosci extends WyjatekAplikacji {

    public KolizjaWartosci(ErrorCode code, String message) {
        super(code, message);
    }

    public static KolizjaWartosci emailZajety(String email) {
        return new KolizjaWartosci(ErrorCode.EMAIL_ZAJETY, "Email '" + email + "' jest już używany.");
    }

    public static KolizjaWartosci nazwaZajeta(String nazwa) {
        return new KolizjaWartosci(ErrorCode.NAZWA_UZYTKOWNIKA_ZAJETA,
                "Nazwa użytkownika '" + nazwa + "' jest już zajęta.");
    }
}
