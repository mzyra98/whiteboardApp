package pl.tablica.wbapp.wyjatek;

public class WyjatekAplikacji extends RuntimeException {

    private final ErrorCode code;

    public WyjatekAplikacji(ErrorCode code) {
        super(code.getDomyslnyKomunikat());
        this.code = code;
    }

    public WyjatekAplikacji(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public WyjatekAplikacji(ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
