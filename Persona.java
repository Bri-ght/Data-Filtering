import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.util.Random;

public class Persona{
    int nac;
    int fac;
    int gen; // 0 -> HOMBRE, 1 -> MUJER
    String nom;
    
    public static Persona[] leeFichero(String nomfich) throws IOException{
        BufferedReader br = new BufferedReader (new FileReader(new File(nomfich)),131072);
        int n = Integer.parseInt(br.readLine());
        System.out.println("_|____|____|____|____|");
        long t0, t1, t2;
        t0 = System.nanoTime();
        Persona[] dat = new Persona[n]; 
        for(int i = 0; i<n; i++){
            dat[i]=new Persona();
        }
        t1 = System.nanoTime()-t0;
        System.out.print("*");
        int lim = n/20;
        t0 = System.nanoTime();
        for(int i = 0; i<n; i++){
            dat[i].parse(br.readLine(),br.readLine());
            if(i % lim == lim - 1){
                System.out.print("*");
            }
        }
        br.close();
        t2 = System.nanoTime() - t0;
        System.out.printf("\nCreación array: %.5f seg. Lectura fichero: %.5f seg.\n", 1e-9*t1, 1e-9*t2);
        System.out.printf("n = %d personas en total.\n\n",dat.length);
        return dat;
    }

    public void parse(String lin1, String lin2){
        nac = (lin1.charAt(0)-48) *10000 + (lin1.charAt(1)-48)*1000 +
              (lin1.charAt(2)-48) *100 + (lin1.charAt(3)-48)*10 +
              (lin1.charAt(4)-48);
        fac = (lin1.charAt(6)-48) *10000 + (lin1.charAt(7)-48)*1000 +
              (lin1.charAt(8)-48) *100 + (lin1.charAt(9)-48)*10 +
              (lin1.charAt(10)-48);
        gen = lin1.charAt(12)-48;
        nom = lin2;
    }

    public static int traduceFecha(String fec){
        String[] trozos = fec.split("/");
        int dia = Integer.parseInt(trozos[0]);
        int mes = Integer.parseInt(trozos[1]);
        int ano = Integer.parseInt(trozos[2]);
        return 367*ano - (7*(ano+5001+(mes-9)/7))/4 + (275*mes)/9 + dia - 692561;
    }

    public static Peers[] makebigger(Peers[] old){
        int len = old.length;
        Peers[] big = new Peers[2*old.length];
        for (int i = 0; i < len; i++){
            big[i] = old[i];
        }
        return big;
    }

    public static Peers[] process(int min, int max, Persona[] arr, Counter filt, Counter ins){
        Peers[] processed = new Peers[100];
        int k = 0;
        int len = arr.length;
        long t0, t1;
        for (int i = 0; i < len; i++){
            boolean flag = false;
            Persona person = arr[i];
            filt.compsPlusOne();
            t0 = System.nanoTime();
            if (min <= person.nac && max >= person.nac){
                t1 = System.nanoTime();
                filt.addTime(t1-t0);
                // BUSCAR EN Peers SI HAY ESE NOMBRE
                t0 = System.nanoTime();
                for (int j = 0; j < k; j++){
                    ins.compsPlusOne();
                    if (person.nom.equals(processed[j].getName())){
                        processed[j].plus();
                        flag = true;
                        break;
                    }
                }
                if (!flag){
                    ins.movesPlusOne();
                    processed[k] = new Peers(person.nom);
                    k += 1;
                    if (k == processed.length) {
                        ins.addMoves(k);
                        processed = makebigger(processed);
                    }
                }
                t1 = System.nanoTime();
                ins.addTime(t1-t0);
            }
        }
        return processed;
    }
    
    public static void aleatoriza(Peers[] v, int len) {
    	Peers tmp;
        int tmp_int;
        Random rnd = new Random();
        for(int i = 0; i < len; i++) {
            tmp = v[i];
            tmp_int = rnd.nextInt(len-i);
            v[i] = v[tmp_int];
            v[tmp_int] = tmp;
        }
    }

    public static void sort(Peers[] v, int len, Counter counter){
        Random rnd = new Random();
        System.out.println(len+"   "+rnd.nextInt(len));
        Peers tmp;
        int tmp_int=0;
        System.out.println("ANTES: "+tmp_int);
        long t0 = System.nanoTime();
        sortRec(v, 0, len-1, counter);
        long t1 = System.nanoTime();
        counter.addTime(t1-t0);
    }

    public static void sortRec(Peers[] v, int low, int hi, Counter counter){
        if (low >= hi){return;}
        int lim = partition(v, low, hi, counter);
        sortRec(v, low, lim-1, counter);
        sortRec(v, lim+1, hi, counter);
    }

    public static int partition(Peers[] v, int low, int hi, Counter counter){
        // Pivote inicial v[low]
        // Empiezo a comprobar en el siguiente low+1
        int lim = low+1;
        for (int i = low+1; i <= hi; i++){
            counter.compsPlusOne();
            counter.compsPlusOne();
            if (v[i].getNumber() > v[low].getNumber()) {
                counter.movesPlusOne();
                Peers temp = new Peers(v[i].getName(), v[i].getNumber());
                v[i] = v[lim];
                v[lim] = temp;
                lim++;
            }
        }
        counter.movesPlusOne();
        Peers temp = v[low];
        v[low] = v[lim-1];
        v[lim-1] =  temp;
        return lim - 1;
    }

    public static void main(String[] args){
        Persona[] personas = new Persona[0];
        Peers[] data;
        Counter filtrado = new Counter();
        Counter insercion = new Counter();
        Counter extraccion = new Counter();
        Counter seguimiento = new Counter();
        int fecha_min, fecha_max, numero;
        String nombre, fechas;
        String[] fechas_trad;
        long t0, t1;
        Scanner in = new Scanner(System.in);
        
        try{
            System.out.printf("Nombre del fichero: ");
            personas = leeFichero(in.nextLine());
        }
        catch(IOException e){
            System.out.println("Error en la apertura del archivo.");
            System.exit(0);
        }
        
        System.out.printf("\n*** OPCIONES GENERALES ***\nNúmeo de nombres a mostrar: ");
        numero = in.nextInt();
        System.out.printf("Nombre del usuario: ");
        nombre = in.next();
        
        while(true){
            System.out.printf("\n*** BUCLE DE CONSULTAS ***\nIntervalo de fechas de nacimiento: ");
            in.nextLine();
            fechas = in.nextLine();
            fechas_trad = fechas.split(" ");
            fecha_min = traduceFecha(fechas_trad[0]);
            fecha_max = traduceFecha(fechas_trad[1]);

            data = process(fecha_min, fecha_max, personas, filtrado, insercion);
            int size = 0;
            int m = 0;
            try{
                int temp;
                for (int i = 0; i < data.length; i++){
                    temp = data[i].getNumber();
                    m += temp;
                    size += 1;
                }
            }catch(NullPointerException n){}
            
            aleatoriza(data, size);
            sort(data, size, extraccion);

            for (int i = 0; i < numero; i++) {
                System.out.printf("%d %s %d\n",i+1,data[i].getName(), data[i].getNumber());
            }
            int different_names = filtrado.getComps();
            t0 = System.nanoTime();
            for (int i = 0; i < different_names; i++) {
                seguimiento.compsPlusOne();
                try{
                    if (data[i].getName().equals(nombre)) {
                        System.out.printf("%d %s %d\n",i+1,data[i].getName(), data[i].getNumber());
                        break;
                    }
                }catch(NullPointerException e){
                    System.out.println("No se encuentra el nombre en los registros.");
                    break;
                }
            }
            t1 = System.nanoTime();
            seguimiento.addTime(t1-t0);
            System.out.printf("\nm = %d personas que cumplen las condiciones.\n", m);
            System.out.printf("p = %d nombres distintos.\nd = %d dias en el intervalo.\n", size, fecha_max-fecha_min);
            System.out.printf("Filtrado\t%d comps\t%d moves\t\t%.5f sec\n",filtrado.getComps(), filtrado.getMoves(), 1e-9*filtrado.getTime());
            System.out.printf("Insercion\t%d comps\t%d moves\t%.5f sec\n",insercion.getComps(), insercion.getMoves(), 1e-9*insercion.getTime());
            System.out.printf("Extraccion\t%d comps\t%d moves\t%.5f sec\n",extraccion.getComps(), extraccion.getMoves(),1e-9*extraccion.getTime());
            System.out.printf("Seguimiento\t%d comps\t%d moves\t\t%.5f sec\n",seguimiento.getComps(), 0, 1e-9*seguimiento.getTime());
            System.out.printf("\n¿Continuar?[S|N]");
            String continuar = in.next();
            if (continuar.equals("N")){break;}
        }
        in.close();
    }
}

class Peers{
    private String name;
    private int number;

    Peers(String a){
        name = a;
        number = 1;
    }

    Peers(String a, int b){
        name = a;
        number = b;
    }

    void plus(){
        number += 1;
    }

    String getName(){
        return name;
    }
    
    int getNumber(){
        return number;
    }
}

class Counter{
    private int moves;
    private int comps;
    private long time;

    public Counter(){
        this.time = 0;
        this.moves = 0;
        this.comps = 0;
    }

    public void movesPlusOne(){
        moves += 1;
    }

    public void addMoves(int a){
        moves += a;
    }

    public void compsPlusOne(){
        comps += 1;
    }

    public int getMoves(){
        return moves;
    }

    public int getComps(){
        return comps;
    }

    public long getTime(){
        return time;
    }

    public void addTime(long a){
        time += a;
    }
}