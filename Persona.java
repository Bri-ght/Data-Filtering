import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

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
            big[i] = new Peers(old[i].getname(), old[i].getnumber());
        }
        return big;
    }

    public static Peers[] process(int min, int max, Persona[] arr){
        Peers[] processed = new Peers[100];
        int k = 0;
        int len = arr.length;
        for (int i = 0; i < len; i++){
            boolean flag = false;
            Persona person = arr[i];
            if (min <= person.nac && max >= person.nac){
                // BUSCAR EN Peers SI HAY ESE NOMBRE
                for (int j = 0; j < k; j++){
                    if (person.nom.equals(processed[j].getname())){
                        processed[j].plus();
                        flag = true;
                        break;
                    }
                }
                if (!flag){
                    processed[k] = new Peers(person.nom);
                    k += 1;
                }
            }
            //System.out.println(k);
            if (k == processed.length){
                processed = makebigger(processed);
                //System.out.println(processed.length);
            }
        }
        return processed;
    }

    public static void main(String[] args){
        Persona[] personas = new Persona[0];
        try{
            personas = leeFichero("personas_va.txt");
        }
        catch(IOException e){
            System.out.println("Error en la apertura del archivo.");
            System.exit(1);
        }
        int NUMERO = 5;
        String NOMBRE = "CESAR";
        //TODO CAMBIAR FINALS Y A MAYUSCULAS EL NOMBRE
        String date = "1/1/1974 31/12/1982";
        String[] splitdate = date.split(" ");
        int datemin = traduceFecha(splitdate[0]);
        int datemax = traduceFecha(splitdate[1]);
        Peers[] data = process(datemin, datemax, personas);
        //System.out.println(data.length);
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

    String getname(){
        return name;
    }
    
    int getnumber(){
        return number;
    }
}