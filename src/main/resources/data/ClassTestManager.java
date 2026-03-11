package it.edu.iisgubbio.oggetti;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * Classe di utilità per i test
 * @hidden
 */
public class ClassTestManager {

    private final boolean terminale;
    private final String errore;
    private final String ok;
    public String pacchetto;
    public String classe;
    ArrayList<String> messaggi = new ArrayList<>();

    /************************************************************************
     * Crea un nuovo manager
     * @param pacchetto su cui viene svolto il test
     * @param terminale true se l'output va su terminale "a colori"
     ***********************************************************************/
    public ClassTestManager(String pacchetto, boolean terminale) {
        errore = terminale ? "\u001B[31mERRORE\u001B[0m" : "ERRORE";
        ok = terminale ? "\u001B[32mOK\u001B[0m" : "OK";
        this.pacchetto=pacchetto;
        this.terminale=terminale;
    }

    public ArrayList<String> getMessaggi() {
        return messaggi;
    }

    public void stampa(String messaggio , Object atteso, Object ricevuto){
        stampa(messaggio, atteso, ricevuto, false);
    }

    public void stampa(String messaggio , Object atteso, Object ricevuto, boolean tolleranza){
        boolean esito;
        if(atteso instanceof String a && ricevuto instanceof String b){
            if(tolleranza){
                esito = distanzaLevenshtein(a, b) <= 2;
            } else {
                esito = a.equals(b);
            }
        }else{
            esito = atteso.equals(ricevuto);
        }
        if(esito){
            String m = classe+" "+messaggio+" "+ok;
            messaggi.add(m);
            if(terminale){
                System.out.println(m);
            }
        }else{
            String m = classe+" "+messaggio+" {atteso:\""+atteso+"\" ricevuto:\""+ricevuto+"\"} "+errore;
            messaggi.add(m);
            if(terminale){
                System.out.println(m);
            }
        }
    }

    public void stampa(String messaggio, boolean test) {
        String m = classe+" "+messaggio+" "+ ( test ? ok : errore );
        messaggi.add(m);
        if(terminale){
            System.out.println(m);
        }
    }

    public void stampa(Exception ex) {
        Throwable t = ex;
        // mi arrampico sui "caused by"
        while(t.getCause()!=null) {
            t = t.getCause();
        }
        String colpevole = t.getMessage().replaceAll("\\b[a-z\\.]+\\.", "");
        String tipo = t.getClass().getSimpleName();
        stampa(tipo+" "+colpevole, false);
    }

    private static int distanzaLevenshtein(String s1, String s2) {
        s1 = s1.replaceAll(" ", "").toLowerCase();
        s2 = s2.replaceAll(" ", "").toLowerCase();

        int[][] distanza = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            distanza[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            distanza[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    distanza[i][j] = distanza[i - 1][j - 1];
                } else {
                    distanza[i][j] = 1 + Math.min(distanza[i - 1][j], Math.min(distanza[i][j - 1], distanza[i - 1][j - 1]));
                }
            }
        }
        return distanza[s1.length()][s2.length()];
    }

    /* parecchio in breve serve per aggirare il meccanismo di boxing quando vengono
    chiamate le funzioni crea e chiama */
    private static Class<?> toPrimitive(Class<?> c) {
        if (c == Double.class)  return double.class;
        if (c == Integer.class) return int.class;
        if (c == Float.class)   return float.class;
        if (c == Long.class)    return long.class;
        if (c == Boolean.class) return boolean.class;
        return c;
    }

    public Object crea(Object... args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
        String className = this.pacchetto+"."+this.classe;
        Class<?> clazz = Class.forName(className);
        if (args.length == 0) {
            return clazz.getDeclaredConstructor().newInstance();
        }
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = toPrimitive(args[i].getClass());
        }
        return clazz.getDeclaredConstructor(types).newInstance(args);
    }

    public Object chiama(Object obj, String method, Object... args) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (obj == null) return null;
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = toPrimitive(args[i].getClass());
        }
        return obj.getClass().getMethod(method, types).invoke(obj, args);
    }
}
