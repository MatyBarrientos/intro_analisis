package dominio;

import java.util.*;
import java.text.*;

public class SimuladorInversion {

    static final double CAPITAL_INICIAL = 850000.0;
    static final String[] BANCOS = { "Banco Provincia", "Banco Nación", "Banco Hipotecario" };

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int nBancos = BANCOS.length;

        double[] tnaProm = new double[nBancos];
        double[] anual = new double[nBancos];
        double[] trimestre = new double[nBancos];
        double[] mensual = new double[nBancos];

        for (int i = 0; i < nBancos; i++) {
            double[] tasas = leerTasas(sc, BANCOS[i]);
            tnaProm[i] = promedio(tasas);
            anual[i] = rendimientoAnual(CAPITAL_INICIAL, tnaProm[i]);
            trimestre[i] = rendimientoTrimestral(CAPITAL_INICIAL, tnaProm[i]);
            mensual[i] = rendimientoMensual(CAPITAL_INICIAL, tnaProm[i]);
        }

        mostrarTabla(tnaProm, anual, trimestre, mensual);

        // Recomendación general
        int mejorBanco = -1;
        int mejorModalidad = -1; // 0=anual,1=trim,2=mes
        double mejorMonto = -1.0;
        for (int i = 0; i < nBancos; i++) {
            double[] montos = { anual[i], trimestre[i], mensual[i] };
            for (int m = 0; m < 3; m++) {
                if (montos[m] > mejorMonto) {
                    mejorMonto = montos[m];
                    mejorBanco = i;
                    mejorModalidad = m;
                }
            }
        }

        String modalidad = (mejorModalidad == 0) ? "1 año"
                : (mejorModalidad == 1) ? "trimestres (reinvirtiendo)"
                        : "meses (reinvirtiendo)";

        System.out.println("\n=== Recomendación ===");
        System.out.println("Banco: " + BANCOS[mejorBanco]);
        System.out.println("Modalidad: " + modalidad);
        System.out.println("Ganancia final estimada: " + formatoPesoArg(mejorMonto));
    }

    // Ingreso de los datos (3 por banco - 9 en total)
    static double[] leerTasas(Scanner sc, String banco) {
        System.out.println("\n> Ingresá 3 TNAs históricas para " + banco + " (en %).");
        double[] valores = new double[3];
        for (int i = 0; i < 3; i++) {
            while (true) {
                System.out.print("  Año " + (i + 1) + ": ");
                String s = sc.nextLine().trim().replace(',', '.');
                try {
                    double t = Double.parseDouble(s);
                    if (t < 0) {
                        System.out.println("  La tasa no puede ser negativa.");
                        continue;
                    }
                    valores[i] = t / 100.0; // a decimal
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("  Valor inválido. Ej: 78.5");
                }
            }
        }
        return valores;
    }

    static double promedio(double[] tasas) {
        double sumatoria = 0.0;
        for (double x : tasas)
            sumatoria += x;
        return sumatoria / tasas.length;
    }

    // tna = Tasa Nominal Anual (en decimales)
    // 3 tipos de capitalización Anual,Trimestral y Menusual
    // por cada sub periodo
    // trimestre =tna/4 (4 trimestres -> 3 inversiones)
    // anual =tna/12 (12 meses)
    // para la re inversión aplico el mismo factor n cantidad de veces (4 o 12)
    // CapitalFututo= CapitalInicial *(1+tna/n)^n

    static double rendimientoAnual(double CapInicial, double tna) {
        double ra = CapInicial * (1 + tna);
        return ra - CapInicial; // solo ganancia (Gracias Cami)
    }

    static double rendimientoTrimestral(double CapInicial, double tna) {
        double rt = CapInicial * Math.pow(1 + tna / 4.0, 4.0);
        return rt - CapInicial;
    }

    static double rendimientoMensual(double CapInicial, double tna) {
        double rm = CapInicial * Math.pow(1 + tna / 12.0, 12.0);
        return rm - CapInicial;

    }

    // --- Salida decorada ---
    static void mostrarTabla(double[] tnaProm, double[] anual, double[] trim, double[] mes) {
        System.out.println("\n=== Resultados ===");
        System.out.println("Capital inicial: " + formatoPesoArg(CAPITAL_INICIAL) + "\n");
        System.out.printf("%-25s %10s %18s %18s %18s%n",
                "Banco", "TNA prom.", "Anual", "Trimestral", "Mensual");
        for (int i = 0; i < BANCOS.length; i++) {
            System.out.printf("%-25s %9.2f%% %18s %18s %18s%n",
                    BANCOS[i],
                    tnaProm[i] * 100.0,
                    formatoPesoArg(anual[i]),
                    formatoPesoArg(trim[i]),
                    formatoPesoArg(mes[i]));
        }
    }

    static String formatoPesoArg(double value) {
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setDecimalSeparator(',');
        s.setGroupingSeparator('.');
        // Método sacado del tp de Prog4
        DecimalFormat decimalFormat = new DecimalFormat("$ #,##0.00", s);
        return decimalFormat.format(value);
    }
}