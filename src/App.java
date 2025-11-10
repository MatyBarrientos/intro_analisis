import java.util.*;
import java.text.*;

public class App {

    public class SimuladorInversion {

        static final double CAPITAL_INICIAL = 850000.0;
        static final String[] BANCOS = { "Banco Provincia", "Banco Nación", "Banco Hipotecario" };

        public static void main(String[] args) {
            Scanner sc = new Scanner(System.in);
            int nBancos = BANCOS.length;

            double[] tnaProm = new double[nBancos];
            double[] fvAnual = new double[nBancos];
            double[] fvTrim = new double[nBancos];
            double[] fvMes = new double[nBancos];

            for (int i = 0; i < nBancos; i++) {
                double[] tasas = leerTasas(sc, BANCOS[i]);
                tnaProm[i] = promedio(tasas);
                fvAnual[i] = futuroAnual(CAPITAL_INICIAL, tnaProm[i]);
                fvTrim[i] = futuroTrimestral(CAPITAL_INICIAL, tnaProm[i]);
                fvMes[i] = futuroMensual(CAPITAL_INICIAL, tnaProm[i]);
            }

            mostrarTabla(tnaProm, fvAnual, fvTrim, fvMes);

            // Recomendación general
            int mejorBanco = -1, mejorModalidad = -1; // 0=anual,1=trim,2=mes
            double mejorMonto = -1.0;
            for (int i = 0; i < nBancos; i++) {
                double[] montos = { fvAnual[i], fvTrim[i], fvMes[i] };
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
            System.out.println("Monto final estimado: " + formatoPesoArg(mejorMonto));
        }

        // Ingreso de los datos (3 por banco - 9 en total)
        static double[] leerTasas(Scanner sc, String banco) {
            System.out.println("\n> Ingresá 3 TNAs históricas para " + banco + " (en %).");
            double[] v = new double[3];
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
                        v[i] = t / 100.0; // a decimal
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("  Valor inválido. Ej: 78.5");
                    }
                }
            }
            return v;
        }

        static double promedio(double[] a) {
            double s = 0.0;
            for (double x : a)
                s += x;
            return s / a.length;
        }

        static double futuroAnual(double P, double rTNA) {
            return P * (1 + rTNA);
        }

        static double futuroTrimestral(double P, double rTNA) {
            return P * Math.pow(1 + rTNA / 4.0, 4.0);
        }

        static double futuroMensual(double P, double rTNA) {
            return P * Math.pow(1 + rTNA / 12.0, 12.0);
        }

        // --- Salida ---
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
            DecimalFormat decimalFormat = new DecimalFormat("$ #,##0.00", s);
            return decimalFormat.format(value);
        }
    }
}