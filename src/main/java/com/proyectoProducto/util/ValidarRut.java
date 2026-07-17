package com.proyectoProducto.util;

public class ValidarRut {

   public static String limpiarRut(String rut){
        if(rut==null){
            throw new IllegalArgumentException("El RUT es obligatorio");
        }
        return rut.replace(".","").replace(" ","").toUpperCase();

    }
    public static boolean formatoValido(String rut){
        rut=limpiarRut(rut);
        return rut.matches("^\\d{7,8}-[\\dK]$");
    }

    public static String rutFormateado(String rut){
        rut=limpiarRut(rut);
        if(!formatoValido(rut)){
            throw new IllegalArgumentException("Formato de RUT invalido");
        }

        String[] partes=rut.split("-");
        String numero = partes[0];
        String dv = partes[1];

        StringBuilder sb = new StringBuilder(numero);

        for(int i = sb.length() - 3; i > 0; i -= 3){
            sb.insert(i, ".");
        }

        return sb + "-" + dv;

    }
    public static void rutValidado(String rut){
        rut=limpiarRut(rut);
        if (!formatoValido(rut)) {
            throw new IllegalArgumentException("Formato de RUT invalido");
        }
        int indexGuion=rut.indexOf("-");
        String numero=rut.substring(0,indexGuion);
        char dv = rut.charAt(rut.length() - 1);

        int multiplicador =2;
        int suma=0;

        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(numero.charAt(i));

            suma+=digito*multiplicador;
            multiplicador++;
            if(multiplicador>7){
                multiplicador=2;
            }
        }
        int resto=suma%11;
        int dvFinal=11-resto;
       char dvEsperado;

       if(dvFinal==11){
           dvEsperado='0';
       }else if(dvFinal==10){
           dvEsperado='K';
       }else{
           dvEsperado=Character.forDigit(dvFinal,10);
       }

        if (dvEsperado != dv) {
            throw new IllegalArgumentException("El RUT no es válido");
        }
    }
    public static String validarYFormatearRut(String rut){
       rutValidado(rut);
       return rutFormateado(rut);
    }
}
