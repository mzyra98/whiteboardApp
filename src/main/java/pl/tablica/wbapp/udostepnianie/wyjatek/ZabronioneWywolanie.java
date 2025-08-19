package pl.tablica.wbapp.udostepnianie.wyjatek;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ZabronioneWywolanie extends RuntimeException { }