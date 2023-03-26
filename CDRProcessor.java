
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class CDRProcessor {
    public static void main(String[] args) {
        String inputFilePath = "cdr.txt";

        // Step 1: Read CDR file and parse CDR records
        List<CDR> cdrList = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(inputFilePath));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split(",");
                CDR cdr = new CDR(fields[0], fields[1], fields[2], fields[3], fields[4]);
                cdrList.add(cdr);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Step 2: Group CDR records by subscriber number
        Map<String, List<CDR>> cdrByNumber = new HashMap<>();
        Map<String, Long> minByNum = new HashMap<>();

        // Group CDR records by subscriber number
        for (CDR cdr : cdrList) {
            List<CDR> cdrsForNumber = cdrByNumber.getOrDefault(cdr.getNumber(), new ArrayList<>());
            cdrsForNumber.add(cdr);
            cdrByNumber.put(cdr.getNumber(), cdrsForNumber);

        }

        // Calculate charges and generate report for each subscriber
        for (String number : cdrByNumber.keySet()) {
            List<CDR> cdrsForNumber = cdrByNumber.get(number);

            // Sort CDR records by call start time
            cdrsForNumber.sort(Comparator.comparing(CDR::getStartTime));

            // Initialize variables for charge calculation
            int totalMinutes = 0;
            double totalCharge = 0;
            String tariff = cdrsForNumber.get(0).getTariffType();

            // Generate report header
            StringBuilder report = new StringBuilder();
            report.append("Tariff index:").append(cdrsForNumber.get(0).getTariffType()).append("\n");
            report.append("----------------------------------------------------------------------------").append("\n");
            report.append("Report for phone number").append(number).append(":\n");
            report.append("----------------------------------------------------------------------------").append("\n");
            report.append("| Call Type |   Start Time        |     End Time        | Duration | Cost  |\n" +
                    "----------------------------------------------------------------------------\n");


            // Calculate charges and generate report for each call
            for (CDR cdr : cdrsForNumber) {
                long callMinutes = cdr.getDuration() / 60000;
                totalMinutes += callMinutes;

                double charge = 0.0;
                switch (tariff) {
                    case " 06":
                        if (totalMinutes < 300) {
                            charge = 0;
                        } else {
                            charge += (totalMinutes - 300);
                        }
                        break;
                    case " 03":
                        charge = callMinutes * 1.5;
                        break;
                    case " 11":
                        if (cdr.getCallType().equals("02")) {
                            if (totalMinutes <= 100) {
                                charge = 0.5 * callMinutes;
                            } else {
                                charge = (totalMinutes - 100) * 1.5 + 50;
                            }
                        }
                        break;
                }
                totalCharge += charge;

                report.append("|    ").append(cdr.getCallType()).append("    |").append(cdr.getStartTime()).append(" | ").
                        append(cdr.getEndTime()).append(" | ").append(cdr.getDur()).append(" | ").append(charge).append(" | \n");
            }
            report.append("----------------------------------------------------------------------------\n");


            if (tariff.equals(" 06")) {
                int freeMinutes = Math.min(totalMinutes, 300);
                double chargedMinutes = Math.max(0, totalMinutes - freeMinutes);
                double chargedAmount = chargedMinutes + 100;
                totalCharge = Math.max(totalCharge, chargedAmount);

            }
            report.append("|                                           Total Cost: |     ").append(totalCharge).append(" rubles |\n" +
                    "----------------------------------------------------------------------------");


            // Write report to file
            String filename = "reports/" + number + ".txt";
            try (PrintWriter writer = new PrintWriter(filename)) {
                writer.write(report.toString());
            } catch (FileNotFoundException e) {
                System.err.println("Error writing report to file: " + e.getMessage());
            }
        }
    }


}
